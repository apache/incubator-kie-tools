/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.cms.screen.home;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ContentManagerHomeView extends Composite implements ContentManagerHomeScreen.View {

    @Inject
    ContentManagerI18n i18n;

    @Inject
    @DataField
    Span titleSpan;

    @Inject
    @DataField
    Span welcomeSpan;

    @Inject
    @DataField
    Span createSpan;

    @Inject
    @DataField
    Anchor createPerspectiveAnchor;

    @Inject
    @DataField
    Button createPerspectiveButton;

    ContentManagerHomeScreen presenter;

    @Override
    public void init(ContentManagerHomeScreen presenter) {
        this.presenter = presenter;
        titleSpan.setTextContent(i18n.getContentManagerHomeTitle());
        welcomeSpan.setTextContent(i18n.getContentManagerHomeWelcome());
        createSpan.setTextContent(i18n.getContentManagerHomeCreate());
        createPerspectiveAnchor.setTextContent(i18n.getContentManagerHomeNewPerspectiveLink());
        createPerspectiveButton.setTextContent(i18n.getContentManagerHomeNewPerspectiveButton());
    }

    @Override
    public void setPerspectiveCreationVisible(boolean visible) {
        if (visible) {
            createSpan.getStyle().removeProperty("display");
            createPerspectiveAnchor.getStyle().removeProperty("display");
            createPerspectiveButton.getStyle().removeProperty("display");
        } else {
            createSpan.getStyle().setProperty("display", "none");
            createPerspectiveAnchor.getStyle().setProperty("display", "none");
            createPerspectiveButton.getStyle().setProperty("display", "none");
        }
    }

    @EventHandler("createPerspectiveButton")
    public void createPerspectiveButton(final ClickEvent event) {
        presenter.createNewPerspective();
    }

    @EventHandler("createPerspectiveAnchor")
    public void createPerspectiveAnchor( final ClickEvent event ) {
        presenter.createNewPerspective();
    }
}