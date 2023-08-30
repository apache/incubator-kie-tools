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


package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

public class DefinitionAdapterBindings {

    private Class<?> baseType;
    private Class<?> graphFactory;
    private String idField;
    private String labelsField;
    private String titleField;
    private String categoryField;
    private String descriptionField;
    private List<String> propertiesFieldNames;
    private List<Boolean> typedPropertyFields;
    private PropertyMetaTypes metaTypes;
    private Class<? extends ElementFactory> elementFactory;

    public DefinitionAdapterBindings() {
        this.baseType = Object.class;
    }

    public PropertyMetaTypes getMetaTypes() {
        return metaTypes;
    }

    public DefinitionAdapterBindings setMetaTypes(PropertyMetaTypes metaTypes) {
        this.metaTypes = metaTypes;
        return this;
    }

    public Class<?> getBaseType() {
        return baseType;
    }

    public DefinitionAdapterBindings setBaseType(Class<?> baseType) {
        this.baseType = baseType;
        return this;
    }

    public Class<?> getGraphFactory() {
        return graphFactory;
    }

    public DefinitionAdapterBindings setGraphFactory(Class<?> graphFactory) {
        this.graphFactory = graphFactory;
        return this;
    }

    public String getIdField() {
        return idField;
    }

    public DefinitionAdapterBindings setIdField(String idField) {
        this.idField = idField;
        return this;
    }

    public String getLabelsField() {
        return labelsField;
    }

    public DefinitionAdapterBindings setLabelsField(String labelsField) {
        this.labelsField = labelsField;
        return this;
    }

    public String getTitleField() {
        return titleField;
    }

    public DefinitionAdapterBindings setTitleField(String titleField) {
        this.titleField = titleField;
        return this;
    }

    public String getCategoryField() {
        return categoryField;
    }

    public DefinitionAdapterBindings setCategoryField(String categoryField) {
        this.categoryField = categoryField;
        return this;
    }

    public DefinitionAdapterBindings setElementFactory(Class<? extends ElementFactory> factory) {
        this.elementFactory = factory;
        return this;
    }

    public Class<? extends ElementFactory> getElementFactory() {
        return elementFactory;
    }

    public String getDescriptionField() {
        return descriptionField;
    }

    public DefinitionAdapterBindings setDescriptionField(String descriptionField) {
        this.descriptionField = descriptionField;
        return this;
    }

    public List<String> getPropertiesFieldNames() {
        return propertiesFieldNames;
    }

    public DefinitionAdapterBindings setPropertiesFieldNames(List<String> propertiesFieldNames) {
        this.propertiesFieldNames = propertiesFieldNames;
        return this;
    }

    public List<Boolean> getTypedPropertyFields() {
        return typedPropertyFields;
    }

    public DefinitionAdapterBindings setTypedPropertyFields(List<Boolean> typedPropertyFields) {
        this.typedPropertyFields = typedPropertyFields;
        return this;
    }

    public static class PropertyMetaTypes {

        private static final int NAME = 0;
        private static final int WIDTH = 1;
        private static final int HEIGHT = 2;
        private static final int RADIUS = 3;
        private static final int ID = 4;

        private final int[] metaTypeFieldIndexes = new int[]{-1, -1, -1, -1, -1};

        public static PropertyMetaTypes parse(String s) {
            String[] values = s.trim().split(",");
            return new PropertyMetaTypes().parse(values);
        }

        public static String format(PropertyMetaTypes dpmt) {
            return Arrays.stream(dpmt.metaTypeFieldIndexes).mapToObj(Integer::toString).collect(Collectors.joining(","));
        }

        public String format() {
            return format(this);
        }

        PropertyMetaTypes parse(String[] values) {
            metaTypeFieldIndexes[NAME] = Integer.parseInt(values[NAME]);
            metaTypeFieldIndexes[WIDTH] = Integer.parseInt(values[WIDTH]);
            metaTypeFieldIndexes[HEIGHT] = Integer.parseInt(values[HEIGHT]);
            metaTypeFieldIndexes[RADIUS] = Integer.parseInt(values[RADIUS]);
            metaTypeFieldIndexes[ID] = Integer.parseInt(values[ID]);
            return this;
        }

        public void setIndex(org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes type, int index) {
            switch (type) {
                case NAME:
                    setNameIndex(index);
                    break;
            }
        }

        public int getIndex(org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes type) {
            switch (type) {
                case NAME:
                    return getNameIndex();
            }
            return -1;
        }

        public void setNameIndex(int index) {
            metaTypeFieldIndexes[NAME] = index;
        }

        public void setWidthIndex(int index) {
            metaTypeFieldIndexes[WIDTH] = index;
        }

        public void setHeightIndex(int index) {
            metaTypeFieldIndexes[HEIGHT] = index;
        }

        public void setRadiusIndex(int index) {
            metaTypeFieldIndexes[RADIUS] = index;
        }

        public void setIdIndex(int index) {
            metaTypeFieldIndexes[ID] = index;
        }

        public int getNameIndex() {
            return metaTypeFieldIndexes[NAME];
        }

        public int getWidthIndex() {
            return metaTypeFieldIndexes[WIDTH];
        }

        public int getHeightIndex() {
            return metaTypeFieldIndexes[HEIGHT];
        }

        public int getRadiusIndex() {
            return metaTypeFieldIndexes[RADIUS];
        }

        public int getIdIndex() {
            return metaTypeFieldIndexes[ID];
        }
    }
}
