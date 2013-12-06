public interface Core
{
	public void update(double t);

	public void rest();

	public void activate();

	public boolean isActive();

	public void setLambda(double lam);

	public boolean hasNotFailed();

	public boolean justFailed(double t);

	public boolean shouldRest();
}
