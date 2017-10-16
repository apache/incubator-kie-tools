/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.soup.project.datamodel.commons.oracle.ProjectDataModelOracleImpl;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.TypeSource;

/**
 * Base FactBuilder containing common code
 */
public abstract class BaseFactBuilder implements FactBuilder {

    protected final ProjectDataModelOracleBuilder builder;

    private final String type;
    private final List<ModelField> fields = new ArrayList<ModelField>();

    private final boolean isCollection;
    private final boolean isEvent;
    protected final TypeSource typeSource;

    public BaseFactBuilder(final ProjectDataModelOracleBuilder builder,
                           final Class<?> clazz,
                           final boolean isEvent,
                           final TypeSource typeSource) {
        this.builder = builder;
        this.type = getFullClassName(clazz);
        this.isCollection = isCollection(clazz);
        this.isEvent = isEvent;
        this.typeSource = typeSource;

        addField(new ModelField(DataType.TYPE_THIS,
                                type,
                                ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                ModelField.FIELD_ORIGIN.SELF,
                                FieldAccessorsAndMutators.ACCESSOR,
                                DataType.TYPE_THIS));
    }

    public BaseFactBuilder(final ProjectDataModelOracleBuilder builder,
                           final String type,
                           final boolean isCollection,
                           final boolean isEvent,
                           final TypeSource typeSource) {
        this.builder = builder;
        this.type = type;
        this.isCollection = isCollection;
        this.isEvent = isEvent;
        this.typeSource = typeSource;

        addField(new ModelField(DataType.TYPE_THIS,
                                type,
                                ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                                ModelField.FIELD_ORIGIN.SELF,
                                FieldAccessorsAndMutators.ACCESSOR,
                                DataType.TYPE_THIS));
    }

    private boolean isCollection(final Class<?> clazz) {
        return (clazz != null && Collection.class.isAssignableFrom(clazz));
    }

    private String getFullClassName(final Class<?> clazz) {
        return clazz.getName();
    }

    public String getType() {
        return type;
    }

    protected FactBuilder addField(final ModelField field) {
        this.fields.add(field);
        return this;
    }

    @Override
    public ProjectDataModelOracleBuilder end() {
        return builder;
    }

    @Override
    public void build(final ProjectDataModelOracleImpl oracle) {
        oracle.addProjectModelFields(buildModelFields());
        oracle.addProjectCollectionTypes(buildCollectionTypes());
        oracle.addProjectEventTypes(buildEventTypes());
        oracle.addProjectTypeSources(buildTypeSources());
    }

    public ProjectDataModelOracleBuilder getDataModelBuilder() {
        return this.builder;
    }

    private Map<String, ModelField[]> buildModelFields() {
        final Map<String, ModelField[]> loadableFactsAndFields = new HashMap<String, ModelField[]>();
        final ModelField[] loadableFields = new ModelField[fields.size()];
        fields.toArray(loadableFields);
        loadableFactsAndFields.put(type,
                                   loadableFields);
        return loadableFactsAndFields;
    }

    private Map<String, Boolean> buildCollectionTypes() {
        final Map<String, Boolean> loadableCollectionTypes = new HashMap<String, Boolean>();
        loadableCollectionTypes.put(type,
                                    isCollection);
        return loadableCollectionTypes;
    }

    private Map<String, Boolean> buildEventTypes() {
        final Map<String, Boolean> loadableEventTypes = new HashMap<String, Boolean>();
        loadableEventTypes.put(type,
                               isEvent);
        return loadableEventTypes;
    }

    private Map<String, TypeSource> buildTypeSources() {
        final Map<String, TypeSource> loadableTypeSources = new HashMap<String, TypeSource>();
        loadableTypeSources.put(type,
                                typeSource);
        return loadableTypeSources;
    }
}
