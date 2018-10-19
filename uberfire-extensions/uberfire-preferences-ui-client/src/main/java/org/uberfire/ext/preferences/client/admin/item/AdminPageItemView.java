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

package org.uberfire.ext.preferences.client.admin.item;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Paragraph;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.preferences.client.admin.page.AdminTool;

@Dependent
@Templated
public class AdminPageItemView implements IsElement,
                                          AdminPageItemPresenter.View {

    private final TranslationService translationService;
    @Inject
    @DataField("item")
    Div item;
    @DataField("item-icon")
    Element icon = DOM.createElement("i");
    @Inject
    @DataField("item-text")
    Div text;
    @Inject
    @DataField("item-counter-container")
    Div counterContainer;
    @Inject
    @DataField("item-counter")
    Paragraph counterText;
    private AdminPageItemPresenter presenter;

    @Inject
    public AdminPageItemView(final TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void init(final AdminPageItemPresenter presenter) {
        this.presenter = presenter;
        final AdminTool adminTool = presenter.getAdminTool();

        if (adminTool.getIconCss() != null) {
            adminTool.getIconCss().forEach(css -> icon.addClassName(css));
        }

        text.setTextContent(adminTool.getTitle());

        if (adminTool.hasCounter()) {
            adminTool.fetchCounter(counter -> counterText.setTextContent(String.valueOf(counter)));
        } else {
            counterContainer.setHidden(true);
        }
    }

    @EventHandler("item")
    public void enter(ClickEvent event) {
        presenter.enter();
    }
}
