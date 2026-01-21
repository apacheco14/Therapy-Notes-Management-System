package com.alexpacheco.therapynotes.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;

public class JavaUtils
{
	public static boolean isNullOrEmpty(String s)
	{
		return (s == null || s.isEmpty() || s.isBlank());
	}
	
	public static boolean convertBitToBoolean(int bit)
	{
		return bit == 0 ? false : true;
	}
	
	public static int convertBooleanToBit(boolean bool)
	{
		return bool ? 1 : 0;
	}
	
	public static String getStackTraceAsString(Throwable e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	/**
	 * Sanitizes a string for use in a filename.
	 */
	public static String sanitizeFilename(String input)
	{
		if (input == null)
		{
			return "";
		}
		return input.replaceAll("[^a-zA-Z0-9.-]", "_");
	}
	
	/**
	 * Moves any AssessmentOption with name "Other" to the end of the list. Modifies the list in place.
	 * 
	 * @param options The list of AssessmentOptions to reorder
	 */
	public static void moveOtherToEnd(List<AssessmentOption> options)
	{
		if (options == null || options.size() < 2)
		{
			return;
		}
		
		int insertPos = options.size() - 1;
		int i = 0;
		
		while (i <= insertPos)
		{
			if ("OTHER".equals(options.get(i).getName().toUpperCase()))
			{
				AssessmentOption other = options.remove(i);
				options.add(other);
				insertPos--;
			}
			else
			{
				i++;
			}
		}
	}
}
