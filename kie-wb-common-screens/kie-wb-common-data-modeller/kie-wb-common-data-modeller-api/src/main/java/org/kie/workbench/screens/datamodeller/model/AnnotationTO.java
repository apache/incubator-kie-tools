/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.screens.datamodeller.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.HashMap;
import java.util.Map;

@Portable
public class AnnotationTO {

    private String className;

    private String name;

    private AnnotationDefinitionTO annotationDefinition;

    private Map<String, Object> values = new HashMap<String, Object>();

    public AnnotationTO() {
    }

    public AnnotationTO(AnnotationDefinitionTO annotationDefinition) {
        this.annotationDefinition = annotationDefinition;
        this.className = annotationDefinition.getClassName();
        this.name = annotationDefinition.getName();
    }

    public Object getValue(String annotationMemberName) {
        return values.get(annotationMemberName);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValue(String annotationMemberName, Object value) {
        values.put(annotationMemberName,  value);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnnotationDefinitionTO getAnnotationDefinition() {
        return annotationDefinition;
    }

    public void setAnnotationDefinition(AnnotationDefinitionTO annotationDefinition) {
        this.annotationDefinition = annotationDefinition;
    }
}
