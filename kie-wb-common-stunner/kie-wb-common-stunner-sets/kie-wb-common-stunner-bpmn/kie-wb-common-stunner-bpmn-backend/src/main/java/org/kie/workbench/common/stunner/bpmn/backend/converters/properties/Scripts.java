/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.Task;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

public class Scripts {

    public static OnEntryAction onEntry(Task task) {
        return new OnEntryAction(onEntry(task.getExtensionValues()));
    }

    public static ScriptTypeListValue onEntry(List<ExtensionAttributeValue> extensions) {
        @SuppressWarnings("unchecked")
        List<OnEntryScriptType> onEntryExtensions =
                (List<OnEntryScriptType>) extensions.get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);

        if (!onEntryExtensions.isEmpty()) {
            List<ScriptTypeValue> scripts =
                    onEntryExtensions.stream()
                            .map(onEntryScriptType ->
                                         new ScriptTypeValue(
                                                 scriptLanguageFromUri(onEntryScriptType.getScriptFormat()),
                                                 onEntryScriptType.getScript()
                                         ))
                            .collect(Collectors.toList());
            return new ScriptTypeListValue(scripts);
        }

        return new ScriptTypeListValue()
                .addValue(new ScriptTypeValue("java", ""));
    }

    public static String scriptLanguageFromUri(String format) {
        if (format == null) {
            return null;
        }
        switch (format) {
            case "http://www.java.com/java":
                return "java";
            case "http://www.mvel.org/2.0":
                return "mvel";
            case "http://www.javascript.com/javascript":
                return "javascript";
            default:
                return null;
        }
    }

    public static ScriptTypeListValue onExit(List<ExtensionAttributeValue> extensions) {
        @SuppressWarnings("unchecked")
        List<OnExitScriptType> onExitExtensions =
                (List<OnExitScriptType>) extensions.get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);

        if (!onExitExtensions.isEmpty()) {
            List<ScriptTypeValue> scripts = onExitExtensions.stream()
                    .map(onExitScriptType ->
                                 new ScriptTypeValue(
                                         scriptLanguageFromUri(onExitScriptType.getScriptFormat()),
                                         onExitScriptType.getScript()
                                 ))
                    .collect(Collectors.toList());
            return new ScriptTypeListValue(scripts);
        }

        return new ScriptTypeListValue()
                .addValue(new ScriptTypeValue("java", ""));
    }
}
