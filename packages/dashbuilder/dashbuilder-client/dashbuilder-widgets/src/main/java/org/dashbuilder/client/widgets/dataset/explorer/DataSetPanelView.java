/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.widgets.dataset.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.ShowEvent;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import javax.enterprise.context.Dependent;

/**
 * <p>Default view for DataSetPanel presenter.</p>
 * 
 * @since 0.3.0
 */
@Dependent
public class DataSetPanelView extends Composite implements DataSetPanel.View {

    interface DataSetPanelViewBinder extends UiBinder<Widget, DataSetPanelView> {}
    private static DataSetPanelViewBinder uiBinder = GWT.create(DataSetPanelViewBinder.class);

    interface DataSetPanelViewStyle extends CssResource {
    }

    @UiField
    DataSetPanelViewStyle style;
    
    @UiField
    PanelHeader headerPanel;
    
    @UiField
    Image typeIcon;

    @UiField
    Heading title;
    
    @UiField
    PanelCollapse collapsePanel;

    @UiField
    PanelBody bodyPanel;

    @UiField(provided = true)
    DataSetSummary.View summaryView;
    
    @UiField
    Button actionButton;

    private DataSetPanel presenter;
    private HandlerRegistration actionButtonHandlerRegistration;

    public DataSetPanelView() {
    }
    
    @Override
    public void init(final DataSetPanel presenter) {
        this.presenter = presenter;
    }

    @Override
    public DataSetPanel.View configure(final DataSetSummary.View summaryView) {
        this.summaryView = summaryView;
        initWidget(uiBinder.createAndBindUi(this));
        return this;
    }

    @Override
    public DataSetPanel.View showHeader(final String uuid, final String parentCollapseId, 
                                        final SafeUri dataSetTypeImageUri, final String dataSetTypeImageTitle, final String dataSetTitle) {
        typeIcon.setUrl(dataSetTypeImageUri);
        typeIcon.setTitle(dataSetTypeImageTitle);
        typeIcon.setAltText(dataSetTypeImageTitle);
        title.setText(dataSetTitle);
        setDynamicToggleId(uuid, parentCollapseId);
        collapsePanel.addShowHandler(new ShowHandler() {
            @Override
            public void onShow(final ShowEvent shownEvent) {
                presenter.open();
            }
        });
        return this;
    }
    
    private void setDynamicToggleId(final String uuid, final String parentCollapseId) {
        final String tId = "collapsePanel" + uuid;
        collapsePanel.setId(tId);
        headerPanel.setDataToggle(Toggle.COLLAPSE);
        headerPanel.setDataTarget("#" + tId);
        headerPanel.setDataParent("#" + parentCollapseId);
    }

    @Override
    public DataSetPanel.View showSummary() {
        return this;

    }

    @Override
    public DataSetPanel.View hideSummary() {
        collapsePanel.setIn(false);
        return this;
    }

    @Override
    public DataSetPanel.View enableActionButton(final String buttonTitle, final ClickHandler clickHandler) {
        actionButton.setText(buttonTitle);
        actionButton.setEnabled(true);
        if (actionButtonHandlerRegistration != null) {
            actionButtonHandlerRegistration.removeHandler();
        }
        actionButtonHandlerRegistration = actionButton.addClickHandler(clickHandler);
        return this;
    }

    @Override
    public DataSetPanel.View disableActionButton() {
        actionButton.setEnabled(false);
        return this;
    }

}
