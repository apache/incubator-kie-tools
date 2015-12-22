/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.workitems.client.editor;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.workitems.client.widget.WorkItemDefinitionEditor;
import org.drools.workbench.screens.workitems.client.widget.WorkItemDefinitionElementSelectedListener;
import org.drools.workbench.screens.workitems.client.widget.WorkItemDefinitionElementsBrowser;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Row;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class WorkItemsEditorViewImpl
        extends KieEditorViewImpl
        implements WorkItemsEditorView,
                   WorkItemDefinitionElementSelectedListener {

    //Scroll-bar Height + Container padding * 2
    private static int SCROLL_BAR_SIZE = 32;
    private static int CONTAINER_PADDING = 15;
    private static int VERTICAL_MARGIN = SCROLL_BAR_SIZE + ( CONTAINER_PADDING * 2 );

    interface WorkItemsEditorViewBinder
            extends
            UiBinder<Widget, WorkItemsEditorViewImpl> {

    }

    private static WorkItemsEditorViewBinder uiBinder = GWT.create( WorkItemsEditorViewBinder.class );

    @UiField
    Row row;

    @UiField
    Column editorContainer;

    @UiField
    WorkItemDefinitionEditor workItemWidget;

    @UiField(provided = true)
    WorkItemDefinitionElementsBrowser workItemBrowser;

    @Inject
    public WorkItemsEditorViewImpl( final WorkItemDefinitionElementsBrowser workItemBrowser ) {
        this.workItemBrowser = checkNotNull( "workItemBrowser", workItemBrowser );
    }

    @PostConstruct
    public void bind() {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.workItemBrowser.init( this );
    }

    @Override
    public void setContent( final String definition,
                            final List<String> workItemImages ) {
        workItemWidget.setContent( definition );
        workItemBrowser.setImages( workItemImages );
    }

    @Override
    public String getContent() {
        return workItemWidget.getContent();
    }

    @Override
    public void setReadOnly( final boolean readOnly ) {
        workItemWidget.setReadOnly( readOnly );
    }

    @Override
    public void onElementSelected( final String title,
                                   final String value ) {
        workItemWidget.insertAtCursor( value );
    }

    @Override
    public void onResize() {
        final int height = getParent().getOffsetHeight() - VERTICAL_MARGIN;
        row.setHeight( ( height > 0 ? height : 0 ) + "px" );
        editorContainer.setHeight( ( ( height > 0 ? height : 0 ) + SCROLL_BAR_SIZE ) + "px" );
        workItemWidget.onResize();
    }

}
