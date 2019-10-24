package org.guvnor.messageconsole.utils;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class CSVConverterTest {

    @Test
    public void testNull() {
        String csv = CSVConverter.convertTable(null);
        assertTrue(csv.equals(""));
    }

    @Test
    public void testEmpty() {
        List<List<String>> table = new ArrayList<>();

        String csv = CSVConverter.convertTable(table);
        assertTrue(csv.equals(""));
    }

    @Test
    public void testSimpleCSV() {
        List<List<String>> table = new ArrayList<>();
        table.add(Arrays.asList("NAME", "AGE", "CITY"));
        table.add(Arrays.asList("John", "32", "Paris"));
        table.add(Arrays.asList("Paul", "41", "New York"));
        table.add(Arrays.asList("Mary", "34", "Santiago"));

        String csv = CSVConverter.convertTable(table);
        assertTrue(csv.equals("\"NAME\",\"AGE\",\"CITY\"\n" +
                              "\"John\",\"32\",\"Paris\"\n" +
                              "\"Paul\",\"41\",\"New York\"\n" +
                              "\"Mary\",\"34\",\"Santiago\""));
    }

    @Test
    public void testComplexCSV() {
        List<List<String>> table = new ArrayList<>();
        table.add(Arrays.asList("NAME", "AGE", "CITY"));
        table.add(Arrays.asList("John", "32", "Paris"));
        table.add(Arrays.asList());
        table.add(Arrays.asList("Peter", "", "New York, São Paulo"));
        table.add(Arrays.asList("Mary \"Rose\" O'Brien", "34", "Santiago"));
        table.add(Arrays.asList(""));
        table.add(Arrays.asList("SILVA, A. G., SOUZA, B. R.", "29", "Recife", "Brazil"));

        String csv = CSVConverter.convertTable(table);
        assertTrue(csv.equals("\"NAME\",\"AGE\",\"CITY\"\n" +
                              "\"John\",\"32\",\"Paris\"\n" +
                              "\n" +
                              "\"Peter\",\"\",\"New York, São Paulo\"\n" +
                              "\"Mary \"\"Rose\"\" O'Brien\",\"34\",\"Santiago\"\n" +
                              "\"\"\n" +
                              "\"SILVA, A. G., SOUZA, B. R.\",\"29\",\"Recife\",\"Brazil\""));
    }
}
