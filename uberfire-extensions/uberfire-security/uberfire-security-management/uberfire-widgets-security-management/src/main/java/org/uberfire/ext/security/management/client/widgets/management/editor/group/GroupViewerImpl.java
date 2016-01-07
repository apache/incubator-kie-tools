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

package org.uberfire.ext.security.management.client.widgets.management.editor.group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;

import javax.enterprise.context.Dependent;

@Dependent
public class GroupViewerImpl extends Composite implements GroupViewer.View {

    interface GroupEditorViewBinder
            extends
            UiBinder<Widget, GroupViewerImpl> {

    }

    private static GroupEditorViewBinder uiBinder = GWT.create( GroupEditorViewBinder.class );

    @UiField
    Heading groupTitle;
    
    @UiField
    Button deleteButton;

    GroupViewer presenter;
    
    @Override
    public void init(final GroupViewer presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public GroupViewer.View show(final String name) {
        groupTitle.setText(name);
        return this;
    }
    
    @Override
    public GroupViewer.View setShowDeleteButton(boolean isVisible) {
        deleteButton.setVisible(isVisible);
        return this;
    }

    @Override
    public GroupViewer.View clear() {
        groupTitle.setText("");
        deleteButton.setVisible(false);
        return this;
    }

    @UiHandler( "deleteButton" )
    public void onDeleteButtonClick( final ClickEvent event ) {
        if (presenter != null) presenter.onDelete();
    }

}