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
package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.client.widgets.resources.i18n.DataSetEditorConstants;
import org.dashbuilder.common.client.editor.ToggleSwitchEditor;
import org.dashbuilder.dataset.def.DataSetDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * <p>Data Set refresh attributes editor presenter.</p>
 * 
 * @since 0.4.0 
 */
@Dependent
public class DataSetDefRefreshAttributesEditor implements IsWidget, org.dashbuilder.dataset.client.editor.DataSetDefRefreshAttributesEditor {

    public interface View extends UberView<DataSetDefRefreshAttributesEditor> {
        /**
         * <p>Specify the views to use for each sub-editor before calling <code>initWidget</code>.</p>
         */
        void initWidgets(IsWidget enabledView, DataSetDefRefreshIntervalEditor.View valueView);
        
        void addRefreshEnabledButtonHandler(Command handler);
        
        void setEnabled(boolean enabled);
    }
    
    ToggleSwitchEditor refreshAlways;
    DataSetDefRefreshIntervalEditor refreshTime;
    public View view;
    boolean isRefreshEnabled;

    @Inject
    public DataSetDefRefreshAttributesEditor(final ToggleSwitchEditor refreshAlways,
                                             final DataSetDefRefreshIntervalEditor refreshTime,
                                             final View view) {
        this.refreshAlways = refreshAlways;
        this.refreshTime = refreshTime;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.initWidgets(refreshAlways.asWidget(), refreshTime.view);
        view.addRefreshEnabledButtonHandler(refreshEnabledButtonHandler);
        refreshTime.addHelpContent(DataSetEditorConstants.INSTANCE.attributeRefreshInterval(),
                DataSetEditorConstants.INSTANCE.attributeRefreshInterval_description(),
                Placement.RIGHT);
    }
    
    public boolean isRefreshEnabled() {
        return isRefreshEnabled;
    }
    
    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*************************************************************
            ** GWT EDITOR CONTRACT METHODS **
     *************************************************************/

    @Override
    public ToggleSwitchEditor refreshAlways() {
        return refreshAlways;
    }

    @Override
    public DataSetDefRefreshIntervalEditor refreshTime() {
        return refreshTime;
    }

    @Override
    public void flush() {

    }

    @Override
    public void onPropertyChange(final String... paths) {

    }

    @Override
    public void setValue(final DataSetDef value) {
        final String rTime = value != null ? value.getRefreshTime() : null;
        setRefreshEnabled(rTime != null);
    }

    @Override
    public void setDelegate(final EditorDelegate<DataSetDef> delegate) {

    }

    /*************************************************************
     ** PRIVATE PRESENTER METHODS **
     *************************************************************/
    
    private void setRefreshEnabled(final boolean refreshEnabled) {
        this.isRefreshEnabled = refreshEnabled;
        view.setEnabled(refreshEnabled);
        refreshTime.setEnabled(refreshEnabled);
        refreshAlways.setEnabled(refreshEnabled);
    }

    final Command refreshEnabledButtonHandler = new Command() {
        @Override
        public void execute() {
            setRefreshEnabled(!isRefreshEnabled);
        }
    };
}
