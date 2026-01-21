package main.java.com.alexpacheco.therapynotes.model.entities;

import java.time.LocalDateTime;

import main.java.com.alexpacheco.therapynotes.controller.enums.PreferenceType;

/**
 * Entity representing a user preference setting
 */
public class Preference
{
	private String preferenceKey;
	private String preferenceValue;
	private PreferenceType preferenceType;
	private String displayName;
	private String description;
	private String defaultValue;
	private String category;
	private LocalDateTime lastModified;
	
	// Constructors
	
	public Preference()
	{
	}
	
	public Preference(String preferenceKey, String preferenceValue, PreferenceType preferenceType)
	{
		this.preferenceKey = preferenceKey;
		this.preferenceValue = preferenceValue;
		this.preferenceType = preferenceType;
	}
	
	public Preference(String preferenceKey, String preferenceValue, PreferenceType preferenceType, String displayName, String description,
			String defaultValue, String category)
	{
		this.preferenceKey = preferenceKey;
		this.preferenceValue = preferenceValue;
		this.preferenceType = preferenceType;
		this.displayName = displayName;
		this.description = description;
		this.defaultValue = defaultValue;
		this.category = category;
	}
	
	// Getters and Setters
	
	public String getPreferenceKey()
	{
		return preferenceKey;
	}
	
	public void setPreferenceKey(String preferenceKey)
	{
		this.preferenceKey = preferenceKey;
	}
	
	public String getPreferenceValue()
	{
		return preferenceValue;
	}
	
	public void setPreferenceValue(String preferenceValue)
	{
		this.preferenceValue = preferenceValue;
	}
	
	public PreferenceType getPreferenceType()
	{
		return preferenceType;
	}
	
	public void setPreferenceType(PreferenceType preferenceType)
	{
		this.preferenceType = preferenceType;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}
	
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDefaultValue()
	{
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	
	public String getCategory()
	{
		return category;
	}
	
	public void setCategory(String category)
	{
		this.category = category;
	}
	
	public LocalDateTime getLastModified()
	{
		return lastModified;
	}
	
	public void setLastModified(LocalDateTime lastModified)
	{
		this.lastModified = lastModified;
	}
	
	// Convenience Methods for Type Conversion
	
	/**
	 * Get the preference value as a boolean
	 * 
	 * @return boolean value, or false if value is null/invalid
	 */
	public boolean getValueAsBoolean()
	{
		if (preferenceValue == null)
		{
			return defaultValue != null && Boolean.parseBoolean(defaultValue);
		}
		return Boolean.parseBoolean(preferenceValue);
	}
	
	/**
	 * Get the preference value as an integer
	 * 
	 * @return integer value, or default value if current value is null/invalid
	 */
	public int getValueAsInt()
	{
		try
		{
			return preferenceValue != null ? Integer.parseInt(preferenceValue) : Integer.parseInt(defaultValue);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Get the preference value as a string
	 * 
	 * @return string value, or default value if current value is null
	 */
	public String getValueAsString()
	{
		return preferenceValue != null ? preferenceValue : defaultValue;
	}
	
	/**
	 * Get the preference value as a double
	 * 
	 * @return double value, or default value if current value is null/invalid
	 */
	public double getValueAsDouble()
	{
		try
		{
			return preferenceValue != null ? Double.parseDouble(preferenceValue) : Double.parseDouble(defaultValue);
		}
		catch (NumberFormatException e)
		{
			return 0.0;
		}
	}
	
	/**
	 * Check if the current value is different from the default value
	 * 
	 * @return true if value has been customized
	 */
	public boolean isCustomized()
	{
		if (preferenceValue == null && defaultValue == null)
		{
			return false;
		}
		if (preferenceValue == null || defaultValue == null)
		{
			return true;
		}
		return !preferenceValue.equals(defaultValue);
	}
	
	/**
	 * Reset the preference to its default value
	 */
	public void resetToDefault()
	{
		this.preferenceValue = this.defaultValue;
		this.lastModified = LocalDateTime.now();
	}
	
	@Override
	public String toString()
	{
		return "Preference{" + "key='" + preferenceKey + '\'' + ", value='" + preferenceValue + '\'' + ", type=" + preferenceType
				+ ", category='" + category + '\'' + '}';
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		Preference that = (Preference) o;
		return preferenceKey != null ? preferenceKey.equals(that.preferenceKey) : that.preferenceKey == null;
	}
	
	@Override
	public int hashCode()
	{
		return preferenceKey != null ? preferenceKey.hashCode() : 0;
	}
}