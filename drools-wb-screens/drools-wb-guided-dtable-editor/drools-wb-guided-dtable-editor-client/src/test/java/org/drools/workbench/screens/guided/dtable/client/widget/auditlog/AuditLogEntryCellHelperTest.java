/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.auditlog;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.workitems.PortableStringParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.auditlog.UpdateColumnAuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.WorkItemColumnParameterValueDiffImpl;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static java.lang.String.format;
import static org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52.FIELD_CONSTRAINT_VALUE_TYPE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AuditLogEntryCellHelperTest {

    private static Map<String, String> fieldValues;

    @Mock
    private DateTimeFormat format;
    private String labelClass = "mockCssClassForLabel";
    private AuditLogEntryCellHelper helper;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        when(format.format(any(Date.class))).thenReturn("mockDateString");
        // This also mock AuditLogEntryCellHelper.Template, which then for each method call returns "<method name>(<call arguments>)
        // instead of the "templated" String. Calling SafeHtml.asString() then gives us something like:
        // commentHeader(<new header>)updatedFields(updatedField(<field description>, <old value>, <new value>)...)
        // From that we can assert that all diffs were properly used and displayed.
        helper = new AuditLogEntryCellHelper(format, labelClass, "mockCssClassForValue");

        if (fieldValues == null) {
            fieldValues = new HashMap<>();
            fieldValues.put(DTColumnConfig52.FIELD_HEADER, GuidedDecisionTableConstants.INSTANCE.ColumnHeader());
            fieldValues.put(DTColumnConfig52.FIELD_HIDE_COLUMN, GuidedDecisionTableConstants.INSTANCE.HideThisColumn());
            fieldValues.put(DTColumnConfig52.FIELD_DEFAULT_VALUE, GuidedDecisionTableConstants.INSTANCE.DefaultValue());
            fieldValues.put(MetadataCol52.FIELD_METADATA, GuidedDecisionTableConstants.INSTANCE.Metadata1());
            fieldValues.put(AttributeCol52.FIELD_REVERSE_ORDER, GuidedDecisionTableConstants.INSTANCE.ReverseOrder());
            fieldValues.put(AttributeCol52.FIELD_USE_ROW_NUMBER, GuidedDecisionTableConstants.INSTANCE.UseRowNumber());
            fieldValues.put(Pattern52.FIELD_ENTRY_POINT_NAME, GuidedDecisionTableConstants.INSTANCE.DTLabelFromEntryPoint());
            fieldValues.put(ConditionCol52.FIELD_BINDING, GuidedDecisionTableConstants.INSTANCE.Binding());
            fieldValues.put(ConditionCol52.FIELD_CONSTRAINT_VALUE_TYPE, GuidedDecisionTableConstants.INSTANCE.CalculationType());
            fieldValues.put(ConditionCol52.FIELD_OPERATOR, GuidedDecisionTableConstants.INSTANCE.Operator());
            fieldValues.put(ConditionCol52.FIELD_FIELD_TYPE, GuidedDecisionTableConstants.INSTANCE.FieldType());
            fieldValues.put(ActionSetFieldCol52.FIELD_TYPE, GuidedDecisionTableConstants.INSTANCE.FieldType());
            fieldValues.put(ActionSetFieldCol52.FIELD_UPDATE, GuidedDecisionTableConstants.INSTANCE.UpdateEngineWithChanges());
            fieldValues.put(ActionInsertFactCol52.FIELD_IS_INSERT_LOGICAL, GuidedDecisionTableConstants.INSTANCE.LogicallyInsert());
            fieldValues.put(ActionWorkItemCol52.FIELD_WORKITEM_DEFINITION_NAME, GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemName());
            fieldValues.put(ActionWorkItemCol52.FIELD_WORKITEM_DEFINITION_PARAMETER_NAME, GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterName());
            fieldValues.put(Pattern52.FIELD_FACT_TYPE, GuidedDecisionTableConstants.INSTANCE.FactType());
            fieldValues.put(ActionInsertFactCol52.FIELD_FACT_TYPE, GuidedDecisionTableConstants.INSTANCE.FactType());
            fieldValues.put(Pattern52.FIELD_BOUND_NAME, GuidedDecisionTableConstants.INSTANCE.Binding());
            fieldValues.put(ActionInsertFactCol52.FIELD_BOUND_NAME, GuidedDecisionTableConstants.INSTANCE.Binding());
            fieldValues.put(ActionSetFieldCol52.FIELD_BOUND_NAME, GuidedDecisionTableConstants.INSTANCE.Binding());
            fieldValues.put(ConditionCol52.FIELD_FACT_FIELD, GuidedDecisionTableConstants.INSTANCE.Field());
            fieldValues.put(ActionInsertFactCol52.FIELD_FACT_FIELD, GuidedDecisionTableConstants.INSTANCE.Field());
            fieldValues.put(ActionSetFieldCol52.FIELD_FACT_FIELD, GuidedDecisionTableConstants.INSTANCE.Field());
            fieldValues.put(ConditionCol52.FIELD_VALUE_LIST, GuidedDecisionTableConstants.INSTANCE.ValueList());
            fieldValues.put(ActionInsertFactCol52.FIELD_VALUE_LIST, GuidedDecisionTableConstants.INSTANCE.ValueList());
            fieldValues.put(ActionSetFieldCol52.FIELD_VALUE_LIST, GuidedDecisionTableConstants.INSTANCE.ValueList());
            fieldValues.put(ActionWorkItemInsertFactCol52.FIELD_PARAMETER_CLASSNAME, GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterClassName());
            fieldValues.put(ActionWorkItemSetFieldCol52.FIELD_PARAMETER_CLASSNAME, GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterClassName());
            fieldValues.put(ActionWorkItemInsertFactCol52.FIELD_WORK_ITEM_NAME, GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemName());
            fieldValues.put(ActionWorkItemSetFieldCol52.FIELD_WORK_ITEM_NAME, GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemName());
            fieldValues.put(ActionWorkItemInsertFactCol52.FIELD_WORK_ITEM_RESULT_PARAM_NAME, GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterName());
            fieldValues.put(ActionWorkItemSetFieldCol52.FIELD_WORK_ITEM_RESULT_PARAM_NAME, GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterName());
            fieldValues.put(LimitedEntryActionInsertFactCol52.FIELD_VALUE, GuidedDecisionTableConstants.INSTANCE.Value());
            fieldValues.put(LimitedEntryActionSetFieldCol52.FIELD_VALUE, GuidedDecisionTableConstants.INSTANCE.Value());
            fieldValues.put(LimitedEntryConditionCol52.FIELD_VALUE, GuidedDecisionTableConstants.INSTANCE.Value());
        }
    }

    @Test
    public void convertValueToString() {
        assertEquals(null, helper.convertValueToString(null));
        assertEquals("true", helper.convertValueToString(true));
        assertEquals("false", helper.convertValueToString(false));
        Date date = new Date();
        assertEquals(format.format(date), helper.convertValueToString(date));
        assertEquals(BigDecimal.ONE.toPlainString(), helper.convertValueToString(BigDecimal.ONE));
        assertEquals(BigInteger.ONE.toString(), helper.convertValueToString(BigInteger.ONE));
        Byte b = Byte.MAX_VALUE;
        assertEquals(b.toString(), helper.convertValueToString(b));
        Double d = 123.456;
        assertEquals(d.toString(), helper.convertValueToString(d));
        Float f = new Float(123.456);
        assertEquals(f.toString(), helper.convertValueToString(f));
        Integer i = 123;
        assertEquals(i.toString(), helper.convertValueToString(i));
        Long l = new Long(123);
        assertEquals(l.toString(), helper.convertValueToString(l));
        Short s = 123;
        assertEquals(s.toString(), helper.convertValueToString(s));
        assertEquals("surprise!", helper.convertValueToString("surprise!"));
    }

    @Test
    public void getSafeHtml_Metadata() {
        MetadataCol52 originalColumn = new MetadataCol52();
        originalColumn.setHideColumn(true);
        originalColumn.setDefaultValue(new DTCellValue52("def1"));
        // header & metadata of a metadata column cannot be updated in the ui
        originalColumn.setMetadata("meta");
        originalColumn.setHeader("meta");

        MetadataCol52 newColumn = new MetadataCol52();
        newColumn.setHideColumn(false);
        newColumn.setDefaultValue(new DTCellValue52("def2"));
        // header & metadata of a metadata column cannot be updated in the ui
        newColumn.setMetadata("meta");
        newColumn.setHeader("meta");

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_Attribute() {
        AttributeCol52 originalColumn = new AttributeCol52();
        originalColumn.setReverseOrder(false);
        originalColumn.setUseRowNumber(false);
        originalColumn.setHideColumn(false);
        originalColumn.setDefaultValue(new DTCellValue52("def1"));
        // header & attribute name of an attribute column cannot be updated in the ui
        originalColumn.setAttribute(Attribute.SALIENCE.getAttributeName());
        originalColumn.setHeader(Attribute.SALIENCE.getAttributeName());

        AttributeCol52 newColumn = new AttributeCol52();
        newColumn.setReverseOrder(true);
        newColumn.setUseRowNumber(true);
        newColumn.setHideColumn(true);
        newColumn.setDefaultValue(new DTCellValue52("def2"));
        // header & attribute name of an attribute column cannot be updated in the ui
        newColumn.setAttribute(Attribute.SALIENCE.getAttributeName());
        newColumn.setHeader(Attribute.SALIENCE.getAttributeName());

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getAttributeHeaderRepre(newColumn.getAttribute()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_Condition() {
        ConditionCol52 originalColumn = new ConditionCol52();
        originalColumn.setBinding("bind1");
        originalColumn.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        originalColumn.setFactField("field1");
        originalColumn.setFieldType("FieldType1");
        originalColumn.setOperator("==");
        originalColumn.setValueList("a,b,c");
        originalColumn.setHeader("condition1");
        originalColumn.setHideColumn(false);
        originalColumn.setDefaultValue(new DTCellValue52("def1"));

        ConditionCol52 newColumn = new ConditionCol52();
        newColumn.setBinding("bind2");
        newColumn.setConstraintValueType(BaseSingleFieldConstraint.TYPE_PREDICATE);
        newColumn.setFactField("field2");
        newColumn.setFieldType("FieldType2");
        newColumn.setOperator("!=");
        newColumn.setValueList("x,y,z");
        newColumn.setHeader("condition2");
        newColumn.setHideColumn(true);
        newColumn.setDefaultValue(new DTCellValue52("def2"));

        Pattern52 originalPattern = new Pattern52();
        originalPattern.setBoundName("patBind1");
        originalPattern.setFactType("FactType1");
        originalPattern.setEntryPointName("ep1");

        Pattern52 newPattern = new Pattern52();
        newPattern.setBoundName("patBind2");
        newPattern.setFactType("FactType2");
        newPattern.setEntryPointName("ep2");

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);
        diffs.addAll(originalPattern.diff(newPattern));

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getConditionHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_ActionInsert() {
        //hide, insert, value list, default value, field, header
        ActionInsertFactCol52 originalColumn = new ActionInsertFactCol52();
        originalColumn.setFactField("field1");
        originalColumn.setInsertLogical(false);
        originalColumn.setValueList("q,w,e");
        originalColumn.setHeader("action1");
        originalColumn.setHideColumn(false);
        originalColumn.setDefaultValue(new DTCellValue52("def1"));

        ActionInsertFactCol52 newColumn = new ActionInsertFactCol52();
        newColumn.setFactField("field2");
        newColumn.setInsertLogical(true);
        newColumn.setValueList("a,s,d");
        newColumn.setHeader("action2");
        newColumn.setHideColumn(true);
        newColumn.setDefaultValue(new DTCellValue52("def2"));

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getActionHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_ActionSetField() {
        ActionSetFieldCol52 originalColumn = new ActionSetFieldCol52();
        originalColumn.setBoundName("bind1");
        originalColumn.setFactField("field1");
        originalColumn.setUpdate(false);
        originalColumn.setValueList("q,w,e");
        originalColumn.setHeader("action1");
        originalColumn.setHideColumn(false);
        originalColumn.setDefaultValue(new DTCellValue52("def1"));

        ActionSetFieldCol52 newColumn = new ActionSetFieldCol52();
        newColumn.setBoundName("bind2");
        newColumn.setFactField("field2");
        newColumn.setUpdate(true);
        newColumn.setValueList("a,s,d");
        newColumn.setHeader("action2");
        newColumn.setHideColumn(true);
        newColumn.setDefaultValue(new DTCellValue52("def2"));

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getActionHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_ActionRetract() {
        ActionRetractFactCol52 originalColumn = new ActionRetractFactCol52();
        originalColumn.setHeader("action1");
        originalColumn.setHideColumn(false);
        originalColumn.setDefaultValue(new DTCellValue52("def1"));

        ActionRetractFactCol52 newColumn = new ActionRetractFactCol52();
        newColumn.setHeader("action2");
        newColumn.setHideColumn(true);
        newColumn.setDefaultValue(new DTCellValue52("def2"));

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_ActionWorkItemExecute_Simple() {
        ActionWorkItemCol52 originalColumn = new ActionWorkItemCol52();
        originalColumn.setHeader("action1");
        originalColumn.setHideColumn(false);

        ActionWorkItemCol52 newColumn = new ActionWorkItemCol52();
        newColumn.setHeader("action2");
        newColumn.setHideColumn(true);

        PortableStringParameterDefinition param1 = new PortableStringParameterDefinition();
        param1.setName("param1");
        param1.setValue("value1");

        PortableWorkDefinition def1 = new PortableWorkDefinition();
        def1.setName("def1name");
        def1.addParameter(param1);

        PortableStringParameterDefinition param3 = new PortableStringParameterDefinition();
        param3.setName("param3");
        param3.setValue("value3");

        PortableWorkDefinition def2 = new PortableWorkDefinition();
        def2.setName("def2name");
        def2.addParameter(param3);

        originalColumn.setWorkItemDefinition(def1);
        newColumn.setWorkItemDefinition(def2);

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getActionHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_ActionWorkItemExecute_Complex() {
        ActionWorkItemCol52 originalColumn = new ActionWorkItemCol52();
        originalColumn.setHeader("action1");
        originalColumn.setHideColumn(false);

        ActionWorkItemCol52 newColumn = new ActionWorkItemCol52();
        newColumn.setHeader("action2");
        newColumn.setHideColumn(true);

        PortableStringParameterDefinition param1 = new PortableStringParameterDefinition();
        param1.setName("param1");
        param1.setValue("value1");

        PortableStringParameterDefinition param2 = new PortableStringParameterDefinition();
        param2.setName("param2");
        param2.setValue("value2");

        PortableWorkDefinition def1 = new PortableWorkDefinition();
        def1.setName("def1name");
        def1.addParameter(param1);
        def1.addParameter(param2);

        PortableStringParameterDefinition param3 = new PortableStringParameterDefinition();
        param3.setName("param3");
        param3.setValue("value3");

        PortableStringParameterDefinition param4 = new PortableStringParameterDefinition();
        param4.setName("param1");
        param4.setValue("value1");

        PortableStringParameterDefinition param5 = new PortableStringParameterDefinition();
        param5.setName("param5");
        param5.setBinding("binding5");

        PortableStringParameterDefinition param6 = new PortableStringParameterDefinition();
        param6.setName("param2");
        param6.setValue("value6");

        PortableWorkDefinition def2 = new PortableWorkDefinition();
        def2.setName("def2name");
        def2.addParameter(param3);
        def2.addParameter(param4);
        def2.addParameter(param5);
        def2.addParameter(param6);

        originalColumn.setWorkItemDefinition(def1);
        newColumn.setWorkItemDefinition(def2);

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getActionHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_ActionWorkItemInsert() {
        ActionWorkItemInsertFactCol52 originalColumn = new ActionWorkItemInsertFactCol52();
        originalColumn.setParameterClassName("ParamClass1");
        originalColumn.setWorkItemName("WI1");
        originalColumn.setWorkItemResultParameterName("param1");
        originalColumn.setBoundName("b1");
        originalColumn.setFactField("field1");
        originalColumn.setInsertLogical(false);
        originalColumn.setHeader("action1");
        originalColumn.setHideColumn(false);

        ActionWorkItemInsertFactCol52 newColumn = new ActionWorkItemInsertFactCol52();
        newColumn.setParameterClassName("ParamClass2");
        newColumn.setWorkItemName("WI2");
        newColumn.setWorkItemResultParameterName("param2");
        newColumn.setBoundName("b2");
        newColumn.setFactField("field2");
        newColumn.setInsertLogical(true);
        newColumn.setHeader("action2");
        newColumn.setHideColumn(true);

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getActionHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_ActionWorkItemSetField() {
        ActionWorkItemSetFieldCol52 originalColumn = new ActionWorkItemSetFieldCol52();
        originalColumn.setParameterClassName("ParamClass1");
        originalColumn.setWorkItemName("WI1");
        originalColumn.setWorkItemResultParameterName("param1");
        originalColumn.setBoundName("bind1");
        originalColumn.setFactField("field1");
        originalColumn.setUpdate(false);
        originalColumn.setHeader("action1");
        originalColumn.setHideColumn(false);

        ActionWorkItemSetFieldCol52 newColumn = new ActionWorkItemSetFieldCol52();
        newColumn.setParameterClassName("ParamClass2");
        newColumn.setWorkItemName("WI2");
        newColumn.setWorkItemResultParameterName("param2");
        newColumn.setBoundName("bind2");
        newColumn.setFactField("field2");
        newColumn.setUpdate(true);
        newColumn.setHeader("action2");
        newColumn.setHideColumn(true);

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getActionHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_BrlCondition() {
        // Definition diffs are currently not supported, maybe #soon?
        BRLConditionColumn originalColumn = new BRLConditionColumn();
//        originalColumn.setDefinition(Arrays.asList(new FactPattern("FactType1")));
        originalColumn.setHeader("condition1");
        originalColumn.setHideColumn(false);

        BRLConditionColumn newColumn = new BRLConditionColumn();
//        originalColumn.setDefinition(Arrays.asList(new FactPattern("FactType2"), new FactPattern("FactType3")));
        newColumn.setHeader("condition2");
        newColumn.setHideColumn(true);

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_BrlCondition_DefinitionOnly() {
        BRLConditionColumn originalColumn = new BRLConditionColumn();
        originalColumn.setDefinition(Arrays.asList(new FactPattern("FactType1")));
        originalColumn.setHeader("condition");
        originalColumn.setHideColumn(false);

        BRLConditionColumn newColumn = new BRLConditionColumn();
        originalColumn.setDefinition(Arrays.asList(new FactPattern("FactType2"), new FactPattern("FactType3")));
        newColumn.setHeader("condition");
        newColumn.setHideColumn(false);

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        // Definition diffs are currently not supported, maybe #soon?
//        assertEquals(getHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
        assertEquals(getHeaderRepre(newColumn.getHeader()) + getDiffRepre(new ArrayList<>()), result.asString());
    }

    @Test
    public void getSafeHtml_BrlAction() {
        // Definition diffs are currently not supported, maybe #soon?
        BRLActionColumn originalColumn = new BRLActionColumn();
//        originalColumn.setDefinition(Arrays.asList(new ActionRetractFact("fact1")));
        originalColumn.setHeader("action1");
        originalColumn.setHideColumn(false);

        BRLActionColumn newColumn = new BRLActionColumn();
//        newColumn.setDefinition(Arrays.asList(new ActionRetractFact("fact2"), new ActionRetractFact("fact3")));
        newColumn.setHeader("action2");
        newColumn.setHideColumn(true);

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        assertEquals(getHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
    }

    @Test
    public void getSafeHtml_BrlAction_DefinitionOnly() {
        BRLActionColumn originalColumn = new BRLActionColumn();
        originalColumn.setDefinition(Arrays.asList(new ActionRetractFact("fact1")));
        originalColumn.setHeader("action");
        originalColumn.setHideColumn(false);

        BRLActionColumn newColumn = new BRLActionColumn();
        newColumn.setDefinition(Arrays.asList(new ActionRetractFact("fact2"), new ActionRetractFact("fact3")));
        newColumn.setHeader("action");
        newColumn.setHideColumn(false);

        List<BaseColumnFieldDiff> diffs = originalColumn.diff(newColumn);

        SafeHtml result = helper.getSafeHtml(new UpdateColumnAuditLogEntry("mock user", originalColumn, newColumn, diffs));

        // Definition diffs are currently not supported, maybe #soon?
//        assertEquals(getHeaderRepre(newColumn.getHeader()) + getDiffRepre(diffs), result.asString());
        assertEquals(getHeaderRepre(newColumn.getHeader()) + getDiffRepre(new ArrayList<>()), result.asString());
    }

    private String getHeaderRepre(String header) {
        return format("commentHeader(DecisionTableAuditLogUpdateColumn(%s))", header);
    }

    private String getAttributeHeaderRepre(String header) {
        return format("commentHeader(DecisionTableAuditLogUpdateAttribute(%s))", header);
    }

    private String getConditionHeaderRepre(String header) {
        return format("commentHeader(DecisionTableAuditLogUpdateCondition(%s))", header);
    }

    private String getActionHeaderRepre(String header) {
        return format("commentHeader(DecisionTableAuditLogUpdateAction(%s))", header);
    }

    private String getDiffRepre(List<BaseColumnFieldDiff> diffs) {
        String diffsRepre = "";
        for (BaseColumnFieldDiff diff : diffs) {
            diffsRepre += getDiffRepre(diff);
        }
        return format("updatedFields(%s, %s)", diffsRepre, labelClass);
    }

    private String getDiffRepre(BaseColumnFieldDiff diff) {
        boolean constraintValueType = diff.getFieldName().equals(FIELD_CONSTRAINT_VALUE_TYPE);
        boolean WIParamValueOnly = diff instanceof WorkItemColumnParameterValueDiffImpl;
        String paramName = WIParamValueOnly ? ((WorkItemColumnParameterValueDiffImpl) diff).getParameterName() : "";
        return format("updatedField(%s, '%s', '%s')",
                      getDisplayableFieldValue(diff.getFieldName(), paramName, WIParamValueOnly), getNonNullValue(diff.getOldValue(), constraintValueType), getNonNullValue(diff.getValue(), constraintValueType));
    }

    private String getNonNullValue(Object diffValue, boolean constraintValueType) {
        String value = "";
        if (diffValue != null) {
            if (constraintValueType) {
                value = helper.getLiteralForCalculationType((Integer) diffValue);
            } else {
                value = diffValue.toString();
            }
        }
        return value;
    }

    // this might be useful in the helper itself..?
    private String getDisplayableFieldValue(String field, String optionalParameter, boolean WIParamValueOnly) {
        if (Objects.equals(field, ActionWorkItemCol52.FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE)) {
            if (WIParamValueOnly) {
                return GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterValueOnly0(optionalParameter);
            } else {
                return GuidedDecisionTableConstants.INSTANCE.DecisionTableAuditLogWorkItemParameterValue();
            }
        }

        return fieldValues.getOrDefault(field, "");
    }
}