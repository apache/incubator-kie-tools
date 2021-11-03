/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.breadcrumbs.header;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.Header;

@ApplicationScoped
@Templated
public class UberfireBreadcrumbsContainerImpl implements Header,
                                                         UberfireBreadcrumbsContainer {

    @Inject
    @DataField("breadcrumbs-container")
    Div breadcrumbsContainer;

    @Override
    public void init(HTMLElement child) {
        DOMUtil.removeAllChildren(breadcrumbsContainer);
        breadcrumbsContainer.appendChild(child);
    }

    @Override
    public String getId() {
        return "UberfireBreadcrumbsContainer";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void enable() {
        DOMUtil.removeCSSClass(breadcrumbsContainer,
                               "breadcrumb-disabled");
        DOMUtil.addCSSClass(breadcrumbsContainer,
                            "breadcrumbs-container");

    }

    @Override
    public void disable() {
        DOMUtil.removeCSSClass(breadcrumbsContainer,
                               "breadcrumbs-container");
        DOMUtil.addCSSClass(breadcrumbsContainer,
                            "breadcrumb-disabled");
    }
}

