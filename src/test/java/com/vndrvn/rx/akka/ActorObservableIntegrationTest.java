package com.vndrvn.rx.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Notification;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ActorObservableIntegrationTest {

	private static class ExpectedMessage {}
	private static class UnexpectedMessage {}

	private static ActorSystem actorSystem;

	@BeforeClass
	public static void setupAkka() {
		actorSystem = ActorSystem.create();
	}

	@Test
	public void testExpectedMessage() {
		final ActorObservable<ExpectedMessage> observable = ActorObservable.create(actorSystem, ExpectedMessage.class);
		final ExpectedMessage expectedMessage = new ExpectedMessage();

		tellAfterMillis(100, observable.getActorRef(), expectedMessage);
		tellAfterMillis(125, observable.getActorRef(), expectedMessage);
		tellAfterMillis(150, observable.getActorRef(), expectedMessage);
		tellAfterMillis(175, observable.getActorRef(), expectedMessage);

		final List<ExpectedMessage> messages = observable.limit(4).toList().toBlocking().single();
		Assert.assertEquals(4, messages.size());
		Assert.assertTrue(messages.stream().allMatch(message -> message.equals(expectedMessage)));
	}

	@Test
	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	public void testUnexpectedMessage() throws InterruptedException, TimeoutException {
		final ActorObservable<ExpectedMessage> observable = ActorObservable.create(actorSystem, ExpectedMessage.class);
		final UnexpectedMessage unexpectedMessage = new UnexpectedMessage();

		tellAfterMillis(100, observable.getActorRef(), unexpectedMessage);

		final Notification<ExpectedMessage> notification = observable.materialize().toBlocking().first();
		Assert.assertEquals(Notification.Kind.OnError, notification.getKind());
		Assert.assertEquals(UnexpectedActorMessageException.class, notification.getThrowable().getClass());

		final UnexpectedActorMessageException throwable = (UnexpectedActorMessageException) notification.getThrowable();
		Assert.assertEquals(unexpectedMessage, throwable.getActorMessage());
	}

	@Test
	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	public void testUnexpectedMessageAfterOne() {
		final ActorObservable<ExpectedMessage> observable = ActorObservable.create(actorSystem, ExpectedMessage.class);
		final ExpectedMessage expectedMessage = new ExpectedMessage();
		final UnexpectedMessage unexpectedMessage = new UnexpectedMessage();

		tellAfterMillis(100, observable.getActorRef(), expectedMessage);
		tellAfterMillis(200, observable.getActorRef(), unexpectedMessage);

		final List<Notification<ExpectedMessage>> notifications = observable.materialize().toList().toBlocking().first();
		Assert.assertEquals(2, notifications.size());

		Assert.assertEquals(Notification.Kind.OnNext, notifications.get(0).getKind());
		Assert.assertEquals(expectedMessage, notifications.get(0).getValue());

		Assert.assertEquals(Notification.Kind.OnError, notifications.get(1).getKind());
		Assert.assertEquals(UnexpectedActorMessageException.class, notifications.get(1).getThrowable().getClass());

		final UnexpectedActorMessageException throwable = (UnexpectedActorMessageException) notifications.get(1).getThrowable();
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
