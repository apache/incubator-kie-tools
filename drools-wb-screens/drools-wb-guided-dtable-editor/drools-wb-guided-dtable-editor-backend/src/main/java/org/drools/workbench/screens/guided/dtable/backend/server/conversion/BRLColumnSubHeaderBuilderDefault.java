/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTDRLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.ColumnContext;

/**
 * Makes a single column out of the BRL column(s)
 */
public class BRLColumnSubHeaderBuilderDefault
        implements BRLColumnSubHeaderBuilder {

    private SubHeaderBuilder subHeaderBuilder;
    private ColumnContext columnContext;
    private final GuidedDecisionTable52 dtable;
    private final Map<String, String> varListInOrder = new HashMap<>();// original, new one

    public BRLColumnSubHeaderBuilderDefault(final SubHeaderBuilder subHeaderBuilder,
                                            final ColumnContext columnContext,
                                            final GuidedDecisionTable52 dtable) {
        this.subHeaderBuilder = subHeaderBuilder;
        this.columnContext = columnContext;
        this.dtable = dtable;
    }

    @Override
    public void buildBrlActions(final BRLActionColumn brlColumn) {

        subHeaderBuilder.addHeaderAndTitle(SubHeaderBuilder.ACTION,
                                           brlColumn.getHeader());
        subHeaderBuilder.getFieldRow().createCell(subHeaderBuilder.getTargetColumnIndex()).setCellValue(replaceTempVars(brlColumn.getChildColumns(),
                                                                                                                        makeRHSDrl(brlColumn)));
    }

    @Override
    public void buildBrlConditions(final BRLConditionColumn brlColumn) {

        for (IPattern iPattern : brlColumn.getDefinition()) {
            if (iPattern instanceof FactPattern) {
                columnContext.addBoundName(((FactPattern) iPattern).getBoundName());
            }
        }

        subHeaderBuilder.addHeaderAndTitle(SubHeaderBuilder.CONDITION,
                                           brlColumn.getHeader());
        subHeaderBuilder.getFieldRow().createCell(subHeaderBuilder.getTargetColumnIndex()).setCellValue(replaceTempVars(brlColumn.getChildColumns(),
                                                                                                                        makeLHSDrl(brlColumn)));
    }

    private String makeRHSDrl(final BRLActionColumn brlColumn) {
        return subString(GuidedDTDRLPersistence.getInstance().marshal(makeTempFullGuidedDecisionTable(brlColumn)), "then", "end");
    }

    private void updateBoundNames(final BRLActionColumn brlColumn) {
        for (final IAction iAction : brlColumn.getDefinition()) {
            if (iAction instanceof ActionInsertFact) {
                ActionInsertFact insertFact = (ActionInsertFact) iAction;
                if (insertFact.getBoundName() == null) {
                    insertFact.setBoundName(getBoundName(insertFact));
                }
            }
        }
    }

    private String getBoundName(final ActionInsertFact insertFact) {
        if (StringUtils.isNotEmpty(insertFact.getBoundName())) {
            return insertFact.getBoundName();
        } else {
            return columnContext.getNextFreeColumnFactName();
        }
    }

    private String makeLHSDrl(final BRLConditionColumn brlColumn) {
        return subString(GuidedDTDRLPersistence.getInstance().marshal(makeTempLHSGuidedDecisionTable(brlColumn)), "when", "then");
    }

    private GuidedDecisionTable52 makeTempFullGuidedDecisionTable(final BRLActionColumn brlColumn) {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.getAttributeCols().addAll(dtable.getAttributeCols());
        dt.getMetadataCols().addAll(dtable.getMetadataCols());
        dt.getConditions().addAll(dtable.getConditions());
        final ArrayList<DTCellValue52> list = new ArrayList<>();

        updateBoundNames(brlColumn);

        for (int i = 0; i < dt.getExpandedColumns().size(); i++) {
            list.add(dtable.getData().get(0).get(i));
        }

        list.addAll(setUpVarNamesWithTemps(brlColumn.getChildColumns()));
        dt.getData().add(list);

        dt.getActionCols().add(brlColumn);
        return dt;
    }

    private GuidedDecisionTable52 makeTempLHSGuidedDecisionTable(final BRLConditionColumn brlColumn) {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.getConditions().add(brlColumn);
        final ArrayList<DTCellValue52> list = new ArrayList<>();
        list.add(new DTCellValue52(1)); // Row number
        list.add(new DTCellValue52("")); // Rule Name
        list.add(new DTCellValue52("")); // Description

        list.addAll(setUpVarNamesWithTemps(brlColumn.getChildColumns()));
        dt.getData().add(list);
        return dt;
    }

    private List<DTCellValue52> setUpVarNamesWithTemps(final List<? extends BRLVariableColumn> childColumns) {
        final List<DTCellValue52> result = new ArrayList<>();

        for (int i = 0; i < childColumns.size(); i++) {
            final BRLVariableColumn brlConditionVariableColumn = childColumns.get(i);
            columnContext.addBoundName(brlConditionVariableColumn.getVarName());
            if (StringUtils.isEmpty(brlConditionVariableColumn.getVarName())) {
                result.add(new DTCellValue52(true));
            } else {
                String varName = brlConditionVariableColumn.getVarName();
                String key = "";
                if (!varName.startsWith("$")) {
                    key = varName;
                    varName = "@{" + varName + "}";
                } else {
                    key = Integer.toString(i);
                    varName = "@{" + i + "}";
                }
                varListInOrder.put(brlConditionVariableColumn.getVarName(), key);
                result.add(new DTCellValue52(varName));
            }
        }

        return result;
    }

    private String replaceTempVars(final List<? extends BRLVariableColumn> childColumns,
                                   final String drl) {
        int varIndex = 1;
        String result = drl;
        for (BRLVariableColumn childColumn : childColumns) {

            final String var = varListInOrder.get(childColumn.getVarName());

            final Pattern pattern = Pattern.compile("@\\{(" + var + ")}");
            final Matcher matcher = pattern.matcher(result);
            result = matcher.replaceAll("\\$" + varIndex++);
        }
        return result;
    }

    private String subString(final String marshal,
                             final String from,
                             final String to) {

        final Pattern regex = Pattern.compile("\\s*" + from + "\\s*(.*)" + to, Pattern.DOTALL);
        final Matcher regexMatcher = regex.matcher(marshal);
        if (regexMatcher.find()) {
            return regexMatcher.group(1);
        } else {
            throw new IllegalStateException(String.format("No substring between '%s' and '%s' found",
                                                          from,
                                                          to));
        }
    }
}
