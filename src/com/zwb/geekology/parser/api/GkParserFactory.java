package com.zwb.geekology.parser.api;

import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.impl.GkParserAggregator;

public class GkParserFactory 
{
	public static IGkParser createParserAggregator()
	{
		return new GkParserAggregator();
	}
}
