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
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.panels.PanelManager;

import com.google.gwt.user.client.ui.IsWidget;
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

        void addPart(IsWidget title,
                     WorkbenchPartPresenter.View view);

        void addPanel(PanelDefinition panel,
                      WorkbenchPanelPresenter.View view,
                      Position position);

        void changeTabContent(int indexOfPartToChangeTabContent,
                              IsWidget tabContent);

        void selectPart(int index);

        void removePart(int index);

        void removePanel();

        void setFocus(boolean hasFocus);

    }

    private View                 view;

    private PanelManager         panelManager;

    private PanelDefinition      definition;

    private List<PartDefinition> orderedParts = new ArrayList<PartDefinition>();

    @Inject
    public WorkbenchPanelPresenter(final View view,
                                   final PanelManager panelManager) {
        this.view = view;
        this.panelManager = panelManager;
    }

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

    public void addPart(final IsWidget tabWidget,
                        final PartDefinition part,
                        final WorkbenchPartPresenter.View view) {
        //The model for a Perspective is already fully populated. Don't go adding duplicates.
        if ( !definition.getParts().contains( part ) ) {
            definition.addPart( part );
        }
        if ( !orderedParts.contains( part ) ) {
            orderedParts.add( part );
        }
        getPanelView().addPart( tabWidget,
                                view );
    }

    public void changeTabContent(final PartDefinition part,
                                 final IsWidget tabContent) {
        if ( !contains( part ) ) {
            return;
        }
        final int indexOfPartToChangeTabContent = orderedParts.indexOf( part );
        getPanelView().changeTabContent( indexOfPartToChangeTabContent,
                                         tabContent );
    }

    public void addPanel(final PanelDefinition panel,
                         final WorkbenchPanelPresenter.View view,
                         final Position position) {
        getPanelView().addPanel( panel,
                                 view,
                                 position );
        definition.insertChild( position,
                                panel );
    }

    public void clear() {
        view.clear();
    }

    public void removePart(final PartDefinition part) {
        if ( !contains( part ) ) {
            return;
        }
        final int indexOfPartToRemove = orderedParts.indexOf( part );
        view.removePart( indexOfPartToRemove );
        definition.getParts().remove( part );
        orderedParts.remove( part );
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

    public void onPartFocus(final PartDefinition part) {
        panelManager.onPartFocus( part );
    }

    public void onPartLostFocus() {
        panelManager.onPartLostFocus();
    }

    public void onPanelFocus() {
        panelManager.onPanelFocus( definition );
    }

    public void onBeforePartClose(final PartDefinition part) {
        panelManager.onBeforePartClose( part );
    }

    public View getPanelView() {
        return view;
    }

    public void onResize(final int width,
                         final int height) {
        getDefinition().setWidth( width == 0 ? null : width );
        getDefinition().setHeight( height == 0 ? null : height );
    }

}
