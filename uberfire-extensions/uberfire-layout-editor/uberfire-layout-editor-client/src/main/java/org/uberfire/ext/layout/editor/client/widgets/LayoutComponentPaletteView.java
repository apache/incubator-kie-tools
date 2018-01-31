/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.client.widgets;

import org.jboss.errai.common.client.dom.*;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Templated
@Dependent
public class LayoutComponentPaletteView
        implements UberElement<LayoutComponentPalettePresenter>,
                    LayoutComponentPalettePresenter.View, IsElement {

    @Inject
    @DataField
    Div components;

    private LayoutComponentPalettePresenter presenter;

    @Override
    public void init(LayoutComponentPalettePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addDraggableComponentGroup(UberElement<LayoutDragComponentGroupPresenter> group) {
        components.appendChild(group.getElement());
    }

    @Override
    public void removeDraggableComponentGroup(UberElement<LayoutDragComponentGroupPresenter> group) {
        components.removeChild(group.getElement());
    }
}