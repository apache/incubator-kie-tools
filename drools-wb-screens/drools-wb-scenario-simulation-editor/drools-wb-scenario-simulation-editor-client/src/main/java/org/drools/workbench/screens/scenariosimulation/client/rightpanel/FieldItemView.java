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

import com.google.gwt.dom.client.LIElement;

public interface FieldItemView {

    interface Presenter {

        /**
         * @param parentPath The parent path (empty if the properties belongs to a <b>top-level</b> element)
         * @param factName
         * @param fieldName
         * @param className
         * @param classTypeName
         * @return
         */
        LIElement getLIElement(String parentPath, String factName, String fieldName, String className, String classTypeName);

        void onFieldElementClick(FieldItemView selected);

        void setListGroupItemPresenter(ListGroupItemView.Presenter listGroupItemPresenter);

        void unselectAll();

        void showAll();

        void reset();
    }

    String getFullPath();

    String getFactName();

    String getFieldName();

    String getClassName();

    String getClassTypeName();

    void setPresenter(FieldItemView.Presenter fieldItemPresenter);

    /**
     * It manages the fieldElement when is <b>automatically</b> selected.
     */
    void onFieldElementSelected();

    /**
     * @param fullPath The parent path (empty if the properties belongs to a <b>top-level</b> element)
     * @param factName
     * @param fieldName
     * @param className
     * @param classTypeName
     */
    void setFieldData(String fullPath, String factName, String fieldName, String className, String classTypeName);

    LIElement getLIElement();

    void showCheck(boolean show);

    boolean isCheckShown();

    void unselect();

    void hide();

    void show();

}
