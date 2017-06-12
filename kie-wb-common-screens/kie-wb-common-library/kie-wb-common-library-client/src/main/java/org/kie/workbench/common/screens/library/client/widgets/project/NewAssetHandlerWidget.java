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

package org.kie.workbench.common.screens.library.client.widgets.project;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
public class NewAssetHandlerWidget implements IsElement {

    @Inject
    @DataField
    Button button;

    @Inject
    @DataField
    Div text;

    @Inject
    @DataField
    Div icon;

    public void init(final String title,
                     final IsWidget iconWidget,
                     final Command onClick) {
        text.setTextContent(title);

        if (iconWidget != null) {
            HTMLElement assetIconHtml = TemplateUtil.<HTMLElement>nativeCast(iconWidget.asWidget().getElement());
            final Node clonedAssetIconHtml = assetIconHtml.cloneNode(true);
            this.icon.appendChild(clonedAssetIconHtml);
        }

        if (onClick != null) {
            button.setOnclick(e -> onClick.execute());
        }
    }
}
