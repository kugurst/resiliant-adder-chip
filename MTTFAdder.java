import java.util.concurrent.atomic.AtomicInteger;

public class MTTFAdder
{
	public static StringBuilder	stats	= new StringBuilder();

	public MTTFAdder(final int P, final int spares, final int active)
	{
		int cores = Runtime.getRuntime().availableProcessors();
		final int simulCount = (int) Math.round(80 / (double) cores);
		final double timeArr[][] = new double[cores][simulCount];
		final double timeSum[] = new double[cores];
		final double lambda = 0.05; // 1 / lambda = 20 days
		final int Q = 10; // ns
		if (P == 1)
			stats.append(lambda + "\t" + spares + "\t" + active + "\t");
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
						if (i == 0 && P == 1 && worker == 0)
							stats.append(a.timeStep + "\t" + AdderCore.k);
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
		stats.append("\t" + (totalSum / (cores * simulCount)));
	}

	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		AdderCore.k = args.length > 2 ? Integer.parseInt(args[2]) : AdderCore.k;
		for (int i = 1; i < 4; i++)
			new MTTFAdder(i, args.length > 0 ? Integer.parseInt(args[0]) : 0, args.length > 1
				? Integer.parseInt(args[1]) : 1);
		System.out.println(stats);
		System.out.println("Time taken: "
			+ ((System.currentTimeMillis() - start) / Math.pow(10.0, 3)) + "s");
	}
}
