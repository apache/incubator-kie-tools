/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.definition;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = EmbeddedSubprocess.EmbeddedSubprocessBuilder.class)
@CanContain(roles = {"all"})
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class EmbeddedSubprocess extends BaseSubprocess implements DataIOModel {

    @Title
    public static final transient String title = "Embedded Sub-Process";

    @Description
    public static final transient String description = "An embedded sub-process.";

    @NonPortable
    public static class EmbeddedSubprocessBuilder extends BaseSubprocessBuilder<EmbeddedSubprocess> {

        @Override
        public EmbeddedSubprocess build() {
            return new EmbeddedSubprocess(
                    new BPMNGeneralSet("Subprocess"),
                    new BackgroundSet("#FFFFFF",
                                      BORDER_COLOR,
                                      BORDER_SIZE),
                    new FontSet(),
                    new RectangleDimensionsSet(450d,
                                               250d),
                    new SimulationSet(),
                    new OnEntryAction(""),
                    new OnExitAction(""),
                    new ScriptLanguage(""),
                    new IsAsync(),
                    new ProcessData());
        }
    }

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "general",
            settings = {@FieldParam(name = "rows", value = "5")}
    )
    @Valid
    private OnEntryAction onEntryAction;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "onEntryAction",
            settings = {@FieldParam(name = "rows", value = "5")}
    )
    @Valid
    private OnExitAction onExitAction;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            afterElement = "onExitAction"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.ScriptLanguageFormProvider")
    @Valid
    protected ScriptLanguage scriptLanguage;

    @Property
    @FormField(
            afterElement = "scriptLanguage"
    )
    @Valid
    private IsAsync isAsync;

    @PropertySet
    @FormField(
            afterElement = "isAsync"
    )
    @Valid
    private ProcessData processData;

    public EmbeddedSubprocess() {
        super();
    }

    public EmbeddedSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                              final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                              final @MapsTo("fontSet") FontSet fontSet,
                              final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                              final @MapsTo("simulationSet") SimulationSet simulationSet,
                              final @MapsTo("onEntryAction") OnEntryAction onEntryAction,
                              final @MapsTo("onExitAction") OnExitAction onExitAction,
                              final @MapsTo("scriptLanguage") ScriptLanguage scriptLanguage,
                              final @MapsTo("isAsync") IsAsync isAsync,
                              final @MapsTo("processData") ProcessData processData) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);
        this.onEntryAction = onEntryAction;
        this.onExitAction = onExitAction;
        this.scriptLanguage = scriptLanguage;
        this.isAsync = isAsync;
        this.processData = processData;
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return false;
    }

    @Override
    public boolean hasOutputVars() {
        return true;
    }

    @Override
    public boolean isSingleOutputVar() {
        return false;
    }

    public String getTitle() {
        return title;
    }

    public ProcessData getProcessData() {
        return processData;
    }

    public void setProcessData(final ProcessData processData) {
        this.processData = processData;
    }

    public OnEntryAction getOnEntryAction() {
        return onEntryAction;
    }

    public void setOnEntryAction(final OnEntryAction onEntryAction) {
        this.onEntryAction = onEntryAction;
    }

    public OnExitAction getOnExitAction() {
        return onExitAction;
    }

    public void setOnExitAction(final OnExitAction onExitAction) {
        this.onExitAction = onExitAction;
    }

    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

    public void setScriptLanguage(final ScriptLanguage scriptLanguage) {
        this.scriptLanguage = scriptLanguage;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(final IsAsync isAsync) {
        this.isAsync = isAsync;
    }
}
