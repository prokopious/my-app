package util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MethodPoller<T> {

	private int pollDurationSec;
	private int pollIntervalMillis;
	private Supplier<T> pollMethod = null;
	private Predicate<T> pollResultPredicate = null;

	public MethodPoller() {
	}

	public MethodPoller<T> poll(int pollDurationSec, int pollIntervalMillis) {
		this.pollDurationSec = pollDurationSec;
		this.pollIntervalMillis = pollIntervalMillis;
		return this;
	}
	
	public MethodPoller<T> method(Supplier<T> supplier) {
		pollMethod = supplier;
		return this;
	}

	public MethodPoller<T> untilCondition(Predicate<T> predicate) {
		pollResultPredicate = predicate;
		return this;
	}

	public T execute() throws Exception {

		T result = null;
		boolean pollSucceeded = false;
		long startTime = System.currentTimeMillis();

		while (!pollSucceeded) {
	
			long currentTime = System.currentTimeMillis();
			long endTime = startTime + (pollDurationSec * 1000);
			if (currentTime > endTime) {
				throw new Exception("Polling timeout.");
			}			
			System.out.println("polling..");
			Thread.sleep(pollIntervalMillis);
			result = pollMethod.get();
			pollSucceeded = pollResultPredicate.test(result);		
		}
		return result;
	}

}