public class CoreWithRest implements Core
{
	public static final int	ACTIVE		= 1;
	public static final int	RESTING		= 2;

	private boolean			failed		= false;
	private boolean			resting		= true;
	private double			lambda;
	private double			baseLambda;
	private double			elapsedT;
	private int				prevEpochs;
	private double			epsilon		= 1E-5;
	private int				lastState	= RESTING;
	private int				nextState;

	public CoreWithRest(double lambda)
	{
		this.lambda = lambda;
		baseLambda = lambda;
	}

	public void update(double t)
	{
		if (failed)
			return;
		if (lastState != nextState) {
			elapsedT = 0;
			prevEpochs = 0;
			lastState = nextState;
		}
		elapsedT += t;
		if (resting) {
			int elapsedEpochs = (int) Math.floor(elapsedT * baseLambda);
			while (elapsedEpochs > prevEpochs) {
				prevEpochs++;
				if (Math.abs(lambda - baseLambda) > epsilon)
					lambda -= baseLambda;
				// reset
				else
					lambda = baseLambda;
			}
		} else {
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
		nextState = RESTING;
	}

	public void activate()
	{
		if (failed)
			return;
		resting = false;
		nextState = ACTIVE;
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
		if (lambda >= 2 * baseLambda) { return true; }
		return false;
	}
}
