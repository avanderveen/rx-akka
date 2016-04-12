package com.vndrvn.rx.akka;

import lombok.Getter;
import rx.Single;
import rx.SingleSubscriber;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
public class SingleExpectation<T> extends SingleSubscriber<T> implements Single.OnSubscribe<T> {

	protected static final HashCodeComparator COMPARATOR = new HashCodeComparator();

	protected final Set<SingleSubscriber<? super T>> subscribers;
	protected final Class<T> messageClass;

	public SingleExpectation(final Class<T> messageClass) {
		this.subscribers = new ConcurrentSkipListSet<>(COMPARATOR);
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
