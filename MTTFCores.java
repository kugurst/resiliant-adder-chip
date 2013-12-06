import java.util.concurrent.atomic.AtomicInteger;

public class MTTFCores
{
	public static double	LAMBDA	= 0.05;
	public double			average;

	public MTTFCores(final int type)
	{
		int cores = Runtime.getRuntime().availableProcessors();
		final int simulCount = (int) Math.round(1025 / (double) cores);
		final double timeArr[][] = new double[cores][simulCount];
		final double timeSum[] = new double[cores];
		final int spares = 512;
		final int active = 16;
		final double resolution = 2;
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
		average = (totalSum / (cores * simulCount));
		System.out.println("Average time: " + average);
	}

	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		double constant = -1;
		double floor = -1;
		double rest = -1;
		for (int i = 0; i < 3; i++) {
			MTTFCores cores = new MTTFCores(i + 1);
			if (constant == -1)
				constant = cores.average;
			else if (floor == -1)
				floor = cores.average;
			else if (rest == -1)
				rest = cores.average;
		}
		System.out.println(constant / floor);
		System.out.println(constant / rest);
		System.out.println(rest / floor);
		System.out.println("Time taken: "
			+ ((System.currentTimeMillis() - start) / Math.pow(10.0, 3)) + "s");
	}
}
