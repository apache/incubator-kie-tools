/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.toolbar.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconRotate;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.mvp.Command;

public class ToolbarItemView extends Composite implements ToolbarItem.View {

    interface ViewBinder extends UiBinder<Widget, ToolbarItemView> {

    }

    private static ViewBinder uiBinder = GWT.create(ViewBinder.class);

    @UiField
    Button button;

    private AbstractToolbarItem presenter;

    @Override
    public void init(final AbstractToolbarItem presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
        button.getElement().getStyle().setMargin(5,
                                                 Style.Unit.PX);
    }

    @Override
    public ToolbarItem.View setIcon(final IconType icon) {
        button.setIcon(icon);
        return this;
    }

    @Override
    public AbstractToolbarItem.View setIconRotate(final IconRotate rotate) {
        button.setIconRotate(rotate);
        return this;
    }

    @Override
    public AbstractToolbarItem.View setIconSize(final IconSize size) {
        button.setIconSize(size);
        return this;
    }

    @Override
    public AbstractToolbarItem.View setCaption(final String caption) {
        button.setText(caption);
        return this;
    }

    @Override
    public ToolbarItem.View setTooltip(final String tooltip) {
        button.setTitle(tooltip);
        return this;
    }

    @Override
    public ToolbarItem.View setClickHandler(final Command command) {
        button.addClickHandler(clickEvent -> command.execute());
        return this;
    }

    @Override
    public ToolbarItem.View setEnabled(final boolean enabled) {
        button.setEnabled(enabled);
        return this;
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void destroy() {
        this.removeFromParent();
        presenter = null;
    }
}
