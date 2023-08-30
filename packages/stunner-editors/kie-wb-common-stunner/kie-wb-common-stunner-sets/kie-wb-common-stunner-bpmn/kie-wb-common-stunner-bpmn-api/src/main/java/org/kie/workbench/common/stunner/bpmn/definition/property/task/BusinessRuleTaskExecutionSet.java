/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.forms.model.ComboBoxFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(startElement = "ruleLanguage")
public class BusinessRuleTaskExecutionSet implements BPMNPropertySet {

    public static final String RULE_LANGUAGE = "ruleLanguage";
    public static final String RULE_FLOW_GROUP = "ruleFlowGroup";
    public static final String FILE_NAME = "fileName";
    public static final String NAMESPACE = "namespace";
    public static final String DECISON_NAME = "decisionName";
    public static final String DMN_MODEL_NAME = "dmnModelName";

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "DRL")}
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.RuleLanguageProvider")
    @Valid
    private RuleLanguage ruleLanguage;

    @Property
    @FormField(
            afterElement = "ruleLanguage",
            type = ComboBoxFieldType.class
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.RuleFlowGroupFormProvider")
    @Valid
    protected RuleFlowGroup ruleFlowGroup;

    @Property
    @FormField(
            afterElement = "ruleFlowGroup",
            type = ComboBoxFieldType.class
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.FileNameFormProvider")
    @Valid
    private FileName fileName;

    @Property
    @FormField(
            afterElement = "fileName"
    )
    @Valid
    private Namespace namespace;

    @Property
    @FormField(
            afterElement = "namespace",
            type = ComboBoxFieldType.class
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.DecisionNameFormProvider"
    )
    @Valid
    private DecisionName decisionName;

    @Property
    @FormField(
            afterElement = "decisionName"
    )
    @Valid
    private DmnModelName dmnModelName;

    @Property
    @FormField(afterElement = "dmnModelName",
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")})
    @Valid
    private OnEntryAction onEntryAction;

    @Property
    @FormField(afterElement = "onEntryAction",
            settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")})
    @Valid
    private OnExitAction onExitAction;

    @Property
    @FormField(afterElement = "onExitAction")
    @Valid
    private IsAsync isAsync;

    @Property
    @FormField(afterElement = "isAsync")
    @Valid
    private AdHocAutostart adHocAutostart;

    @Property
    @FormField(afterElement = "adHocAutostart")
    @Valid
    private SLADueDate slaDueDate;

    public BusinessRuleTaskExecutionSet() {
        this(new RuleLanguage(),
             new RuleFlowGroup(),
             new FileName(),
             new Namespace(),
             new DecisionName(),
             new DmnModelName(),
             new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                      ""))),
             new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue("java",
                                                                                     ""))),
             new IsAsync(),
             new AdHocAutostart(),
             new SLADueDate());
    }

    public BusinessRuleTaskExecutionSet(final @MapsTo("ruleLanguage") RuleLanguage ruleLanguage,
                                        final @MapsTo("ruleFlowGroup") RuleFlowGroup ruleFlowGroup,
                                        final @MapsTo("fileName") FileName fileName,
                                        final @MapsTo("namespace") Namespace namespace,
                                        final @MapsTo("decisionName") DecisionName decisionName,
                                        final @MapsTo("dmnModelName") DmnModelName dmnModelName,
                                        final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                                        final @MapsTo("onExitAction") OnExitAction onExitAction,
                                        final @MapsTo("isAsync") IsAsync isAsync,
                                        final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart,
                                        final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        this.ruleLanguage = ruleLanguage;
        this.ruleFlowGroup = ruleFlowGroup;
        this.fileName = fileName;
        this.namespace = namespace;
        this.decisionName = decisionName;
        this.dmnModelName = dmnModelName;
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
        this.isAsync = isAsync;
        this.adHocAutostart = adHocAutostart;
        this.slaDueDate = slaDueDate;
    }

    public RuleLanguage getRuleLanguage() {
        return ruleLanguage;
    }

    public void setRuleLanguage(final RuleLanguage ruleLanguage) {
        this.ruleLanguage = ruleLanguage;
    }

    public RuleFlowGroup getRuleFlowGroup() {
        return ruleFlowGroup;
    }

    public void setRuleFlowGroup(final RuleFlowGroup ruleFlowGroup) {
        this.ruleFlowGroup = ruleFlowGroup;
    }

    public FileName getFileName() {
        return fileName;
    }

    public void setFileName(FileName fileName) {
        this.fileName = fileName;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public DecisionName getDecisionName() {
        return decisionName;
    }

    public void setDecisionName(DecisionName decisionName) {
        this.decisionName = decisionName;
    }

    public DmnModelName getDmnModelName() {
        return dmnModelName;
    }

    public void setDmnModelName(DmnModelName dmnModelName) {
        this.dmnModelName = dmnModelName;
    }

    public OnEntryAction getOnEntryAction() {
        return onEntryAction;
    }

    public void setOnEntryAction(OnEntryAction onEntryAction) {
        this.onEntryAction = onEntryAction;
    }

    public OnExitAction getOnExitAction() {
        return onExitAction;
    }

    public void setOnExitAction(OnExitAction onExitAction) {
        this.onExitAction = onExitAction;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    public AdHocAutostart getAdHocAutostart() {
        return adHocAutostart;
    }

    public void setAdHocAutostart(AdHocAutostart adHocAutostart) {
        this.adHocAutostart = adHocAutostart;
    }

    public SLADueDate getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(SLADueDate slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(ruleLanguage),
                                         Objects.hashCode(ruleFlowGroup),
                                         Objects.hashCode(fileName),
                                         Objects.hashCode(namespace),
                                         Objects.hashCode(decisionName),
                                         Objects.hashCode(dmnModelName),
                                         Objects.hashCode(onEntryAction),
                                         Objects.hashCode(onExitAction),
                                         Objects.hashCode(isAsync),
                                         Objects.hashCode(adHocAutostart),
                                         Objects.hashCode(slaDueDate));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BusinessRuleTaskExecutionSet) {
            BusinessRuleTaskExecutionSet other = (BusinessRuleTaskExecutionSet) o;
            return Objects.equals(ruleLanguage, other.ruleLanguage) &&
                    Objects.equals(ruleFlowGroup, other.ruleFlowGroup) &&
                    Objects.equals(fileName, other.fileName) &&
                    Objects.equals(namespace, other.namespace) &&
                    Objects.equals(decisionName, other.decisionName) &&
                    Objects.equals(dmnModelName, other.dmnModelName) &&
                    Objects.equals(onEntryAction, other.onEntryAction) &&
                    Objects.equals(onExitAction, other.onExitAction) &&
                    Objects.equals(isAsync, other.isAsync) &&
                    Objects.equals(adHocAutostart, other.adHocAutostart) &&
                    Objects.equals(slaDueDate, other.slaDueDate);
        }
        return false;
    }
}
