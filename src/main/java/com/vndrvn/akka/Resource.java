package com.vndrvn.akka;

import akka.actor.ActorSystem;
import com.vndrvn.akka.resources.ActorSingle;

public class Resource {

	private final ActorSystem system;

	protected Resource(final ActorSystem system) {
		this.system = system;
	}

	protected final <T> ActorSingle<T> expect(final Class<T> messageClass) {
		return ActorSingle.create(system, messageClass);
	}

}
