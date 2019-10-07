/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.backend.server.indexing;

import java.util.Collection;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.backend.util.DataUtilities;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;

public class GuidedDecisionTableFactory {

    public static GuidedDecisionTable52 makeTableWithAttributeCol(final String packageName,
                                                                  final Collection<Import> imports,
                                                                  final String tableName) {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setPackageName(packageName);
        dt.getImports().getImports().addAll(imports);
        dt.setTableName(tableName);

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute(Attribute.RULEFLOW_GROUP.getAttributeName());
        dt.getAttributeCols().add(attr);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "myRuleFlowGroup"}
        }));

        return dt;
    }

    public static GuidedDecisionTable52 makeTableWithConditionCol(final String packageName,
                                                                  final Collection<Import> imports,
                                                                  final String tableName) {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setPackageName(packageName);
        dt.getImports().getImports().addAll(imports);
        dt.setTableName(tableName);

        Pattern52 p1 = new Pattern52();
        p1.setBoundName("$a");
        p1.setFactType("Applicant");

        ConditionCol52 con1 = new ConditionCol52();
        con1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con1.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        con1.setFactField("age");
        con1.setHeader("Applicant age");
        con1.setOperator("==");
        p1.getChildColumns().add(con1);

        dt.getConditions().add(p1);

        Pattern52 p2 = new Pattern52();
        p2.setBoundName("$m");
        p2.setFactType("Mortgage");

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        con2.setFieldType(DataType.TYPE_NUMERIC_INTEGER);
        con2.setFactField("amount");
        con2.setHeader("Mortgage amount");
        con2.setOperator("==");
        p2.getChildColumns().add(con2);

        dt.getConditions().add(p2);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "33", ""}
        }));

        return dt;
    }

    public static GuidedDecisionTable52 makeTableWithActionCol(final String packageName,
                                                               final Collection<Import> imports,
                                                               final String tableName) {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setPackageName(packageName);
        dt.getImports().getImports().addAll(imports);
        dt.setTableName(tableName);

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName("$i");
        ins.setFactType("Applicant");
        ins.setFactField("age");
        ins.setType(DataType.TYPE_NUMERIC_INTEGER);
        dt.getActionCols().add(ins);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "33"}
        }));

        return dt;
    }

    public static GuidedDecisionTable52 makeTableWithBRLFragmentConditionCol(final String packageName,
                                                                             final Collection<Import> imports,
                                                                             final String tableName) {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setPackageName(packageName);
        dt.getImports().getImports().addAll(imports);
        dt.setTableName(tableName);

        final BRLConditionColumn brl = new BRLConditionColumn();

        final FactPattern fp1 = new FactPattern();
        fp1.setFactType("Applicant");
        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setFactType("Applicant");
        sfc1.setOperator("==");
        sfc1.setFieldName("age");
        sfc1.setValue("f1");
        fp1.addConstraint(sfc1);

        final FactPattern fp2 = new FactPattern();
        fp2.setFactType("Mortgage");
        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setFactType("Mortgage");
        sfc2.setOperator("==");
        sfc2.setFieldName("amount");
        sfc2.setValue("f2");
        fp2.addConstraint(sfc2);

        brl.getDefinition().add(fp1);
        brl.getDefinition().add(fp2);
        brl.getChildColumns().add(new BRLConditionVariableColumn("f1",
                                                                 DataType.TYPE_NUMERIC_INTEGER));
        brl.getChildColumns().add(new BRLConditionVariableColumn("f2",
                                                                 DataType.TYPE_NUMERIC_INTEGER));
        dt.getConditions().add(brl);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "33", ""}
        }));

        return dt;
    }

    public static GuidedDecisionTable52 makeTableWithBRLFragmentConditionColWithPredicate(final String packageName,
                                                                                          final Collection<Import> imports,
                                                                                          final String tableName) {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setPackageName(packageName);
        dt.getImports().getImports().addAll(imports);
        dt.setTableName(tableName);

        final BRLConditionColumn brl = new BRLConditionColumn();

        final FactPattern fp1 = new FactPattern();
        fp1.setFactType("Applicant");
        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_PREDICATE);
        sfc1.setValue("age = 45");
        fp1.addConstraint(sfc1);

        brl.getDefinition().add(fp1);
        brl.getChildColumns().add(new BRLConditionVariableColumn("f1",
                                                                 DataType.TYPE_BOOLEAN));
        dt.getConditions().add(brl);

        dt.setData(DataUtilities.makeDataLists(new Object[][]{
                new Object[]{"1", "desc", true}
        }));

        return dt;
    }

    public static GuidedDecisionTable52 makeTableWithBRLFragmentActionCol(final String packageName,
                                                                          final Collection<Import> imports,
                                                                          final String tableName) {
        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setPackageName(packageName);
        dt.getImports().getImports().addAll(imports);
        dt.setTableName(tableName);

        final BRLActionColumn brl = new BRLActionColumn();

        final ActionInsertFact ifc1 = new ActionInsertFact();
        ifc1.setFactType("Applicant");
        ifc1.setBoundName("$a");
        final ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("age");
        afv1.setValue("f1");
        ifc1.addFieldValue(afv1);

        final ActionInsertFact ifc2 = new ActionInsertFact();
        ifc2.setFactType("Mortgage");
        ifc2.setBoundName("$m");
        final ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv2.setField("amount");
        afv2.setValue("f2");
        ifc2.addFieldValue(afv2);

        final ActionSetField asf = new ActionSetField();
        asf.setVariable("$a");
        asf.addFieldValue(new ActionFieldValue("age",
                                               "33",
                                               DataType.TYPE_NUMERIC_INTEGER));

        final ActionUpdateField auf = new ActionUpdateField();
        asf.setVariable("$m");
        asf.addFieldValue(new ActionFieldValue("amount",
                                               "10000",
                                               DataType.TYPE_NUMERIC_INTEGER));

        brl.getDefinition().add(ifc1);
        brl.getDefinition().add(ifc2);
        brl.getChildColumns().add(new BRLActionVariableColumn("f1",
                                                              DataType.TYPE_NUMERIC_INTEGER));
        brl.getChildColumns().add(new BRLActionVariableColumn("f2",
                                                              DataType.TYPE_NUMERIC_INTEGER));
        brl.getDefinition().add(asf);
        brl.getDefinition().add(auf);

        dt.getConditions().add(brl);

        dt.setData(DataUtilities.makeDataLists(new String[][]{
                new String[]{"1", "desc", "33", ""}
        }));

        return dt;
    }
}
