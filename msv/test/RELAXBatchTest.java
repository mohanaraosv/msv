import junit.framework.*;

/**
 * tests the entire RELAX test suite by using BatchVerifyTester 
 */
public class RELAXBatchTest
{
	public static TestSuite suite()
	{
		return
			new BatchVerifyTester(
				"relax",
				System.getProperty("RELAXBatchTestDir"),
				".rlx" ).suite();
	}
}
