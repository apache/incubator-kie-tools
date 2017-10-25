/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.empty.ProviderTypeEmptyPresenter;
import org.guvnor.ala.ui.client.navigation.ProviderTypeNavigationPresenter;
import org.guvnor.ala.ui.client.navigation.providertype.ProviderTypePresenter;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ProvisioningManagementBrowserView_Title;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

@Dependent
@Templated
public class ProvisioningManagementBrowserView
        implements org.jboss.errai.ui.client.local.api.IsElement,
                   ProvisioningManagementBrowserPresenter.View {

    @Inject
    @DataField
    private Div container;

    @Inject
    @DataField("first-nav")
    private Div firstLevelNavigation;

    @Inject
    @DataField("second-nav")
    private Div secondLevalNavigation;

    @Inject
    @DataField
    private Div content;

    private ProvisioningManagementBrowserPresenter presenter;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    public void init() {
        removeAllChildren(container);
    }

    @Override
    public void init(ProvisioningManagementBrowserPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getTitle() {
        return translationService.getTranslation(ProvisioningManagementBrowserView_Title);
    }

    @Override
    public void setProviderTypesNavigation(final ProviderTypeNavigationPresenter.View view) {
        container.appendChild(firstLevelNavigation);
        removeAllChildren(firstLevelNavigation);
        firstLevelNavigation.appendChild(view.getElement());
    }

    @Override
    public void setProviderType(final ProviderTypePresenter.View view) {
        content.getClassList().remove("col-md-10");
        content.getClassList().remove("col-sm-9");
        content.getClassList().add("col-md-8");
        content.getClassList().add("col-sm-6");

        if (secondLevalNavigation.getParentElement() == null) {
            if (container.getChildNodes().getLength() == 2) { //was empty
                container.removeChild(container.getLastChild());
            }
            container.appendChild(secondLevalNavigation);
            container.appendChild(content);
        }
        removeAllChildren(secondLevalNavigation);
        secondLevalNavigation.appendChild(view.getElement());
    }

    @Override
    public void setEmptyView(final ProviderTypeEmptyPresenter.View view) {
        content.getClassList().remove("col-md-8");
        content.getClassList().remove("col-sm-6");
        content.getClassList().add("col-md-10");
        content.getClassList().add("col-sm-9");

        try {
            container.removeChild(secondLevalNavigation);
        } catch (final Exception ignore) {
        }

        removeAllChildren(content);
        content.appendChild(view.getElement());

        if (content.getParentNode() == null) {
            container.appendChild(content);
        }
    }

    @Override
    public void setContent(final IsElement view) {
        removeAllChildren(content);
        content.appendChild(view.getElement());
        if (content.getParentElement() == null) {
            container.appendChild(content);
        }
    }
}
