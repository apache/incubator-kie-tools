/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.screens.view;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLParagraphElement;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.screens.NotFoundScreen;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class NotFoundScreenView implements NotFoundScreen.View {
    
    private static AppConstants i18n = AppConstants.INSTANCE;
    
    @Inject
    @DataField
    HTMLDivElement root;
    
    @Inject
    @DataField
    HTMLParagraphElement subTitle;

    @Override
    public HTMLElement getElement() {
        return root;
    }

    @Override
    public void init(NotFoundScreen presenter) {
        // do nothing
    }
    
    @Override
    public void setNotFoundPerspective(String perspectiveName) {
        subTitle.textContent = i18n.notFoundDashboard(perspectiveName);
    }
    
}