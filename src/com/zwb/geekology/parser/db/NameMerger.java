package com.zwb.geekology.parser.db;

import java.util.List;

import com.zwb.geekology.parser.api.db.IGkDbItem;

public class NameMerger
{
    public static String mergeNames(List<? extends IGkDbItem> items)
    {
	return items.get(0).getName();
    }
    
}
