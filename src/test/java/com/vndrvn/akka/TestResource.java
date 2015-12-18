package com.vndrvn.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.vndrvn.akka.resources.ActorSingle;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Slf4j
public class TestResource extends Resource {

	private final ActorRef dispatchSystem;

	@Inject
	public TestResource(final ActorSystem system, final ActorRef dispatchSystem) {
		super(system);
		this.dispatchSystem = dispatchSystem;
	}

	@MessageIn(Introduction.class)
	public ActorSingle<Greeting> hello(final Introduction intro) {
		final ActorSingle<Greeting> contract = expect(Greeting.class);

		this.dispatchSystem.tell(intro, contract.getActorRef());

		return contract;
	}

	@Value
	public static class Greeting {
		protected final String message;
	}

	@Value
	public static class Introduction {
		protected final String name;
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface MessageIn {
		Class value();
	}

}
