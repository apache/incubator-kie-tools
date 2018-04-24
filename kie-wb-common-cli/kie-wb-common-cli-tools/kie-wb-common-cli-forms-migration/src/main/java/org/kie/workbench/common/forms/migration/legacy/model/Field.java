/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.migration.legacy.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Definition of a Form Field.
 */
public class Field implements Serializable,
                              Comparable<Field> {

    private Long id;

    private Map<String, String> title;

    private Map<String, String> label;

    private Map<String, String> errorMessage;

    private String fieldName;

    private Boolean fieldRequired;

    private Boolean readonly;

    private Long size;

    private String formula;

    private Boolean groupWithPrevious;

    private String pattern;

    private Long maxlength;

    private String styleclass;

    private String cssStyle;

    private String height;

    private Long tabindex;

    private String accesskey;

    private String rangeFormula;

    private String labelCSSStyle;
    private String labelCSSClass;

    private Boolean isHTML;
    private Boolean hideContent;
    private String defaultValueFormula;

    private Map<String, String> htmlContent;

    private FieldType fieldType;
    private String bag;

    private String inputBinding;
    private String outputBinding;

    private int position;

    //Subform data
    private String defaultSubform;
    private String previewSubform;
    private String tableSubform;

    private Map<String, String> newItemText = new HashMap<>();
    private Map<String, String> addItemText = new HashMap<>();
    private Map<String, String> cancelItemText = new HashMap<>();

    private Boolean deleteItems;
    private Boolean updateItems;
    private Boolean visualizeItem;
    private Boolean hideCreateItem;
    private Boolean expanded;

    private Boolean enableTableEnterData;

    //Custom types
    private String customFieldType;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private String param5;

    private String onChangeScript;

    private Boolean verticalAlignment = Boolean.TRUE;

    private String movedToForm;
    private String sourceLink;

    private Form form;

    public Field() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFieldRequired() {
        return fieldRequired;
    }

    public void setFieldRequired(Boolean fieldRequired) {
        this.fieldRequired = fieldRequired;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public Long getSize() {
        return this.size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getGroupWithPrevious() {
        return groupWithPrevious;
    }

    public void setGroupWithPrevious(Boolean groupWithPrevious) {
        this.groupWithPrevious = groupWithPrevious;
    }

    public Long getMaxlength() {
        return this.maxlength;
    }

    public void setMaxlength(Long maxlength) {
        this.maxlength = maxlength;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public String getStyleclass() {
        return this.styleclass;
    }

    public void setStyleclass(String styleclass) {
        this.styleclass = styleclass;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Long getTabindex() {
        return this.tabindex;
    }

    public void setTabindex(Long tabindex) {
        this.tabindex = tabindex;
    }

    public String getAccesskey() {
        return this.accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public FieldType getFieldType() {
        return this.fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getBag() {
        return bag;
    }

    public void setBag(String bag) {
        this.bag = bag;
    }

    public String getRangeFormula() {
        return rangeFormula;
    }

    public void setRangeFormula(String rangeFormula) {
        this.rangeFormula = rangeFormula;
    }

    public Boolean getIsHTML() {
        return isHTML;
    }

    public void setIsHTML(Boolean HTML) {
        isHTML = HTML;
    }

    public Boolean getHideContent() {
        return hideContent;
    }

    public void setHideContent(Boolean hideContent) {
        this.hideContent = hideContent;
    }

    public String getDefaultValueFormula() {
        return defaultValueFormula;
    }

    public void setDefaultValueFormula(String defaultValueFormula) {
        this.defaultValueFormula = defaultValueFormula;
    }

    //Subform data
    public String getDefaultSubform() {
        return defaultSubform;
    }

    public void setDefaultSubform(String defaultSubform) {
        this.defaultSubform = defaultSubform;
    }

    public String getPreviewSubform() {
        return previewSubform;
    }

    public void setPreviewSubform(String previewSubform) {
        this.previewSubform = previewSubform;
    }

    public String getTableSubform() {
        return tableSubform;
    }

    public void setTableSubform(String tableSubform) {
        this.tableSubform = tableSubform;
    }

    public Map<String, String> getNewItemText() {
        return newItemText;
    }

    public void setNewItemText(Map<String, String> newItemText) {
        this.newItemText = newItemText;
    }

    public Map<String, String> getAddItemText() {
        return addItemText;
    }

    public void setAddItemText(Map<String, String> addItemText) {
        this.addItemText = addItemText;
    }

    public Map<String, String> getCancelItemText() {
        return cancelItemText;
    }

    public void setCancelItemText(Map<String, String> cancelItemText) {
        this.cancelItemText = cancelItemText;
    }

    public Boolean getDeleteItems() {
        return deleteItems;
    }

    public void setDeleteItems(Boolean deleteItems) {
        this.deleteItems = deleteItems;
    }

    public Boolean getUpdateItems() {
        return updateItems;
    }

    public void setUpdateItems(Boolean updateItems) {
        this.updateItems = updateItems;
    }

    public Boolean getVisualizeItem() {
        return visualizeItem;
    }

    public void setVisualizeItem(Boolean visualizeItem) {
        this.visualizeItem = visualizeItem;
    }

    public Boolean getHideCreateItem() {
        return hideCreateItem;
    }

    public void setHideCreateItem(Boolean hideCreateItem) {
        this.hideCreateItem = hideCreateItem;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Boolean getEnableTableEnterData() {
        return enableTableEnterData;
    }

    public void setEnableTableEnterData(Boolean enableTableEnterData) {
        this.enableTableEnterData = enableTableEnterData;
    }

    public String getCustomFieldType() {
        return customFieldType;
    }

    public void setCustomFieldType(String customFieldType) {
        this.customFieldType = customFieldType;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    public String getParam5() {
        return param5;
    }

    public void setParam5(String param5) {
        this.param5 = param5;
    }

    public String getOnChangeScript() {
        return onChangeScript;
    }

    public void setOnChangeScript(String onChangeScript) {
        this.onChangeScript = onChangeScript;
    }

    public String toString() {
        return getId().toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof Field)) {
            return false;
        }
        Field castOther = (Field) other;
        return this.getId().equals(castOther.getId());
    }

    public int hashCode() {
        return getId().hashCode();
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(Map<String, String> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> label) {
        this.label = label;
    }

    public String getMovedToForm() {
        return movedToForm;
    }

    public void setMovedToForm(String movedToForm) {
        this.movedToForm = movedToForm;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public String getFieldPattern() {
        if ((getPattern() == null || "".equals(getPattern())) && getFieldType() != null) {
            return getFieldType().getPattern();
        } else {
            return getPattern();
        }
    }

    public String getFieldFormula() {
        if ((getFormula() == null || "".equals(getFormula())) && getFieldType() != null) {
            return getFieldType().getFormula();
        } else {
            return getFormula();
        }
    }

    public String getFieldRangeFormula() {
        if ((getRangeFormula() == null || "".equals(getRangeFormula())) && getFieldType() != null) {
            return getFieldType().getRangeFormula();
        } else {
            return getRangeFormula();
        }
    }

    public String getLabelCSSStyle() {
        return labelCSSStyle;
    }

    public void setLabelCSSStyle(String labelCSSStyle) {
        this.labelCSSStyle = labelCSSStyle;
    }

    public String getLabelCSSClass() {
        return labelCSSClass;
    }

    public void setLabelCSSClass(String labelCSSClass) {
        this.labelCSSClass = labelCSSClass;
    }

    public Map<String, String> getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(Map<String, String> htmlContent) {
        this.htmlContent = htmlContent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Boolean getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(Boolean verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public String getInputBinding() {
        return inputBinding;
    }

    public void setInputBinding(String inputBinding) {
        this.inputBinding = inputBinding;
    }

    public String getOutputBinding() {
        return outputBinding;
    }

    public void setOutputBinding(String outputBinding) {
        this.outputBinding = outputBinding;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public int compareTo(Field o) {
        return new Integer(getPosition()).compareTo(o.getPosition());
    }
}
