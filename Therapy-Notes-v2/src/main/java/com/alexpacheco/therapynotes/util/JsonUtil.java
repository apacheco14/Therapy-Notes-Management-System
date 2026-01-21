package com.alexpacheco.therapynotes.util;

import com.alexpacheco.therapynotes.controller.AppController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Generic utility class for serializing and deserializing POJOs to/from JSON
 */
public class JsonUtil
{
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	private static final Gson compactGson = new Gson();
	
	/**
	 * Serialize an object to JSON string
	 * 
	 * @param <T>    The type of object to serialize
	 * @param object The object to serialize
	 * @return JSON string representation of the object
	 * @throws IllegalArgumentException if object is null
	 */
	public static <T> String toJson(T object)
	{
		if (object == null)
		{
			throw new IllegalArgumentException("Object cannot be null");
		}
		
		return gson.toJson(object);
	}
	
	/**
	 * Deserialize a JSON string to an object of the specified type
	 * 
	 * @param <T>   The type to deserialize to
	 * @param json  The JSON string to deserialize
	 * @param clazz The class of the type to deserialize to
	 * @return Object of type T
	 * @throws IllegalArgumentException if json is null or empty, or if clazz is null
	 * @throws JsonSyntaxException      if json is malformed
	 */
	public static <T> T fromJson(String json, Class<T> clazz)
	{
		if (json == null || json.trim().isEmpty())
		{
			throw new IllegalArgumentException("JSON string cannot be null or empty");
		}
		
		if (clazz == null)
		{
			throw new IllegalArgumentException("Class cannot be null");
		}
		
		try
		{
			return gson.fromJson(json, clazz);
		}
		catch (JsonSyntaxException e)
		{
			AppController.logException("JsonUtil", e);
			throw new JsonSyntaxException("Invalid JSON format for " + clazz.getSimpleName() + ": " + e.getMessage(), e);
		}
	}
	
	/**
	 * Serialize an object to JSON string with compact formatting (no pretty printing)
	 * 
	 * @param <T>    The type of object to serialize
	 * @param object The object to serialize
	 * @return Compact JSON string representation of the object
	 * @throws IllegalArgumentException if object is null
	 */
	public static <T> String toJsonCompact(T object)
	{
		if (object == null)
		{
			throw new IllegalArgumentException("Object cannot be null");
		}
		
		return compactGson.toJson(object);
	}
	
	/**
	 * Safely deserialize a JSON string to an object, returning null if parsing fails
	 * 
	 * @param <T>   The type to deserialize to
	 * @param json  The JSON string to deserialize
	 * @param clazz The class of the type to deserialize to
	 * @return Object of type T or null if parsing fails
	 */
	public static <T> T fromJsonSafe(String json, Class<T> clazz)
	{
		if (json == null || json.trim().isEmpty() || clazz == null)
		{
			return null;
		}
		
		try
		{
			return gson.fromJson(json, clazz);
		}
		catch (JsonSyntaxException e)
		{
			AppController.logException("JsonUtil", e);
			return null;
		}
	}
	
	/**
	 * Validate if a JSON string represents a valid object of the specified type
	 * 
	 * @param <T>   The type to validate against
	 * @param json  The JSON string to validate
	 * @param clazz The class of the type to validate against
	 * @return true if valid, false otherwise
	 */
	public static <T> boolean isValidJson(String json, Class<T> clazz)
	{
		return fromJsonSafe(json, clazz) != null;
	}
	
	/**
	 * Create a deep copy of an object by serializing and deserializing it
	 * 
	 * @param <T>    The type of object to clone
	 * @param object The object to clone
	 * @param clazz  The class of the type
	 * @return A deep copy of the object
	 * @throws IllegalArgumentException if object or clazz is null
	 */
	public static <T> T deepCopy(T object, Class<T> clazz)
	{
		if (object == null)
		{
			throw new IllegalArgumentException("Object cannot be null");
		}
		
		if (clazz == null)
		{
			throw new IllegalArgumentException("Class cannot be null");
		}
		
		String json = gson.toJson(object);
		return gson.fromJson(json, clazz);
	}
}