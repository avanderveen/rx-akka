package com.vndrvn.akka.resources;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.FI;
import akka.japi.pf.ReceiveBuilder;

public class ExpectingActor<T> extends AbstractActor {

	public static <U> Props props(final Expectation<U> expectation) {
		return Props.create(ExpectingActor.class, expectation);
	}

	protected ExpectingActor(final Expectation<T> expectation) {
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

}
