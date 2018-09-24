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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;

@Dependent
public class FieldItemPresenter implements FieldItemView.Presenter {

    @Inject
    ViewsProvider viewsProvider;

    RightPanelView.Presenter rightPanelPresenter;

    @Override
    public LIElement getLIElement(String parentPath, String factName, String fieldName, String className) {
        FieldItemView fieldItemView = viewsProvider.getFieldItemView();
        fieldItemView.setFieldData(parentPath, factName, fieldName, className);
        fieldItemView.setPresenter(this);
        LIElement toReturn = fieldItemView.getLIElement();
        return toReturn;
    }

    @Override
    public void setRightPanelPresenter(RightPanelView.Presenter rightPanelPresenter) {
        this.rightPanelPresenter = rightPanelPresenter;
    }

    public void onFieldElementDoubleClick(String fullPath, String fieldName, String className) {
        rightPanelPresenter.onModifyColumn(fullPath, fieldName, className);
    }
}
