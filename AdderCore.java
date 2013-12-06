public class AdderCore
{
	public static final int	ACTIVE	= 1;
	public static final int	RESTING	= 2;

	private boolean			resting	= true;
	private boolean			failed	= false;
	private double			baseLambda;
	private double			currentLambda;

	private int				lastState;

	public AdderCore(double baseLamdba)
	{
		this.baseLambda = baseLamdba;
		this.currentLambda = baseLamdba;
		lastState = RESTING;
	}

	public void update(double t)
	{
		// TODO Auto-generated method stub

	}

	public void rest()
	{
		// TODO Auto-generated method stub

	}

	public void activate()
	{
		// TODO Auto-generated method stub

	}

	public boolean isActive()
	{
		if (failed)
			return false;
		return !resting;
	}

	public boolean hasNotFailed()
	{
		return failed;
	}

	public boolean justFailed(double t, int p)
	{
		if (failed)
			return true;
		return false;
	}

	public boolean shouldDeactivate()
	{
		if (currentLambda > 3.0 * baseLambda)
			return true;
		return false;
	}

}
