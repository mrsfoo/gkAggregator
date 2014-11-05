package com.zwb.geekology.parser.internal;

import java.util.ArrayList;
import java.util.List;

import com.zwb.config.api.ConfigurationFactory;
import com.zwb.config.api.IConfiguration;

public class Config
{
    private static String CONFIG_NAME = "aggregator.config";
    private static String CONFIG_KEY_IMPLEMENTATIONS = "implementations";
    private static final String SOURCE_STRING = "aggregator";

    private static String CONFIG_KEY_MERGE_PRIO_TRACK_DURATION = "merge.prio.track.duration";
    private static String CONFIG_KEY_MERGE_PRIO_TRACK_DISCNO = "merge.prio.track.discno";
    private static String CONFIG_KEY_MERGE_PRIO_TRACK_TRACKNO = "merge.prio.track.trackno";

    private static IConfiguration config = ConfigurationFactory.getConfiguration(CONFIG_NAME);
    
    public static List<String> getImplementations()
    {
	return config.getListOfStrings(CONFIG_KEY_IMPLEMENTATIONS, new ArrayList<String>());
    }
    
    public static String getSourceString()
    {
	return SOURCE_STRING;
    }
    
    public static List<String> getMergePrioTrackDuration()
    {
	return config.getListOfStrings(CONFIG_KEY_MERGE_PRIO_TRACK_DURATION, new ArrayList<String>());
    }
    
    public static List<String> getMergePrioTrackDiscno()
    {
	return config.getListOfStrings(CONFIG_KEY_MERGE_PRIO_TRACK_DISCNO, new ArrayList<String>());
    }
    
    public static List<String> getMergePrioTrackTrackno()
    {
	return config.getListOfStrings(CONFIG_KEY_MERGE_PRIO_TRACK_TRACKNO, new ArrayList<String>());
    }
    
}
