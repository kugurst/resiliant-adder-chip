import java.util.concurrent.atomic.AtomicInteger;

public class MTTFAdder
{

	public MTTFAdder(final int P)
	{
		int cores = Runtime.getRuntime().availableProcessors();
		final int simulCount = (int) Math.round(1000 / (double) cores);
		final double timeArr[][] = new double[cores][simulCount];
		final double timeSum[] = new double[cores];
		final int spares = 16;
		final int active = 1;
		final double lambda = 0.005; // 1 / lambda = 20 days
		final int Q = 10; // ns
		Thread workers[] = new Thread[cores];
		final AtomicInteger workerNum = new AtomicInteger(0);
		for (int j = 0; j < workers.length; j++) {
			workers[j] = new Thread(new Runnable() {
				@Override
				public void run()
				{
					int worker = workerNum.getAndIncrement();
					for (int i = 0; i < simulCount; i++) {
						Adder a =
							new Adder(active, spares, lambda, 86400.0, Q, Math.pow(10.0, -9.0));
						while (!a.hasFailed(P));
						timeArr[worker][i] = a.getTimeOfDeath();
					}
					for (double time : timeArr[worker])
						timeSum[worker] += time;
				}
			});
			workers[j].start();
		}
		for (Thread worker : workers)
			try {
				worker.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		double totalSum = 0;
		for (double timeTotal : timeSum)
			totalSum += timeTotal;
		System.out.println("Average time: " + (totalSum / (cores * simulCount)));
	}

	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		for (int i = 1; i < 4; i++)
			new MTTFAdder(i);
		System.out.println("Time taken: "
			+ ((System.currentTimeMillis() - start) / Math.pow(10.0, 3)) + "s");
	}
}
