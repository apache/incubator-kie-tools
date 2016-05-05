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

package org.uberfire.ext.security.management.client.widgets.management.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.uberfire.ext.security.management.client.widgets.management.explorer.EntitiesExplorerView;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;

/**
 * <p>An editor view implementation for modifying the assigned entities using a modal panel.</p>
 * <p>This view wraps the given <code>ExplorerView</code> instance in a GWT Bootstrap 3 Modal panel.</p>
 *
 * @since 0.8.0
 */

@Dependent
@AssignedEntitiesModalEditor
public class AssignedEntitiesModalEditorView<T> extends Composite
        implements
        AssignedEntitiesEditor<T> {

    interface AssignedEntitiesModalEditorViewBinder
            extends
            UiBinder<FlowPanel, AssignedEntitiesModalEditorView> {

    }

    private static AssignedEntitiesModalEditorViewBinder uiBinder = GWT.create(AssignedEntitiesModalEditorViewBinder.class);

    @UiField
    FlowPanel mainPanel;

    @UiField
    Modal entitiesModal;
    
    @UiField(provided = true)
    EntitiesExplorerView entitiesExplorerView;
    
    @UiField
    Button closeButton;

    @UiField
    Button saveButton;

    private T presenter;

    @Override
    public void init(final T presenter) {
        this.presenter = presenter;
    }

    @Override
    public AssignedEntitiesEditor<T> configure(final EntitiesExplorerView explorerView) {
        this.entitiesExplorerView = explorerView;
        initWidget( uiBinder.createAndBindUi( this ) );
        return this;
    }

    @Override
    public AssignedEntitiesEditor<T> configureClose(final String closeText, final Command closeCallback) {
        closeButton.setText(closeText);
        closeButton.setTitle(closeText);
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent clickEvent) {
                if (closeCallback != null) {
                    closeCallback.execute();;
                } else {
                    entitiesModal.hide();
                }
            }
        });
        return this;
    }

    @Override
    public AssignedEntitiesEditor<T> configureSave(final String saveText, final Command saveCallback) {
        saveButton.setText(saveText);
        saveButton.setTitle(saveText);
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent clickEvent) {
                if (saveCallback != null) {
                    saveCallback.execute();;
                } else {
                    entitiesModal.hide();
                }
            }
        });
        return this;
    }

    @Override
    public AssignedEntitiesEditor<T> show(final String title) {
        entitiesModal.setTitle(title);
        entitiesModal.show();
        return this;
    }

    @Override
    public AssignedEntitiesEditor<T> hide() {
        entitiesModal.hide();
        return this;
    }

}