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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.Objects;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * This class is used to represent a <b>simple</b> (i.e. not expandable) property, like for example java primitives
 */
@Templated
public class FieldItemViewImpl implements FieldItemView {

    @DataField("fieldElement")
    protected LIElement fieldElement = Document.get().createLIElement();

    @DataField("fieldNameElement")
    protected SpanElement fieldNameElement = Document.get().createSpanElement();

    @DataField("checkElement")
    protected SpanElement checkElement = Document.get().createSpanElement();

    private Presenter fieldItemPresenter;

    private String fullPath;
    private String factName;
    private String fieldName;
    private String className;

    @Override
    public void setFieldData(String fullPath, String factName, String fieldName, String className) {
        String innerHtml = new StringBuilder()
                .append("<a>")
                .append(fieldName)
                .append("</a> [")
                .append(className)
                .append("]")
                .toString();
        fieldNameElement.setInnerHTML(innerHtml);
        fieldNameElement.setAttribute("id", "fieldElement-" + factName + "-" + fieldName);
        fieldNameElement.setAttribute("fieldName", fieldName);
        fieldNameElement.setAttribute("className", className);
        fieldNameElement.setAttribute("fullPath", fullPath);
        this.factName = factName;
        this.fieldName = fieldName;
        this.className = className;
        this.fullPath = fullPath;
    }

    @Override
    public String getFullPath() {
        return fullPath;
    }

    @Override
    public String getFactName() {
        return factName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setPresenter(Presenter fieldItemPresenter) {
        this.fieldItemPresenter = fieldItemPresenter;
    }

    @Override
    public LIElement getLIElement() {
        return fieldElement;
    }

    @EventHandler("fieldElement")
    public void onFieldElementClick(ClickEvent clickEvent) {
        onFieldElementClick();
    }

    @Override
    public void onFieldElementSelected() {
        fieldElement.addClassName(ConstantHolder.SELECTED);
        fieldItemPresenter.onFieldElementClick(this);
    }

    public void onFieldElementClick() {
        fieldElement.addClassName(ConstantHolder.SELECTED);
        showCheck(true);
        fieldItemPresenter.onFieldElementClick(this);
    }

    @Override
    public void showCheck(boolean show) {
        if (show) {
            checkElement.getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            checkElement.getStyle().setDisplay(Style.Display.NONE);
        }
    }

    @Override
    public boolean isCheckShown() {
        return !Objects.equals(Style.Display.NONE.getCssName(), checkElement.getStyle().getDisplay());
    }

    @Override
    public void unselect() {
        fieldElement.removeClassName(ConstantHolder.SELECTED);
        showCheck(false);
    }

    @Override
    public void hide() {
        fieldElement.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void show() {
        fieldElement.getStyle().setDisplay(Style.Display.BLOCK);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldItemViewImpl that = (FieldItemViewImpl) o;
        return Objects.equals(getFullPath(), that.getFullPath()) &&
                Objects.equals(getFactName(), that.getFactName()) &&
                Objects.equals(getFieldName(), that.getFieldName()) &&
                Objects.equals(getClassName(), that.getClassName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFullPath(), getFactName(), getFieldName(), getClassName());
    }

    @Override
    public String toString() {
        return "FieldItemViewImpl{" +
                "fullPath='" + fullPath + '\'' +
                ", factName='" + factName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
