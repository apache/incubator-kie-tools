/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;

import com.google.gwt.user.client.ui.LayoutDataUtils.LayoutData;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
public class WorkbenchPanelPresenter {

    public interface View
        extends
        UberView<WorkbenchPanelPresenter>,
        RequiresResize {

        WorkbenchPanelPresenter getPresenter();

        void clear();

        void addPart(String title,
                     WorkbenchPartPresenter.View view);

        void addPanel(PanelDefinition panel,
                      WorkbenchPanelPresenter.View view,
                      Position position);

        void selectPart(int index);

        void removePart(int index);

        void removePanel();

        void setFocus(boolean hasFocus);

    }

    @Inject
    private View                                 view;

    @Inject
    private Event<WorkbenchPartBeforeCloseEvent> workbenchPartBeforeCloseEvent;

    @Inject
    private Event<WorkbenchPartOnFocusEvent>     workbenchPartOnFocusEvent;

    @Inject
    private Event<WorkbenchPartLostFocusEvent>   workbenchPartLostFocusEvent;

    @Inject
    private Event<WorkbenchPanelOnFocusEvent>    workbenchPanelOnFocusEvent;

    private PanelDefinition                      definition   = new PanelDefinition();

    private List<PartDefinition>                 orderedParts = new ArrayList<PartDefinition>();

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        view.init( this );
    }

    public PanelDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(final PanelDefinition definition) {
        this.definition = definition;
    }

    public void addPart(final String title,
                        final PartDefinition part,
                        final WorkbenchPartPresenter.View view) {
        //The model for a Perspective is already fully populated. Don't go adding duplicates.
        if ( !definition.getParts().contains( part ) ) {
            definition.addPart( part );
        }
        if ( !orderedParts.contains( part ) ) {
            orderedParts.add( part );
        }
        getPanelView().addPart( title,
                                view );
    }

    public void addPanel(final PanelDefinition panel,
                         final WorkbenchPanelPresenter.View view,
                         final Position position) {
        definition.setChild( position,
                             panel );
        getPanelView().addPanel( panel,
                                 view,
                                 position );
    }

    public void clear() {
        view.clear();
    }

    public void removePart(final PartDefinition part) {
        if ( !contains( part ) ) {
            return;
        }
        final int indexOfPartToRemove = orderedParts.indexOf( part );
        definition.getParts().remove( part );
        orderedParts.remove( part );
        view.removePart( indexOfPartToRemove );
    }

    public void removePanel() {
        view.removePanel();
    }

    public void setFocus(final boolean hasFocus) {
        view.setFocus( hasFocus );
    }

    public void selectPart(final PartDefinition part) {
        if ( !contains( part ) ) {
            return;
        }
        final int indexOfPartToSelect = orderedParts.indexOf( part );
        view.selectPart( indexOfPartToSelect );
    }

    private boolean contains(final PartDefinition part) {
        return definition.getParts().contains( part );
    }

    public void onPartFocus(final int index) {
        final PartDefinition definition = orderedParts.get( index );
        workbenchPartOnFocusEvent.fire( new WorkbenchPartOnFocusEvent( definition ) );
    }

    public void onPartLostFocus(final int index) {
        final PartDefinition definition = orderedParts.get( index );
        workbenchPartLostFocusEvent.fire( new WorkbenchPartLostFocusEvent( definition ) );
    }

    public void onPanelFocus() {
        workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( getDefinition() ) );
    }

    public void onBeforePartClose(final int index) {
        final PartDefinition definition = orderedParts.get( index );
        workbenchPartBeforeCloseEvent.fire( new WorkbenchPartBeforeCloseEvent( definition ) );
    }

    public View getPanelView() {
        return view;
    }

    public void onResize(final LayoutData ld) {
        switch ( ld.direction ) {
            case NORTH :
            case SOUTH :
                definition.setHeight( ld.size );
                break;
            case EAST :
            case WEST :
                definition.setWidth( ld.size );
                break;
        }
    }

}
