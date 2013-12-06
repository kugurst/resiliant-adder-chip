import java.util.concurrent.atomic.AtomicInteger;

public class MTTFAdder
{

	public MTTFAdder()
	{
		int cores = Runtime.getRuntime().availableProcessors();
		final int simulCount = (int) Math.round(10000 / (double) cores);
		final double timeArr[][] = new double[cores][simulCount];
		final double timeSum[] = new double[cores];
		final int spares = 31;
		final int active = 1;
		final double lambda = 0.05; // 1 / lambda = 20 days
		final double Q = 1; // ns
		Thread workers[] = new Thread[cores];
		final AtomicInteger workerNum = new AtomicInteger(0);
		for (int j = 0; j < workers.length; j++) {
			workers[j] = new Thread(new Runnable() {
				@Override
				public void run()
				{
					int worker = workerNum.getAndIncrement();
					for (int i = 0; i < simulCount; i++) {
						Adder a = new Adder(active, spares, lambda, Q);
						while (!a.hasFailed());
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
		new MTTFAdder();
		System.out.println("Time taken: "
			+ ((System.currentTimeMillis() - start) / Math.pow(10.0, 3)) + "s");
	}
}
