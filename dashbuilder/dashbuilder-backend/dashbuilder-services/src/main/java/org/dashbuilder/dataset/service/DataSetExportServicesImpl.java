/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetDefRegistryCDI;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetManagerCDI;
import org.dashbuilder.dataset.group.Interval;
import org.dashbuilder.dataset.uuid.UUIDGenerator;
import org.dashbuilder.exception.ExceptionManager;
import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Service
public class DataSetExportServicesImpl implements DataSetExportServices {

    private static final String TEXT_CELL = "text_cell";
    protected static Logger log = LoggerFactory.getLogger(DataSetExportServicesImpl.class);
    protected DataSetManagerCDI dataSetManager;
    protected DataSetDefRegistryCDI gitStorage;
    protected UUIDGenerator uuidGenerator;
    protected ExceptionManager exceptionManager;

    protected String DEFAULT_SEPARATOR_CHAR = ";";
    protected String DEFAULT_QUOTE_CHAR = "\"";
    protected String DEFAULT_ESCAPE_CHAR = "\\";

    protected String dateFormatPattern = "dd/MM/yyyy HH:mm:ss";
    protected String numberFormatPattern = "#,###.##########";

    protected DecimalFormat decf = new DecimalFormat(numberFormatPattern);
    protected DateFormat datef = new SimpleDateFormat(dateFormatPattern);

    public DataSetExportServicesImpl() {
    }

    @Inject
    public DataSetExportServicesImpl(DataSetManagerCDI dataSetManager,
                                     DataSetDefRegistryCDI gitStorage,
                                     ExceptionManager exceptionManager) {
        this.dataSetManager = dataSetManager;
        this.gitStorage = gitStorage;
        this.uuidGenerator = DataSetCore.get().getUuidGenerator();
        this.exceptionManager = exceptionManager;
    }

    public org.uberfire.backend.vfs.Path exportDataSetCSV(DataSetLookup lookup) {
        DataSet dataSet = dataSetManager.lookupDataSet(lookup);
        return exportDataSetCSV(dataSet);
    }

    public org.uberfire.backend.vfs.Path exportDataSetCSV(DataSet dataSet) {
        try {
            if (dataSet == null) {
                throw new IllegalArgumentException("Null dataSet specified!");
            }
            int columnCount = dataSet.getColumns().size();
            int rowCount = dataSet.getRowCount();

            List<String[]> lines = new ArrayList<>(rowCount+1);

            String[] line = new String[columnCount];
            for (int cc = 0; cc < columnCount; cc++) {
                DataColumn dc = dataSet.getColumnByIndex(cc);
                line[cc] = dc.getId();
            }
            lines.add(line);

            for (int rc = 0; rc < rowCount; rc++) {
                line = new String[columnCount];
                for (int cc = 0; cc < columnCount; cc++) {
                    line[cc] = formatAsString(dataSet.getValueAt(rc, cc));
                }
                lines.add(line);
            }

            String tempCsvFile = uuidGenerator.newUuid() + ".csv";
            Path tempCsvPath = gitStorage.createTempFile(tempCsvFile);

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(tempCsvPath)));
                 CSVWriter writer = new CSVWriter(bw,
                                                  DEFAULT_SEPARATOR_CHAR.charAt(0),
                                                  DEFAULT_QUOTE_CHAR.charAt(0),
                                                  DEFAULT_ESCAPE_CHAR.charAt(0))
                 ) {
                     writer.writeAll(lines);
                     writer.flush();
            }

            return Paths.convert(tempCsvPath);
        }
        catch (Exception e) {
            throw exceptionManager.handleException(e);
        }
    }

    @Override
    public org.uberfire.backend.vfs.Path exportDataSetExcel(DataSetLookup dataSetLookup) {
        DataSet dataSet = dataSetManager.lookupDataSet( dataSetLookup );
        return exportDataSetExcel( dataSet );
    }

    @Override
    public org.uberfire.backend.vfs.Path exportDataSetExcel(DataSet dataSet) {
        try {
            SXSSFWorkbook wb = dataSetToWorkbook(dataSet);

            // Write workbook to Path
            String tempXlsFile = uuidGenerator.newUuid() + ".xlsx";
            Path tempXlsPath = gitStorage.createTempFile(tempXlsFile);
            try (OutputStream os = Files.newOutputStream(tempXlsPath)) {
                wb.write(os);
                os.flush();
            }

            // Dispose of temporary files backing this workbook on disk
            if (!wb.dispose()) {
                log.warn("Could not dispose of temporary file associated to data export!");
            }
            return Paths.convert(tempXlsPath);
        } catch (Exception e) {
            throw exceptionManager.handleException(e);
        }
    }

    //Package private to enable testing
    SXSSFWorkbook dataSetToWorkbook(DataSet dataSet) {
        // TODO?: Excel 2010 limits: 1,048,576 rows by 16,384 columns; row width 255 characters
        if (dataSet == null) {
            throw new IllegalArgumentException("Null dataSet specified!");
        }
        int columnCount = dataSet.getColumns().size();
        int rowCount = dataSet.getRowCount() + 1; //Include header row;
        int row = 0;

        SXSSFWorkbook wb = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
        Map<String, CellStyle> styles = createStyles(wb);
        SXSSFSheet sh = wb.createSheet("Sheet 1");

        // General setup
        sh.setDisplayGridlines(true);
        sh.setPrintGridlines(false);
        sh.setFitToPage(true);
        sh.setHorizontallyCenter(true);
        sh.trackAllColumnsForAutoSizing();
        PrintSetup printSetup = sh.getPrintSetup();
        printSetup.setLandscape(true);

        // Create header
        Row header = sh.createRow(row++);
        header.setHeightInPoints(20f);
        for (int i = 0; i < columnCount; i++) {
            Cell cell = header.createCell(i);
            cell.setCellStyle(styles.get("header"));
            cell.setCellValue(dataSet.getColumnByIndex(i).getId());
        }

        // Create data rows
        for (; row < rowCount; row++) {
            Row _row = sh.createRow(row);
            for (int cellnum = 0; cellnum < columnCount; cellnum++) {
                Cell cell = _row.createCell(cellnum);
                Object value = dataSet.getValueAt(row - 1,
                                                  cellnum);
                if (value instanceof Short || value instanceof Long || value instanceof Integer || value instanceof BigInteger) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellStyle(styles.get("integer_number_cell"));
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellStyle(styles.get("decimal_number_cell"));
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Date) {
                    cell.setCellType(CellType.STRING);
                    cell.setCellStyle(styles.get("date_cell"));
                    cell.setCellValue((Date) value);
                } else if (value instanceof Interval) {
                    cell.setCellType(CellType.STRING);
                    cell.setCellStyle(styles.get(TEXT_CELL));
                    cell.setCellValue(((Interval) value).getName());
                } else {
                    cell.setCellType(CellType.STRING);
                    cell.setCellStyle(styles.get(TEXT_CELL));
                    String val = value == null ? "" : value.toString();
                    cell.setCellValue(val);
                }
            }
        }

        // Adjust column size
        for (int i = 0; i < columnCount; i++) {
            sh.autoSizeColumn(i);
        }
        return wb;
    }

    private String formatAsString(Object value) {
        if (value == null) return "";
        if (value instanceof Number) return decf.format(value);
        else if (value instanceof Date) return datef.format(value);
        // TODO verify if this is correct
        else if (value instanceof Interval) return ((Interval)value).getName();
        else return value.toString();
    }

    private Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style;

        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)12);
        titleFont.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor( IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(titleFont);
        style.setWrapText(false);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
        styles.put("header", style);

        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short)10);
        cellFont.setBold(true);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        style.setFont(cellFont);
        style.setWrapText(false);
        style.setDataFormat(wb.createDataFormat().getFormat( BuiltinFormats.getBuiltinFormat( 3 )));
        styles.put("integer_number_cell", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        style.setFont(cellFont);
        style.setWrapText(false);
        style.setDataFormat(wb.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(4)));
        styles.put("decimal_number_cell", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        style.setFont(cellFont);
        style.setWrapText(false);
        style.setDataFormat( (short) BuiltinFormats.getBuiltinFormat("text") );
        styles.put(TEXT_CELL, style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        style.setFont(cellFont);
        style.setWrapText(false);
        style.setDataFormat(wb.createDataFormat().getFormat( DateFormatConverter.convert( Locale.getDefault(), dateFormatPattern )));
        styles.put("date_cell", style);
        return styles;
    }
}
