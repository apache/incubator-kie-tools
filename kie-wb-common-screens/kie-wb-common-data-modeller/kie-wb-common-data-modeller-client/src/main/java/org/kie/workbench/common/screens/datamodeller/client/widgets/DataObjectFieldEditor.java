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

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.*;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorCallback;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;

public class DataObjectFieldEditor extends Composite {

    interface DataObjectFieldEditorUIBinder
            extends UiBinder<Widget, DataObjectFieldEditor> {

    };

    private static DataObjectFieldEditorUIBinder uiBinder = GWT.create(DataObjectFieldEditorUIBinder.class);

    @UiField
    Label titleLabel;

    @UiField
    TextBox name;

    @UiField
    TextBox label;

    @UiField
    TextArea description;

    @UiField
    ListBox typeSelector;

    @UiField
    CheckBox equalsSelector;

    @UiField
    Label positionLabel;

    @UiField
    Icon positionHelpIcon;

    @UiField
    TextBox positionText;

    @UiField
    ListBox positionSelector;

    @Inject
    Event<DataModelerEvent> dataModelerEventEvent;

    @Inject
    private ValidatorService validatorService;

    private DataObjectFieldEditorErrorPopup ep = new DataObjectFieldEditorErrorPopup();

    private Map<String, AnnotationDefinitionTO> annotationDefinitions = new HashMap<String, AnnotationDefinitionTO>();

    private DataObjectTO dataObject;

    private ObjectPropertyTO objectField;

    private DataModelerContext context;

    public DataObjectFieldEditor() {
        initWidget(uiBinder.createAndBindUi(this));

        typeSelector.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                typeChanged(event);
            }
        });

        positionSelector.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                positionChanged(event);
            }
        });

        positionHelpIcon.getElement().getStyle().setPaddingLeft(4, Style.Unit.PX);
        positionHelpIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
    }

    public DataObjectTO getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObjectTO dataObject) {
        this.dataObject = dataObject;
    }

    public ObjectPropertyTO getObjectField() {
        return objectField;
    }

    public void setObjectField(ObjectPropertyTO objectField) {
        this.objectField = objectField;
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext(DataModelerContext context) {
        this.context = context;
        initTypeList();
    }

    private DataModelTO getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    // Event notifications

    private void notifyFieldChange(String memberName, Object oldValue, Object newValue) {
        DataObjectFieldChangeEvent changeEvent = new DataObjectFieldChangeEvent(DataModelerEvent.DATA_OBJECT_FIELD_EDITOR, getDataModel(), getDataObject(), getObjectField(), memberName, oldValue, newValue);
        // Notify helper directly
        getContext().getHelper().dataModelChanged(changeEvent);
        dataModelerEventEvent.fire(changeEvent);
    }

    // Event observers
    private void onFieldSelected(@Observes DataObjectFieldSelectedEvent event) {
        if (event.isFrom(getDataModel())) {
            loadDataObjectField(event.getCurrentDataObject(), event.getCurrentField());
        }
    }

    private void onDataObjectFieldDeleted(@Observes DataObjectFieldDeletedEvent event) {
        // When all attributes from the current object have been deleted clean
        if (event.isFrom(getDataModel())) {
            if (getDataObject().getProperties().size() == 0) {
                clean();
            }
        }
    }

    private void onDataObjectChange(@Observes DataObjectChangeEvent event) {
        if (event.isFrom(getDataModel())) {
            if ("name".equals(event.getPropertyName()) ||
                "packageName".equals(event.getPropertyName()) ||
                "label".equals(event.getPropertyName())) {

                initTypeList();
                setSelectedType();
            }
        }
    }

    private void onDataObjectCreated(@Observes DataObjectCreatedEvent event) {
        if (event.isFrom(getDataModel())) {
            initTypeList();
        }
    }

    private void onDataObjectDeleted(@Observes DataObjectDeletedEvent event) {
        if (event.isFrom(getDataModel())) {
            initTypeList();
            if (getDataModel() != null && getDataModel().getDataObjects().size() == 0) {
                clean();
                setDataObject(null);
                setObjectField(null);
            }
        }
    }

    private void onDataObjectSelected(@Observes DataObjectSelectedEvent event) {
        if (event.isFrom(getDataModel())) {
            clean();
            setDataObject(event.getCurrentDataObject());
            setObjectField(null);
        }
    }

    private void loadDataObjectField(DataObjectTO dataObject, ObjectPropertyTO objectField) {
        clean();
        initTypeList();
        if (dataObject != null && objectField != null) {
            setDataObject(dataObject);
            setObjectField(objectField);

            initPositions();

            name.setText(getObjectField().getName());

            AnnotationTO annotation = objectField.getAnnotation(AnnotationDefinitionTO.LABEL_ANNOTATION);
            if (annotation != null) {
                label.setText( (String) annotation.getValue(AnnotationDefinitionTO.VALUE_PARAM) );
            }

            annotation = objectField.getAnnotation(AnnotationDefinitionTO.DESCRIPTION_ANNOTATION);
            if (annotation != null) {
                description.setText( (String) annotation.getValue(AnnotationDefinitionTO.VALUE_PARAM));
            }

            setSelectedType();

            annotation = objectField.getAnnotation(AnnotationDefinitionTO.EQUALS_ANNOTATION);
            if (annotation != null) {
                equalsSelector.setValue(Boolean.TRUE);
            }

            annotation = objectField.getAnnotation(AnnotationDefinitionTO.POSITION_ANNOTATON);
            if (annotation != null) {
                String position = (String) annotation.getValue(AnnotationDefinitionTO.VALUE_PARAM);
                positionText.setText(position);
                positionSelector.setSelectedValue(position);
            }
        }
    }

    // TODO listen to DataObjectFieldDeletedEvent?

    // Event handlers
    @UiHandler("name")
    void nameChanged(ValueChangeEvent<String> event) {
        if (getObjectField() == null) return;
        // Set widgets to errorpopup for styling purposes etc.
        ep.setTitleWidget(titleLabel);
        ep.setValueWidget(name);

        final String oldValue = getObjectField().getName();
        final String newValue = DataModelerUtils.getInstance().unCapitalize(name.getValue());

        // In case an invalid name (entered before), was corrected to the original value, don't do anything but reset the label style
        if (oldValue.equalsIgnoreCase(name.getValue())) {
            name.setText(oldValue);
            titleLabel.setStyleName(null);
            return;
        }

        validatorService.isValidIdentifier(newValue, new ValidatorCallback() {
            @Override
            public void onFailure() {
                ep.showMessage(Constants.INSTANCE.validation_error_invalid_object_attribute_identifier(newValue));
            }

            @Override
            public void onSuccess() {
                validatorService.isUniqueAttributeName(newValue, getDataObject(), new ValidatorCallback() {
                    @Override
                    public void onFailure() {
                        ep.showMessage(Constants.INSTANCE.validation_error_object_attribute_already_exists(newValue));
                    }

                    @Override
                    public void onSuccess() {
                        titleLabel.setStyleName(null);
                        objectField.setName(newValue);
                        notifyFieldChange("name", oldValue, newValue);
                    }
                });
            }
        });
    }

    @UiHandler("label")
    void labelChanged(final ValueChangeEvent<String> event) {
        if (getObjectField() == null) return;

        String oldValue = null;
        final String _label = label.getValue();
        AnnotationTO annotation = getObjectField().getAnnotation(AnnotationDefinitionTO.LABEL_ANNOTATION);

        if (annotation != null) {
            oldValue = AnnotationValueHandler.getInstance().getStringValue(annotation, AnnotationDefinitionTO.VALUE_PARAM);
            if ( _label != null && !"".equals(_label) ) annotation.setValue(AnnotationDefinitionTO.VALUE_PARAM, _label);
            else getObjectField().removeAnnotation(annotation);
        } else {
            if ( _label != null && !"".equals(_label) ) {
                getObjectField().addAnnotation(getContext().getAnnotationDefinitions().get(AnnotationDefinitionTO.LABEL_ANNOTATION), AnnotationDefinitionTO.VALUE_PARAM, _label );
            }
        }
        // TODO replace 'label' literal with annotation definition constant
        notifyFieldChange("label", oldValue, _label);
    }

    @UiHandler("description")
    void descriptionChanged(final ValueChangeEvent<String> event) {
        if (getObjectField() == null) return;

        String oldValue = null;
        final String _description = description.getValue();
        AnnotationTO annotation = getObjectField().getAnnotation(AnnotationDefinitionTO.DESCRIPTION_ANNOTATION);

        if (annotation != null) {
            oldValue = AnnotationValueHandler.getInstance().getStringValue(annotation, AnnotationDefinitionTO.VALUE_PARAM);
            if ( _description != null && !"".equals(_description) ) annotation.setValue(AnnotationDefinitionTO.VALUE_PARAM, _description);
            else getObjectField().removeAnnotation(annotation);
        } else {
            if ( _description != null && !"".equals(_description) ) {
                getObjectField().addAnnotation(getContext().getAnnotationDefinitions().get(AnnotationDefinitionTO.DESCRIPTION_ANNOTATION), AnnotationDefinitionTO.VALUE_PARAM, _description );
            }
        }
        notifyFieldChange(AnnotationDefinitionTO.DESCRIPTION_ANNOTATION, oldValue, _description);
    }

    private void typeChanged(ChangeEvent event) {
        if (getObjectField() == null) return;

        String oldValue = getObjectField().getClassName();
        String type = typeSelector.getValue();
        if (DataModelerUtils.isMultipleType(type)) {
            type = DataModelerUtils.getCanonicalClassName(type);
            getObjectField().setMultiple(true);
        } else {
            getObjectField().setMultiple(false);
        }
        getObjectField().setClassName(type);

        if (getContext().getHelper().isBaseType(type)) {
            getObjectField().setBaseType(true);
        } else {
            // Un-reference former type reference and set the new one
            getObjectField().setBaseType(false);
            getContext().getHelper().dataObjectUnReferenced(oldValue, getDataObject().getClassName());
            getContext().getHelper().dataObjectReferenced(type, getDataObject().getClassName());
        }
        notifyFieldChange("className", oldValue, type);
    }

    @UiHandler("equalsSelector")
    void equalsChanged(final ClickEvent event) {
        if (getObjectField() == null) return;

        Boolean oldEquals = null;
        AnnotationTO annotation = getObjectField().getAnnotation(AnnotationDefinitionTO.EQUALS_ANNOTATION);
        if (annotation != null) {
            Object annotationValue = annotation.getValue(AnnotationDefinitionTO.VALUE_PARAM);
            oldEquals = annotationValue != null ? (Boolean) annotationValue : Boolean.FALSE;
        }
        final Boolean setEquals = equalsSelector.getValue();

        if (annotation != null && !setEquals) getObjectField().removeAnnotation(annotation);
        else if (annotation == null && setEquals) getObjectField().addAnnotation(new AnnotationTO(getContext().getAnnotationDefinitions().get(AnnotationDefinitionTO.EQUALS_ANNOTATION)));
        notifyFieldChange(AnnotationDefinitionTO.EQUALS_ANNOTATION, oldEquals, setEquals);
    }

    void positionChanged(ChangeEvent event) {

    }

    @UiHandler("positionText")
    void positionChanged(final ValueChangeEvent<String> event) {
        if (getObjectField() == null) return;

        // Set widgets to errorpopup for styling purposes etc.
        ep.setTitleWidget(positionLabel);
        ep.setValueWidget(positionText);

        AnnotationTO annotation = getObjectField().getAnnotation(AnnotationDefinitionTO.POSITION_ANNOTATON);
        final String oldPosition = (annotation != null) ? annotation.getValue(AnnotationDefinitionTO.VALUE_PARAM).toString() : "";
        final String newPosition = positionText.getValue();

        // In case an invalid position (entered before), was corrected to the original value, don't do anything but reset the label style
        if (oldPosition.equalsIgnoreCase(newPosition)) {
            positionLabel.setStyleName(null);
            return;
        }

        validatorService.isValidPosition(newPosition, new ValidatorCallback() {
            @Override
            public void onFailure() {
                ep.showMessage(Constants.INSTANCE.validation_error_invalid_position());
            }

            @Override
            public void onSuccess() {
                AnnotationTO annotation = getObjectField().getAnnotation(AnnotationDefinitionTO.POSITION_ANNOTATON);

                if (annotation != null) {
                    if ( newPosition != null && !"".equals(newPosition) ) annotation.setValue(AnnotationDefinitionTO.VALUE_PARAM, newPosition);
                    else getObjectField().removeAnnotation(annotation);
                } else {
                    if ( newPosition != null && !"".equals(newPosition) ) {
                        getObjectField().addAnnotation(getContext().getAnnotationDefinitions().get(AnnotationDefinitionTO.POSITION_ANNOTATON), AnnotationDefinitionTO.VALUE_PARAM, newPosition );
                    }
                }
                notifyFieldChange(AnnotationDefinitionTO.POSITION_ANNOTATON, oldPosition, newPosition);
            }
        });
    }

    private void initTypeList() {
        typeSelector.clear();

        SortedMap<String, String> typeNames = new TreeMap<String, String>();
        if (getDataModel() != null) {
            // First add all base types, ordered
            for (Map.Entry<String, String> baseType : getContext().getHelper().getOrderedBaseTypes().entrySet()) {
                typeSelector.addItem(baseType.getKey(), baseType.getValue());
                // TODO add multiple types for base types?
            }

            // Second add all model types, ordered
            for (DataObjectTO dataObject : getDataModel().getDataObjects()) {
                String className = dataObject.getClassName();
                String className_m = className + DataModelerUtils.MULTIPLE;
                String classLabel = DataModelerUtils.getDataObjectFullLabel(dataObject);
                String classLabel_m = classLabel  + DataModelerUtils.MULTIPLE;
                typeNames.put(classLabel, className);
                typeNames.put(classLabel_m, className_m);
            }
            for (Map.Entry<String, String> typeName : typeNames.entrySet()) {
                typeSelector.addItem(typeName.getKey(), typeName.getValue());
            }

            // Then add all external types, ordered
            typeNames.clear();
            for (String extClass : getDataModel().getExternalClasses()) {
                String extClass_m = extClass + DataModelerUtils.MULTIPLE;
                typeNames.put(DataModelerUtils.EXTERNAL_PREFIX + extClass, extClass);
                typeNames.put(DataModelerUtils.EXTERNAL_PREFIX + extClass_m, extClass_m);
            }
            for (Map.Entry<String, String> typeName : typeNames.entrySet()) {
                typeSelector.addItem(typeName.getKey(), typeName.getValue());
            }
        }
    }

    private void setSelectedType() {
        String type = getObjectField() != null ?
                (getObjectField().getClassName() + (getObjectField().isMultiple() ? DataModelerUtils.MULTIPLE:"")) : null;
        typeSelector.setSelectedValue(type);
    }

    private void initPositions() {
        positionSelector.clear();
        List<ObjectPropertyTO> properties = null;
        if (getDataModel() != null && getDataObject() != null && (properties = getDataObject().getProperties()) != null && properties.size() > 0) {
            SortedMap<Integer, String> positions = new TreeMap<Integer, String>();
            String positionValue;
            Integer positionIntValue;
            for (ObjectPropertyTO propertyTO : properties) {
                positionValue = AnnotationValueHandler.getInstance().getStringValue(propertyTO, AnnotationDefinitionTO.POSITION_ANNOTATON, "value");
                if (positionValue != null) {
                    try {
                        positionIntValue = new Integer(positionValue);
                        positions.put(positionIntValue, positionValue);
                    } catch (NumberFormatException e) {
                    }
                }
            }
            for (Map.Entry<Integer, String> position : positions.entrySet()) {
                positionSelector.addItem(position.getValue(), position.getValue());
            }
        }
    }

    private void clean() {
        titleLabel.setStyleName(null);
        name.setText(null);
        label.setText(null);
        description.setText(null);
        typeSelector.setSelectedValue(null);
        equalsSelector.setValue(Boolean.FALSE);
        positionLabel.setStyleName(null);
        positionText.setText(null);
        positionSelector.clear();
    }

    // TODO extract this to parent widget to avoid duplicate code
    private class DataObjectFieldEditorErrorPopup extends ErrorPopup {
        private Widget titleWidget;
        private Widget valueWidget;
        private DataObjectFieldEditorErrorPopup() {
            setAfterCloseEvent(new Command() {
                @Override
                public void execute() {
                    titleWidget.setStyleName("text-error");
                    if (valueWidget instanceof Focusable) ((FocusWidget)valueWidget).setFocus(true);
                    if (valueWidget instanceof ValueBoxBase) ((ValueBoxBase)valueWidget).selectAll();
                    clearWidgets();
                }
            });
        }
        private void setTitleWidget(Widget titleWidget){this.titleWidget = titleWidget;titleWidget.setStyleName(null);}
        private void setValueWidget(Widget valueWidget){this.valueWidget = valueWidget;}
        private void clearWidgets() {
            titleWidget = null;
            valueWidget = null;
        }
    }
}