package org.dashbuilder.dataset.service;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.DataSetManagerCDI;
import org.dashbuilder.exception.ExceptionManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetExportServicesTest {

    private static final String JAVA_AWT_HEADLESS_PROPERTY = "java.awt.headless";
    private static String originalJavaAwtHeadless;

    @Mock
    DataSetManagerCDI dataSetManagerM;
    @Mock
    DataSetDefRegistryCDI gitStorageM;
    @Mock
    ExceptionManager exceptionManagerM;

    @BeforeClass
    public static void setUp() {
        /*
         * Make the test run in headless mode. It used to fail frequently on Jenkins (40 failures in 6 months) with
         * the following stack trace (abridged):
         *
         * java.awt.AWTError: Can't connect to X11 window server using ':93' as the value of the DISPLAY variable.
         *   at sun.awt.X11GraphicsEnvironment.initDisplay(Native Method)
         *   at sun.awt.X11GraphicsEnvironment.access$200(X11GraphicsEnvironment.java:65)
         *   at sun.awt.X11GraphicsEnvironment$1.run(X11GraphicsEnvironment.java:115)
         *   at sun.awt.X11GraphicsEnvironment.<clinit>(X11GraphicsEnvironment.java:74)
         *   at java.lang.Class.forName0(Native Method)
         *   at java.lang.Class.forName(Class.java:264)
         *   at java.awt.GraphicsEnvironment.createGE(GraphicsEnvironment.java:103)
         *   at java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment(GraphicsEnvironment.java:82)
         *   at sun.awt.X11FontManager.isHeadless(X11FontManager.java:509)
         *   at sun.awt.X11FontManager.getFontPath(X11FontManager.java:786)
         *   at sun.font.SunFontManager.getPlatformFontPath(SunFontManager.java:3282)
         *   at sun.font.SunFontManager$10.run(SunFontManager.java:3308)
         *   at sun.font.SunFontManager.loadFonts(SunFontManager.java:3304)
         *   at sun.awt.X11FontManager.loadFonts(X11FontManager.java:460)
         *   at sun.font.SunFontManager.findFont2D(SunFontManager.java:2348)
         *   at java.awt.Font.getFont2D(Font.java:500)
         *   at java.awt.Font.canDisplayUpTo(Font.java:2060)
         *   at java.awt.font.TextLayout.singleFont(TextLayout.java:470)
         *   at java.awt.font.TextLayout.<init>(TextLayout.java:531)
         *   at org.apache.poi.ss.util.SheetUtil.getDefaultCharWidth(SheetUtil.java:275)
         *   at org.apache.poi.xssf.streaming.AutoSizeColumnTracker.<init>(AutoSizeColumnTracker.java:117)
         *   at org.apache.poi.xssf.streaming.SXSSFSheet.<init>(SXSSFSheet.java:82)
         *   at org.apache.poi.xssf.streaming.SXSSFWorkbook.createAndRegisterSXSSFSheet(SXSSFWorkbook.java:658)
         *   at org.apache.poi.xssf.streaming.SXSSFWorkbook.createSheet(SXSSFWorkbook.java:679)
         *   at org.dashbuilder.dataset.service.DataSetExportServicesImpl.dataSetToWorkbook(DataSetExportServicesImpl.java:194)
         *   at org.dashbuilder.dataset.service.DataSetExportServicesTest.exportToExcelWorksWhenDataSetHasNulls(DataSetExportServicesTest.java:41)
         *
         * To reproduce locally, use -Djava.awt.headless and set DISPLAY to a non-existent display number, e.g. :12345.
         *
         * Headless mode doesn't seem to affect the SXSSFWorkbook behavior. SheetUtil.getDefaultCharWidth() returns 0 in
         * both modes.
         */
        originalJavaAwtHeadless = System.getProperty(JAVA_AWT_HEADLESS_PROPERTY);
        System.setProperty(JAVA_AWT_HEADLESS_PROPERTY, "true");
    }

    @AfterClass
    public static void tearDown() {
        if (originalJavaAwtHeadless == null) {
            System.clearProperty(JAVA_AWT_HEADLESS_PROPERTY);
        } else {
            System.setProperty(JAVA_AWT_HEADLESS_PROPERTY, originalJavaAwtHeadless);
        }
    }

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
