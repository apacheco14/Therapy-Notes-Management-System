package main.java.com.alexpacheco.therapynotes.model.entities;

import java.time.LocalDateTime;
import java.util.Date;

public class Client
{
	private Integer clientId;
	private String firstName;
	private String lastName;
	private String clientCode;
	private boolean inactive;
	private LocalDateTime insertDate;
	private LocalDateTime updateDate;
	private String email1;
	private String email2;
	private String email3;
	private String phone1;
	private String phone2;
	private String phone3;
	private Date dateOfBirth;
	
	public Integer getClientId()
	{
		return clientId;
	}
	
	public void setClientId(Integer clientId)
	{
		this.clientId = clientId;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public String getClientCode()
	{
		return clientCode;
	}
	
	public void setClientCode(String clientCode)
	{
		this.clientCode = clientCode;
	}
	
	public boolean isInactive()
	{
		return inactive;
	}
	
	public void setInactive(boolean inactive)
	{
		this.inactive = inactive;
	}
	
	public LocalDateTime getInsertDate()
	{
		return insertDate;
	}
	
	public void setInsertDate(LocalDateTime insertDate)
	{
		this.insertDate = insertDate;
	}
	
	public LocalDateTime getUpdateDate()
	{
		return updateDate;
	}
	
	public void setUpdateDate(LocalDateTime updateDate)
	{
		this.updateDate = updateDate;
	}
	
	public String getEmail1()
	{
		return email1;
	}
	
	public void setEmail1(String email1)
	{
		this.email1 = email1;
	}
	
	public String getEmail2()
	{
		return email2;
	}
	
	public void setEmail2(String email2)
	{
		this.email2 = email2;
	}
	
	public String getEmail3()
	{
		return email3;
	}
	
	public void setEmail3(String email3)
	{
		this.email3 = email3;
	}
	
	public String getPhone1()
	{
		return phone1;
	}
	
	public void setPhone1(String phone1)
	{
		this.phone1 = phone1;
	}
	
	public String getPhone2()
	{
		return phone2;
	}
	
	public void setPhone2(String phone2)
	{
		this.phone2 = phone2;
	}
	
	public String getPhone3()
	{
		return phone3;
	}
	
	public void setPhone3(String phone3)
	{
		this.phone3 = phone3;
	}
	
	public Date getDateOfBirth()
	{
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getFullName()
	{
		return this.firstName + " " + this.lastName;
	}
}