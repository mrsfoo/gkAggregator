package com.zwb.geekology.parser.api;

import java.util.Arrays;
import java.util.List;

import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.impl.GkParserAggregator;

public class GkParserFactory
{
    public static IGkParser createParserAggregator()
    {
	return new GkParserAggregator();
    }
    
    public static IGkParser createParserAggregator(List<String> packages)
    {
	return new GkParserAggregator(packages);
    }
    
    public static IGkParser createParserAggregator(String... packages)
    {
	return new GkParserAggregator(Arrays.asList(packages));
    }
}
