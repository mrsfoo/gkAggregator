package com.zwb.geekology.parser.db;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbItemWithDesc;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.discogs.db.GkDbTagDiscogs;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;
import com.zwb.stringutil.ISatiniseFilterArray;

public class GkDbTagMerged extends AbstrGkDbItem implements IGkDbTag
{
    private Logger log = LogManager.getLogger((this.getClass().getName()));
    private List<IGkDbTag> tags;
    private Ptr<String> description = new Ptr<String>();
    private Ptr<String> descriptionSummary = new Ptr<String>();
    
    public GkDbTagMerged(List<IGkDbTag> tags)
    {
	super(NameMerger.mergeNames(tags), GkParserObjectFactory.createSource(Config.getSourceString()));
	this.tags = tags;
    }
    
    @Override
    public String getDescriptionSummary()
    {
	String methodName = "getDescriptionSummary";
	try
	{
	    return LazyLoader.loadLazy(this.descriptionSummary, new MergeLoaderTextblocks(this.tags, IGkDbItemWithDesc.class.getMethod(methodName)));
	}
	catch (NoSuchMethodException | SecurityException e)
	{
	    log.error(e.getClass().getName() + " thrown while trying to load <" + this.getClass().getName() + "::" + methodName + ">");
	    return "";
	}
    }
    
    @Override
    public String getDescription()
    {
	String methodName = "getDescription";
	try
	{
	    return LazyLoader.loadLazy(this.description, new MergeLoaderTextblocks(this.tags, IGkDbItemWithDesc.class.getMethod(methodName)));
	}
	catch (NoSuchMethodException | SecurityException e)
	{
	    log.error(e.getClass().getName() + " thrown while trying to load <" + this.getClass().getName() + "::" + methodName + ">");
	    return "";
	}
    }
    
    @Override
    public boolean hasDescriptionSummary()
    {
	return (this.getDescriptionSummary() == null) || this.getDescriptionSummary().isEmpty();
    }
    
    @Override
    public boolean hasDescription()
    {
	return (this.getDescription() == null) || this.getDescription().isEmpty();
    }
    
    @Override
    public Double getWeight()
    {
	// TODO Auto-generated method stub
	return 0.0;
    }
    
    @Override
    public List<IGkDbTag> getSimilar()
    {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public List<String> getSimilarsNames()
    {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public boolean hasSimilar()
    {
	return (this.getSimilar()==null)||this.getSimilar().isEmpty();
    }
    
    @Override
    public int compareTo(IGkDbTag o)
    {
	if (this.getWeight() > o.getWeight())
	{
	    return -1;
	}
	else if (this.getWeight() < o.getWeight())
	{
	    return 1;
	}
	else
	{
	    return this.getName().compareTo(o.getName());
	}
    }
    
    @Override
    public boolean equals(Object o)
    {
	if (o == null)
	{
	    return false;
	}
	if (!this.getClass().equals(o.getClass()))
	{
	    return false;
	}
	GkDbTagDiscogs cmpTag = (GkDbTagDiscogs) o;
	return this.getName().equals(cmpTag.getName());
    }
    
    @Override
    public int hashCode()
    {
	return this.getName().hashCode();
    }

    @Override
    public ISatiniseFilterArray getFilters()
    {
	throw new IllegalArgumentException("NOT IMPLEMENTED YET!");
    }
}
