package test.java.com.alexpacheco.therapynotes.util;

import com.google.gson.JsonSyntaxException;

import main.java.com.alexpacheco.therapynotes.util.JsonUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class JsonUtilTest
{
	// Simple test POJO
	static class TestPerson
	{
		private String name;
		private int age;
		private String email;
		
		public TestPerson()
		{
		}
		
		public TestPerson(String name, int age, String email)
		{
			this.name = name;
			this.age = age;
			this.email = email;
		}
		
		public String getName()
		{
			return name;
		}
		
		public void setName(String name)
		{
			this.name = name;
		}
		
		public int getAge()
		{
			return age;
		}
		
		public void setAge(int age)
		{
			this.age = age;
		}
		
		public String getEmail()
		{
			return email;
		}
		
		public void setEmail(String email)
		{
			this.email = email;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			TestPerson that = (TestPerson) o;
			return age == that.age && (name != null ? name.equals(that.name) : that.name == null)
					&& (email != null ? email.equals(that.email) : that.email == null);
		}
	}
	
	private TestPerson testPerson;
	
	@BeforeEach
	void setUp()
	{
		testPerson = new TestPerson("John Doe", 30, "john@example.com");
	}
	
	@Test
	void testToJson_validObject_returnsJsonString()
	{
		String json = JsonUtil.toJson(testPerson);
		
		assertNotNull(json);
		assertTrue(json.contains("\"name\": \"John Doe\""));
		assertTrue(json.contains("\"age\": 30"));
		assertTrue(json.contains("\"email\": \"john@example.com\""));
	}
	
	@Test
	void testToJson_nullObject_throwsIllegalArgumentException()
	{
		assertThrows(IllegalArgumentException.class, () ->
		{
			JsonUtil.toJson(null);
		});
	}
	
	@Test
	void testFromJson_validJson_returnsObject()
	{
		String json = "{\"name\":\"Jane Smith\",\"age\":25,\"email\":\"jane@example.com\"}";
		
		TestPerson person = JsonUtil.fromJson(json, TestPerson.class);
		
		assertNotNull(person);
		assertEquals("Jane Smith", person.getName());
		assertEquals(25, person.getAge());
		assertEquals("jane@example.com", person.getEmail());
	}
	
	@Test
	void testFromJson_nullJson_throwsIllegalArgumentException()
	{
		assertThrows(IllegalArgumentException.class, () ->
		{
			JsonUtil.fromJson(null, TestPerson.class);
		});
	}
	
	@Test
	void testFromJson_emptyJson_throwsIllegalArgumentException()
	{
		assertThrows(IllegalArgumentException.class, () ->
		{
			JsonUtil.fromJson("", TestPerson.class);
		});
	}
	
	@Test
	void testFromJson_whitespaceJson_throwsIllegalArgumentException()
	{
		assertThrows(IllegalArgumentException.class, () ->
		{
			JsonUtil.fromJson("   ", TestPerson.class);
		});
	}
	
	@Test
	void testFromJson_nullClass_throwsIllegalArgumentException()
	{
		String json = "{\"name\":\"John Doe\"}";
		
		assertThrows(IllegalArgumentException.class, () ->
		{
			JsonUtil.fromJson(json, null);
		});
	}
	
	@Test
	void testFromJson_malformedJson_throwsJsonSyntaxException()
	{
		String malformedJson = "{\"name\":\"John Doe\",\"age\":}";
		
		assertThrows(JsonSyntaxException.class, () ->
		{
			JsonUtil.fromJson(malformedJson, TestPerson.class);
		});
	}
	
	@Test
	void testToJsonCompact_validObject_returnsCompactJson()
	{
		String json = JsonUtil.toJsonCompact(testPerson);
		
		assertNotNull(json);
		assertFalse(json.contains("\n"));
		assertTrue(json.contains("\"name\":\"John Doe\""));
		assertTrue(json.contains("\"age\":30"));
	}
	
	@Test
	void testToJsonCompact_nullObject_throwsIllegalArgumentException()
	{
		assertThrows(IllegalArgumentException.class, () ->
		{
			JsonUtil.toJsonCompact(null);
		});
	}
	
	@Test
	void testFromJsonSafe_validJson_returnsObject()
	{
		String json = "{\"name\":\"Bob\",\"age\":40,\"email\":\"bob@example.com\"}";
		
		TestPerson person = JsonUtil.fromJsonSafe(json, TestPerson.class);
		
		assertNotNull(person);
		assertEquals("Bob", person.getName());
		assertEquals(40, person.getAge());
	}
	
	@Test
	void testFromJsonSafe_nullJson_returnsNull()
	{
		TestPerson person = JsonUtil.fromJsonSafe(null, TestPerson.class);
		
		assertNull(person);
	}
	
	@Test
	void testFromJsonSafe_emptyJson_returnsNull()
	{
		TestPerson person = JsonUtil.fromJsonSafe("", TestPerson.class);
		
		assertNull(person);
	}
	
	@Test
	void testFromJsonSafe_malformedJson_returnsNull()
	{
		String malformedJson = "{\"name\":\"John\",\"age\":";
		
		TestPerson person = JsonUtil.fromJsonSafe(malformedJson, TestPerson.class);
		
		assertNull(person);
	}
	
	@Test
	void testFromJsonSafe_nullClass_returnsNull()
	{
		String json = "{\"name\":\"John Doe\"}";
		
		TestPerson person = JsonUtil.fromJsonSafe(json, null);
		
		assertNull(person);
	}
	
	@Test
	void testIsValidJson_validJson_returnsTrue()
	{
		String json = "{\"name\":\"Alice\",\"age\":28,\"email\":\"alice@example.com\"}";
		
		boolean isValid = JsonUtil.isValidJson(json, TestPerson.class);
		
		assertTrue(isValid);
	}
	
	@Test
	void testIsValidJson_malformedJson_returnsFalse()
	{
		String malformedJson = "{\"name\":\"Alice\",\"age\":";
		
		boolean isValid = JsonUtil.isValidJson(malformedJson, TestPerson.class);
		
		assertFalse(isValid);
	}
	
	@Test
	void testIsValidJson_nullJson_returnsFalse()
	{
		boolean isValid = JsonUtil.isValidJson(null, TestPerson.class);
		
		assertFalse(isValid);
	}
	
	@Test
	void testDeepCopy_validObject_returnsCopy()
	{
		TestPerson copy = JsonUtil.deepCopy(testPerson, TestPerson.class);
		
		assertNotNull(copy);
		assertNotSame(testPerson, copy);
		assertEquals(testPerson.getName(), copy.getName());
		assertEquals(testPerson.getAge(), copy.getAge());
		assertEquals(testPerson.getEmail(), copy.getEmail());
	}
	
	@Test
	void testDeepCopy_modifyOriginal_doesNotAffectCopy()
	{
		TestPerson copy = JsonUtil.deepCopy(testPerson, TestPerson.class);
		
		testPerson.setName("Modified Name");
		testPerson.setAge(99);
		
		assertEquals("John Doe", copy.getName());
		assertEquals(30, copy.getAge());
	}
	
	@Test
	void testDeepCopy_nullObject_throwsIllegalArgumentException()
	{
		assertThrows(IllegalArgumentException.class, () ->
		{
			JsonUtil.deepCopy(null, TestPerson.class);
		});
	}
	
	@Test
	void testDeepCopy_nullClass_throwsIllegalArgumentException()
	{
		assertThrows(IllegalArgumentException.class, () ->
		{
			JsonUtil.deepCopy(testPerson, null);
		});
	}
	
	@Test
	void testRoundTrip_serializeAndDeserialize_preservesData()
	{
		String json = JsonUtil.toJson(testPerson);
		TestPerson deserialized = JsonUtil.fromJson(json, TestPerson.class);
		
		assertEquals(testPerson, deserialized);
	}
	
	@Test
	void testToJson_objectWithNullFields_includesNullFields()
	{
		TestPerson personWithNulls = new TestPerson(null, 25, null);
		String json = JsonUtil.toJson(personWithNulls);
		
		assertTrue(json.contains("\"name\": null"));
		assertTrue(json.contains("\"email\": null"));
		assertTrue(json.contains("\"age\": 25"));
	}
}