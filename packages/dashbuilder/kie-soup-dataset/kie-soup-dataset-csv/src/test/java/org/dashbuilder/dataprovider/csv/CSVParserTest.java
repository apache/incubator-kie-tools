package org.dashbuilder.dataprovider.csv;


import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class CSVParserTest {

    //Dummy DataSetDef just to get separator/quote/escape char definition for the parser
    private final CSVDataSetDef csvDataSet = (CSVDataSetDef) DataSetDefFactory.newCSVDataSetDef()
            .separatorChar(',')
            .quoteChar('\'')
            .escapeChar('\\')
            .datePattern("YYYY-MM-dd")
            .numberPattern("##.##")
            .buildDef();

    /* Reproducer of DASHBUILDE-172 */
    @Test
    public void exceptionThrown_whenFirstLineHasLessFieldsThanHeader() throws Exception {
        //Header with 2 fields, 1st row missing comma -> has only 1 field
        final String CSV_DATA = "'Name','Age'\n'Jan''15'";

        CSVFileStorage mockStorage = new MockCSVFileStorage(CSV_DATA);
        CSVParser testedParser = new CSVParser(csvDataSet, mockStorage);
        try {
            DataSet dataSetShouldNotLoad = testedParser.load();
            Assert.fail("IllegalArgumentException should be thrown when 1st row of CSV data has less fields than the header");
        } catch (IllegalArgumentException iae) {
            String expectedExceptionMessage = "CSV parse error : The first row has fewer columns (1) than the header (2)";
            assertEquals(expectedExceptionMessage, iae.getMessage());
        }
    }

    @Test
    public void dataSetCreated_whenCsvDataValid() throws Exception {
        final String CSV_DATA = "'Name','Weight','Date of birth'\n'Jan','75.64','1950-01-20'";

        CSVFileStorage mockStorage = new MockCSVFileStorage(CSV_DATA);
        CSVParser testedParser = new CSVParser(csvDataSet, mockStorage);
        DataSet dset = testedParser.load();

        assertEquals(1, dset.getRowCount());
        assertEquals(ColumnType.LABEL, dset.getColumnById("Name").getColumnType());
        assertEquals(ColumnType.NUMBER, dset.getColumnById("Weight").getColumnType());
        assertEquals(ColumnType.DATE, dset.getColumnById("Date of birth").getColumnType());
    }

    @Test
    public void stringFieldStartingWithNumber_shouldBeParsedAsLabel() throws Exception {
        final String CSV_DATA = "Age,Address\n" +
                "25,12 Downing street\n" +
                "75,White House 52";

        CSVFileStorage mockStorage = new MockCSVFileStorage(CSV_DATA);
        CSVParser testedParser = new CSVParser(csvDataSet, mockStorage);
        DataSet dset = testedParser.load();

        assertEquals(2, dset.getRowCount());
        assertEquals(ColumnType.NUMBER, dset.getColumnById("Age").getColumnType());
        assertEquals(ColumnType.LABEL, dset.getColumnById("Address").getColumnType());
    }

    static class MockCSVFileStorage implements CSVFileStorage {

        private final String csvData;

        MockCSVFileStorage(String csvData) {
            this.csvData = csvData;
        }

        @Override
        public InputStream getCSVInputStream(CSVDataSetDef ignored) {
            return new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public String getCSVString(CSVDataSetDef def) {
            return null;
        }

        @Override
        public void saveCSVFile(CSVDataSetDef def) {
        }

        @Override
        public void deleteCSVFile(CSVDataSetDef def) {
        }
    }
}
