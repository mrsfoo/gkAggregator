package com.zwb.geekology.parser.db;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zwb.geekology.parser.abstr.db.AbstrGkDbItem;
import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.IGkDbItemWithDesc;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.db.IGkDbTrack;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.impl.util.NameLoader;
import com.zwb.geekology.parser.internal.Config;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;
import com.zwb.stringutil.ISatiniseFilterArray;

public class GkDbTrackMerged extends AbstrGkDbItem implements IGkDbTrack
{
    private Logger log = LogManager.getLogger((this.getClass().getName()));
    private IGkDbArtist artist;
    private IGkDbRelease release;
    private List<IGkDbTrack> tracks;
    private Ptr<List<IGkDbTag>> tags = new Ptr<List<IGkDbTag>>();
    private Ptr<List<String>> tagNames = new Ptr<List<String>>();
    private Ptr<String> description = new Ptr<String>();
    private Ptr<String> descriptionSummary = new Ptr<String>();
    private Ptr<Integer> absPos = new Ptr<Integer>();
    private Ptr<Integer> trackNo = new Ptr<Integer>();
    private Ptr<Integer> discNo = new Ptr<Integer>();
    private Ptr<Long> duration = new Ptr<Long>();
    
    public GkDbTrackMerged(IGkDbArtist artist, IGkDbRelease release, List<IGkDbTrack> tracks)
    {
	super(NameMerger.mergeNames(tracks), GkParserObjectFactory.createSource(Config.getSourceString()));
	this.tracks = tracks;
	this.artist = artist;
	this.release = release;
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
    public IGkDbRelease getRelease()
    {
	return this.release;
    }
    
    @Override
    public IGkDbArtist getArtist()
    {
	return this.artist;
    }
    
    @Override
    public boolean hasDuration()
    {
	return this.getDuration() > -1;
    }
    
    @Override
    public String getDescriptionSummary()
    {
	String methodName = "getDescriptionSummary";
	try
	{
	    return LazyLoader.loadLazy(this.descriptionSummary, new MergeLoaderTextblocks(this.tracks, IGkDbItemWithDesc.class.getMethod(methodName)));
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
	    return LazyLoader.loadLazy(this.description, new MergeLoaderTextblocks(this.tracks, IGkDbItemWithDesc.class.getMethod(methodName)));
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
	return LazyLoader.loadLazy(this.tags, new MergeLoaderStyleTags(this.tracks));
    }
    
    @Override
    public List<String> getStyleTagNames()
    {
	return LazyLoader.loadLazy(this.tagNames, new NameLoader(this.getStyleTags()));
    }
    
    @Override
    public Integer getTrackNo()
    {
	String methodName = "getTrackNo";
	try
	{
	    Method method = this.getClass().getMethod(methodName);
	    Class<Integer> returnType = Integer.class;
	    List<String> prios = Config.getMergePrioTrackTrackno();
	    return LazyLoader.loadLazy(this.trackNo, new MergeLoaderExclusiveMerge<Integer>(this.tracks, method, returnType, prios));
	}
	catch (NoSuchMethodException | SecurityException e)
	{
	    log.error(e.getClass().getName() + " thrown while trying to load <" + this.getClass().getName() + "::" + methodName + ">");
	    return null;
	}
    }
    
    @Override
    public Integer getDiscNo()
    {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public Long getDuration()
    {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public Integer getAbsolutePosition()
    {
	return LazyLoader.loadLazy(this.absPos, new AbsolutePositionLoader());
    }
    
    @Override
    public int compareTo(IGkDbTrack o)
    {
	if (this.getAbsolutePosition() > o.getAbsolutePosition())
	{
	    return 1;
	}
	else if (this.getAbsolutePosition() < o.getAbsolutePosition())
	{
	    return -1;
	}
	else
	{
	    return this.getName().compareTo(o.getName());
	}
    }
    
    class AbsolutePositionLoader implements ILoader<Integer>
    {
	@Override
	public Integer load()
	{
	    Map<Integer, Integer> trackNos = new HashMap<Integer, Integer>();
	    int abs = 0;
	    for (int i = 1; i < GkDbTrackMerged.this.getDiscNo(); i++)
	    {
		abs += trackNos.get(i);
	    }
	    abs += GkDbTrackMerged.this.getTrackNo();
	    return abs;
	}
    }

    @Override
    public ISatiniseFilterArray getFilters()
    {
	throw new IllegalArgumentException("NOT IMPLEMENTED YET!");
    }
}
