package com.vndrvn.rx.akka;

import java.util.Comparator;

public class HashCodeComparator implements Comparator<Object> {

	@Override
	public int compare(final Object o1, final Object o2) {
		return rank(o1) - rank(o2);
	}

	public static int rank(final Object object) {
		return object == null ? 0 : object.hashCode();
	}

}
