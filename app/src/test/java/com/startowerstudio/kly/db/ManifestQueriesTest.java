package com.startowerstudio.kly.db;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Riley on 2/5/2018.
 */
public class ManifestQueriesTest {
    @Test
    public void massageNames() throws Exception {
        // Two names, both three characters
        String expected = "SELECT manifest_id AS '_id', username, occupation, location FROM kly_manifest WHERE username LIKE '%mrd%'\n" +
                "OR username LIKE '%bee%'\n";
        String input = "mrd bee";
        assertEquals(expected, ManifestQueries.getInstance().massageNames(input));

        // Two names, one three characters, one two characters
        expected = "SELECT manifest_id AS '_id', username, occupation, location FROM kly_manifest WHERE username LIKE '%mrd%'\n" +
                "OR username LIKE 'be%'\n";
        input = "mrd be";
        assertEquals(expected, ManifestQueries.getInstance().massageNames(input));

        // One name, three characters
        expected = "SELECT manifest_id AS '_id', username, occupation, location FROM kly_manifest WHERE username LIKE '%mrd%'\n";
        input = "mrd";
        assertEquals(expected, ManifestQueries.getInstance().massageNames(input));
    }

}