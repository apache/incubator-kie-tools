/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.breadcrumbs.widget;

import javax.inject.Inject;

import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLLIElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElemental;

@Templated
public class DefaultBreadcrumbsView implements UberElemental<DefaultBreadcrumbsPresenter>,
                                               DefaultBreadcrumbsPresenter.View,
                                               IsElement {

    @Inject
    @DataField("breadcrumb")
    private HTMLLIElement breadcrumb;

    @Inject
    @DataField("breadcrumbLink")
    private HTMLAnchorElement breadcrumbLink;

    private DefaultBreadcrumbsPresenter presenter;

    @Override
    public void init(DefaultBreadcrumbsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setup(final String label) {
        breadcrumbLink.textContent = label;
        breadcrumbLink.onclick = e -> {
            presenter.onClick();
            return null;
        };
    }

    @Override
    public void activate() {
        breadcrumbLink.className = "breadcrumb-activated";
    }

    @Override
    public void deactivate() {
        breadcrumbLink.className = "breadcrumb-deactivated";
    }

    @Override
    public void setNoAction() {
        breadcrumb.className = "breadcrumb-no-action";
    }
}