package com.zwb.geekology.parser.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zwb.geekology.parser.api.db.IGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbItemWithDesc;
import com.zwb.lazyload.ILoader;

public class MergeLoaderTextblocks implements ILoader<String>
{
    private Logger log = LogManager.getLogger((this.getClass().getName()));
    private List<? extends IGkDbItemWithDesc> input;
    private Method method;
    
    public MergeLoaderTextblocks(List<? extends IGkDbItemWithDesc> input, Method method)
    {
	this.input = input;
	this.method = method;
    }
    
    public String load()
    {
	if ((this.method.getParameterCount() != 0) || !this.method.getReturnType().equals(String.class))
	{
	    log.error("error! passed call method <" + this.method.toGenericString() + "> for text merger must have signature \"String method()\"");
	    return "";
	}
	String output = "";
	for (IGkDbItemWithDesc i : this.input)
	{
	    try
	    {
		String s = (String) this.method.invoke(i);
		if ((s != null) && !s.isEmpty())
		{
		    output += "[[[" + i.getSource().getId() + "]]] " + s + "\n";
		}
	    }
	    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
	    {
		log.error(e.getClass().getName() + " while calling <" + this.method.toGenericString() + ">: " + e.getMessage());
	    }
	}
	return output;
    }
}
