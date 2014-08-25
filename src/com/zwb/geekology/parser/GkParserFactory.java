package com.zwb.geekology.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.zwb.geekology.parser.api.parser.IGkParser;
import com.zwb.geekology.parser.api.parser.IGkParserQuery;
import com.zwb.geekology.parser.api.parser.IGkParsingEvent;
import com.zwb.geekology.parser.api.parser.IGkParsingResultArtist;
import com.zwb.geekology.parser.api.parser.IGkParsingResultSampler;
import com.zwb.geekology.parser.api.parser.IGkParsingSource;
import com.zwb.geekology.parser.enums.GkParsingEventType;
import com.zwb.geekology.parser.impl.GkParserQueryArtist;
import com.zwb.geekology.parser.impl.GkParserQuerySampler;
import com.zwb.geekology.parser.impl.GkParsingEvent;
import com.zwb.geekology.parser.impl.GkParsingResultArtist;
import com.zwb.geekology.parser.impl.GkParsingResultSampler;
import com.zwb.geekology.parser.internal.Config;
import com.zwb.geekology.parser.internal.MyLogger;

public class GkParserFactory 
{
	private static MyLogger log = new MyLogger(GkParserFactory.class);
	
	public static List<IGkParser> createRegisteredParsers()
	{
		List<IGkParser> list = new ArrayList<>();
		List<String> implementations = Config.getImplementations();
		for(String impl: implementations)
		{
			try
			{
				log.debug("creating GkParserInstance for package <"+impl+">");
				Reflections reflections = new Reflections(impl);
				Set<Class<? extends IGkParser>> allParsers = reflections.getSubTypesOf(IGkParser.class);
				if(allParsers.size()!=1)
				{
					log.debug("no GkParserImplementations found for package <"+impl+">");
					continue;
				}
				IGkParser parser = allParsers.iterator().next().newInstance();
				list.add(parser);
				log.debug("instantiated GkParserImplementation for package <"+impl+"> with source: "+parser.getSource().getId());
			}
			catch (InstantiationException | IllegalAccessException e) 
			{
				log.error("error instantiating GkParserImplementation for package <"+impl+">: ["+e.getClass()+"] "+e.getMessage());
				continue;
			}
//			catch (Throwable t) 
//			{
//				log.error("unknown error creating GkParserImplementation for package <"+impl+">: ["+t.getClass()+"] "+t.getMessage());
//				continue;
//			}
		}
		return list;	
	}
}
