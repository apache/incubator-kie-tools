/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.decisiontable.parser.ActionType.Code;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.template.model.SnippetBuilder;
import org.drools.template.model.SnippetBuilder.SnippetType;
import org.drools.template.parser.DecisionTableParseException;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessageType;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.kie.soup.project.datamodel.oracle.DataType;

/**
 * Builder for Action columns
 */
public class GuidedDecisionTableRHSBuilder
        implements
        HasColumnHeadings,
        GuidedDecisionTableSourceBuilderIndirect {

    private final int headerRow;
    private final int headerCol;
    private final String variable;

    private final List<String> drlFragments = new ArrayList<String>();

    //Map of column headers, keyed on XLS column index
    private final Map<Integer, String> columnHeaders = new HashMap<Integer, String>();

    //Map of column value parsers, keyed on XLS column index
    private final Map<Integer, ParameterizedValueBuilder> valueBuilders = new HashMap<Integer, ParameterizedValueBuilder>();

    private List<GuidedDecisionTableSourceBuilder> sourceBuilders;

    //Utility class to convert XLS parameters to BRLFragment Template keys
    private final ParameterUtilities parameterUtilities;

    private ConversionResult conversionResult;

    public GuidedDecisionTableRHSBuilder(final int row,
                                         final int column,
                                         final String boundVariable,
                                         final List<GuidedDecisionTableSourceBuilder> sourceBuilders,
                                         final ParameterUtilities parameterUtilities,
                                         final ConversionResult conversionResult) {
        this.headerRow = row;
        this.headerCol = column;
        this.variable = boundVariable == null ? "" : boundVariable.trim();
        this.sourceBuilders = sourceBuilders;
        this.parameterUtilities = parameterUtilities;
        this.conversionResult = conversionResult;
    }

    @Override
    public List<BRLVariableColumn> getVariableColumns() {
        //Sort column builders by column index to ensure columns are added in the correct sequence
        final Set<Integer> sortedIndexes = new TreeSet<Integer>(this.valueBuilders.keySet());
        final List<BRLVariableColumn> variableColumns = new ArrayList<BRLVariableColumn>();
        for (Integer index : sortedIndexes) {
            final ParameterizedValueBuilder vb = this.valueBuilders.get(index);
            final List<BRLVariableColumn> vbVariableColumns = addColumn(vb);
            for (BRLVariableColumn vbVariableColumn : vbVariableColumns) {
                ((BRLActionVariableColumn) vbVariableColumn).setHeader(this.columnHeaders.get(index));
            }
            variableColumns.addAll(vbVariableColumns);
        }
        return variableColumns;
    }

    @Override
    public Map<Integer, ParameterizedValueBuilder> getValueBuilders() {
        return this.valueBuilders;
    }

    private List<BRLVariableColumn> addColumn(final ParameterizedValueBuilder vb) {
        if (vb instanceof LiteralValueBuilder) {
            return addLiteralColumn((LiteralValueBuilder) vb);
        } else {
            return addBRLFragmentColumn(vb);
        }
    }

    private List<BRLVariableColumn> addLiteralColumn(final LiteralValueBuilder vb) {
        final List<BRLVariableColumn> variableColumns = new ArrayList<BRLVariableColumn>();
        final BRLActionVariableColumn parameterColumn = new BRLActionVariableColumn("",
                                                                                    DataType.TYPE_BOOLEAN);
        variableColumns.add(parameterColumn);

        //Store DRL fragment for use by GuidedDecisionTableRHSBuilder
        drlFragments.add(vb.getTemplate());
        return variableColumns;
    }

    private List<BRLVariableColumn> addBRLFragmentColumn(final ParameterizedValueBuilder vb) {
        final List<BRLVariableColumn> variableColumns = new ArrayList<BRLVariableColumn>();
        for (String parameter : vb.getParameters()) {
            final BRLActionVariableColumn parameterColumn = new BRLActionVariableColumn(parameter,
                                                                                        DataType.TYPE_OBJECT);
            variableColumns.add(parameterColumn);
        }

        //Store DRL fragment for use by GuidedDecisionTableRHSBuilder
        drlFragments.add(vb.getTemplate());
        return variableColumns;
    }

    @Override
    public void addTemplate(final int row,
                            final int column,
                            final String content) {
        //Validate column template
        if (valueBuilders.containsKey(column)) {
            final String message = "Internal error: Can't have a code snippet added twice to one spreadsheet column.";
            this.conversionResult.addMessage(message,
                                             ConversionMessageType.ERROR);
            return;
        }

        //Add new template
        String template = content.trim();
        if (isBoundVar()) {
            template = variable + "." + template;
        }
        if (!template.endsWith(";")) {
            template = template + ";";
        }
        try {
            this.valueBuilders.put(column,
                                   getValueBuilder(template));
        } catch (DecisionTableParseException pe) {
            this.conversionResult.addMessage(pe.getMessage(),
                                             ConversionMessageType.WARNING);
        }
    }

    private boolean isBoundVar() {
        return !("".equals(variable));
    }

    @Override
    public void setColumnHeader(final int column,
                                final String value) {
        this.columnHeaders.put(column,
                               value.trim());
    }

    private ParameterizedValueBuilder getValueBuilder(final String template) {
        final SnippetType type = SnippetBuilder.getType(template);
        switch (type) {
            case INDEXED:
                return new IndexedParametersValueBuilder(template,
                                                         parameterUtilities,
                                                         ParameterizedValueBuilder.Part.RHS);
            case PARAM:
                return new SingleParameterValueBuilder(template,
                                                       parameterUtilities,
                                                       ParameterizedValueBuilder.Part.RHS);
            case SINGLE:
                return new LiteralValueBuilder(template);
        }
        throw new DecisionTableParseException("SnippetBuilder.SnippetType '" + type.toString() + "' is not supported. The column will not be added.");
    }

    @Override
    public void addCellValue(final int row,
                             final int column,
                             final String value) {
        //Add new row to column data
        final ParameterizedValueBuilder vb = this.valueBuilders.get(column);
        if (vb == null) {
            final String message = "No code snippet for ACTION, above cell " +
                    RuleSheetParserUtil.rc2name(this.headerRow + 2,
                                                this.headerCol);
            this.conversionResult.addMessage(message,
                                             ConversionMessageType.ERROR);
            return;
        }
        vb.addCellValue(row,
                        column,
                        value);
    }

    @Override
    public String getResult() {
        final StringBuilder sb = new StringBuilder();
        for (String drlFragment : drlFragments) {
            sb.append(drlFragment).append("\n");
        }
        return sb.toString();
    }

    @Override
    public Code getActionTypeCode() {
        return Code.ACTION;
    }

    @Override
    public void clearValues() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasValues() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRowCount() {
        int maxRowCount = 0;
        for (ParameterizedValueBuilder pvb : valueBuilders.values()) {
            maxRowCount = Math.max(maxRowCount,
                                   pvb.getColumnData().size());
        }
        return maxRowCount;
    }

    @Override
    public int getColumn() {
        return headerCol;
    }
}
