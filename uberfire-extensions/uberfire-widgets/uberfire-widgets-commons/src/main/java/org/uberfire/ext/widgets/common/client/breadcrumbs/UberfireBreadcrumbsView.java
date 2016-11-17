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

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.OrderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbsPresenter;

import javax.inject.Inject;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

@Templated
public class UberfireBreadcrumbsView implements UberElement<UberfireBreadcrumbs>,
        UberfireBreadcrumbs.View, IsElement {


    private UberfireBreadcrumbs presenter;

    @Inject
    @DataField
    OrderedList breadcrumbs;

    @Inject
    @DataField
    Div breadcrumbsToolbar;

    @Override
    public void init( UberfireBreadcrumbs presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        removeAllChildren( breadcrumbs );
        removeAllChildren( breadcrumbsToolbar );
    }

    @Override
    public void addBreadcrumb( UberElement<BreadcrumbsPresenter> view ) {
        breadcrumbs.appendChild( view.getElement() );
    }

    @Override
    public void addBreadcrumbToolbar( Element toolbar ) {
        removeAllChildren( breadcrumbsToolbar );
        breadcrumbsToolbar.appendChild( toolbar );
    }
}