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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * This class is used to represent a <b>simple</b> (i.e. not expandable) property, like for example java primitives
 */
@Templated
public class FieldItemViewImpl implements FieldItemView {

    @DataField("fieldElement")
    LIElement fieldElement = Document.get().createLIElement();

    private Presenter fieldItemPresenter;

    private String fullPath;
    private String factName;
    private String fieldName;
    private String className;

    @Override
    public void setFieldData(String fullPath, String factName, String fieldName, String className) {
        String innerHtml = new StringBuilder()
                .append("<a title=\"")
                .append(fieldName)
                .append("\" href=\"#\">")
                .append(fieldName)
                .append("</a> ")
                .append(className)
                .toString();
        fieldElement.setInnerHTML(innerHtml);
        fieldElement.setAttribute("id", "fieldElement-" + factName + "-" + fieldName);
        fieldElement.setAttribute("fieldName", fieldName);
        fieldElement.setAttribute("className", className);
        fieldElement.setAttribute("fullPath", fullPath);
        this.factName = factName;
        this.fieldName = fieldName;
        this.className = className;
        this.fullPath = fullPath;
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
    public void onFieldElementDoubleClick(DoubleClickEvent clickEvent) {
        fieldItemPresenter.onFieldElementDoubleClick(fullPath, fieldName, className);
    }
}
