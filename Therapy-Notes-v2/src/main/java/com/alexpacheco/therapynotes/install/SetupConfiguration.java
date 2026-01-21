package main.java.com.alexpacheco.therapynotes.install;

/**
 * Data class holding all setup wizard configuration values. This is populated step-by-step as the user progresses through the wizard.
 */
public class SetupConfiguration
{
	// Practice Information
	private String practiceName;
	private String practitionerName;
	private String licenseNumber;
	private String phone;
	private String email;
	private String address;
	
	// Database Settings
	private String databasePath;
	
	// Security Settings
	private boolean pinEnabled = true;
	private int autoLockMinutes = 15;
	
	// Constructors
	public SetupConfiguration()
	{
	}
	
	// Practice Information Getters/Setters
	
	public String getPracticeName()
	{
		return practiceName;
	}
	
	public void setPracticeName( String practiceName )
	{
		this.practiceName = practiceName;
	}
	
	public String getPractitionerName()
	{
		return practitionerName;
	}
	
	public void setPractitionerName( String practitionerName )
	{
		this.practitionerName = practitionerName;
	}
	
	public String getLicenseNumber()
	{
		return licenseNumber;
	}
	
	public void setLicenseNumber( String licenseNumber )
	{
		this.licenseNumber = licenseNumber;
	}
	
	public String getPhone()
	{
		return phone;
	}
	
	public void setPhone( String phone )
	{
		this.phone = phone;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail( String email )
	{
		this.email = email;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public void setAddress( String address )
	{
		this.address = address;
	}
	
	// Database Getters/Setters
	
	public String getDatabasePath()
	{
		return databasePath;
	}
	
	public void setDatabasePath( String databasePath )
	{
		this.databasePath = databasePath;
	}
	
	// Security Getters/Setters
	
	public boolean isPinEnabled()
	{
		return pinEnabled;
	}
	
	public void setPinEnabled( boolean pinEnabled )
	{
		this.pinEnabled = pinEnabled;
	}
	
	public int getAutoLockMinutes()
	{
		return autoLockMinutes;
	}
	
	public void setAutoLockMinutes( int autoLockMinutes )
	{
		this.autoLockMinutes = autoLockMinutes;
	}
	
	@Override
	public String toString()
	{
		return "SetupConfiguration{" + "practiceName='" + practiceName + '\'' + ", practitionerName='" + practitionerName + '\''
				+ ", licenseNumber='" + licenseNumber + '\'' + ", databasePath='" + databasePath + '\'' + '}';
	}
}