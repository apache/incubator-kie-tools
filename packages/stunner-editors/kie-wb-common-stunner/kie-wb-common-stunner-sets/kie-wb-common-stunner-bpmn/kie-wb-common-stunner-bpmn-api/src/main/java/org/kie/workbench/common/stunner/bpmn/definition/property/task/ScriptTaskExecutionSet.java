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
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition(
        startElement = "script"
)
public class ScriptTaskExecutionSet implements BPMNPropertySet {

    @Property
    @FormField(settings = {@FieldParam(name = "mode", value = "ACTION_SCRIPT")})
    @Valid
    private Script script;

    @Property
    @FormField(
            afterElement = "script"
    )
    @Valid
    private IsAsync isAsync;

    @Property
    @FormField(afterElement = "isAsync")
    @Valid
    private AdHocAutostart adHocAutostart;

    public ScriptTaskExecutionSet() {
        this(new Script(new ScriptTypeValue("java",
                                            "")),
             new IsAsync(),
             new AdHocAutostart());
    }

    public ScriptTaskExecutionSet(final @MapsTo("script") Script script,
                                  final @MapsTo("isAsync") IsAsync isAsync,
                                  final @MapsTo("adHocAutostart") AdHocAutostart adHocAutostart) {
        this.script = script;
        this.isAsync = isAsync;
        this.adHocAutostart = adHocAutostart;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(final Script script) {
        this.script = script;
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

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(script),
                                         Objects.hashCode(isAsync),
                                         Objects.hashCode(adHocAutostart));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ScriptTaskExecutionSet) {
            ScriptTaskExecutionSet other = (ScriptTaskExecutionSet) o;
            return Objects.equals(script, other.script) &&
                    Objects.equals(isAsync, other.isAsync) &&
                    Objects.equals(adHocAutostart, other.adHocAutostart);
        }
        return false;
    }
}
