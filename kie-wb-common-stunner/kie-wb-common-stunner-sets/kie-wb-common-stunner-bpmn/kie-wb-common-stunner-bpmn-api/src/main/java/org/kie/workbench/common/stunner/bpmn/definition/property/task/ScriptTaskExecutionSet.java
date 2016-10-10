/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.metaModel.ListBox;
import org.kie.workbench.common.forms.metaModel.SelectorDataProvider;
import org.kie.workbench.common.forms.metaModel.TextArea;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import javax.validation.Valid;

@Portable
@Bindable
@PropertySet
public class ScriptTaskExecutionSet implements BPMNPropertySet {

    @Name
    public static final transient String propertySetName = "Implementation/Execution";

    @Property
    @FieldDef( label = "Script", property = "value", position = 1 )
    @TextArea( rows = 5 )
    @Valid
    private Script script;

    @Property
    @FieldDef( label = "Script Language", property = "value", position = 2 )
    @ListBox
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.REMOTE,
            className = "org.kie.workbench.common.stunner.bpmn.backend.dataproviders.ScriptLanguageFormProvider" )
    @Valid
    protected ScriptLanguage scriptLanguage;

    public ScriptTaskExecutionSet() {
        this( new Script( "" ), new ScriptLanguage( "" ) );
    }

    public ScriptTaskExecutionSet( @MapsTo( "script" ) Script script,
                                   @MapsTo( "scriptLanguage" ) ScriptLanguage scriptLanguage ) {
        this.script = script;
        this.scriptLanguage = scriptLanguage;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public Script getScript() {
        return script;
    }

    public void setScript( Script script ) {
        this.script = script;
    }

    public ScriptLanguage getScriptLanguage() {
        return scriptLanguage;
    }

    public void setScriptLanguage( ScriptLanguage scriptLanguage ) {
        this.scriptLanguage = scriptLanguage;
    }
}
