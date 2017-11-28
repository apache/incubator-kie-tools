/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class RendererSelectorListBoxView extends Composite implements RendererSelector.ListBoxView {

    interface RendererSelectorBinder extends UiBinder<Widget, RendererSelectorListBoxView> {}
    private static final RendererSelectorBinder uiBinder = GWT.create(RendererSelectorBinder.class);

    @UiField
    Panel mainPanel;

    @UiField
    ListBox listBox;

    RendererSelector presenter = null;

    @Override
    public void init(final RendererSelector presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));

        listBox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                presenter.onRendererSelected();
            }
        });
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width + "px");
        mainPanel.setWidth(width + "px");
        listBox.setWidth(width + "px");
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height + "px");
        mainPanel.setHeight(height + "px");
        listBox.setWidth(height + "px");
    }

    @Override
    public void clearRendererSelector() {
        listBox.clear();
    }

    @Override
    public void addRendererItem(String renderer) {
        listBox.addItem(renderer);
    }

    @Override
    public void setSelectedRendererIndex(int index) {
        listBox.setSelectedIndex(index);
    }

    @Override
    public String getRendererSelected() {
        return listBox.getSelectedValue();
    }
}