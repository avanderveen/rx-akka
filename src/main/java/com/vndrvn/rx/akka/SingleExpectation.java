package com.vndrvn.rx.akka;

import lombok.Getter;
import rx.Single;
import rx.SingleSubscriber;

import java.util.HashSet;
import java.util.Set;

@Getter
public class SingleExpectation<T> extends SingleSubscriber<T> implements Single.OnSubscribe<T> {

	protected final Set<SingleSubscriber<? super T>> subscribers;
	protected final Class<T> messageClass;

	public SingleExpectation(final Class<T> messageClass) {
		this.subscribers = new HashSet<>();
		this.messageClass = messageClass;
	}

	@Override
	public void call(SingleSubscriber<? super T> subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void onSuccess(final T message) {
		subscribers.forEach(s -> s.onSuccess(message));
	}

	@Override
	public void onError(final Throwable throwable) {
		subscribers.forEach(s -> s.onError(throwable));
	}

}
