/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.layout.editor.client.infra;

import org.uberfire.ext.layout.editor.api.css.*;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutElementWithProperties;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.resources.i18n.CssAllowedValueConstants;
import org.uberfire.ext.layout.editor.client.resources.i18n.PropertiesConstants;
import org.uberfire.ext.layout.editor.client.validator.LayoutEditorPropertyLengthValidator;
import org.uberfire.ext.layout.editor.client.validator.LayoutEditorPropertyNumberValidator;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.properties.editor.model.validators.PropertyFieldValidator;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class LayoutEditorCssHelper {

    private Map<CssValueType,PropertyFieldValidator> valueTypeValidatorMap = new HashMap<>();
    private Map<String,String> propertyCategoryMapI18n = new HashMap<>();
    private Map<CssProperty,String> propertyNameMapI18n = new HashMap<>();
    private Map<CssProperty,String> propertyHelpMapI18n = new HashMap<>();

    public static final String CSS_CATEGORY_PANEL = "panel";
    public static final String CSS_CATEGORY_TEXT = "text";
    public static final String CSS_CATEGORY_MARGIN = "margin";
    public static final String CSS_CATEGORY_PADDING = "padding";

    public LayoutEditorCssHelper() {
        valueTypeValidatorMap.put(CssValueType.LENGTH, new LayoutEditorPropertyLengthValidator());
        valueTypeValidatorMap.put(CssValueType.NUMBER, new LayoutEditorPropertyNumberValidator());

        propertyCategoryMapI18n.put(CSS_CATEGORY_PANEL, PropertiesConstants.INSTANCE.panel());
        propertyCategoryMapI18n.put(CSS_CATEGORY_TEXT, PropertiesConstants.INSTANCE.text());
        propertyCategoryMapI18n.put(CSS_CATEGORY_MARGIN, PropertiesConstants.INSTANCE.margin());
        propertyCategoryMapI18n.put(CSS_CATEGORY_PADDING, PropertiesConstants.INSTANCE.padding());

        propertyNameMapI18n.put(CssProperty.WIDTH, PropertiesConstants.INSTANCE.width());
        propertyNameMapI18n.put(CssProperty.HEIGHT, PropertiesConstants.INSTANCE.height());
        propertyNameMapI18n.put(CssProperty.BACKGROUND_COLOR, PropertiesConstants.INSTANCE.background_color());
        propertyNameMapI18n.put(CssProperty.MARGIN_TOP, PropertiesConstants.INSTANCE.margin_top());
        propertyNameMapI18n.put(CssProperty.MARGIN_BOTTOM, PropertiesConstants.INSTANCE.margin_bottom());
        propertyNameMapI18n.put(CssProperty.MARGIN_LEFT, PropertiesConstants.INSTANCE.margin_left());
        propertyNameMapI18n.put(CssProperty.MARGIN_RIGHT, PropertiesConstants.INSTANCE.margin_right());
        propertyNameMapI18n.put(CssProperty.PADDING_TOP, PropertiesConstants.INSTANCE.padding_top());
        propertyNameMapI18n.put(CssProperty.PADDING_BOTTOM, PropertiesConstants.INSTANCE.padding_bottom());
        propertyNameMapI18n.put(CssProperty.PADDING_LEFT, PropertiesConstants.INSTANCE.padding_left());
        propertyNameMapI18n.put(CssProperty.PADDING_RIGHT, PropertiesConstants.INSTANCE.padding_right());
        propertyNameMapI18n.put(CssProperty.TEXT_ALIGN, PropertiesConstants.INSTANCE.text_align());
        propertyNameMapI18n.put(CssProperty.TEXT_DECORATION, PropertiesConstants.INSTANCE.text_decoration());
        propertyNameMapI18n.put(CssProperty.COLOR, PropertiesConstants.INSTANCE.color());
        propertyNameMapI18n.put(CssProperty.FONT_SIZE, PropertiesConstants.INSTANCE.font_size());
        propertyNameMapI18n.put(CssProperty.FONT_WEIGHT, PropertiesConstants.INSTANCE.font_weight());

        propertyHelpMapI18n.put(CssProperty.WIDTH, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.HEIGHT, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.MARGIN_TOP, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.MARGIN_BOTTOM, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.MARGIN_LEFT, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.MARGIN_RIGHT, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.PADDING_TOP, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.PADDING_BOTTOM, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.PADDING_LEFT, PropertiesConstants.INSTANCE.length_help());
        propertyHelpMapI18n.put(CssProperty.PADDING_RIGHT, PropertiesConstants.INSTANCE.length_help());
    }

    public String formatCssAllowedValue(CssProperty cssProperty) {
        return propertyNameMapI18n.get(cssProperty);
    }

    public String getHelpText(CssProperty cssProperty) {
        return propertyHelpMapI18n.get(cssProperty);
    }

    public List<PropertyFieldValidator> getValidators(CssProperty property) {
        List<PropertyFieldValidator> result = new ArrayList<>();
        for (CssValueType cssValueType : property.getSupportedValueTypes()) {
            PropertyFieldValidator validator = valueTypeValidatorMap.get(cssValueType);
            if (validator != null) {
                result.add(validator);
            }
        }
        return result;
    }

    public PropertyEditorType resolveFieldType(CssProperty cssProperty) {
        if (cssProperty.supportsValueType(CssValueType.COLOR)) {
            return PropertyEditorType.COLOR;
        }
        if (!cssProperty.getAllowedValues().isEmpty()) {
            return PropertyEditorType.COMBO;
        }
        return PropertyEditorType.TEXT;
    }

    public PropertyEditorCategory createCategory(String categoryCode) {
        String name = propertyCategoryMapI18n.get(categoryCode);
        return new PropertyEditorCategory(name);
    }

    public PropertyEditorFieldInfo createField(LayoutElementWithProperties element, CssProperty property) {
        return createField(element.getProperties(), property);
    }

    public PropertyEditorFieldInfo createField(Map<String,String> propertyMap, CssProperty property) {
        String propValue = propertyMap.get(property.getName());
        return createPropertyField(property, propValue == null ? "" : propValue);
    }

    public PropertyEditorFieldInfo createPropertyField(CssProperty property, String value) {
        String displayName = propertyNameMapI18n.get(property);
        String helpText = propertyHelpMapI18n.get(property);
        PropertyEditorType propType = resolveFieldType(property);
        List<PropertyFieldValidator> validators = getValidators(property);
        List<CssAllowedValue> allowedValues = property.getAllowedValues();
        CssAllowedValue allowedValue = parseCssAllowedValue(property, value);
        String currentValue = allowedValue != null ? formatCssAllowedValue(property, allowedValue) : value;

        PropertyEditorFieldInfo field = new PropertyEditorFieldInfo(displayName, currentValue, propType)
                .withKey(property.getName());

        if (helpText != null) {
            field.withHelpInfo("", helpText);
        }
        if (validators != null && !validators.isEmpty()) {
            field.withValidators(validators);
        }
        if (allowedValues != null && !allowedValues.isEmpty()) {
            field.withComboValues(allowedValues.stream()
                    .map(v -> formatCssAllowedValue(property, v))
                    .collect(Collectors.toList()));
        }

        return field;
    }

    public String formatCssAllowedValue(CssProperty property, CssAllowedValue value) {
        String i18nKey = property.toString() + "__" + value.toString();
        return CssAllowedValueConstants.INSTANCE.getString(i18nKey);
    }

    public CssAllowedValue parseCssAllowedValue(String property, String value) {
        CssProperty cssProperty = CssProperty.get(property);
        if (cssProperty != null) {
            return parseCssAllowedValue(cssProperty, value);
        }
        return null;
    }

    public CssAllowedValue parseCssAllowedValue(CssProperty property, String value) {
        for (CssAllowedValue cssAllowedValue : property.getAllowedValues()) {
            String targetValue = formatCssAllowedValue(property, cssAllowedValue);
            if (value.equals(targetValue)) {
                return cssAllowedValue;
            }
        }
        return null;
    }

    public List<CssValue> readCssValues(Map<String,String> propertyMap) {
        if (propertyMap == null || propertyMap.isEmpty()) {
            return new ArrayList<>();
        }

        return propertyMap.entrySet().stream()
            .filter(entry -> CssProperty.get(entry.getKey()) != null)
            .map(entry -> new CssValue(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    // Layout element properties

    public List<PropertyEditorCategory> getContainerPropertyCategories(Container container) {
        List<PropertyEditorCategory> result = new ArrayList<>();

        PropertyEditorCategory category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_PANEL);
        category.withField(createField(container, CssProperty.WIDTH));
        category.withField(createField(container, CssProperty.HEIGHT));
        category.withField(createField(container, CssProperty.BACKGROUND_COLOR));
        result.add(category);

        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_MARGIN);
        category.withField(createField(container, CssProperty.MARGIN_TOP));
        category.withField(createField(container, CssProperty.MARGIN_BOTTOM));
        category.withField(createField(container, CssProperty.MARGIN_LEFT));
        category.withField(createField(container, CssProperty.MARGIN_RIGHT));
        result.add(category);

        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_PADDING);
        category.withField(createField(container, CssProperty.PADDING_TOP));
        category.withField(createField(container, CssProperty.PADDING_BOTTOM));
        category.withField(createField(container, CssProperty.PADDING_LEFT));
        category.withField(createField(container, CssProperty.PADDING_RIGHT));
        result.add(category);

        return result;
    }

    public List<PropertyEditorCategory> getRowPropertyCategories(Row row) {
        List<PropertyEditorCategory> result = new ArrayList<>();

        PropertyEditorCategory category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_PANEL);
        category.withField(createField(row, CssProperty.WIDTH));
        category.withField(createField(row, CssProperty.HEIGHT));
        category.withField(createField(row, CssProperty.BACKGROUND_COLOR));
        result.add(category);

        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_MARGIN);
        category.withField(createField(row, CssProperty.MARGIN_TOP));
        category.withField(createField(row, CssProperty.MARGIN_BOTTOM));
        category.withField(createField(row, CssProperty.MARGIN_LEFT));
        category.withField(createField(row, CssProperty.MARGIN_RIGHT));
        result.add(category);

        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_PADDING);
        category.withField(createField(row, CssProperty.PADDING_TOP));
        category.withField(createField(row, CssProperty.PADDING_BOTTOM));
        category.withField(createField(row, CssProperty.PADDING_LEFT));
        category.withField(createField(row, CssProperty.PADDING_RIGHT));
        result.add(category);

        return result;
    }

    public List<PropertyEditorCategory> getComponentPropertyCategories(LayoutComponent layoutComponent) {
        List<PropertyEditorCategory> result = new ArrayList<>();
        Map<String,String> propertyMap = layoutComponent.getProperties();

        PropertyEditorCategory category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_PANEL);
        category.withField(createField(propertyMap, CssProperty.WIDTH));
        category.withField(createField(propertyMap, CssProperty.HEIGHT));
        category.withField(createField(propertyMap, CssProperty.BACKGROUND_COLOR));
        result.add(category);

        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_MARGIN);
        category.withField(createField(propertyMap, CssProperty.MARGIN_TOP));
        category.withField(createField(propertyMap, CssProperty.MARGIN_BOTTOM));
        category.withField(createField(propertyMap, CssProperty.MARGIN_LEFT));
        category.withField(createField(propertyMap, CssProperty.MARGIN_RIGHT));
        result.add(category);

        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_PADDING);
        category.withField(createField(propertyMap, CssProperty.PADDING_TOP));
        category.withField(createField(propertyMap, CssProperty.PADDING_BOTTOM));
        category.withField(createField(propertyMap, CssProperty.PADDING_LEFT));
        category.withField(createField(propertyMap, CssProperty.PADDING_RIGHT));
        result.add(category);

        return result;
    }
    
    public List<PropertyEditorCategory> allCategories(Map<String,String> propertyMap) {
        List<PropertyEditorCategory> result = new ArrayList<>();

        PropertyEditorCategory category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_PANEL);
        category.withField(createField(propertyMap, CssProperty.WIDTH));
        category.withField(createField(propertyMap, CssProperty.HEIGHT));
        category.withField(createField(propertyMap, CssProperty.BACKGROUND_COLOR));
        result.add(category);

        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_MARGIN);
        category.withField(createField(propertyMap, CssProperty.MARGIN_TOP));
        category.withField(createField(propertyMap, CssProperty.MARGIN_BOTTOM));
        category.withField(createField(propertyMap, CssProperty.MARGIN_LEFT));
        category.withField(createField(propertyMap, CssProperty.MARGIN_RIGHT));
        result.add(category);

        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_PADDING);
        category.withField(createField(propertyMap, CssProperty.PADDING_TOP));
        category.withField(createField(propertyMap, CssProperty.PADDING_BOTTOM));
        category.withField(createField(propertyMap, CssProperty.PADDING_LEFT));
        category.withField(createField(propertyMap, CssProperty.PADDING_RIGHT));
        result.add(category);
        
        category = createCategory(LayoutEditorCssHelper.CSS_CATEGORY_TEXT);
        category.withField(createField(propertyMap, CssProperty.FONT_SIZE));
        category.withField(createField(propertyMap, CssProperty.FONT_WEIGHT));
        category.withField(createField(propertyMap, CssProperty.TEXT_ALIGN));
        category.withField(createField(propertyMap, CssProperty.TEXT_DECORATION));
        category.withField(createField(propertyMap, CssProperty.COLOR));
        result.add(category);

        return result;
    }    
}
