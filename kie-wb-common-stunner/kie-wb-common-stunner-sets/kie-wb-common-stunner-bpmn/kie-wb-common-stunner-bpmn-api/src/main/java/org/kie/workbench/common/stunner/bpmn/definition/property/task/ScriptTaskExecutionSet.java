/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        startElement = "script"
)
public class ScriptTaskExecutionSet implements BPMNPropertySet {

    @Name
    @FieldLabel
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            settings = {@FieldParam(name = "rows", value = "5")}
    )
    @Valid
    private Script script;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            afterElement = "script"
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

    public ScriptTaskExecutionSet() {
        this(new Script(""),
             new ScriptLanguage(""),
             new IsAsync());
    }

    public ScriptTaskExecutionSet(final @MapsTo("script") Script script,
                                  final @MapsTo("scriptLanguage") ScriptLanguage scriptLanguage,
                                  final @MapsTo("isAsync") IsAsync isAsync) {
        this.script = script;
        this.scriptLanguage = scriptLanguage;
        this.isAsync = isAsync;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(final Script script) {
        this.script = script;
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

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(script.hashCode(),
                                         scriptLanguage.hashCode(),
                                         isAsync.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ScriptTaskExecutionSet) {
            ScriptTaskExecutionSet other = (ScriptTaskExecutionSet) o;
            return script.equals(other.script) &&
                    scriptLanguage.equals(other.scriptLanguage) &&
                    isAsync.equals(other.isAsync);
        }
        return false;
    }
}
