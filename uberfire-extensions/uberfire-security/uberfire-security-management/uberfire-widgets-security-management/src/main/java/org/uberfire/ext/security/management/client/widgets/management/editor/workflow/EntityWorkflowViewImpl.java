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

package org.uberfire.ext.security.management.client.widgets.management.editor.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.html.Text;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

@Dependent
public class EntityWorkflowViewImpl extends Composite implements EntityWorkflowView {

    interface UserEditorWorkflowViewBinder
            extends
            UiBinder<Widget, EntityWorkflowViewImpl> {

    }

    private static UserEditorWorkflowViewBinder uiBinder = GWT.create(UserEditorWorkflowViewBinder.class);

    @UiField
    Alert notifications;

    @UiField
    Text notificationLabel;
    
    @UiField
    Column content;
    
    @UiField
    Button saveButton;

    @UiField
    Button cancelButton;

    private Callback callback;
    
    @PostConstruct
    public void init() {
        // Bind this view and initialize the widget.
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public EntityWorkflowView setCallback(final Callback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public EntityWorkflowView setWidget(final IsWidget widget) {
        content.clear();
        content.add(widget);
        return this;
    }

    @Override
    public EntityWorkflowView setCancelButtonVisible(boolean isVisible) {
        cancelButton.setVisible(isVisible);
        return this;
    }

    @Override
    public EntityWorkflowView setSaveButtonEnabled(boolean isEnabled) {
        saveButton.setEnabled(isEnabled);
        return this;
    }

    @Override
    public EntityWorkflowView setSaveButtonVisible(boolean isVisible) {
        saveButton.setVisible(isVisible);
        return this;
    }

    @Override
    public EntityWorkflowView setSaveButtonText(final String text) {
        saveButton.setText(text);
        saveButton.setTitle(text);
        return this;
    }

    @Override
    public EntityWorkflowView showNotification(final String text) {
        notificationLabel.setText(text);
        notifications.setVisible(true);
        return this;
    }

    @Override
    public EntityWorkflowView clearNotification() {
        notificationLabel.setText("");
        notifications.setVisible(false);
        return this;
    }

    @UiHandler( "saveButton" )
    public void onEditButtonClick( final ClickEvent event ) {
        if (callback != null) {
            callback.onSave();
        }
    }

    @UiHandler( "cancelButton" )
    public void onDeleteButtonClick( final ClickEvent event ) {
        if (callback != null) {
            callback.onCancel();
        }
    }

}
