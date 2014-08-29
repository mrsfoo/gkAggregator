package com.zwb.geekology.parser.impl;

import java.util.List;

import com.zwb.geekology.parser.api.exception.GkParserException;
import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.api.parser.IGkParserQuery;
import com.zwb.geekology.parser.api.parser.IGkParsingResultArtist;
import com.zwb.geekology.parser.api.parser.IGkParsingResultSampler;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.internal.GkInternalParserFactory;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;

public class GkParserAggregator implements IGkParser
{
	private Ptr<List<IGkParser>> parsers = new Ptr<>();

	@Override
	public IGkParsingResultArtist parseArtist(IGkParserQuery query) throws GkParserException 
	{
		if(this.getParsers().size()!=1)
		{
			throw new RuntimeException("NOT IMPLEMENTED YET!");
		}
		IGkParser p = this.getParsers().get(0);
		return p.parseArtist(query);
	}

	@Override
	public IGkParsingResultSampler parseSampler(IGkParserQuery query) throws GkParserException 
	{
		throw new RuntimeException("NOT IMPLEMENTED YET!");
	}

	@Override
	public IGkParsingSource getSource() 
	{
		throw new RuntimeException("NOT IMPLEMENTED YET!");
	}

	private List<IGkParser> getParsers()
	{
		return LazyLoader.loadLazy(this.parsers, new ParserLoader());
	}
	
	class ParserLoader implements ILoader
	{
		@Override
		public Object load() 
		{
			return GkInternalParserFactory.createRegisteredParsers();
		}
	}
}
