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


package org.kie.workbench.common.forms.fields.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EntityRelationField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.HasPlaceHolder;
import org.kie.workbench.common.forms.model.MetaDataEntry;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldTypeEntry;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryManager;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFieldManager implements FieldManager {

    private static transient Logger log = LoggerFactory.getLogger(FieldManager.class);

    protected Set<BasicTypeFieldProvider> basicProviders = new TreeSet<>((o1, o2) -> o1.getPriority() - o2.getPriority());

    protected Set<BasicTypeFieldProvider> basicMultipleProviders  = new TreeSet<>((o1, o2) -> o1.getPriority() - o2.getPriority());

    protected Map<String, FieldProvider> entityTypeFieldProvider = new HashMap<>();

    protected Map<String, FieldProvider> multipleEntityTypeFieldProvider = new HashMap<>();

    protected Map<String, FieldProvider> providersByFieldCode = new HashMap<>();

    protected Map<Class<? extends FieldType>, FieldProvider> providerByFieldType = new HashMap<>();

    protected String defaultSingleEntity = SubFormFieldDefinition.FIELD_TYPE.getTypeName();
    protected String defaultMultipleEntity = MultipleSubFormFieldDefinition.FIELD_TYPE.getTypeName();

    private MetaDataEntryManager metaDataEntryManager;

    public AbstractFieldManager(MetaDataEntryManager metaDataEntryManager) {
        this.metaDataEntryManager = metaDataEntryManager;
    }

    protected void registerFieldProvider(FieldProvider provider) {

        boolean isMultiple = provider instanceof MultipleValueFieldProvider;

        if (provider instanceof BasicTypeFieldProvider) {
            BasicTypeFieldProvider basicTypeProvider = (BasicTypeFieldProvider) provider;

            if(isMultiple) {
                basicMultipleProviders.add(basicTypeProvider);
            } else {
                basicProviders.add(basicTypeProvider);
            }
        } else {
            if (isMultiple) {
                multipleEntityTypeFieldProvider.put(provider.getFieldTypeName(),
                                                    provider);
            } else {
                entityTypeFieldProvider.put(provider.getFieldTypeName(),
                                            provider);
            }
        }

        providersByFieldCode.put(provider.getFieldTypeName(),
                                 provider);
        providerByFieldType.put(provider.getFieldType(),
                                provider);
    }

    @Override
    public Collection<String> getBaseFieldTypes() {
        List<String> fieldCodes = new ArrayList<>();

        for (BasicTypeFieldProvider provider : basicProviders) {
            fieldCodes.add(provider.getFieldTypeName());
        }

        fieldCodes.addAll(entityTypeFieldProvider.keySet());
        fieldCodes.addAll(multipleEntityTypeFieldProvider.keySet());

        return fieldCodes;
    }

    @Override
    public FieldDefinition getDefinitionByFieldType(FieldType fieldType) {
        return getDefinitionByFieldTypeName(fieldType.getTypeName());
    }

    @Override
    public FieldDefinition getDefinitionByFieldTypeName(String fieldTypeCode) {
        FieldProvider provider = providersByFieldCode.get(fieldTypeCode);

        if (provider != null) {
            return provider.getDefaultField();
        }

        return null;
    }

    @Override
    public FieldDefinition getDefinitionByDataType(TypeInfo typeInfo) {

        if (!TypeKind.OBJECT.equals(typeInfo.getType())) {

            return getFieldDefinitionFromBasicProvider(typeInfo);
        }

        FieldProvider provider;

        if (typeInfo.isMultiple()) {
            provider = multipleEntityTypeFieldProvider.get(defaultMultipleEntity);
        } else {
            provider = entityTypeFieldProvider.get(defaultSingleEntity);
        }

        if (provider != null) {
            FieldDefinition instance = provider.getFieldByType(typeInfo);
            instance.setStandaloneClassName(typeInfo.getClassName());
            return instance;
        }
        return null;
    }

    protected FieldDefinition getFieldDefinitionFromBasicProvider(TypeInfo typeInfo) {

        Predicate<BasicTypeFieldProvider> filterPredicate = provider -> provider.isSupported(typeInfo);

        Function<BasicTypeFieldProvider, FieldDefinition> mapFunction = provider -> {
            FieldDefinition field = provider.getFieldByType(typeInfo);
            field.setStandaloneClassName(typeInfo.getClassName());
            return field;
        };

        if(typeInfo.isMultiple()) {
            return basicMultipleProviders.stream().filter(filterPredicate).findFirst().map(mapFunction).orElse(null);
        }
        return basicProviders.stream().filter(filterPredicate).findFirst().map(mapFunction).orElse(null);
    }

    @Override
    public FieldDefinition getDefinitionByModelProperty(ModelProperty modelProperty) {

        FieldTypeEntry fieldTypeEntry = (FieldTypeEntry) modelProperty.getMetaData().getEntry(FieldTypeEntry.NAME);

        FieldDefinition fieldDefinition = null;

        if (fieldTypeEntry != null) {
            fieldDefinition = getFieldFromProvider(fieldTypeEntry.getValue(),
                                                   modelProperty.getTypeInfo());
        }

        if (fieldDefinition == null) {
            Optional<FieldDefinition> optional = Optional.ofNullable(getDefinitionByDataType(modelProperty.getTypeInfo()));
            if (optional.isPresent()) {
                fieldDefinition = optional.get();
            }
        }

        if (fieldDefinition != null) {
            fieldDefinition.setName(modelProperty.getName());
            fieldDefinition.setBinding(modelProperty.getName());

            String label = modelProperty.getName();
            label = label.substring(0, 1).toUpperCase() + label.substring(1);

            fieldDefinition.setLabel(label);
            fieldDefinition.setStandaloneClassName(modelProperty.getTypeInfo().getClassName());

            if (fieldDefinition instanceof HasPlaceHolder) {
                ((HasPlaceHolder) fieldDefinition).setPlaceHolder(label);
            }

            for (MetaDataEntry entry : modelProperty.getMetaData().getEntries()) {
                MetaDataEntryProcessor processor = metaDataEntryManager.getProcessorForEntry(entry);
                if (processor != null && processor.supports(fieldDefinition)) {
                    processor.process(entry,
                                      fieldDefinition);
                }
            }
            return fieldDefinition;
        }
        return null;
    }

    @Override
    public Collection<String> getCompatibleFields(FieldDefinition fieldDefinition) {
        if (fieldDefinition.getStandaloneClassName() != null) {
            if (fieldDefinition instanceof EntityRelationField) {
                if (fieldDefinition.getFieldTypeInfo().isMultiple()) {
                    return new TreeSet<>(multipleEntityTypeFieldProvider.keySet());
                }
                return new TreeSet<>(entityTypeFieldProvider.keySet());
            }

            Set result = new TreeSet();

            if(fieldDefinition.getFieldTypeInfo().isMultiple()) {
                for (BasicTypeFieldProvider provider : basicMultipleProviders) {
                    if (provider.isCompatible(fieldDefinition)) {
                        result.add(provider.getFieldTypeName());
                    }
                }
            } else {
                for (BasicTypeFieldProvider provider : basicProviders) {
                    if (provider.isCompatible(fieldDefinition)) {
                        result.add(provider.getFieldTypeName());
                    }
                }
            }

            return result;
        } else {
            if (fieldDefinition instanceof EntityRelationField) {
                if (fieldDefinition.getFieldTypeInfo().isMultiple()) {
                    return new TreeSet<>(multipleEntityTypeFieldProvider.keySet());
                }
                return new TreeSet<>(entityTypeFieldProvider.keySet());
            }

            BasicTypeFieldProvider provider = (BasicTypeFieldProvider) providersByFieldCode.get(fieldDefinition.getFieldType().getTypeName());

            Set result = new TreeSet();
            for (String className : provider.getSupportedTypes()) {
                result.addAll(getCompatibleTypes(className));
            }
            return result;
        }
    }

    @Override
    public Collection<String> getCompatibleTypes(FieldDefinition fieldDefinition) {
        FieldProvider provider = providersByFieldCode.get(fieldDefinition.getFieldType().getTypeName());

        if (provider == null) {
            throw new IllegalArgumentException("Unexpected field type '" + fieldDefinition.getFieldType().getTypeName() + "'");
        }

        if (provider instanceof BasicTypeFieldProvider) {
            return Arrays.asList(((BasicTypeFieldProvider) provider).getSupportedTypes());
        }
        return Arrays.asList(fieldDefinition.getStandaloneClassName());
    }

    @Override
    public FieldDefinition getFieldFromProvider(String typeCode,
                                                TypeInfo typeInfo) {
        Assert.notNull("TypeInfo cannot be null",
                       typeInfo);

        if (typeCode == null) {
            return getDefinitionByDataType(typeInfo);
        }

        for (BasicTypeFieldProvider basicProvider : basicProviders) {
            if (basicProvider.getFieldTypeName().equals(typeCode)) {
                return basicProvider.getFieldByType(typeInfo);
            }
        }

        for (BasicTypeFieldProvider basicProvider : basicMultipleProviders) {
            if (basicProvider.getFieldTypeName().equals(typeCode)) {
                return basicProvider.getFieldByType(typeInfo);
            }
        }

        FieldProvider provider = entityTypeFieldProvider.get(typeCode);

        if (provider == null) {
            provider = multipleEntityTypeFieldProvider.get(typeCode);
        }

        if (provider != null) {
            return provider.getFieldByType(typeInfo);
        }

        return null;
    }

    @Override
    public FieldDefinition getDefinitionByFieldType(Class<? extends FieldType> fieldType,
                                                    TypeInfo typeInfo) {
        FieldProvider provider = providerByFieldType.get(fieldType);

        if (provider != null) {
            FieldDefinition field = provider.getFieldByType(typeInfo);
            if (field != null) {
                field.setStandaloneClassName(typeInfo.getClassName());
            }
            return field;
        }

        return null;
    }

    protected List<String> getCompatibleTypes(String className) {
        List<String> result = new ArrayList<>();

        for (BasicTypeFieldProvider provider : basicProviders) {
            if (Arrays.asList(provider.getSupportedTypes()).contains(className)) {
                result.add(provider.getFieldTypeName());
            }
        }

        return result;
    }
}
