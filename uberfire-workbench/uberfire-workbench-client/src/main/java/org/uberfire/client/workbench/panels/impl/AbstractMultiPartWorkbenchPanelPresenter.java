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

import static org.uberfire.workbench.model.ContextDisplayMode.*;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.events.MinimizePlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

public abstract class AbstractMultiPartWorkbenchPanelPresenter<P extends AbstractMultiPartWorkbenchPanelPresenter<P>> extends AbstractWorkbenchPanelPresenter<P> {

    protected ActivityManager activityManager;
    private ContextActivity perspectiveContext = null;
    private ContextActivity panelContext = null;
    private final Map<PartDefinition, ContextActivity> partMap = new HashMap<PartDefinition, ContextActivity>();

    protected AbstractMultiPartWorkbenchPanelPresenter( final WorkbenchPanelView<P> view,
                                                        final ActivityManager activityManager,
                                                        final PanelManager panelManager,
                                                        final Event<MaximizePlaceEvent> maximizePanelEvent,
                                                        final Event<MinimizePlaceEvent> minimizePanelEvent ) {
        super( view, panelManager, maximizePanelEvent, minimizePanelEvent );
        this.activityManager = activityManager;
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
    public void setDefinition( final PanelDefinition definition ) {
        super.setDefinition( definition );

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
    public void addPart( final WorkbenchPartPresenter.View view,
                         final String contextId ) {
        getPanelView().addPart( view );
        if ( panelManager.getPerspective().getContextDisplayMode() == SHOW
                && getDefinition().getContextDisplayMode() == SHOW
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
            } else {
                System.out.println("Warning: couldn't add this view to the partMap (no activity found): " + view);
            }

        }
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        boolean removed = super.removePart( part );
        partMap.remove( partMap );
        return removed;
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
