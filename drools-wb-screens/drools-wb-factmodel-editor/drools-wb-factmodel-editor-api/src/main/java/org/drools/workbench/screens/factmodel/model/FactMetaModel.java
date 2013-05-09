/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.factmodel.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the GUI data for a fact model definition.
 */
@Portable
public class FactMetaModel {

    private String name;
    private String superType;
    private List<FieldMetaModel>      fields      = new ArrayList<FieldMetaModel>();
    private List<AnnotationMetaModel> annotations = new ArrayList<AnnotationMetaModel>();

    public FactMetaModel() {
    }

    public FactMetaModel( final String name ) {
        this.name = name;
    }

    public FactMetaModel( final String name,
                          final List<FieldMetaModel> fields ) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public List<FieldMetaModel> getFields() {
        return fields;
    }

    public void setFields( final List<FieldMetaModel> fields ) {
        this.fields = fields;
    }

    public List<AnnotationMetaModel> getAnnotations() {
        return annotations;
    }

    public void setAnnotations( final List<AnnotationMetaModel> annotations ) {
        this.annotations = annotations;
    }

    public void setSuperType( final String superType ) {
        this.superType = superType;
    }

    public String getSuperType() {
        return this.superType;
    }

    public boolean hasSuperType() {
        return this.superType != null;
    }

}
