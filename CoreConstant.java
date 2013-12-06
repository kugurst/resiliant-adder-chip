public class CoreConstant implements Core
{
	private boolean	failed	= false;
	private boolean	resting	= true;
	private double	lambda;

	public CoreConstant(double lambda)
	{
		this.lambda = lambda;
	}

	public void update(double t)
	{}

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
		return (failed = Math.random() < 1 - Math.exp(-lambda * t));
	}

	public void setLambda(double lam)
	{
		lambda = lam;
	}
}
