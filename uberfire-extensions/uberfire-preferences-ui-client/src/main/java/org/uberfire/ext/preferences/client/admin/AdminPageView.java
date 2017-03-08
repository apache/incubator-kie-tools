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

package org.uberfire.ext.preferences.client.admin;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryPresenter;
import org.uberfire.ext.preferences.client.resources.i18n.Constants;

@Dependent
@Templated
public class AdminPageView implements IsElement,
                                      AdminPagePresenter.View {

    @Inject
    @DataField("admin-page-content")
    Div content;

    @Inject
    @Named("h1")
    @DataField("admin-page-title")
    Heading title;

    private AdminPagePresenter presenter;

    @Inject
    private TranslationService translationService;

    @Override
    public void init(final AdminPagePresenter presenter) {
        this.presenter = presenter;
        title.setTextContent(getTitle());
    }

    @Override
    public void add(final AdminPageCategoryPresenter.View categoryView) {
        content.appendChild(categoryView.getElement());
    }

    @Override
    public String getTitle() {
        final String screen = presenter.getScreen();
        final String title = presenter.getAdminPage().getScreenTitle(screen);

        return title;
    }

    @Override
    public String getNoScreenParameterError() {
        return translationService.format(Constants.AdminPagePresenter_NoScreenParameterError);
    }

    @Override
    public String getNoScreenFoundError(final String screen) {
        return translationService.format(Constants.AdminPagePresenter_NoScreenFoundError,
                                         screen);
    }
}
