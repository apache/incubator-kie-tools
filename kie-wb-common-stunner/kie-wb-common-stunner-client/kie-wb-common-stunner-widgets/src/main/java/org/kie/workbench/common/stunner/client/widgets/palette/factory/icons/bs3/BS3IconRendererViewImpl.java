/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.bs3;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;

@Templated
@Dependent
public class BS3IconRendererViewImpl implements BS3IconRendererView,
                                                IsElement {

    @Inject
    @DataField
    private Div content;

    private BS3IconRenderer presenter;

    private Icon icon;

    @Override
    public void init(BS3IconRenderer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render() {
        icon = new Icon(presenter.getIconResource().getResource());

        resize();

        DOMUtil.removeAllChildren(content);
        DOMUtil.appendWidgetToElement(content,
                                      icon);
    }

    @Override
    public void resize() {
        if (icon != null) {
            if (presenter.getSize().equals(IconRenderer.Size.LARGE)) {
                icon.setSize(IconSize.LARGE);
            } else {
                icon.setSize(IconSize.NONE);
            }
        }
    }
}
