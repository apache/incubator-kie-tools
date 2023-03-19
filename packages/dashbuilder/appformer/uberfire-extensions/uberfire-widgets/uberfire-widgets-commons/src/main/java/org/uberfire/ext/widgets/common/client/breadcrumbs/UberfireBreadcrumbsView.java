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

package org.uberfire.ext.widgets.common.client.breadcrumbs;

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLOListElement;
import elemental2.dom.Node;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbPresenter;

@Templated
public class UberfireBreadcrumbsView implements UberElement<UberfireBreadcrumbs>,
                                                UberfireBreadcrumbs.View,
                                                IsElement {

    @Inject
    @DataField
    HTMLOListElement breadcrumbs;

    @Inject
    @DataField
    HTMLDivElement breadcrumbsToolbar;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Override
    public void init(final UberfireBreadcrumbs presenter) {
    }

    @Override
    public void clear() {
        elemental2DomUtil.removeAllElementChildren(breadcrumbs);
        elemental2DomUtil.removeAllElementChildren(breadcrumbsToolbar);
    }

    @Override
    public void addBreadcrumb(UberElemental<? extends BreadcrumbPresenter> view) {
        breadcrumbs.appendChild(view.getElement());
    }

    @Override
    public void addBreadcrumbToolbar(Element toolbar) {
        elemental2DomUtil.removeAllElementChildren(breadcrumbsToolbar);
        breadcrumbsToolbar.appendChild((Node) toolbar);
    }
}