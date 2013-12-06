public class Adder
{
	private AdderCore[]	cores;
	private double		timeStep;
	private boolean		failed;
	private int			minimumCores;

	/** Constructs a new adder with the given parameters. Q should be the same unit as t. That is, if
	 * Q = t = 1, and Q is of unit seconds, then t is of unit seconds
	 * @param active
	 * @param spares
	 * @param baseLambda
	 * @param p
	 * @param Q */
	public Adder(int active, int spares, double baseLambda, double Q)
	{
		cores = new AdderCore[active + spares];
		for (int i = 0; i < cores.length; i++)
			cores[i] = new AdderCore(baseLambda);
		this.timeStep = pickStep(Q, baseLambda);
		minimumCores = active;
	}

	private double pickStep(double q, double baseLambda)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasFailed()
	{
		if (failed)
			return failed;
		int P = 1;
		int remaining = 0;
		int activateCount = 0;
		boolean wasActivated[] = new boolean[cores.length];
		for (int i = 0; i < cores.length; i++) {
			if (!cores[i].justFailed(timeStep, P)) {
				cores[i].update(timeStep);
				remaining++;
				// We should activate if we need to
				if (activateCount > 0 && !cores[i].isActive()) {
					cores[i].activate();
					activateCount--;
				}
				// Deactivate this core and attempt to activate the next one
				else if (cores[i].isActive() && cores[i].shouldDeactivate()) {
					wasActivated[i] = true;
					cores[i].rest();
					activateCount++;
				}
			} else {
				// Attempt to activate the next one, as this one failed
				activateCount++;
				continue;
			}
		}
		// Do one more pass to activate until we have an active number of cores
		for (int i = cores.length - 1; i >= 0 && activateCount > 0; i--) {
			if (cores[i].hasNotFailed() && !cores[i].isActive() && !wasActivated[i]) {
				cores[i].activate();
				activateCount--;
			}
		}
		// Do one last pass to activate until we have an active number of cores
		for (int i = 0; i < cores.length && activateCount > 0; i++) {
			if (cores[i].hasNotFailed() && !cores[i].isActive()) {
				cores[i].activate();
				activateCount--;
			}
		}
		if (remaining < minimumCores)
			failed = true;
		return failed;
	}

}
