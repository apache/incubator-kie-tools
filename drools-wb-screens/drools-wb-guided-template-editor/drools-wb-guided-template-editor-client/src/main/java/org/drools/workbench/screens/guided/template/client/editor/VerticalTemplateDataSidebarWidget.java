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
package org.drools.workbench.screens.guided.template.client.editor;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.client.editor.events.SetInternalTemplateDataModelEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractVerticalDecoratedGridSidebarWidget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CopyPasteContextMenu;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.ResourcesProvider;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.RowMapper;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetInternalModelEvent;

/**
 * A "sidebar" for a vertical Template Data editor
 */
public class VerticalTemplateDataSidebarWidget extends AbstractVerticalDecoratedGridSidebarWidget<TemplateModel, TemplateDataColumn> {

    private CopyPasteContextMenu contextMenu;

    /**
     * Construct a "sidebar" for a vertical Template Data editor
     */
    public VerticalTemplateDataSidebarWidget( ResourcesProvider<TemplateDataColumn> resources,
                                              boolean isReadOnly,
                                              EventBus eventBus ) {
        // Argument validation performed in the superclass constructor
        super( resources,
               isReadOnly,
               eventBus );

        contextMenu = new CopyPasteContextMenu( eventBus );

        //Wire-up event handlers
        eventBus.addHandler( SetInternalTemplateDataModelEvent.TYPE,
                             this );
    }

    public void onSetInternalModel( SetInternalModelEvent<TemplateModel, TemplateDataColumn> event ) {
        this.data = event.getData();
        this.rowMapper = new RowMapper( this.data );
        this.redraw();
    }

    @Override
    public void showContextMenu( int index,
                                 int clientX,
                                 int clientY ) {
        contextMenu.setContextRows( rowMapper.mapToAllAbsoluteRows( index ) );
        contextMenu.setPopupPosition( clientX,
                                      clientY );
        contextMenu.show();
    }

}
