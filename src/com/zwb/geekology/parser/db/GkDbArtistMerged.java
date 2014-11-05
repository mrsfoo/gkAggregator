package com.zwb.geekology.parser.db;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbItemWithDesc;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.impl.util.NameLoader;
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;
import com.zwb.stringutil.ISatiniseFilterArray;

public class GkDbArtistMerged extends AbstrGkDbItem implements IGkDbArtist
{
    private Logger log = LogManager.getLogger((this.getClass().getName()));
    private List<IGkDbArtist> artists;
    private Ptr<List<IGkDbTag>> tags = new Ptr<List<IGkDbTag>>();
    private Ptr<List<String>> tagNames = new Ptr<List<String>>();
    private Ptr<String> description = new Ptr<String>();
    private Ptr<String> descriptionSummary = new Ptr<String>();
    private Ptr<List<IGkDbRelease>> releases = new Ptr<List<IGkDbRelease>>();
    private Ptr<List<String>> releaseNames = new Ptr<List<String>>();
    
    public GkDbArtistMerged(List<IGkDbArtist> artists)
    {
	super(NameMerger.mergeNames(artists), GkParserObjectFactory.createSource(Config.getSourceString()));
	this.artists = artists;
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
    public boolean hasSimilars()
    {
	return (this.getSimilar() == null) || this.getSimilar().isEmpty();
    }
    
    @Override
    public String getDescriptionSummary()
    {
	String methodName = "getDescriptionSummary";
	try
	{
	    return LazyLoader.loadLazy(this.descriptionSummary, new MergeLoaderTextblocks(this.artists, IGkDbItemWithDesc.class.getMethod(methodName)));
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
	    return LazyLoader.loadLazy(this.description, new MergeLoaderTextblocks(this.artists, IGkDbItemWithDesc.class.getMethod(methodName)));
	}
	catch (NoSuchMethodException | SecurityException e)
	{
	    log.error(e.getClass().getName() + " thrown while trying to load <" + this.getClass().getName() + "::" + methodName + ">");
	    return "";
	}
    }
    
    @Override
    public List<IGkDbTag> getStyleTags()
    {
	return LazyLoader.loadLazy(this.tags, new MergeLoaderStyleTags(this.artists));
    }
    
    @Override
    public List<String> getStyleTagNames()
    {
	return LazyLoader.loadLazy(this.tagNames, new NameLoader(this.getStyleTags()));
    }
    
    @Override
    public List<IGkDbRelease> getReleases()
    {
	String methodName = "getReleases";
	String postProcessingMethodName = "setArtist";
	try
	{
	    Method method = IGkDbArtist.class.getMethod(methodName);
	    Class<GkDbReleaseMerged> returnType = GkDbReleaseMerged.class;
	    MergeLoaderListFullMerge<GkDbReleaseMerged> loader = new MergeLoaderListFullMerge<GkDbReleaseMerged>(this.artists, method, returnType);
	    
	    Method postProcessingMethod = GkDbReleaseMerged.class.getMethod(postProcessingMethodName, this.getClass());
	    loader.setPostProcessing(postProcessingMethod, this);
	    
	    List<IGkDbRelease> releases = LazyLoader.loadLazy(this.releases, loader);
	    return releases;
	}
	catch (NoSuchMethodException | SecurityException e)
	{
	    log.error(e.getClass().getName() + ": " + e.getMessage());
	    return null;
	}
    }
    
    @Override
    public List<String> getReleaseNames()
    {
	return LazyLoader.loadLazy(this.releaseNames, new NameLoader(this.getReleases()));
    }
    
    @Override
    public List<IGkDbArtist> getSimilar()
    {
	// TODO Auto-generated method stub
	return new ArrayList<IGkDbArtist>();
    }
    
    @Override
    public List<String> getSimilarsNames()
    {
	// TODO Auto-generated method stub
	return new ArrayList<String>();
    }

    @Override
    public ISatiniseFilterArray getFilters()
    {
	throw new IllegalArgumentException("NOT IMPLEMENTED YET!");
    }
}
