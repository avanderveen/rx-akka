package com.vndrvn.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vndrvn.akka.example.MessageIn;
import com.vndrvn.akka.example.MessageOne;
import com.vndrvn.akka.example.MessageTwo;
import com.vndrvn.akka.example.Request;
import com.vndrvn.akka.example.Response;
import com.vndrvn.akka.resources.ActorSingle;
import lombok.extern.slf4j.Slf4j;
import rx.Single;

@Slf4j
@SuppressWarnings("BindingAnnotationWithoutInject")
public class TestResources {

	public static class NoDependencies {

		@MessageIn(Request.class)
		public ActorSingle<Response> actionWithContract(
				final Request request,
				final ActorSingle<Response> contract,
				@Named("foo") final ActorRef foo
		) {
			contract.subscribe(response -> log.info("Message from {}: {}", request.getName(), response.getMessage()));
			foo.tell(request, contract.getActorRef());
			return contract;
		}

		@MessageIn(Request.class)
		public Single<Response> actionWithMappedContract(
				final Request request,
				final ActorSingle<MessageOne> messageOne,
				@Named("foo") final ActorRef foo
		) {
			foo.tell(request.partOne(), messageOne.getActorRef());
			return messageOne.map(one -> new Response("Part 1: " + one.getPart()));
		}

		@MessageIn(Request.class)
		public Single<Response> actionWithComposedContract(
				final Request request,
				final ActorSingle<MessageOne> messageOne,
				final ActorSingle<MessageTwo> messageTwo,
				@Named("foo") final ActorRef foo
		) {
			foo.tell(request.partOne(), messageOne.getActorRef());
			foo.tell(request.partTwo(), messageTwo.getActorRef());
			return Single.zip(messageOne, messageTwo, (one, two) -> new Response(one.getPart() + two.getPart()));
		}

		@MessageIn(Request.class)
		public void actionWithSenderRef(
				final Request request,
				final ActorRef contractRef,
				@Named("foo") final ActorRef foo
		) {
			foo.tell(request, contractRef);
		}

		@MessageIn(Request.class)
		public void actionWithNoMessages(final Request request) {
			log.info("Hello {}", request.getName());
		}

	}

	public static class WithActorSystem {

		private final ActorSystem system;

		@Inject
		public WithActorSystem(final ActorSystem system) {
			this.system = system;
		}

		@MessageIn(Request.class)
		public ActorSingle<Response> action(
				final Request request,
				@Named("foo") final ActorRef foo) {
			final ActorSingle<Response> contract = ActorSingle.create(system, Response.class);
			foo.tell(request, contract.getActorRef());
			return contract;
		}

	}

	public static class WithResource extends Resource {

		@Inject
		public WithActorSystem(final ActorSystem system) {
			super(system);
		}

		@MessageIn(Request.class)
		public ActorSingle<Response> action(
				final Request request,
				@Named("foo") final ActorRef foo) {
			final ActorSingle<Response> contract = expect(Response.class);
			foo.tell(request, contract.getActorRef());
			return contract;
		}

	}

}
