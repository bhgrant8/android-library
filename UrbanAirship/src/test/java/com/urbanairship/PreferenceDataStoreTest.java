package com.urbanairship;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
public class PreferenceDataStoreTest {

    private ContentResolver resolver;
    private Context context;
    private PreferenceDataStore testPrefs;

    @Before
    public void setUp() {
        context = Robolectric.application.getApplicationContext();
        resolver = context.getContentResolver();

        testPrefs = new PreferenceDataStore(context);
    }

    /**
     * Test that put saves preferences
     */
    @Test
    public void testPut() {
        // Add various preferences
        assertTrue(testPrefs.putSync("test.string", "string"));
        assertTrue(testPrefs.putSync("test.int", 1));
        assertTrue(testPrefs.putSync("test.long", 1L));
        assertTrue(testPrefs.putSync("test.float", 1f));
        assertTrue(testPrefs.putSync("test.boolean", true));

        // Verify they are saved
        assertEquals(1.0f, testPrefs.getFloat("test.float", 0), 0);
        assertEquals(1L, testPrefs.getLong("test.long", 0));
        assertEquals(1, testPrefs.getInt("test.int", 0));
        assertTrue(testPrefs.getBoolean("test.boolean", false));
        assertEquals("string", testPrefs.getString("test.string", null));
    }

    /**
     * Test put null clears the preference
     */
    @Test
    public void testPutNull() {
        // Put null for an empty value
        assertTrue(testPrefs.putSync("test.value", null));

        assertNull("Preference should not return any string when put is null",
                testPrefs.getString("test.value", null));

        assertEquals("Preference should return default value when preference is null",
                "default-value", testPrefs.getString("test.value", "default-value"));

        // Add a value and then clear it with null
        assertTrue(testPrefs.putSync("test.value", "some-value"));
        assertTrue(testPrefs.putSync("test.value", null));

        assertNull("Preference should not return any string when put is null",
                testPrefs.getString("test.value", null));
    }

    /**
     * Test that migrate preferences migrates any shared preferences to a database
     * store
     */
    @Test
    public void testPreferencesMigration() {
        SharedPreferences preferences = context.getSharedPreferences("com.urbanairship.push", 0x00000004);

        // Add preferences to be migrated
        preferences.edit()
                   .putFloat("migrate.float", 1.0f)
                   .putLong("migrate.long", 1l)
                   .putInt("migrate.int", 1)
                   .putBoolean("migrate.boolean", true)
                   .putString("migrate.string", "some-string")
                   .commit();

        // Verify we did it right
        assertEquals(5, preferences.getAll().size());

        // Migrate the preferences
        testPrefs.migrateSharedPreferences(context);

        // Verify they are cleared in the shared preferences
        assertEquals("Preferences should be deleted once migrated", 0, preferences.getAll().size());

        // Verify each preference migrated
        assertEquals(1.0f, testPrefs.getFloat("migrate.float", 0), 0);
        assertEquals(1l, testPrefs.getLong("migrate.long", 0));
        assertEquals(1, testPrefs.getInt("migrate.int", 0));
        assertTrue(testPrefs.getBoolean("migrate.boolean", false));
        assertEquals("some-string", testPrefs.getString("migrate.string", null));
    }

    /**
     * Test get value as a long
     */
    @Test
    public void testGetLong() {
        testPrefs.put("value", "123");
        assertEquals("Did not return expected value", 123, testPrefs.getLong("value", -1));
    }

    /**
     * Test get value as a long for a value that cannot be
     * coerced into a long.
     */
    @Test
    public void testGetLongInvalid() {
        testPrefs.put("value", "incorrect value");
        assertEquals("Did not return default value", -1, testPrefs.getLong("value", -1));
    }

    /**
     * Test get value as an int
     */
    @Test
    public void testGetInt() {
        testPrefs.put("value", "123");
        assertEquals("Did not return expected value", 123, testPrefs.getInt("value", -1));
    }

    /**
     * Test get value as an int for a value that cannot be
     * coerced into an int.
     */
    @Test
    public void testGetIntInvalid() {
        testPrefs.put("value", "incorrect value");
        assertEquals("Did not return default value", -1, testPrefs.getInt("value", -1));
    }

    /**
     * Test get value as a float
     */
    @Test
    public void testGetFloat() {
        testPrefs.put("value", "123");
        assertEquals("Did not return expected value", 123, testPrefs.getFloat("value", -1), 1e-15);
    }

    /**
     * Test get value as a float for a value that cannot be
     * coerced into a float.
     */
    @Test
    public void testGetFloatInvalid() {
        testPrefs.put("value", "incorrect value");
        assertEquals("Did not return default value", -1, testPrefs.getFloat("value", -1), 1e-15);
    }

    /**
     * Test get value as a boolean
     */
    @Test
    public void testGetBoolean() {
        testPrefs.put("value", "true");
        assertTrue("Did not return expected value", testPrefs.getBoolean("value", false));
    }

    /**
     * Test get value as a boolean for a value that cannot be
     * coerced into a boolean.
     */
    @Test
    public void testGetBooleanInvalid() {
        testPrefs.put("value", "incorrect value");
        assertFalse("Invalid boolean string default to false", testPrefs.getBoolean("value", true));
    }

    /**
     * Test get value as a string
     */
    @Test
    public void testGetString() {
        testPrefs.put("value", "true");
        assertEquals("Did not return expected value", "true", testPrefs.getString("value", null));
    }

}