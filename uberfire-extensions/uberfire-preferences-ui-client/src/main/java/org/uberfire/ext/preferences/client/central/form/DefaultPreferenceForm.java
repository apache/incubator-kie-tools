/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.central.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldOption;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.preferences.shared.PropertyFormOptions;
import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;
import org.uberfire.preferences.shared.impl.validation.ValidationResult;

import com.google.gwt.core.client.GWT;

@WorkbenchScreen(identifier = DefaultPreferenceForm.IDENTIFIER)
public class DefaultPreferenceForm {

    public static final String IDENTIFIER = "org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm";
    private final View view;
    private TranslationService translationService;
    private String id;
    private String title;
    private BasePreferencePortable<?> preference;
    private PropertyEditorCategory category;
    private PreferenceHierarchyElement<?> hierarchyElement;

    @Inject
    public DefaultPreferenceForm(final View view,
                                 final TranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        id = placeRequest.getParameter("id",
                                       null);
        title = placeRequest.getParameter("title",
                                          null);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsElement getView() {
        return view;
    }

    public void hierarchyItemFormInitializationEvent(@Observes HierarchyItemFormInitializationEvent event) {
        if (preference == null && event.getItemId().equals(id)) {
            preference = event.getPreference();
            hierarchyElement = event.getHierarchyElement();
            view.init(this);
        }
    }

    public PropertyEditorEvent generatePropertyEditorEvent() {
        if (category == null) {
            createPropertiesEditorCategory();
        }

        PropertyEditorEvent event = new PropertyEditorEvent(id,
                                                            category);
        return event;
    }

    private void createPropertiesEditorCategory() {
        category = new PropertyEditorCategory("Properties");
        category.setIdEvent(id);

        for (Map.Entry<String, PropertyFormType> property : preference.getPropertiesTypes().entrySet()) {
            final String propertyName = property.getKey();
            final PropertyEditorType propertyType = getPropertyEditorType(property.getValue());
            final Object propertyValue = preference.get(propertyName);

            final PropertyEditorFieldInfo fieldInfo = createFieldInfo(propertyName,
                                                                      propertyType,
                                                                      propertyValue);
            category.withField(fieldInfo);
        }
    }

    private void fillEnumValues(final Object propertyValue, final PropertyEditorFieldInfo fieldInfo) {
        if (propertyValue instanceof Enum) {
            Enum<?>[] enumConstants = ((Enum<?>) propertyValue).getDeclaringClass().getEnumConstants();
            List<String> enumValues = Arrays.stream(enumConstants).map(Object::toString).collect(Collectors.toList());
            fieldInfo.withComboValues(enumValues);
        }
    }

    PropertyEditorFieldInfo createFieldInfo(final String propertyName,
                                            final PropertyEditorType propertyType,
                                            final Object propertyValue) {
        final PropertyEditorFieldInfo fieldInfo = new PropertyEditorFieldInfo(translationService.format(hierarchyElement.getBundleKeyByProperty().get(propertyName)),
                                                                              propertyValue != null ? propertyValue.toString() : "",
                                                                              propertyType);

        setupFieldValidators(propertyName,
                             fieldInfo);
        setupFieldHelpText(propertyName,
                           fieldInfo);
        setupFieldOptions(propertyName,
                          fieldInfo);
        setupFieldKey(propertyName,
                      fieldInfo);
        
        if (propertyType == PropertyEditorType.COMBO) {
            fillEnumValues(propertyValue, fieldInfo);
        }

        return fieldInfo;
    }

    private void setupFieldOptions(final String propertyName,
                                   final PropertyEditorFieldInfo fieldInfo) {
        for (PropertyFormOptions option : hierarchyElement.getFormOptionsByProperty().get(propertyName)) {
            fieldInfo.withOptions(PropertyEditorFieldOption.valueOf(option.name()));
        }
    }

    private void setupFieldHelpText(final String propertyName,
                                    final PropertyEditorFieldInfo fieldInfo) {
        final String helpText = hierarchyElement.getHelpBundleKeyByProperty().get(propertyName);
        if (helpText != null && !helpText.isEmpty()) {
            fieldInfo.withHelpInfo("", translationService.format(helpText));
        }
    }

    private void setupFieldValidators(final String propertyName,
                                      final PropertyEditorFieldInfo fieldInfo) {
        final List<PropertyFieldValidator> propertyFieldValidators = new ArrayList<>();

        preference.getPropertyValidators(propertyName).stream()
                .forEach(validator -> propertyFieldValidators.add(new PropertyFieldValidator() {
                    private ValidationResult validationResult;

                    @Override
                    public boolean validate(Object value) {
                        validationResult = validator.validate(value);
                        return validationResult.isValid();
                    }

                    @Override
                    public String getValidatorErrorMessage() {
                        final List<String> validationMessages = validationResult.getMessagesBundleKeys();
                        if (!validationResult.isValid() && !validationMessages.isEmpty()) {
                            return translationService.format(validationMessages.get(0));
                        }

                        return "";
                    }
                }));

        final int validatorsSize = propertyFieldValidators.size();
        final PropertyFieldValidator[] emptyValidatorsArray = new PropertyFieldValidator[validatorsSize];
        final PropertyFieldValidator[] validators = propertyFieldValidators.toArray(emptyValidatorsArray);

        fieldInfo.withValidators(validators);
    }

    private void setupFieldKey(final String propertyName,
                               final PropertyEditorFieldInfo fieldInfo) {
        fieldInfo.withKey(propertyName);
    }

    public void propertyChanged(@Observes PropertyEditorChangeEvent event) {
        if (event.getProperty().getEventId().equals(id)) {
            final String propertyName = event.getProperty().getKey();
            final PropertyFormType propertyType = preference.getPropertyType(propertyName);
            Object newValue = propertyType.fromString(event.getNewValue());
            Object currentValue = preference.get(propertyName);
            if (currentValue instanceof Enum) {
                newValue = getEnumValue(event, currentValue);
            } 
            preference.set(propertyName,
                           newValue);
            
        }
    }

    private Enum<?> getEnumValue(PropertyEditorChangeEvent event, Object currentValue) {
        String selectedValue = event.getProperty().getCurrentStringValue();
        Object[] enumConstants = ((Enum<?>) currentValue).getDeclaringClass().getEnumConstants();
        Enum<?> enumValue = Arrays.stream(enumConstants)
            .filter(e -> e.toString().equals(selectedValue))
            .map(e -> (Enum<?>) e)
            .findFirst().orElseThrow(RuntimeException::new);
        return enumValue;
    }

    public void saveEvent(@Observes PreferencesCentralSaveEvent event) {
        createPropertiesEditorCategory();
        view.init(this);
    }

    public void undoEvent(@Observes PreferencesCentralUndoChangesEvent event) {
        category.undo();
        view.init(this);
    }

    public BasePreferencePortable<?> getPreference() {
        return preference;
    }

    public PropertyEditorType getPropertyEditorType(PropertyFormType propertyFormType) {
        return PropertyEditorType.valueOf(propertyFormType.name());
    }

    public interface View extends UberElement<DefaultPreferenceForm>,
                                  IsElement {

    }
}