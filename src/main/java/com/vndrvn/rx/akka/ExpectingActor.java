package com.vndrvn.rx.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.FI;
import akka.japi.pf.ReceiveBuilder;

import java.util.Optional;

public class ExpectingActor<T> extends AbstractActor {

	public static <U> Props props(final ObservableExpectation<U> expectation) {
		return Props.create(ExpectingActor.class, expectation);
	}

	public static <U> Props props(final SingleExpectation<U> expectation) {
		return Props.create(ExpectingActor.class, expectation);
	}

	protected final Optional<ObservableExpectation<T>> expectation;

	protected ExpectingActor(final ObservableExpectation<T> expectation) {
		this.expectation = Optional.of(expectation);
		receive(ReceiveBuilder
				.match(expectation.getMessageClass(), expectation::onNext)
				.matchAny(any -> expectation.onError(new UnexpectedActorMessageException(any)))
				.build());
	}

	protected ExpectingActor(final SingleExpectation<T> expectation) {
		this.expectation = Optional.empty();
		receive(ReceiveBuilder
				.match(expectation.getMessageClass(), stopAfter(expectation::onSuccess))
				.matchAny(stopAfter(any -> expectation.onError(new UnexpectedActorMessageException(any))))
				.build());
	}

	protected <V> FI.UnitApply<V> stopAfter(final FI.UnitApply<V> consumer) {
		return message -> {
			consumer.apply(message);
			context().stop(self());
		};
	}

	@Override
	public void postStop() {
		if (expectation.isPresent()) {
			expectation.get().onCompleted();
		}
	}

}
