package com.zwb.geekology.parser.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zwb.geekology.parser.api.db.IGkDbItem;
import com.zwb.lazyload.ILoader;

public class MergeLoaderListFullMerge<T> implements ILoader
{
    private Logger log = LogManager.getLogger((this.getClass().getName()));
    private List<? extends IGkDbItem> input;
    private Method method;
    private Class<T> returnType;
    
    private Method postProcessingMethod;
    private Object[] postProcessingParams;
    
    public MergeLoaderListFullMerge(List<? extends IGkDbItem> input, Method method, Class<T> returnType)
    {
	this.input = input;
	this.method = method;
	this.returnType = returnType;
    }
    
    public void setPostProcessing(Method postProcessingMethod, Object... postProcessingParams)
    {
	this.postProcessingMethod = postProcessingMethod;
	this.postProcessingParams = postProcessingParams;
    }
    
    private List<T> doLoading()
    {
	Map<String, List<T>> map = new HashMap<>();
	for (IGkDbItem i : this.input)
	{
	    try
	    {
		String name = i.getName();
		if (!map.containsKey(name))
		{
		    map.put(name, new ArrayList<T>());
		}
		Object o = this.method.invoke(i);
		map.get(name).add((T) o);
	    }
	    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
	    {
		log.error(e.getClass().getName() + " while calling <" + this.method.toGenericString() + ">: " + e.getMessage());
	    }
	}
	List<T> list = new ArrayList<T>();
	for (List<T> l : map.values())
	{
	    try
	    {
		Constructor<T> constructor = returnType.getConstructor(List.class);
		try
		{
		    list.add(constructor.newInstance(l));
		}
		catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException e)
		{
		    log.error(e.getClass().getName() + " while calling constructor <" + constructor.toGenericString() + ">: " + e.getMessage());
		    if(e.getClass().equals(InvocationTargetException.class))
		    {
			log.error("-> nested exception "+e.getCause().getClass().getName()+": "+e.getCause().getMessage());
		    }
		}
	    }
	    catch (NoSuchMethodException | SecurityException ee)
	    {
		log.error(ee.getClass().getName() + ": " + ee.getMessage() + "; return type is " + returnType.getName());
	    }
	}
	// Collections.sort(list);
	return list;
    }
    
    public List<T> load()
    {
	List<T> list = doLoading();
	list = doPostProcessing(list);
	return list;
    }
    
    private List<T> doPostProcessing(List<T> list)
    {
	if (this.postProcessingMethod == null)
	{
	    return list;
	}
	for (T i : list)
	{
	    try
	    {
		if (this.postProcessingParams != null)
		{
		    this.postProcessingMethod.invoke(i, this.postProcessingParams);
		}
		else
		{
		    this.postProcessingMethod.invoke(i);
		}
	    }
	    catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
	    {
		boolean first = true;
		String s = "";
		for (Object o : this.postProcessingParams)
		{
		    if (!first) s += ", ";
		    s += o.toString();
		}
		log.error(e.getClass().getName() + " while calling <" + this.postProcessingMethod.toGenericString() + "> with parameters <" + s + ">: " + e.getMessage());
	    }
	}
	return list;
    }
}
