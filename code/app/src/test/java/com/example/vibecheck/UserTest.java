package com.example.vibecheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Unit tests for the User class.
 */
public class UserTest {

    /**
     * Tests that the parameterized constructor properly sets the username, password, and displayname.
     */
    @Test
    public void testParameterizedConstructor() {
        User user = new User("testUser", "password123", "DisplayName");
        assertEquals("testUser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("DisplayName", user.getDisplayname());
    }

    /**
     * Tests the default constructor and the setter methods for username and password.
     * Note: The setDisplayname() method is currently implemented without a parameter, so its behavior is not as expected.
     */
    @Test
    public void testDefaultConstructorAndSetters() {
        User user = new User();
        // Initially, fields should be null.
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getDisplayname());

        // Test setting username and password.
        user.setUsername("newUser");
        user.setPassword("newPass");

        assertEquals("newUser", user.getUsername());
        assertEquals("newPass", user.getPassword());

        // For displayname, since the setter is defined as:
        // public String setDisplayname() {return this.displayname = displayname;}
        // it does not update the value. Therefore, displayname remains null.
        assertNull(user.getDisplayname());
    }
}
