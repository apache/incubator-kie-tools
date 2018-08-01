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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Task;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT;
import static org.jboss.drools.DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.droolsFactory;

public class Scripts {

    public static OnEntryAction onEntry(Task task) {
        return new OnEntryAction(onEntry(task.getExtensionValues()));
    }

    public static ScriptTypeListValue onEntry(List<ExtensionAttributeValue> extensions) {
        if (extensions.isEmpty()) {
            return new ScriptTypeListValue()
                    .addValue(new ScriptTypeValue("java", ""));
        }

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

    public static String scriptLanguageToUri(String language) {
        if (language == null) {
            return "http://www.java.com/java";
        }
        switch (language) {
            case "java":
                return "http://www.java.com/java";
            case "mvel":
                return "http://www.mvel.org/2.0";
            case "javascript":
                return "http://www.javascript.com/javascript";
            case "drools":
                return "http://www.jboss.org/drools/rule";
            default:
                return "http://www.java.com/java";
        }
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
            case "http://www.jboss.org/drools/rule":
                return "drools";
            default:
                return null;
        }
    }

    public static ScriptTypeListValue onExit(List<ExtensionAttributeValue> extensions) {
        if (extensions.isEmpty()) {
            return new ScriptTypeListValue()
                    .addValue(new ScriptTypeValue("java", ""));
        }

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

    public static void setOnEntryAction(FlowElement flowElement, OnEntryAction onEntryAction) {
        ScriptTypeListValue value = onEntryAction.getValue();
        for (ScriptTypeValue scriptTypeValue : value.getValues()) {
            String scriptText = scriptTypeValue.getScript();
            if (scriptText == null || scriptText.isEmpty()) {
                continue;
            }
            OnEntryScriptType script = droolsFactory.createOnEntryScriptType();
            script.setScript(asCData(scriptTypeValue.getScript()));
            String scriptLanguage = Scripts.scriptLanguageToUri(scriptTypeValue.getLanguage());
            script.setScriptFormat(scriptLanguage);
            addExtensionValue(flowElement, DOCUMENT_ROOT__ON_ENTRY_SCRIPT, script);
        }
    }

    public static void setOnExitAction(FlowElement flowElement, OnExitAction onExitAction) {
        ScriptTypeListValue value = onExitAction.getValue();
        for (ScriptTypeValue scriptTypeValue : value.getValues()) {
            String scriptText = scriptTypeValue.getScript();
            if (scriptText == null || scriptText.isEmpty()) {
                continue;
            }
            OnExitScriptType script = droolsFactory.createOnExitScriptType();
            script.setScript(asCData(scriptText));
            String scriptLanguage = Scripts.scriptLanguageToUri(scriptTypeValue.getLanguage());
            script.setScriptFormat(scriptLanguage);
            addExtensionValue(flowElement, DOCUMENT_ROOT__ON_EXIT_SCRIPT, script);
        }
    }

    private static void addExtensionValue(FlowElement flowElement, EReference eref, Object value) {
        FeatureMap.Entry entry = entryOf(eref, value);
        addExtensionValue(flowElement, entry);
    }

    private static EStructuralFeatureImpl.SimpleFeatureMapEntry entryOf(EReference eref, Object script) {
        return new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                (EStructuralFeature.Internal) eref,
                script);
    }

    private static void addExtensionValue(FlowElement flowElement, FeatureMap.Entry value) {
        extensionFor(flowElement).getValue().add(value);
    }

    private static ExtensionAttributeValue extensionFor(FlowElement flowElement) {
        if (flowElement.getExtensionValues() == null || flowElement.getExtensionValues().isEmpty()) {
            ExtensionAttributeValue eav = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
            flowElement.getExtensionValues().add(eav);
            return eav;
        } else {
            return flowElement.getExtensionValues().get(0);
        }
    }

    // apparently the only way to wrap into CDATA is to do it with string concatenation
    public static String asCData(String original) {
        return "<![CDATA[" + original + "]]>";
    }
}
