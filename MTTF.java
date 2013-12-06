import java.util.concurrent.atomic.AtomicInteger;

public class MTTF
{
	public static double	LAMBDA	= 0.05;

	public MTTF()
	{
		int cores = Runtime.getRuntime().availableProcessors();
		final int simulCount = 100;
		final int timeArr[][] = new int[cores][simulCount];
		final long timeSum[] = new long[cores];
		final int spares = 1024;
		Thread workers[] = new Thread[cores];
		final AtomicInteger workerNum = new AtomicInteger(0);
		for (int j = 0; j < workers.length; j++) {
			workers[j] = new Thread(new Runnable() {
				@Override
				public void run()
				{
					int worker = workerNum.getAndIncrement();
					for (int i = 0; i < simulCount; i++) {
						int t = 0;
						Chip c = new CPU(spares, LAMBDA);
						while (!c.hasFailed(1))
							t++;
						timeArr[worker][i] = t;
					}
					for (int time : timeArr[worker])
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
		long totalSum = 0;
		for (long timeTotal : timeSum)
			totalSum += timeTotal;
		System.out.println("Average time: " + ((double) totalSum / (cores * simulCount)));
	}

	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		new MTTF();
		System.out.println("Time taken: "
			+ ((System.currentTimeMillis() - start) / Math.pow(10.0, 3)) + "s");
	}
}
