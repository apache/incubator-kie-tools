/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResult;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

public class XLSBuilder {

    private static final int RULE_SET_ROW = 1;
    private static final int IMPORTS_ROW = 2;
    private static final int RULE_TABLE_ROW = 4;
    private final GuidedDecisionTable52 dtable;
    private final Sheet sheet;
    private final Workbook workbook;
    private final PackageDataModelOracle dmo;

    public XLSBuilder(final GuidedDecisionTable52 dtable,
                      final PackageDataModelOracle dmo) {

        this.dtable = PortablePreconditions.checkNotNull("dtable", dtable);
        this.dmo = PortablePreconditions.checkNotNull("dmo", dmo);
        this.workbook = new HSSFWorkbook();
        this.sheet = workbook.createSheet("Hello");
    }

    public BuildResult build() {
        try {

            makeRuleSet();
            makeImports();

            makeTable();
        } catch (final Exception e) {
            return new BuildResult(workbook,
                                   new XLSConversionResult(e.getMessage()));
        }
        return new BuildResult(workbook,
                               new XLSConversionResult());
    }

    private void makeRuleSet() {
        final Row headerRow = sheet.createRow(RULE_SET_ROW);
        final Cell ruleSetCellLabel = headerRow.createCell(1);
        ruleSetCellLabel.setCellValue("RuleSet");

        final Cell ruleSetCell = headerRow.createCell(2);
        ruleSetCell.setCellValue(dtable.getPackageName());
    }

    private void makeImports() {
        final Row headerRow = sheet.createRow(IMPORTS_ROW);
        headerRow.createCell(1)
                .setCellValue("Import");

        headerRow.createCell(2)
                .setCellValue(dtable.getImports().getImports()
                                      .stream()
                                      .map(anImport -> anImport.getType())
                                      .collect(Collectors.joining(", ")));
    }

    private void makeTable() {

        makeTableHeader();
        makeTableSubHeader();
        makePatternRow();
        makeDTableColumns();
    }

    private void makeTableHeader() {
        final Row headerRow = sheet.createRow(RULE_TABLE_ROW);

        final Cell cell = headerRow.createCell(1);
        cell.setCellValue("RuleTable " + dtable.getTableName());
    }

    private void makeTableSubHeader() {
        new SubHeaderBuilder(sheet, dtable).build();
    }

    private void makePatternRow() {
        new PatternRowBuilder(sheet, dtable).build();
    }

    private void makeDTableColumns() {
        new DataBuilder(sheet, dtable, dmo).build();
    }

    class BuildResult {

        private final Workbook workbook;
        private final XLSConversionResult conversionResult;

        BuildResult(final Workbook workbook,
                    final XLSConversionResult conversionResult) {
            this.workbook = workbook;
            this.conversionResult = conversionResult;
        }

        public Workbook getWorkbook() {
            return workbook;
        }

        public XLSConversionResult getConversionResult() {
            return conversionResult;
        }
    }
}
