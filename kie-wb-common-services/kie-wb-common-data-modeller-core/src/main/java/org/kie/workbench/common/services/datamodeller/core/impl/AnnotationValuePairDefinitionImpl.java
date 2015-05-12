/*
 * Copyright 2014 JBoss Inc
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
package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;

public class AnnotationValuePairDefinitionImpl implements AnnotationValuePairDefinition {

    private String name;
    
    private String className;
    
    private Object defaultValue;

    private boolean array = false;

    private ValuePairType type = ValuePairType.STRING;

    private String[] enumValues = new String[0];

    private AnnotationDefinition annotationDefinition;

    public AnnotationValuePairDefinitionImpl() {
        //errai marshalling
    }

    public AnnotationValuePairDefinitionImpl( String name, String className, ValuePairType type, boolean isArray, Object defaultValue ) {
        this.name = name;
        this.className = className;
        this.type = type;
        this.array = isArray;
        this.defaultValue = defaultValue;
    }

    public AnnotationValuePairDefinitionImpl( String name, String className, ValuePairType type, Object defaultValue ) {
        this( name, className, type, false, defaultValue );
    }

    public AnnotationValuePairDefinitionImpl( String name, String className, ValuePairType type ) {
        this.name = name;
        this.className = className;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    @Override
    public boolean isPrimitiveType() {
        return type == ValuePairType.PRIMITIVE;
    }

    @Override
    public boolean isString() {
        return type == ValuePairType.STRING;
    }

    @Override
    public boolean isEnum() {
        return type == ValuePairType.ENUM;
    }

    @Override
    public String[] enumValues() {
        return enumValues;
    }

    public void setEnumValues(String[] enumValues) {
        if ( enumValues == null ) {
            this.enumValues = new String[0];
        } else {
            this.enumValues = enumValues;
        }
    }

    @Override
    public boolean isClass() {
        return type == ValuePairType.CLASS;
    }

    @Override
    public boolean isAnnotation() {
        return type == ValuePairType.ANNOTATION;
    }

    @Override
    public boolean isArray() {
        return array;
    }

    public void setArray( boolean isArray ) {
        this.array = isArray;
    }

    @Override
    public AnnotationDefinition getAnnotationDefinition() {
        return annotationDefinition;
    }

    public void setAnnotationDefinition( AnnotationDefinition annotationDefinition ) {
        this.annotationDefinition = annotationDefinition;
    }
}