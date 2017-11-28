package org.dashbuilder.dataset.service;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetManagerCDI;
import org.dashbuilder.exception.ExceptionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetExportServicesTest {

    @Mock
    DataSetManagerCDI dataSetManagerM;
    @Mock
    DataSetDefRegistryCDI gitStorageM;
    @Mock
    ExceptionManager exceptionManagerM;

    @Test
    public void exportToExcelWorksWhenDataSetHasNulls() {
        DataSetExportServicesImpl exporter = new DataSetExportServicesImpl(dataSetManagerM,
                                                                           gitStorageM,
                                                                           exceptionManagerM);

        DataSet dataSetWithNulls = DataSetFactory.newDataSetBuilder()
                .date("Date of birth")
                .number("Age")
                .text("Description")
                .row(null,
                     null,
                     null).buildDataSet();

        SXSSFWorkbook workbook = exporter.dataSetToWorkbook(dataSetWithNulls);
        assertNotNull("Export of dataset containing null should succeed",
                      workbook);

        // Verify header
        SXSSFRow firstRow = workbook.getSheetAt(0).getRow(0);
        assertEquals("Date of birth", firstRow.getCell(0).getStringCellValue());
        assertEquals("Age", firstRow.getCell(1).getStringCellValue());
        assertEquals("Description", firstRow.getCell(2).getStringCellValue());

        // Verify data row
        SXSSFRow secondRow = workbook.getSheetAt(0).getRow(1);
        assertEquals("", secondRow.getCell(0).getStringCellValue());
        assertEquals("", secondRow.getCell(1).getStringCellValue());
        assertEquals("", secondRow.getCell(2).getStringCellValue());
    }
}
