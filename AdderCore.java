public class AdderCore
{
	public static final int	ACTIVE		= 1;
	public static final int	RESTING		= 2;

	private boolean			resting		= true;
	private boolean			failed		= false;
	private double			baseLambda;
	private double			currentLambda;
	private double			relaxLambda;
	private double			elapsedTimeInCurrentState;
	private double			k			= 2;

	private int				lastState	= RESTING;
	private int				nextState;

	public AdderCore(double baseLamdba)
	{
		this.baseLambda = baseLamdba;
		this.currentLambda = baseLamdba;
		this.relaxLambda = baseLambda;
		lastState = RESTING;
	}

	public void update(double t, int p)
	{
		if (failed)
			return;
		elapsedTimeInCurrentState += t;
		if (lastState == RESTING)
			currentLambda =
				Math.max(baseLambda, relaxLambda * Math.exp(-k * elapsedTimeInCurrentState));
		else if (lastState == ACTIVE)
			currentLambda = baseLambda + p * Math.sqrt(t);
		if (nextState != lastState) {
			elapsedTimeInCurrentState = 0;
			if (nextState == RESTING)
				relaxLambda = currentLambda;
			lastState = nextState;
		}
	}

	public void rest()
	{
		if (failed)
			return;
		nextState = RESTING;
		resting = true;
	}

	public void activate()
	{
		if (failed)
			return;
		nextState = ACTIVE;
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
			return true;
		if (resting)
			return false;
		double prob = 1.0 - Math.exp(-currentLambda * t);
		if (Math.random() < prob)
			failed = true;
		return failed;
	}

	public boolean shouldDeactivate()
	{
		if (currentLambda > 1.2 * baseLambda)
			return true;
		return false;
	}

}
