package com.zwb.geekology.parser.junit;

import java.util.List;

import junit.framework.TestCase;

import com.zwb.geekology.parser.GkParserFactory;
import com.zwb.geekology.parser.api.parser.IGkParser;

public class FactoryTest extends TestCase
{
	public void testFactory()
	{
		List<IGkParser> list = GkParserFactory.createRegisteredParsers();
		assertEquals(1, list.size());
		list.get(0).getSource().getId().equals("last.fm");
	}

}
