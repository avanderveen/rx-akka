package com.vndrvn.rx.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import rx.Observable;

public class ActorObservable<T> extends Observable<T> {

	protected final ActorRef actorRef;

	protected ActorObservable(final ActorSystem system, final ObservableExpectation<T> expectation) {
		super(expectation);
		this.actorRef = system.actorOf(ExpectingActor.props(expectation));
	}

	public static <U> ActorObservable<U> create(final ActorSystem system, final Class<U> messageClass) {
		return new ActorObservable<>(system, new ObservableExpectation<>(messageClass));
	}

}
