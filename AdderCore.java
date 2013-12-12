public class AdderCore
{
	public static final int	ACTIVE						= 1;
	public static final int	RESTING						= 2;

	private boolean			resting						= true;
	private boolean			failed						= false;
	private double			baseLambda;
	private double			currentLambda;
	private double			relaxLambda;
	private double			elapsedTimeInCurrentState	= 0;
	private double			k							= 1024;

	private int				lastState					= RESTING;
	private int				nextState;

	public AdderCore(double baseLambda)
	{
		this.baseLambda = baseLambda;
		this.currentLambda = baseLambda;
		this.relaxLambda = baseLambda;
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
			currentLambda = baseLambda + p * Math.sqrt(elapsedTimeInCurrentState);
		// System.out.println(currentLambda);
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
		if (currentLambda > 1.5 * baseLambda)
			return true;
		return false;
	}

}
