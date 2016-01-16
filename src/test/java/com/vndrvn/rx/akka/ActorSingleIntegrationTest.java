package com.vndrvn.rx.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Notification;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ActorSingleIntegrationTest {

	private static class ExpectedMessage {}
	private static class UnexpectedMessage {}

	private static ActorSystem actorSystem;

	@BeforeClass
	public static void setupAkka() {
		actorSystem = ActorSystem.create();
	}

	@Test
	public void testExpectedMessage() {
		final ActorSingle<ExpectedMessage> single = ActorSingle.create(actorSystem, ExpectedMessage.class);
		final ExpectedMessage expectedMessage = new ExpectedMessage();

		tellAfterMillis(100, single.getActorRef(), expectedMessage);

		Assert.assertEquals(expectedMessage, single.toBlocking().value());
	}

	@Test
	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	public void testUnexpectedMessage() throws InterruptedException, TimeoutException {
		final ActorSingle<ExpectedMessage> single = ActorSingle.create(actorSystem, ExpectedMessage.class);
		final UnexpectedMessage unexpectedMessage = new UnexpectedMessage();

		tellAfterMillis(100, single.getActorRef(), unexpectedMessage);

		final Notification<ExpectedMessage> notification = single.toObservable().materialize().toBlocking().first();
		Assert.assertEquals(Notification.Kind.OnError, notification.getKind());
		Assert.assertEquals(UnexpectedActorMessageException.class, notification.getThrowable().getClass());

		final UnexpectedActorMessageException throwable = (UnexpectedActorMessageException) notification.getThrowable();
		Assert.assertEquals(unexpectedMessage, throwable.getActorMessage());
	}

	protected void tellAfterMillis(final long millis, final ActorRef receiver, final Object message) {
		actorSystem.scheduler().scheduleOnce(
				Duration.create(millis, TimeUnit.MILLISECONDS),
				receiver,
				message,
				actorSystem.dispatcher(),
				ActorRef.noSender());
	}

}
