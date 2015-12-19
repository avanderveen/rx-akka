package com.vndrvn.akka.example;

import lombok.Value;

@Value
public class Request {

	protected final String name;

	public PartOne partOne() {
		return new PartOne(name);
	}

	public PartTwo partTwo() {
		return new PartTwo(name);
	}

	@Value
	public static class PartOne {
		protected final String name;
	}

	@Value
	public static class PartTwo {
		protected final String name;
	}

}
