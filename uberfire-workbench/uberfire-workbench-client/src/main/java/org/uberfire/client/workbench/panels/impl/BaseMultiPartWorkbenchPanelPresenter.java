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
package org.uberfire.client.workbench.panels.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.MaximizePlaceEvent;
import org.uberfire.workbench.events.MinimizePlaceEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import static org.uberfire.workbench.model.ContextDisplayMode.*;

public class BaseMultiPartWorkbenchPanelPresenter implements WorkbenchPanelPresenter {

    protected BaseMultiPartWorkbenchPanelView view;

    protected PanelManager panelManager;

    protected ActivityManager activityManager;

    protected PanelDefinition definition;

    protected Event<MaximizePlaceEvent> maximizePanelEvent;

    protected Event<MinimizePlaceEvent> minimizePanelEvent;

    private ContextActivity perspectiveContext = null;
    private ContextActivity panelContext = null;
    private Map<PartDefinition, ContextActivity> partMap = new HashMap<PartDefinition, ContextActivity>();

    @PostConstruct
    private void init() {
        view.init( this );
    }

    private void buildPerspectiveContext() {
        if ( panelManager.getPerspective().getContextDefinition() != null && panelManager.getPerspective().getContextDisplayMode() == SHOW ) {
            final ContextActivity activity = activityManager.getActivity( ContextActivity.class, panelManager.getPerspective().getContextDefinition().getPlace() );
            if ( activity != null ) {
                perspectiveContext = activity;
            }
        }
    }

    @Override
    public PanelDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition( final PanelDefinition definition ) {
        this.definition = definition;

        if ( definition.getContextDefinition() != null
                && panelManager.getPerspective().getContextDisplayMode() == SHOW
                && definition.getContextDisplayMode() == SHOW ) {
            final ContextActivity activity = activityManager.getActivity( ContextActivity.class, definition.getContextDefinition().getPlace() );
            if ( activity != null ) {
                panelContext = activity;
            }
        }
        buildPerspectiveContext();
    }

    @Override
    public void addPart( WorkbenchPartPresenter.View view ) {
        addPart( view, null );
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view,
                         final String contextId ) {
        getPanelView().addPart( view );
        if ( panelManager.getPerspective().getContextDisplayMode() == SHOW
                && definition.getContextDisplayMode() == SHOW
                && view.getPresenter().getDefinition().getContextDisplayMode() == SHOW ) {
            ContextActivity activity = null;
            if ( contextId != null ) {
                activity = activityManager.getActivity( ContextActivity.class, new DefaultPlaceRequest( contextId ) );
            } else if ( view.getPresenter().getDefinition().getContextDefinition() != null ) {
                activity = activityManager.getActivity( ContextActivity.class, view.getPresenter().getDefinition().getContextDefinition().getPlace() );
            } else if ( view.getPresenter().getContextId() != null ) {
                activity = activityManager.getActivity( ContextActivity.class, new DefaultPlaceRequest( view.getPresenter().getContextId() ) );
            }
            if ( activity != null ) {
                partMap.put( view.getPresenter().getDefinition(), activity );
            }
        }
    }

    @Override
    public void removePart( final PartDefinition part ) {
        view.removePart( part );
        partMap.remove( partMap );
    }

    @Override
    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position ) {
        getPanelView().addPanel( panel,
                                 view,
                                 position );
        definition.insertChild( position,
                                panel );
    }

    @Override
    public void removePanel() {
        view.removePanel();
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDescorator ) {
        getPanelView().changeTitle( part, title, titleDescorator );
    }

    @Override
    public void setFocus( final boolean hasFocus ) {
        view.setFocus( hasFocus );
    }

    @Override
    public void selectPart( final PartDefinition part ) {
        if ( !contains( part ) ) {
            return;
        }
        view.selectPart( part );
    }

    private boolean contains( final PartDefinition part ) {
        return definition.getParts().contains( part );
    }

    @Override
    public void onPartFocus( final PartDefinition part ) {
        panelManager.onPartFocus( part );
    }

    @Override
    public void onPartLostFocus() {
        panelManager.onPartLostFocus();
    }

    @Override
    public void onPanelFocus() {
        panelManager.onPanelFocus( definition );
    }

    @Override
    public void onBeforePartClose( final PartDefinition part ) {
        panelManager.onBeforePartClose( part );
    }

    @Override
    public void maximize() {
        if ( !getDefinition().isRoot() ) {
            for ( PartDefinition part : getDefinition().getParts() ) {
                maximizePanelEvent.fire( new MaximizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    @Override
    public void minimize() {
        if ( !getDefinition().isRoot() ) {
            for ( PartDefinition part : getDefinition().getParts() ) {
                minimizePanelEvent.fire( new MinimizePlaceEvent( part.getPlace() ) );
            }
        }
    }

    @Override
    public WorkbenchPanelView getPanelView() {
        return view;
    }

    @Override
    public void onResize( final int width,
                          final int height ) {
        getDefinition().setWidth( width == 0 ? null : width );
        getDefinition().setHeight( height == 0 ? null : height );
    }

    public ContextActivity resolveContext( final PartDefinition part ) {
        ContextActivity result = perspectiveContext;
        if ( panelContext != null ) {
            result = panelContext;
        }
        if ( partMap.containsKey( part ) ) {
            result = partMap.get( part );
        }
        return result;
    }
}
