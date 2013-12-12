public class Adder
{
	private AdderCore[]	cores;
	double				timeStep;
	private boolean		failed;
	private int			minimumCores;
	private double		elapsedTime;

	/** Constructs a new adder with the given parameters. Q should be the same unit as t. That is, if
	 * Q = t = 1, and Q is of unit seconds, then t is of unit seconds
	 * @param active
	 * @param spares
	 * @param baseLambda
	 * @param p
	 * @param Q
	 * @param d
	 * @param e */
	public Adder(int active, int spares, double baseLambda, double lambdaSeconds, int Q,
		double Qseconds)
	{
		cores = new AdderCore[active + spares];
		int activated = 0;
		for (int i = 0; i < cores.length; i++) {
			cores[i] = new AdderCore(baseLambda / lambdaSeconds);
			if (activated++ < active) {
				cores[i].activate();
				cores[i].update(0, 1);
			}
		}
		timeStep = pickStep(Q, Qseconds, 1.0 / baseLambda, lambdaSeconds);
		minimumCores = active;
	}

	private double pickStep(int clockPeriod, double secondsPerPeriod, double meanLambda,
		double secondsPerLambda)
	{
		double mean = 0.01;// meanLambda * Math.pow(10.0, -1) / 2.0;
		// System.out.println(mean);
		return mean;
	}

	public boolean hasFailed(int P)
	{
		if (failed)
			return failed;
		int remaining = 0;
		int activateCount = 0;
		boolean wasActivate[] = new boolean[cores.length];
		for (int i = 0; i < cores.length; i++) {
			if (cores[i].hasNotFailed()) {
				if (!cores[i].justFailed(timeStep)) {
					cores[i].update(timeStep, P);
					remaining++;
					// We should activate if we need to
					if (activateCount > 0 && !cores[i].isActive()) {
						cores[i].activate();
						activateCount--;
					}
					// Deactivate this core and attempt to activate the next one
					else if (cores[i].isActive() && cores[i].shouldDeactivate()) {
						wasActivate[i] = true;
						cores[i].rest();
						activateCount++;
					}
				} else {
					// Attempt to activate the next one, as this one failed
					activateCount++;
					continue;
				}
			}
		}
		// Do one more pass to activate until we have an active number of cores
		for (int i = cores.length - 1; i >= 0 && activateCount > 0; i--) {
			if (cores[i].hasNotFailed() && !cores[i].isActive() && !wasActivate[i]) {
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
		elapsedTime += timeStep;
		return failed;
	}

	public double getTimeOfDeath()
	{
		return elapsedTime;
	}

}
