import java.util.concurrent.atomic.AtomicInteger;

public class MTTFCores
{
	public static double	LAMBDA	= 0.05;

	public MTTFCores(final int type)
	{
		int cores = Runtime.getRuntime().availableProcessors();
		final int simulCount = (int) Math.round(10000 / (double) cores);
		final double timeArr[][] = new double[cores][simulCount];
		final double timeSum[] = new double[cores];
		final int spares = 32;
		final int active = 1;
		final double resolution = 1;
		Thread workers[] = new Thread[cores];
		final AtomicInteger workerNum = new AtomicInteger(0);
		for (int j = 0; j < workers.length; j++) {
			workers[j] = new Thread(new Runnable() {
				@Override
				public void run()
				{
					int worker = workerNum.getAndIncrement();
					for (int i = 0; i < simulCount; i++) {
						double t = 0;
						Chip c = new MultiCore(active, spares, LAMBDA, type);
						while (!c.hasFailed(1.0 / resolution))
							t += 1.0 / resolution;
						timeArr[worker][i] = t;
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
		for (int i = 0; i < 3; i++)
			new MTTFCores(i + 1);
		System.out.println("Time taken: "
			+ ((System.currentTimeMillis() - start) / Math.pow(10.0, 3)) + "s");
	}
}
