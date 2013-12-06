public class CoreNoRest implements Core
{
	private boolean	failed	= false;
	private boolean	resting	= true;
	private double	lambda;
	private double	baseLambda;
	private double	elapsedT;
	private int		prevEpochs;

	public CoreNoRest(double lambda)
	{
		this.lambda = lambda;
		baseLambda = lambda;
	}

	public void update(double t)
	{
		if (failed)
			return;
		if (!resting) {
			elapsedT += t;
			int elapsedEpochs = (int) Math.floor(elapsedT * baseLambda);
			while (elapsedEpochs > prevEpochs) {
				prevEpochs++;
				lambda += baseLambda;
			}
		}
	}

	public void rest()
	{
		if (failed)
			return;
		resting = true;
	}

	public void activate()
	{
		if (failed)
			return;
		resting = false;
	}

	public boolean isActive()
	{
		if (failed)
			return false;
		return !resting;
	}

	public boolean hasNotFailed()
	{
		return !failed;
	}

	public boolean justFailed(double t)
	{
		if (failed)
			return failed;
		if (resting)
			return false;
		double probOfFail = 1 - Math.exp(-lambda * t);
		if (Math.random() < probOfFail)
			failed = true;
		return failed;
	}

	public void setLambda(double lam)
	{
		lambda = lam;
	}

	@Override
	public boolean shouldRest()
	{
		return false;
	}
}
