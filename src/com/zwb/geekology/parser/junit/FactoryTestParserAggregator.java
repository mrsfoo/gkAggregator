package com.zwb.geekology.parser.junit;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.internal.Config;
import com.zwb.geekology.parser.internal.GkInternalParserFactory;

public class FactoryTestParserAggregator extends TestCase
{
    public void testFactory()
    {
	List<IGkParser> list = GkInternalParserFactory.createRegisteredParsers(Config.getImplementations());
	List<String> expectedSources = Arrays.asList("last.fm", "discogs.com");
	assertEquals(expectedSources.size(), list.size());
	for (IGkParser p : list)
	{
	    assertTrue(expectedSources.contains(p.getSource().getId()));
	}
    }
}
