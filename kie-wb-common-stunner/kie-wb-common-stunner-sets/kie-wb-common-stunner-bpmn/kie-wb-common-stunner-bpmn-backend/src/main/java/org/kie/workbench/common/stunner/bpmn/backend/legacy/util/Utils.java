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

package org.kie.workbench.common.stunner.bpmn.backend.legacy.util;

import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;

import static org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils.LANGUAGE.DROOLS;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils.LANGUAGE.JAVA;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils.LANGUAGE.JAVASCRIPT;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils.LANGUAGE.MVEL;

public class Utils {

    enum LANGUAGE {
        JAVA("java",
             "http://www.java.com/java"),
        JAVASCRIPT("javascript",
                   "http://www.javascript.com/javascript"),
        MVEL("mvel",
             "http://www.mvel.org/2.0"),
        DROOLS("drools",
               "http://www.jboss.org/drools/rule");

        private final String language;

        private final String format;

        LANGUAGE(String language,
                 String format) {
            this.language = language;
            this.format = format;
        }

        String language() {
            return language;
        }

        String format() {
            return format;
        }
    }

    public static String getMetaDataValue(List<ExtensionAttributeValue> extensionValues,
                                          String metaDataName) {
        if (extensionValues != null && extensionValues.size() > 0) {
            for (ExtensionAttributeValue extattrval : extensionValues) {
                FeatureMap extensionElements = extattrval.getValue();
                List<MetaDataType> metadataExtensions = (List<MetaDataType>) extensionElements
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA,
                             true);
                for (MetaDataType metaType : metadataExtensions) {
                    if (metaType.getName() != null && metaType.getName().equals(metaDataName) && metaType.getMetaValue() != null && metaType.getMetaValue().length() > 0) {
                        return metaType.getMetaValue();
                    }
                }
            }
        }
        return null;
    }

    public static void setMetaDataExtensionValue(BaseElement element,
                                                 String metaDataName,
                                                 String metaDataValue) {
        if (element != null) {
            MetaDataType eleMetadata = DroolsFactory.eINSTANCE.createMetaDataType();
            eleMetadata.setName(metaDataName);
            eleMetadata.setMetaValue(metaDataValue);
            if (element.getExtensionValues() == null || element.getExtensionValues().isEmpty()) {
                ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                element.getExtensionValues().add(extensionElement);
            }
            FeatureMap.Entry eleExtensionElementEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                    (EStructuralFeature.Internal) DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA,
                    eleMetadata);
            element.getExtensionValues().get(0).getValue().add(eleExtensionElementEntry);
        }
    }

    public static String getScriptLanguage(final String languageFormat) {
        if (JAVA.format().equals(languageFormat)) {
            return JAVA.language();
        } else if (MVEL.format().equals(languageFormat)) {
            return MVEL.language();
        } else if (JAVASCRIPT.format().equals(languageFormat)) {
            return JAVASCRIPT.language();
        } else if (DROOLS.format().equals(languageFormat)) {
            return DROOLS.language();
        }
        return null;
    }

    public static String getScriptLanguageFormat(final String language) {
        if (JAVA.language().equals(language)) {
            return JAVA.format();
        } else if (MVEL.language().equals(language)) {
            return MVEL.format();
        } else if (JAVASCRIPT.language().equals(language)) {
            return JAVASCRIPT.format();
        } else if (DROOLS.language().equals(language)) {
            return DROOLS.format();
        }
        return null;
    }
}
