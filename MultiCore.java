public class MultiCore implements Chip
{
	private Core[]		cores;
	private boolean		failed	= false;
	private final int	necessary;

	public MultiCore(int active, int spares, double lambda, int type)
	{
		cores = new Core[spares + active];
		necessary = active;
		int activated = 0;
		for (int i = 0; i < cores.length; i++) {
			switch (type) {
			case 1:
				cores[i] = new CoreConstant(lambda);
				break;
			case 2:
				cores[i] = new CoreNoRest(lambda);
				break;
			case 3:
				cores[i] = new CoreWithRest(lambda);
				break;
			}
			cores[i].rest();
			if (activated < active) {
				cores[i].activate();
				activated++;
			}
		}
	}

	@Override
	public boolean hasFailed(double t)
	{
		if (failed)
			return failed;
		int remaining = 0;
		int activateCount = 0;
		boolean wasActivated[] = new boolean[cores.length];
		for (int i = 0; i < cores.length; i++) {
			cores[i].update(t);
			if (cores[i].hasNotFailed()) {
				if (!cores[i].justFailed(t)) {
					remaining++;
					// We should activate if we need to
					if (activateCount > 0 && !cores[i].isActive()) {
						cores[i].activate();
						activateCount--;
					}
					// Deactivate this core and attempt to activate the next one
					else if (cores[i].isActive() && cores[i].shouldRest()) {
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
		if (remaining < necessary)
			failed = true;
		return failed;
	}

	@Override
	public void setLambda(double lam)
	{
		for (Core c : cores)
			c.setLambda(lam);
	}

}
