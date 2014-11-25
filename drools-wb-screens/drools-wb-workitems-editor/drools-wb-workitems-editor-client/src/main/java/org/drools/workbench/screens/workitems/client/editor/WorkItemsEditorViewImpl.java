/*
 * Copyright 2012 JBoss Inc
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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import org.drools.workbench.screens.workitems.client.widget.WorkItemDefinitionEditor;
import org.drools.workbench.screens.workitems.client.widget.WorkItemDefinitionElementSelectedListener;
import org.drools.workbench.screens.workitems.client.widget.WorkItemDefinitionElementsBrowser;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

public class WorkItemsEditorViewImpl
        extends KieEditorViewImpl
        implements WorkItemsEditorView,
                   WorkItemDefinitionElementSelectedListener {

    @Inject
    private WorkItemDefinitionEditor workItemWidget;

    @Inject
    private WorkItemDefinitionElementsBrowser workItemBrowser;

    @PostConstruct
    public void setup() {
        workItemBrowser.init( this );
    }

    @PostConstruct
    public void init() {

        final Grid layout = new Grid( 1,
                                      2 );
        layout.setCellSpacing( 5 );
        layout.setCellPadding( 5 );

        layout.setWidget( 0,
                          0,
                          workItemBrowser );
        layout.setWidget( 0,
                          1,
                          workItemWidget );

        layout.getColumnFormatter().setWidth( 0, "10%" );
        layout.getColumnFormatter().setWidth( 1, "90%" );
        layout.getCellFormatter().setAlignment( 0,
                                                0,
                                                HasHorizontalAlignment.ALIGN_LEFT,
                                                HasVerticalAlignment.ALIGN_TOP );
        layout.getCellFormatter().setAlignment( 0,
                                                1,
                                                HasHorizontalAlignment.ALIGN_LEFT,
                                                HasVerticalAlignment.ALIGN_TOP );
        layout.setWidth( "95%" );

        initWidget( layout );
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
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }
    
    @Override
    public void onElementSelected( final String title,
                                   final String value ) {
        workItemWidget.insertText( value,
                                   true );
    }

}
