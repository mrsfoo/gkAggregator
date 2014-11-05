package com.zwb.geekology.parser.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import com.zwb.geekology.parser.lastfm.Config;
import com.zwb.lazyload.ILoader;
import com.zwb.lazyload.LazyLoader;
import com.zwb.lazyload.Ptr;
import com.zwb.stringutil.ISatiniseFilterArray;

public class GkDbReleaseMerged extends AbstrGkDbItem implements IGkDbRelease
{
    private Logger log = LogManager.getLogger((this.getClass().getName()));
    private IGkDbArtist artist;
    private List<IGkDbRelease> releases;
    private Ptr<Integer> discCount = new Ptr<Integer>();
    private Ptr<List<IGkDbTrack>> tracks = new Ptr<List<IGkDbTrack>>();
    private Ptr<List<IGkDbTag>> tags = new Ptr<List<IGkDbTag>>();
    private Ptr<List<String>> tagNames = new Ptr<List<String>>();
    private Ptr<String> description = new Ptr<String>();
    private Ptr<String> descriptionSummary = new Ptr<String>();

    public GkDbReleaseMerged(IGkDbArtist artist, List<IGkDbRelease> releases)
    {
	super(NameMerger.mergeNames(releases), GkParserObjectFactory.createSource(Config.getSourceString()));
	this.releases = releases;
	this.artist = artist;
    }
    
    public GkDbReleaseMerged(List<IGkDbRelease> releases)
    {
	super("", GkParserObjectFactory.createSource(Config.getSourceString()));
	//TODO NAME MERGING WIEDER AKTIVIEREN!
//	super(NameMerger.mergeNames(releases), GkParserObjectFactory.createSource(Config.getSourceString()));
	this.releases = releases;
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
    public boolean isSampler()
    {
	return false;
    }
    
    @Override
    public IGkDbArtist getArtist()
    {
	return this.artist;
    }
    
    public void setArtist(GkDbArtistMerged artist)
    {
	this.artist = artist;
    }
    
    @Override
    public boolean hasFormats()
    {
	return (this.getFormats() == null) || this.getFormats().isEmpty();
    }
    
    @Override
    public boolean hasLabels()
    {
	return (this.getLabels() == null) || this.getLabels().isEmpty();
    }
    
    @Override
    public String getDescriptionSummary()
    {
	String methodName = "getDescriptionSummary";
	try
	{
	    return LazyLoader.loadLazy(this.descriptionSummary, new MergeLoaderTextblocks(this.releases, IGkDbItemWithDesc.class.getMethod(methodName)));
	}
	catch (NoSuchMethodException | SecurityException e)
	{
	    log.error(e.getClass().getName()+ " thrown while trying to load <"+this.getClass().getName()+"::"+methodName+">");
	    return "";
	}
    }

    @Override
    public String getDescription()
    {
	String methodName = "getDescription";
	try
	{
	    return LazyLoader.loadLazy(this.description, new MergeLoaderTextblocks(this.releases, IGkDbItemWithDesc.class.getMethod(methodName)));
	}
	catch (NoSuchMethodException | SecurityException e)
	{
	    log.error(e.getClass().getName()+ " thrown while trying to load <"+this.getClass().getName()+"::"+methodName+">");
	    return "";
	}
    }

    @Override
    public List<IGkDbTag> getStyleTags()
    {
	return LazyLoader.loadLazy(this.tags, new MergeLoaderStyleTags(this.releases));
    }
    
    @Override
    public List<String> getStyleTagNames()
    {
	return LazyLoader.loadLazy(this.tagNames, new NameLoader(this.getStyleTags()));
    }
    
    @Override
    public List<IGkDbTrack> getTracks()
    {
	return LazyLoader.loadLazy(this.tracks, new TrackMergerLoader());
    }
    
    @Override
    public List<String> getTrackNames()
    {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public Date getReleaseDate()
    {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public boolean hasReleaseDate()
    {
	// TODO Auto-generated method stub
	return false;
    }
    
    @Override
    public Integer getTrackCount()
    {
	return this.getTracks().size();
    }
    
    @Override
    public Integer getDiscCount()
    {
	return LazyLoader.loadLazy(this.discCount, new DiscCountLoader());
    }
    
    @Override
    public List<String> getFormats()
    {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public List<String> getLabels()
    {
	// TODO Auto-generated method stub
	return null;
    }
    
    class DiscCountLoader implements ILoader<Integer>
    {
	public Integer load()
	{
	    List<IGkDbTrack> tracks = GkDbReleaseMerged.this.getTracks();
	    int discs = 0;
	    for (IGkDbTrack t : tracks)
	    {
		discs = Math.max(discs, t.getDiscNo());
	    }
	    return discs;
	}
    }
    
    class TrackMergerLoader implements ILoader
    {
	public List<IGkDbTrack> load()
	{
	    Map<String, List<IGkDbTrack>> map = new HashMap<>();
	    for (IGkDbRelease n : GkDbReleaseMerged.this.releases)
	    {
		for (IGkDbTrack i : n.getTracks())
		{
		    String name = i.getName();
		    if (!map.containsKey(name))
		    {
			map.put(name, new ArrayList<IGkDbTrack>());
		    }
		    map.get(name).add(i);
		}
	    }
	    List<IGkDbTrack> list = new ArrayList<IGkDbTrack>();
	    for (List<IGkDbTrack> l : map.values())
	    {
		list.add(new GkDbTrackMerged(GkDbReleaseMerged.this.getArtist(), GkDbReleaseMerged.this, l));
	    }
	    Collections.sort(list);
	    return list;
	}
    }

    @Override
    public ISatiniseFilterArray getFilters()
    {
	throw new IllegalArgumentException("NOT IMPLEMENTED YET!");
    }
    
}
