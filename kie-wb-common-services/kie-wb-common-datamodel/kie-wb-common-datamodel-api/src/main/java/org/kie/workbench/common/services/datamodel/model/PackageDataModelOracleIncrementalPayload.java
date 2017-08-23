/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.datamodel.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.appformer.project.datamodel.oracle.Annotation;
import org.appformer.project.datamodel.oracle.MethodInfo;
import org.appformer.project.datamodel.oracle.ModelField;
import org.appformer.project.datamodel.oracle.TypeSource;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Payload for incremental client-side Data Model Oracle content
 */
@Portable
public class PackageDataModelOracleIncrementalPayload {

    //Fact Types and their corresponding fields
    private Map<String, ModelField[]> modelFields = new HashMap<String, ModelField[]>();

    //Map of the field that contains the parametrized type of a collection
    //for example given "List<String> name", key = "name" value = "String"
    private Map<String, String> fieldParametersType = new HashMap<String, String>();

    //Map {factType, isEvent} to determine which Fact Type can be treated as events.
    private Map<String, Boolean> eventTypes = new HashMap<String, Boolean>();

    //Map {factType, TypeSource} to determine where a Fact Type as defined.
    private Map<String, TypeSource> typeSources = new HashMap<String, TypeSource>();

    //Map {factType, superType} to determine the Super Type of a FactType.
    private Map<String, List<String>> superTypes = new HashMap<String, List<String>>();

    //Map {factType, Set<Annotation>} containing the FactType's annotations.
    private Map<String, Set<Annotation>> typeAnnotations = new HashMap<String, Set<Annotation>>();

    //Map {factType, Map<fieldName, Set<Annotation>>} containing the FactType's Field annotations.
    private Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    //Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    private Map<String, List<MethodInfo>> methodInformation = new HashMap<String, List<MethodInfo>>();

    // A map of FactTypes {factType, isCollection} to determine which Fact Types are Collections.
    private Map<String, Boolean> collectionTypes = new HashMap<String, Boolean>();

    public Map<String, ModelField[]> getModelFields() {
        return modelFields;
    }

    public void setModelFields( final Map<String, ModelField[]> modelFields ) {
        this.modelFields = modelFields;
    }

    public Map<String, String> getFieldParametersType() {
        return fieldParametersType;
    }

    public void setFieldParametersType( final Map<String, String> fieldParametersType ) {
        this.fieldParametersType = fieldParametersType;
    }

    public Map<String, Boolean> getEventTypes() {
        return eventTypes;
    }

    public void setEventTypes( final Map<String, Boolean> eventTypes ) {
        this.eventTypes = eventTypes;
    }

    public Map<String, TypeSource> getTypeSources() {
        return typeSources;
    }

    public void setTypeSources( final Map<String, TypeSource> typeSources ) {
        this.typeSources = typeSources;
    }

    public Map<String, List<String>> getSuperTypes() {
        return superTypes;
    }

    public void setSuperTypes( final Map<String, List<String>> superTypes ) {
        this.superTypes = superTypes;
    }

    public Map<String, Set<Annotation>> getTypeAnnotations() {
        return typeAnnotations;
    }

    public void setTypeAnnotations( final Map<String, Set<Annotation>> typeAnnotations ) {
        this.typeAnnotations = typeAnnotations;
    }

    public Map<String, Map<String, Set<Annotation>>> getTypeFieldsAnnotations() {
        return typeFieldsAnnotations;
    }

    public void setTypeFieldsAnnotations( final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations ) {
        this.typeFieldsAnnotations = typeFieldsAnnotations;
    }

    public Map<String, List<MethodInfo>> getMethodInformation() {
        return methodInformation;
    }

    public void setMethodInformation( final Map<String, List<MethodInfo>> methodInformation ) {
        this.methodInformation = methodInformation;
    }

    public Map<String, Boolean> getCollectionTypes() {
        return collectionTypes;
    }

    public void setCollectionTypes( final Map<String, Boolean> collectionTypes ) {
        this.collectionTypes = collectionTypes;
    }

}
