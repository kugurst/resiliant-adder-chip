public class MTTFAdder
{

	public MTTFAdder()
	{

	}

	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		new MTTFAdder();
		System.out.println("Time taken: "
			+ ((System.currentTimeMillis() - start) / Math.pow(10.0, 3)) + "s");
	}
}
