package com.vndrvn.akka.resources;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import lombok.Getter;
import rx.Single;

@Getter
public class ActorSingle<T> extends Single<T> {

	protected final ActorRef actorRef;

	protected ActorSingle(final ActorSystem system, final Expectation<T> expectation) {
		super(expectation);
		this.actorRef = system.actorOf(ExpectingActor.props(expectation));
	}

	public static <U> ActorSingle<U> create(final ActorSystem system, final Class<U> messageClass) {
		return new ActorSingle<>(system, new Expectation<>(messageClass));
	}

}
