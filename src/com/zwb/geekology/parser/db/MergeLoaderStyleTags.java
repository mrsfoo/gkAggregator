package com.zwb.geekology.parser.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zwb.geekology.parser.api.db.IGkDbItemWithStyleTags;
import com.zwb.geekology.parser.api.db.IGkDbRelease;
import com.zwb.geekology.parser.api.db.IGkDbTag;
import com.zwb.geekology.parser.api.db.IGkDbTrack;
import com.zwb.lazyload.ILoader;

public class MergeLoaderStyleTags implements ILoader
{
    private List<? extends IGkDbItemWithStyleTags> items;
    
    public MergeLoaderStyleTags(List<? extends IGkDbItemWithStyleTags> items)
    {
	this.items = items;
    }
    
    public List<IGkDbTag> load()
    {
	Map<String, List<IGkDbTag>> map = new HashMap<>();
	for (IGkDbItemWithStyleTags i : items)
	{
	    for (IGkDbTag n : i.getStyleTags())
	    {
		String name = n.getName();
		if (!map.containsKey(name))
		{
		    map.put(name, new ArrayList<IGkDbTag>());
		}
		map.get(name).add(n);
	    }
	}
	List<IGkDbTag> list = new ArrayList<IGkDbTag>();
	for (List<IGkDbTag> l : map.values())
	{
	    list.add(new GkDbTagMerged(l));
	}
	Collections.sort(list);
	return list;
    }
}
