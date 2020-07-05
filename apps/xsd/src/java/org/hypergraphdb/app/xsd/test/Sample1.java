package org.hypergraphdb.app.xsd.test;

import java.math.BigDecimal;

public class Sample1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BigDecimal bg = new BigDecimal("111");
		Sample1 s1=new Sample1();
		s1.evaluate(bg,11);
	}
	
	public boolean evaluate(BigDecimal bd, int totalDigits)
	{
		String s = bd.toPlainString();
		int length = s.length();
		if(-1!=s.indexOf('.'))
		{
			--length;
		}
		if(length>=totalDigits)
		{
			return false;
		}
		return true;
	}

	public boolean evaluate2(BigDecimal bd, int fractionDigits)
	{
		String s = bd.toPlainString();
		int i = s.lastIndexOf('.');
		if(-1!=i)
		{
			i = s.length()-i-1;
		}
		if(i>fractionDigits)
		{
			return false;
		}
		return true;
	}
}
