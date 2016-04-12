package com.vndrvn.rx.akka;

import lombok.Getter;
import rx.Observable;
import rx.Subscriber;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
public class ObservableExpectation<T> extends Subscriber<T> implements Observable.OnSubscribe<T> {

	protected final Set<Subscriber<? super T>> subscribers;
	protected final Class<T> messageClass;

	public ObservableExpectation(final Class<T> messageClass) {
		this.subscribers = new ConcurrentSkipListSet<>();
		this.messageClass = messageClass;
	}

	@Override
	public void call(Subscriber<? super T> subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void onNext(final T next) {
		subscribers.forEach(subscriber -> subscriber.onNext(next));
	}

	@Override
	public void onError(final Throwable throwable) {
		subscribers.forEach(subscriber -> subscriber.onError(throwable));
	}

	@Override
	public void onCompleted() {
		subscribers.forEach(Subscriber::onCompleted);
	}

}
