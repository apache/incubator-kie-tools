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
import java.util.Set;
import java.util.TreeSet;

public class FieldType implements Serializable {

    private String managerClass;

    private String fieldClass;

    private String height;

    private Boolean disabled;

    private Boolean readonly;

    private String size;

    private String code;

    private String formula;

    private String rangeFormula;

    private String pattern;

    private Long maxlength;

    private String styleclass;

    private String cssStyle;

    private Long tabindex;

    private String accesskey;

    public FieldType() {
    }

    public FieldType(FieldType ft) {
        managerClass = ft.getManagerClass();
        fieldClass = ft.getFieldClass();
        height = ft.getHeight();
        disabled = ft.getDisabled();
        readonly = ft.getReadonly();
        size = ft.getSize();
        code = ft.getCode();
        formula = ft.getFormula();
        rangeFormula = ft.getRangeFormula();
        pattern = ft.getPattern();
        maxlength = ft.getMaxlength();
        styleclass = ft.getStyleclass();
        cssStyle = ft.getCssStyle();
        tabindex = ft.getTabindex();
        accesskey = ft.getAccesskey();
    }

    public String getManagerClass() {
        return this.managerClass;
    }

    public void setManagerClass(String managerClass) {
        this.managerClass = managerClass;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(String fieldClass) {
        this.fieldClass = fieldClass;
    }

    public Boolean isDisabled() {
        return this.disabled;
    }

    public Boolean getDisabled() {
        return isDisabled();
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Long getMaxlength() {
        return this.maxlength;
    }

    public void setMaxlength(Long maxlength) {
        this.maxlength = maxlength;
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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getStyleclass() {
        return this.styleclass;
    }

    public void setStyleclass(String styleclass) {
        this.styleclass = styleclass;
    }

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
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

    public String getRangeFormula() {
        return rangeFormula;
    }

    public void setRangeFormula(String rangeFormula) {
        this.rangeFormula = rangeFormula;
    }

    public String toString() {
        return "Field Type [" + getCode() + "]";
    }

    public boolean equals(Object other) {
        if (!(other instanceof FieldType)) {
            return false;
        }
        FieldType castOther = (FieldType) other;
        return this.getCode().equals(castOther.getCode());
    }

    public int hashCode() {
        return getCode().hashCode();
    }

    public Set getPropertyNames() {
        Set names = new TreeSet();

        names.add("height");
        names.add("disabled");
        names.add("readonly");
        names.add("size");
        names.add("code");
        names.add("formula");
        names.add("rangeFormula");
        names.add("pattern");
        names.add("maxlength");
        names.add("styleclass");
        names.add("cssStyle");
        names.add("tabindex");
        names.add("accesskey");

        return names;
    }
}
