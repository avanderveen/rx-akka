package com.vndrvn.akka.resources;

import lombok.Getter;
import rx.Single;
import rx.SingleSubscriber;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Expectation<T> extends SingleSubscriber<T> implements Single.OnSubscribe<T> {

	protected final Set<SingleSubscriber<? super T>> subscribers;

	protected final Class<T> messageClass;

	public Expectation(final Class<T> messageClass) {
		this.subscribers = new HashSet<>();
		this.messageClass = messageClass;
	}

	@Override
	public void call(SingleSubscriber<? super T> subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void onSuccess(final T t) {
		subscribers.forEach(s -> s.onSuccess(t));
	}

	@Override
	public void onError(final Throwable e) {
		subscribers.forEach(s -> s.onError(e));
	}

}
