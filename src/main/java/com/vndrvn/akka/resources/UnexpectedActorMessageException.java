package com.vndrvn.akka.resources;

import lombok.Getter;

@Getter
public class UnexpectedActorMessageException extends RuntimeException {

	protected final Object actorMessage;

	public UnexpectedActorMessageException(final Object actorMessage) {
		super("Unexpected message: " + actorMessage.getClass() + "\r\n\t" + actorMessage.toString());
		this.actorMessage = actorMessage;
	}

}
