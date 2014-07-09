package utils;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class TestUtils extends TestCase{

	
	@Test
	public void testSplit(){
		String str = Utils.loadPath();
		boolean flag = (str != null);
		Assert.assertEquals(true, flag);
	}
	
}
