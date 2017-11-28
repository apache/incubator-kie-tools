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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.RadioButton;

public class RendererSelectorRadioListView extends Composite implements RendererSelector.RadioListView {

    interface RendererSelectorBinder extends UiBinder<Widget, RendererSelectorRadioListView> {}
    private static final RendererSelectorBinder uiBinder = GWT.create(RendererSelectorBinder.class);

    @UiField
    HorizontalPanel radioButtonsPanel;

    RendererSelector presenter = null;
    RadioButton selectedRadio = null;

    @Override
    public void init(final RendererSelector presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width + "px");
        radioButtonsPanel.setWidth(width + "px");
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height + "px");
        radioButtonsPanel.setHeight(height + "px");
    }

    @Override
    public void clearRendererSelector() {
        radioButtonsPanel.clear();
        selectedRadio = null;
    }

    @Override
    public void addRendererItem(final String renderer) {
        final RadioButton rb = new RadioButton(renderer);
        rb.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                unselectCurrent();
                presenter.onRendererSelected();
            }
        });
        radioButtonsPanel.add(rb);
    }

    protected void unselectCurrent() {
        if (selectedRadio != null) {
            selectedRadio.setValue(false);
        }
    }

    @Override
    public void setSelectedRendererIndex(int index) {
        unselectCurrent();
        selectedRadio = (RadioButton) radioButtonsPanel.getWidget(index);
        selectedRadio.setValue(true);
    }

    @Override
    public String getRendererSelected() {
        return selectedRadio == null ? null : selectedRadio.getText();
    }
}