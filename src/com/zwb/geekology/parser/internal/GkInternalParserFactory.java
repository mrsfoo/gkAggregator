package com.zwb.geekology.parser.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.zwb.geekology.parser.api.parser.IGkParser;

public class GkInternalParserFactory
{
    private static Logger log = LogManager.getLogger((GkInternalParserFactory.class.getName()));
    
    public static List<IGkParser> createRegisteredParsers(List<String> implementations)
    {
	List<IGkParser> list = new ArrayList<>();
	for (String impl : implementations)
	{
	    try
	    {
		log.debug("creating GkParserInstance for package <" + impl + ">");
		Reflections reflections = new Reflections(impl);
		Set<Class<? extends IGkParser>> allParsers = reflections.getSubTypesOf(IGkParser.class);
		if (allParsers.size() != 1)
		{
		    log.debug("no GkParserImplementations found for package <" + impl + ">");
		    continue;
		}
		IGkParser parser = allParsers.iterator().next().newInstance();
		list.add(parser);
		log.debug("instantiated GkParserImplementation for package <" + impl + "> with source: " + parser.getSource().getId());
	    }
	    catch (InstantiationException | IllegalAccessException e)
	    {
		log.error("error instantiating GkParserImplementation for package <" + impl + ">: [" + e.getClass() + "] " + e.getMessage());
		continue;
	    }
	}
	return list;
    }
}
