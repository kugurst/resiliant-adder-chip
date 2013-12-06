public class CPU implements Chip
{
	boolean			cores[];
	private boolean	failed;
	private double	lambda;

	public CPU(int spares, double lambda)
	{
		cores = new boolean[spares];
		this.lambda = lambda;
	}

	public boolean hasFailed(double t)
	{
		if (failed)
			return failed;
		boolean ithFailure = true;
		for (int i = 1; i < cores.length; i++) {
			if (cores[i])
				continue;
			ithFailure = false;
			double probOfFail = 1 - Math.exp(-lambda * t);
			if (Math.random() < probOfFail)
				cores[i] = true;
		}
		if (ithFailure) {
			double probOfFail = 1 - Math.exp(-lambda * t);
			if (Math.random() < probOfFail)
				failed = true;
		}
		return failed;
	}

	@Override
	public void setLambda(double lam)
	{
		lambda = lam;
	}
}
