package com.zwb.geekology.parser.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import com.zwb.geekology.parser.api.db.IGkDbArtist;
import com.zwb.geekology.parser.api.db.PrintDetailLevel;
import com.zwb.geekology.parser.api.exception.GkParserException;
import com.zwb.geekology.parser.api.exception.GkParserExceptionExternalError;
import com.zwb.geekology.parser.api.exception.GkParserExceptionIllegalArgument;
import com.zwb.geekology.parser.api.exception.GkParserExceptionNoResultFound;
import com.zwb.geekology.parser.api.parser.GkParserObjectFactory;
import com.zwb.geekology.parser.api.parser.IGkParsingResult;
import com.zwb.geekology.parser.api.parser.IGkParsingResultArtist;
import com.zwb.geekology.parser.impl.GkParserAggregator;
import com.zwb.geekology.parser.impl.util.DbItemFileWriter;
import com.zwb.tab.Tab;

public class TestSandboxParserAggregator extends TestCase
{
    public void testQuery() throws GkParserException
    {
	GkParserAggregator parser = new GkParserAggregator();
	
	Map<String, String> input = new HashMap<>();
	input.put("trümmer", "schutt und asche");
	input.put("ja, panik", "the angst and the money");
	
	Map<String, IGkParsingResultArtist> resultsArtistQuery = new HashMap<>();
	Map<String, IGkParsingResultArtist> resultsReleaseQuery = new HashMap<>();
	List<IGkParsingResult> resultsListArtistQuery = new ArrayList<>();
	List<IGkParsingResult> resultsListReleaseQuery = new ArrayList<>();
	for (Entry<String, String> e : input.entrySet())
	{
	    System.out.println("parsing for --> " + e.getKey());
	    IGkParsingResultArtist result;
	    try
	    {
		result = parser.parseArtist(GkParserObjectFactory.createQueryForArtist(e.getKey()));
	    }
	    catch (GkParserExceptionNoResultFound ex)
	    {
		result = (IGkParsingResultArtist) ex.getResult();
	    }
	    catch (GkParserExceptionIllegalArgument ex)
	    {
		result = (IGkParsingResultArtist) ex.getResult();
	    }
	    resultsArtistQuery.put(e.getKey(), result);
	    resultsListArtistQuery.add(result);
	}
	for (Entry<String, String> e : input.entrySet())
	{
	    System.out.println("parsing for --> " + e.getKey() + "/" + e.getValue());
	    IGkParsingResultArtist result;
	    try
	    {
		result = parser.parseArtist(GkParserObjectFactory.createQueryForArtist(e.getKey(), e.getValue()));
	    }
	    catch (GkParserExceptionNoResultFound ex)
	    {
		result = (IGkParsingResultArtist) ex.getResult();
	    }
	    catch (GkParserExceptionExternalError | GkParserExceptionIllegalArgument ex)
	    {
		result = (IGkParsingResultArtist) ex.getResult();
	    }
	    resultsReleaseQuery.put(e.getKey(), result);
	    resultsListReleaseQuery.add(result);
	    if (result != null && result.getArtist() != null) result.getArtist().getStyleTags();
	}
	
	Tab tab = new Tab("result table for [" + input.size() + "] queries", "#", "artist query string", "release query string", "state query solo", "state query with release", "queried artist solo", "queried artist with release", "event list query solo", "event list query with release");
	int i = 0;
	String protocolsA = "\n\nEVENT PROTOCOLS:\n";
	String protocolsB = "\n\nEVENT PROTOCOLS:\n";
	String details = "\n\nDETAILS:";
	for (Entry<String, String> e : input.entrySet())
	{
	    IGkParsingResultArtist resA = resultsArtistQuery.get(e.getKey());
	    IGkParsingResultArtist resB = resultsReleaseQuery.get(e.getKey());
	    String artistNameA = "NULL";
	    String artistNameB = "NULL";
	    if (resA.getArtist() != null)
	    {
		artistNameA = resA.getArtist().getName();
	    }
	    if (resB.getArtist() != null)
	    {
		artistNameB = resB.getArtist().getName();
	    }
	    tab.addRow(Integer.toString(i), e.getKey(), input.get(e.getKey()), resA.getState().toString(), resB.getState().toString(), artistNameA, artistNameB, resA.getEventList(), resB.getEventList());
	    protocolsA += resA.getEventProtocol() + "\n";
	    protocolsB += resB.getEventProtocol() + "\n";
	    
	    IGkDbArtist a = resA.getArtist();
	    if (a != null)
	    {
		details += "-----------------------------------------------\n";
		details += "artist          : " + a.getName() + "\n";
		details += "summary         : " + a.getDescriptionSummary() + "\n";
		details += "description     : " + a.getDescription() + "\n";
		details += "releases        : " + a.getReleaseNames() + "\n";
		details += "similar artists : " + a.getSimilarsNames() + "\n";
		details += "style tags      : " + a.getStyleTagNames() + "\n";
	    }
	    i++;
	}
	details += "-----------------------------------------------\n";
	System.out.println(protocolsA);
	System.out.println(protocolsB);
	
	System.out.println(details);
	
	System.out.println(tab.printFormatted());
	
	DbItemFileWriter.writeResultsToFile("TestResults/resultsArtistQuery.txt", resultsListArtistQuery, PrintDetailLevel.LOW, false);
	DbItemFileWriter.writeResultsToFile("TestResults/resultsReleaseQuery.txt", resultsListReleaseQuery, PrintDetailLevel.LOW, false);
	
    }
    
}
