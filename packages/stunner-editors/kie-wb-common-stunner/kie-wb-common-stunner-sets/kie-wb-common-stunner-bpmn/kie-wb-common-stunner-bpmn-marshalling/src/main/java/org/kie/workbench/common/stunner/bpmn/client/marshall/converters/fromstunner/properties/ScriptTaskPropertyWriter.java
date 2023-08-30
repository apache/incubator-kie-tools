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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.Set;

import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts.asCData;

public class ScriptTaskPropertyWriter extends ActivityPropertyWriter {

    private final ScriptTask scriptTask;

    public ScriptTaskPropertyWriter(ScriptTask scriptTask, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(scriptTask, variableScope, dataObjects);
        this.scriptTask = scriptTask;
    }

    public void setScript(ScriptTypeValue script) {
        scriptTask.setScriptFormat(
                Scripts.scriptLanguageToUri(script.getLanguage()));
        String scriptText = script.getScript();
        if (scriptText != null && !scriptText.isEmpty()) {
            scriptTask.setScript(asCData(scriptText));
        }
    }

    public void setAsync(Boolean async) {
        CustomElement.async.of(scriptTask).set(async);
    }

    public void setAdHocAutostart(Boolean value) {
        CustomElement.autoStart.of(scriptTask).set(value);
    }
}
