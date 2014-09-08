package com.zwb.geekology.parser.impl;

import java.util.ArrayList;
import java.util.List;

import com.zwb.geekology.parser.abstr.db.AbstrGkParser;
import com.zwb.geekology.parser.api.exception.GkParserException;
import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.api.parser.IGkParserQuery;
import com.zwb.geekology.parser.api.parser.IGkParsingResultArtist;
import com.zwb.geekology.parser.api.parser.IGkParsingResultSampler;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.internal.GkInternalParserFactory;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;

public class GkParserAggregator extends AbstrGkParser implements IGkParser
{
    private Ptr<List<IGkParser>> parsers = new Ptr<>();
    
    public GkParserAggregator()
    {
	super();
	this.setSource(Config.getSourceString());
    }
    
    @Override
    public IGkParsingResultArtist parseArtist(IGkParserQuery query) throws GkParserException
    {
	IGkParsingResultArtist result = (IGkParsingResultArtist) this.setResultStart(query, this.getSource());
	List<IGkParsingResultArtist> singleResults = new ArrayList<>();
	for (IGkParser p : this.getParsers())
	{
	    try
	    {
		singleResults.add(p.parseArtist(query));
	    }
	    catch (GkParserException e)
	    {
		if (e.getResult() != null)
		{
		    singleResults.add((IGkParsingResultArtist) e.getResult());
		}
	    }
	}
	return mergeResults(result, singleResults);
    }
    
    @Override
    public IGkParsingResultSampler parseSampler(IGkParserQuery query) throws GkParserException
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
    
    private IGkParsingResultArtist mergeResults(IGkParsingResultArtist originalResult, List<IGkParsingResultArtist> singleResults)
    {
	if (singleResults.size() == 1)
	{
	    return singleResults.get(0);
	}
	throw new RuntimeException("NOT IMPLEMENTED YET!");
    }
}
