package com.ait.lienzo.client.core.types;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.ait.lienzo.test.LienzoMockitoTestRunner;

@RunWith(LienzoMockitoTestRunner.class)
public class BoundingBoxTest
{
	@Test
	public void testInit()
	{
		BoundingBox box = new BoundingBox(-1000, -200, -100, 300);
		
		assertEquals(box.getX(), -1000d, Double.MIN_VALUE);
		assertEquals(box.getY(), -200d, Double.MIN_VALUE);
		assertEquals(box.getWidth(), 900d, Double.MIN_VALUE);
		assertEquals(box.getHeight(), 500d, Double.MIN_VALUE);
	}
}
