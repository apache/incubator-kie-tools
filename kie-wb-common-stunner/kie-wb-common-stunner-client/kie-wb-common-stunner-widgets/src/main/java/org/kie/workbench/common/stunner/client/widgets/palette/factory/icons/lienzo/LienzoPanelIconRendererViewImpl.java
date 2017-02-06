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

package org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.lienzo;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;

@Templated
@Dependent
public class LienzoPanelIconRendererViewImpl implements LienzoPanelIconRendererView,
                                                        IsElement {

    private static final int LARGE_ICON_SIZE = 30;
    private static final int SMALL_ICON_SIZE = 15;

    @Inject
    @DataField
    private Div content;

    private LienzoPanelIconRenderer presenter;

    private Widget icon;

    @Override
    public void init(LienzoPanelIconRenderer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render() {
        icon = presenter.getIconResource().getResource().asWidget();

        resize();

        DOMUtil.removeAllChildren(content);
        DOMUtil.appendWidgetToElement(content,
                                      icon);
    }

    @Override
    public void resize() {
        if (icon != null) {
            int size;
            if (presenter.getSize().equals(IconRenderer.Size.LARGE)) {
                size = LARGE_ICON_SIZE;
            } else {
                size = SMALL_ICON_SIZE;
            }
            icon.getElement().getStyle().setWidth(size,
                                                  Style.Unit.PX);
            icon.getElement().getStyle().setHeight(size,
                                                   Style.Unit.PX);
        }
    }
}
