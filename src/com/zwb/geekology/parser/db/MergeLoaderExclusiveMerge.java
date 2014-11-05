package com.zwb.geekology.parser.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zwb.geekology.parser.api.db.IGkDbItem;
import com.zwb.lazyload.ILoader;

public class MergeLoaderExclusiveMerge<T> implements ILoader<T>
{
    private Logger log = LogManager.getLogger((this.getClass().getName()));
    private List<? extends IGkDbItem> input;
    private Method method;
    private List<String> prios;
    private Class<T> returnType;
    
    public MergeLoaderExclusiveMerge(List<? extends IGkDbItem> input, Method method, Class<T> returnType, List<String> prios)
    {
	this.input = input;
	this.method = method;
	this.prios = prios;
	this.returnType = returnType;
    }
    
    public T load()
    {
	if ((this.method.getParameterCount() != 0) || !this.method.getReturnType().equals(returnType))
	{
	    log.error("error! passed call method <" + this.method.toGenericString() + "> for exclusive merger must have signature \"" + returnType.getName() + " method()\"");
	    return null;
	}
	/** map nach sourcen gehashed aufbauen */
	Map<String, IGkDbItem> map = new HashMap<String, IGkDbItem>();
	for (IGkDbItem i : input)
	{
	    map.put(i.getSource().getId(), i);
	}
	/** map nach prios sortiert durchgehen */
	for (String source : this.prios)
	{
	    if (map.containsKey(source))
	    {
		IGkDbItem i = map.get(source);
		try
		{
		    Object o = this.method.invoke(i);
		    if (o != null)
		    {
			return returnType.cast(o);
		    }
		    map.remove(source);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
		    log.error(e.getClass().getName() + " while calling <" + this.method.toGenericString() + ">: " + e.getMessage());
		}
	    }
	}
	/** rest der map unsortiert durchgehen */
	for (IGkDbItem i : map.values())
	{
	    try
	    {
		Object o = this.method.invoke(i);
		if (o != null)
		{
		    return returnType.cast(o);
		}
	    }
	    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
	    {
		log.error(e.getClass().getName() + " while calling <" + this.method.toGenericString() + ">: " + e.getMessage());
	    }
	}
	
	return null;
    }
}
