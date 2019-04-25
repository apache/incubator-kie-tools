/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.SequenceGeneratorValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.FieldEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.ColumnField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.IdGeneratorField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.RelationshipField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.SequenceGeneratorField;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.CascadeType;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.RelationType;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.ext.properties.editor.client.fields.BooleanField;
import org.uberfire.ext.properties.editor.client.fields.TextField;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

@Dependent
public class JPADataObjectFieldEditor
        extends FieldEditor
        implements JPADataObjectFieldEditorView.Presenter {

    private static Map<String, DataModelerPropertyEditorFieldInfo> propertyEditorFields = new HashMap<>();

    private JPADataObjectFieldEditorView view;

    @Inject
    public JPADataObjectFieldEditor(JPADataObjectFieldEditorView view,
                                    DomainHandlerRegistry handlerRegistry,
                                    Event<DataModelerEvent> dataModelerEvent,
                                    DataModelCommandBuilder commandBuilder) {
        super(handlerRegistry, dataModelerEvent, commandBuilder);
        this.view = view;
        view.init(this);
    }

    @PostConstruct
    protected void init() {
        loadPropertyEditor();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getName() {
        return "JPA_FIELD_EDITOR";
    }

    @Override
    public String getDomainName() {
        return JPADomainEditor.JPA_DOMAIN;
    }

    @Override
    protected void loadDataObjectField(DataObject dataObject, ObjectProperty objectField) {
        clear();
        setReadonly(true);
        if (dataObject != null && objectField != null) {
            this.dataObject = dataObject;
            this.objectField = objectField;

            updateIdentifierField(objectField.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_ID_ANNOTATION));
            updateColumnFields(objectField.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION));
            updateGeneratedValueField(objectField.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION));
            updateSequenceGeneratorField(objectField.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION));
            updateRelationshipField(getCurrentRelationshipAnnotation(objectField));

            setReadonly(getContext() == null || getContext().isReadonly());
        }
        loadPropertyEditor();
    }

    @Override
    public void onIdentifierFieldChange(DataModelerPropertyEditorFieldInfo fieldInfo, String newValue) {
        if (getObjectField() != null) {

            Boolean doAdd = Boolean.TRUE.toString().equals(newValue);
            commandBuilder.buildFieldAddOrRemoveAnnotationCommand(getContext(),
                                                                  getName(), getDataObject(), getObjectField(),
                                                                  JPADomainAnnotations.JAVAX_PERSISTENCE_ID_ANNOTATION, doAdd).execute();
        }
    }

    @Override
    public void onColumnFieldChange(DataModelerPropertyEditorFieldInfo fieldInfo, String newValue) {
        if (getObjectField() != null) {

            if (JPADataObjectFieldEditorView.COLUMN_NAME_FIELD.equals(fieldInfo.getKey())) {

                String value = DataModelerUtils.nullTrim(newValue);
                commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                      JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, ColumnField.NAME, value, false).execute();
            } else if (JPADataObjectFieldEditorView.COLUMN_UNIQUE_FIELD.equals(fieldInfo.getKey())) {

                commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                      JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, ColumnField.UNIQUE, newValue, false).execute();
            } else if (JPADataObjectFieldEditorView.COLUMN_NULLABLE_FIELD.equals(fieldInfo.getKey())) {

                commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                      JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, ColumnField.NULLABLE, newValue, false).execute();
            } else if (JPADataObjectFieldEditorView.COLUMN_INSERTABLE_FIELD.equals(fieldInfo.getKey())) {

                commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                      JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, ColumnField.INSERTABLE, newValue, false).execute();
            } else if (JPADataObjectFieldEditorView.COLUMN_UPDATABLE_FIELD.equals(fieldInfo.getKey())) {

                commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                      JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, ColumnField.UPDATABLE, newValue, false).execute();
            }

            Annotation annotation = getObjectField().getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION);

            //If the COLUMN annotation just has the by default parameters configured just remove it.
            if (annotation != null && hasOnlyDefaultValues(annotation)) {
                commandBuilder.buildFieldAddOrRemoveAnnotationCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                      JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, false).execute();
            }
        }
    }

    @Override
    public void onGeneratedValueFieldChange(DataModelerPropertyEditorFieldInfo fieldInfo, String newValue) {
        if (getObjectField() != null) {

            String strategy = DataModelerUtils.nullTrim((String) fieldInfo.getCurrentValue(IdGeneratorField.STRATEGY));
            String generator = DataModelerUtils.nullTrim((String) fieldInfo.getCurrentValue(IdGeneratorField.GENERATOR));

            if (strategy == null) {
                commandBuilder.buildFieldAnnotationRemoveCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                 JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION).execute();
            } else {

                commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                      JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION, IdGeneratorField.STRATEGY, strategy, false).execute();

                commandBuilder.buildFieldAnnotationValueChangeCommand(getContext(), getName(), getDataObject(), getObjectField(),
                                                                      JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION, IdGeneratorField.GENERATOR, generator, false).execute();
            }
        }
    }

    @Override
    public void onSequenceGeneratorFieldChange(DataModelerPropertyEditorFieldInfo fieldInfo, String newValue) {
        if (getObjectField() != null) {

            Annotation oldGenerator = getObjectField().getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION);
            SequenceGeneratorValueHandler oldGeneratorHandler = oldGenerator != null ? new SequenceGeneratorValueHandler(oldGenerator) : null;
            Annotation newGenerator = null;

            //TODO add more fine grained control to the changes if needed. By now I can just remove the old generator annotation
            //and add the new one. This may alter the annotations order for the given field, but it's not a problem.
            if (oldGeneratorHandler != null) {
                commandBuilder.buildFieldAnnotationRemoveCommand(getContext(), getName(), getDataObject(),
                                                                 getObjectField(), oldGeneratorHandler.getClassName()).execute();
            }

            String name = DataModelerUtils.nullTrim((String) fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.NAME));
            String sequenceName = DataModelerUtils.nullTrim((String) fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.SEQUENCE_NAME));
            Integer initialValue = (Integer) fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.INITIAL_VALUE);
            Integer allocationSize = (Integer) fieldInfo.getCurrentValue(SequenceGeneratorValueHandler.ALLOCATION_SIZE);

            if (name != null && !"".equals(name.trim())) {
                newGenerator = SequenceGeneratorValueHandler.createAnnotation(name,
                                                                              sequenceName,
                                                                              initialValue,
                                                                              allocationSize,
                                                                              getContext().getAnnotationDefinitions());

                commandBuilder.buildFieldAnnotationAddCommand(getContext(), getName(), getDataObject(),
                                                              getObjectField(), newGenerator).execute();
            }
        }
    }

    @Override
    public void onRelationTypeFieldChange(DataModelerPropertyEditorFieldInfo fieldInfo, String newValue) {
        if (getObjectField() != null) {

            Annotation oldRelation = getCurrentRelationshipAnnotation(getObjectField());
            RelationshipAnnotationValueHandler oldRelationHandler = oldRelation != null ? new RelationshipAnnotationValueHandler(oldRelation) : null;
            Annotation newRelation;

            RelationType newRelationType = (RelationType) fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.RELATION_TYPE);
            List<CascadeType> newCascadeTypes = (List<CascadeType>) fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.CASCADE);
            FetchMode newFetchMode = (FetchMode) fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.FETCH);
            Boolean newOptional = (Boolean) fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.OPTIONAL);
            String newMappedBy = DataModelerUtils.nullTrim((String) fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.MAPPED_BY));
            Boolean newOrphanRemoval = (Boolean) fieldInfo.getCurrentValue(RelationshipAnnotationValueHandler.ORPHAN_REMOVAL);

            //TODO add more fine grained control for the changes if needed. By now I can just remove the old relation annotation
            //and add the new one. This may alter the annotations order for the given field, but it's not a problem.
            if (oldRelationHandler != null) {
                commandBuilder.buildFieldAnnotationRemoveCommand(getContext(), getName(), getDataObject(),
                                                                 getObjectField(), oldRelationHandler.getClassName()).execute();
            }

            newRelation = RelationshipAnnotationValueHandler.createAnnotation(newRelationType,
                                                                              newCascadeTypes, newFetchMode, newOptional, newMappedBy, newOrphanRemoval,
                                                                              getContext().getAnnotationDefinitions());

            if (newRelation != null) {
                getObjectField().addAnnotation(newRelation);
                commandBuilder.buildFieldAnnotationAddCommand(getContext(), getName(), getDataObject(),
                                                              getObjectField(), newRelation).execute();
            }
        }
    }

    protected void loadPropertyEditor() {
        view.loadPropertyEditorCategories(getPropertyEditorCategories());
    }

    protected List<PropertyEditorCategory> getPropertyEditorCategories() {

        final List<PropertyEditorCategory> categories = new ArrayList<>();

        PropertyEditorCategory category = new PropertyEditorCategory(getIdentifierCategoryName(), 1);
        categories.add(category);

        category.withField(createIdentifierField());
        category.withField(createGeneratedValueField());
        category.withField(createSequenceGeneratorField());

        category = new PropertyEditorCategory(getColumnCategoryName(), 2);
        categories.add(category);

        category.withField(createColumnNameField());
        category.withField(createColumnUniqueField());
        category.withField(createColumnNullableField());
        category.withField(createColumnInsertableField());
        category.withField(createColumnUpdatableField());

        category = new PropertyEditorCategory(getRelationshipCategoryName(), 3);
        categories.add(category);

        category.withField(createRelationShipTypeField());

        return categories;
    }

    private DataModelerPropertyEditorFieldInfo createIdentifierField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_identifier_field_label(),
                           JPADataObjectFieldEditorView.IDENTIFIER_FIELD, "false", BooleanField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_identifier_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_identifier_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createGeneratedValueField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_generation_strategy_field_label(),
                           JPADataObjectFieldEditorView.GENERATED_VALUE_FIELD, IdGeneratorField.NOT_CONFIGURED_LABEL, IdGeneratorField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_generation_strategy_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_generation_strategy_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createSequenceGeneratorField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_sequence_generator_field_label(),
                           JPADataObjectFieldEditorView.SEQUENCE_GENERATOR_FIELD, SequenceGeneratorField.NOT_CONFIGURED_LABEL, SequenceGeneratorField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_sequence_generator_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_sequence_generator_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createColumnNameField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_column_field_label(),
                           JPADataObjectFieldEditorView.COLUMN_NAME_FIELD, "", TextField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_column_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_column_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createColumnUniqueField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_unique_field_label(),
                           JPADataObjectFieldEditorView.COLUMN_UNIQUE_FIELD, "false", BooleanField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_unique_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_unique_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createColumnNullableField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_nullable_field_label(),
                           JPADataObjectFieldEditorView.COLUMN_NULLABLE_FIELD, "true", BooleanField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_nullable_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_nullable_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createColumnInsertableField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_insertable_field_label(),
                           JPADataObjectFieldEditorView.COLUMN_INSERTABLE_FIELD, "true", BooleanField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_insertable_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_insertable_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createColumnUpdatableField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_updatable_field_label(),
                           JPADataObjectFieldEditorView.COLUMN_UPDATABLE_FIELD, "true", BooleanField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_updatable_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_updatable_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createRelationShipTypeField() {
        return createField(Constants.INSTANCE.persistence_domain_fieldEditor_relationship_field_label(),
                           JPADataObjectFieldEditorView.RELATIONSHIP_TYPE_FIELD,
                           RelationshipField.NOT_CONFIGURED_LABEL,
                           RelationshipField.class,
                           Constants.INSTANCE.persistence_domain_fieldEditor_relationship_field_help_heading(),
                           Constants.INSTANCE.persistence_domain_fieldEditor_relationship_field_help(),
                           readonly);
    }

    private DataModelerPropertyEditorFieldInfo createField(String label, String key, String currentStringValue, Class<?> customFieldClass, String helpHeading, String helpText, boolean readonly) {
        DataModelerPropertyEditorFieldInfo fieldInfo = propertyEditorFields.get(key);
        if (fieldInfo == null) {
            fieldInfo = new DataModelerPropertyEditorFieldInfo(label, currentStringValue, customFieldClass);
            fieldInfo.withKey(key);
            if (helpHeading != null) {
                fieldInfo.withHelpInfo(helpHeading, helpText);
            }
            propertyEditorFields.put(key, fieldInfo);
        }
        fieldInfo.setDisabled(readonly);
        return fieldInfo;
    }

    private void updatePropertyEditorField(String fieldId, Annotation currentValue, String currentStringValue) {
        DataModelerPropertyEditorFieldInfo fieldInfo = propertyEditorFields.get(fieldId);
        fieldInfo.setCurrentValue(currentValue);
        fieldInfo.setCurrentStringValue(currentStringValue);
    }

    private DataModelerPropertyEditorFieldInfo getField(String fieldId) {
        return propertyEditorFields.get(fieldId);
    }

    private void updateIdentifierField(Annotation annotation) {
        clearIdentifierField();
        if (annotation != null) {
            updatePropertyEditorField(JPADataObjectFieldEditorView.IDENTIFIER_FIELD, annotation, "true");
        }
    }

    private void updateColumnFields(Annotation annotation) {

        clearColumnFields();
        if (annotation != null) {

            String currentStringValue = AnnotationValueHandler.getStringValue(annotation, ColumnField.NAME, "");
            updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_NAME_FIELD, annotation, currentStringValue);

            currentStringValue = AnnotationValueHandler.getStringValue(annotation, ColumnField.UNIQUE, "false");
            updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_UNIQUE_FIELD, annotation, currentStringValue);

            currentStringValue = AnnotationValueHandler.getStringValue(annotation, ColumnField.NULLABLE, "true");
            updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_NULLABLE_FIELD, annotation, currentStringValue);

            currentStringValue = AnnotationValueHandler.getStringValue(annotation, ColumnField.INSERTABLE, "true");
            updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_INSERTABLE_FIELD, annotation, currentStringValue);

            currentStringValue = AnnotationValueHandler.getStringValue(annotation, ColumnField.UPDATABLE, "true");
            updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_UPDATABLE_FIELD, annotation, currentStringValue);
        }
    }

    private void updateGeneratedValueField(Annotation annotation) {

        clearGeneratedValueField();
        if (annotation != null) {

            DataModelerPropertyEditorFieldInfo fieldInfo = getField(JPADataObjectFieldEditorView.GENERATED_VALUE_FIELD);
            String strategy = AnnotationValueHandler.getStringValue(annotation, IdGeneratorField.STRATEGY, null);
            String generator = AnnotationValueHandler.getStringValue(annotation, IdGeneratorField.GENERATOR, null);

            fieldInfo.setCurrentValue(IdGeneratorField.STRATEGY, strategy);
            fieldInfo.setCurrentValue(IdGeneratorField.GENERATOR, generator);

            updatePropertyEditorField(JPADataObjectFieldEditorView.GENERATED_VALUE_FIELD, annotation, strategy != null ? strategy : IdGeneratorField.NOT_CONFIGURED_LABEL);
        }
    }

    private void updateSequenceGeneratorField(Annotation annotation) {

        clearSequenceGeneratorField();
        if (annotation != null) {

            SequenceGeneratorValueHandler valueHandler = new SequenceGeneratorValueHandler(annotation);
            DataModelerPropertyEditorFieldInfo fieldInfo = getField(JPADataObjectFieldEditorView.SEQUENCE_GENERATOR_FIELD);

            fieldInfo.setCurrentValue(SequenceGeneratorValueHandler.NAME, valueHandler.getName());
            fieldInfo.setCurrentValue(SequenceGeneratorValueHandler.SEQUENCE_NAME, valueHandler.getSequenceName());
            fieldInfo.setCurrentValue(SequenceGeneratorValueHandler.INITIAL_VALUE, valueHandler.getInitialValue());
            fieldInfo.setCurrentValue(SequenceGeneratorValueHandler.ALLOCATION_SIZE, valueHandler.getAllocationSize());

            updatePropertyEditorField(JPADataObjectFieldEditorView.SEQUENCE_GENERATOR_FIELD, annotation, valueHandler.getName());
        }
    }

    private void updateRelationshipField(Annotation annotation) {

        clearRelationshipField();
        if (annotation != null) {

            RelationshipAnnotationValueHandler valueHandler = new RelationshipAnnotationValueHandler(annotation);
            DataModelerPropertyEditorFieldInfo fieldInfo = getField(JPADataObjectFieldEditorView.RELATIONSHIP_TYPE_FIELD);

            fieldInfo.setCurrentValue(RelationshipAnnotationValueHandler.RELATION_TYPE, valueHandler.getRelationType());
            fieldInfo.setCurrentValue(RelationshipAnnotationValueHandler.CASCADE, valueHandler.getCascade());
            fieldInfo.setCurrentValue(RelationshipAnnotationValueHandler.FETCH, valueHandler.getFetch());
            fieldInfo.setCurrentValue(RelationshipAnnotationValueHandler.OPTIONAL, valueHandler.getOptional());
            fieldInfo.setCurrentValue(RelationshipAnnotationValueHandler.MAPPED_BY, valueHandler.getMappedBy());
            fieldInfo.setCurrentValue(RelationshipAnnotationValueHandler.ORPHAN_REMOVAL, valueHandler.getOrphanRemoval());

            if (valueHandler.isOneToMany()) {
                fieldInfo.removeCurrentValue(RelationshipAnnotationValueHandler.OPTIONAL);
            } else if (valueHandler.isManyToOne()) {
                fieldInfo.removeCurrentValue(RelationshipAnnotationValueHandler.MAPPED_BY);
                fieldInfo.removeCurrentValue(RelationshipAnnotationValueHandler.ORPHAN_REMOVAL);
            } else if (valueHandler.isManyToMany()) {
                fieldInfo.removeCurrentValue(RelationshipAnnotationValueHandler.OPTIONAL);
                fieldInfo.removeCurrentValue(RelationshipAnnotationValueHandler.ORPHAN_REMOVAL);
            }

            updatePropertyEditorField(JPADataObjectFieldEditorView.RELATIONSHIP_TYPE_FIELD, annotation, valueHandler.getRelationType().name());
        }
    }

    boolean hasOnlyDefaultValues(Annotation columnAnnotation) {

        String strValue;

        strValue = (String) columnAnnotation.getValue(ColumnField.NAME);
        if (strValue != null && !"".equals(strValue)) {
            return false;
        }

        strValue = AnnotationValueHandler.getStringValue(columnAnnotation, ColumnField.UNIQUE, null);
        if (strValue != null && !"false".equals(strValue)) {
            return false;
        }

        strValue = AnnotationValueHandler.getStringValue(columnAnnotation, ColumnField.NULLABLE, null);
        if (strValue != null && !"true".equals(strValue)) {
            return false;
        }

        strValue = AnnotationValueHandler.getStringValue(columnAnnotation, ColumnField.INSERTABLE, null);
        if (strValue != null && !"true".equals(strValue)) {
            return false;
        }

        strValue = AnnotationValueHandler.getStringValue(columnAnnotation, ColumnField.UPDATABLE, null);
        if (strValue != null && !"true".equals(strValue)) {
            return false;
        }

        return true;
    }

    public void clear() {
        clearIdentifierField();
        clearGeneratedValueField();
        clearSequenceGeneratorField();
        clearColumnFields();
        clearRelationshipField();
    }

    protected void clearIdentifierField() {
        updatePropertyEditorField(JPADataObjectFieldEditorView.IDENTIFIER_FIELD, null, "false");
    }

    protected void clearGeneratedValueField() {
        updatePropertyEditorField(JPADataObjectFieldEditorView.GENERATED_VALUE_FIELD, null, IdGeneratorField.NOT_CONFIGURED_LABEL);
        getField(JPADataObjectFieldEditorView.GENERATED_VALUE_FIELD).clearCurrentValues();
    }

    protected void clearSequenceGeneratorField() {
        updatePropertyEditorField(JPADataObjectFieldEditorView.SEQUENCE_GENERATOR_FIELD, null, SequenceGeneratorField.NOT_CONFIGURED_LABEL);
        getField(JPADataObjectFieldEditorView.SEQUENCE_GENERATOR_FIELD).clearCurrentValues();
    }

    protected void clearColumnFields() {
        updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_NAME_FIELD, null, null);
        updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_UNIQUE_FIELD, null, "false");
        updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_INSERTABLE_FIELD, null, "true");
        updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_NULLABLE_FIELD, null, "true");
        updatePropertyEditorField(JPADataObjectFieldEditorView.COLUMN_UPDATABLE_FIELD, null, "true");
    }

    protected void clearRelationshipField() {
        updatePropertyEditorField(JPADataObjectFieldEditorView.RELATIONSHIP_TYPE_FIELD, null, RelationshipField.NOT_CONFIGURED_LABEL);
        getField(JPADataObjectFieldEditorView.RELATIONSHIP_TYPE_FIELD).clearCurrentValues();
    }

    private Annotation getCurrentRelationshipAnnotation(ObjectProperty objectProperty) {
        Annotation annotation;

        if ((annotation = objectField.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_ONE)) != null) {
            return annotation;
        } else if ((annotation = objectField.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_MANY)) != null) {
            return annotation;
        } else if ((annotation = objectField.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_ONE)) != null) {
            return annotation;
        } else if ((annotation = objectField.getAnnotation(JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_MANY)) != null) {
            return annotation;
        }
        return null;
    }

    private String getIdentifierCategoryName() {
        return Constants.INSTANCE.persistence_domain_fieldEditor_identifier_category();
    }

    private String getColumnCategoryName() {
        return Constants.INSTANCE.persistence_domain_fieldEditor_column_category();
    }

    private String getRelationshipCategoryName() {
        return Constants.INSTANCE.persistence_domain_fieldEditor_relationship_category();
    }
}