/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.common.page;

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.views.pfly.multipage.PageImpl;

import static org.jboss.errai.common.client.ui.ElementWrapperWidget.getWidget;

public abstract class DMNPage extends PageImpl {

    protected static final String DMN_PAGE_CSS_CLASS = "kie-dmn-page";

    private final HTMLDivElement pageView;

    public DMNPage(final String i18nTitleKey,
                   final HTMLDivElement pageView,
                   final TranslationService translationService) {

        super(getWidget(pageView), getPageTitle(i18nTitleKey, translationService));
        this.pageView = pageView;
        setupDMNPage();
    }

    private static String getPageTitle(final String i18nTitleKey,
                                       final TranslationService translationService) {
        return translationService.format(i18nTitleKey);
    }

    protected void setupPageCSSClass(final String cssClass) {
        ((Element) getPageView().parentNode.parentNode).classList.add(cssClass);
    }

    private void setupDMNPage() {
        setupPageCSSClass(DMN_PAGE_CSS_CLASS);
    }

    protected HTMLDivElement getPageView() {
        return pageView;
    }
}
