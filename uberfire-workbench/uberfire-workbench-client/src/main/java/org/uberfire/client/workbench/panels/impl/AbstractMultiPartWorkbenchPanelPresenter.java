/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.workbench.panels.impl;

import static org.uberfire.workbench.model.ContextDisplayMode.*;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.ContextDisplayMode;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

public abstract class AbstractMultiPartWorkbenchPanelPresenter<P extends AbstractMultiPartWorkbenchPanelPresenter<P>>
extends AbstractDockingWorkbenchPanelPresenter<P> {

    protected ActivityManager activityManager;
    private ContextActivity perspectiveContext = null;
    private ContextActivity panelContext = null;
    private final Map<PartDefinition, ContextActivity> contextActivities = new HashMap<PartDefinition, ContextActivity>();

    protected AbstractMultiPartWorkbenchPanelPresenter( final WorkbenchPanelView<P> view,
                                                        final ActivityManager activityManager,
                                                        final PerspectiveManager perspectiveManager ) {
        super( view, perspectiveManager );
        this.activityManager = activityManager;
    }

    private void buildPerspectiveContext() {
        final ContextDefinition contextDefinition = perspectiveManager.getLivePerspectiveDefinition().getContextDefinition();
        final ContextDisplayMode contextDisplayMode = perspectiveManager.getLivePerspectiveDefinition().getContextDisplayMode();
        if ( contextDefinition != null && contextDisplayMode == SHOW ) {
            final ContextActivity activity = activityManager.getActivity( ContextActivity.class, contextDefinition.getPlace() );
            if ( activity != null ) {
                perspectiveContext = activity;
            }
        }
    }

    @Override
    public void setDefinition( final PanelDefinition definition ) {
        super.setDefinition( definition );

        final ContextDisplayMode perspectiveContextDisplayMode = perspectiveManager.getLivePerspectiveDefinition().getContextDisplayMode();

        if ( definition.getContextDefinition() != null
                && perspectiveContextDisplayMode == SHOW
                && definition.getContextDisplayMode() == SHOW ) {
            final ContextActivity activity = activityManager.getActivity( ContextActivity.class, definition.getContextDefinition().getPlace() );
            if ( activity != null ) {
                panelContext = activity;
            }
        }
        buildPerspectiveContext();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter part,
                         final String contextId ) {
        super.addPart( part, contextId );
        final ContextDisplayMode perspectiveContextDisplayMode = perspectiveManager.getLivePerspectiveDefinition().getContextDisplayMode();
        if ( perspectiveContextDisplayMode == SHOW
                && getDefinition().getContextDisplayMode() == SHOW
                && part.getDefinition().getContextDisplayMode() == SHOW ) {
            ContextActivity contextActivity = null;
            if ( contextId != null ) {
                contextActivity = activityManager.getActivity( ContextActivity.class, new DefaultPlaceRequest( contextId ) );
            } else if ( part.getDefinition().getContextDefinition() != null ) {
                contextActivity = activityManager.getActivity( ContextActivity.class, part.getDefinition().getContextDefinition().getPlace() );
            } else if ( part.getContextId() != null ) {
                contextActivity = activityManager.getActivity( ContextActivity.class, new DefaultPlaceRequest( part.getContextId() ) );
            }
            if ( contextActivity != null ) {
                contextActivities.put( part.getDefinition(), contextActivity );
            }
        }
    }

    @Override
    public boolean removePart( final PartDefinition part ) {
        boolean removed = super.removePart( part );
        contextActivities.remove( part );
        return removed;
    }

    public ContextActivity resolveContext( final PartDefinition part ) {
        ContextActivity result = perspectiveContext;
        if ( panelContext != null ) {
            result = panelContext;
        }
        if ( contextActivities.containsKey( part ) ) {
            result = contextActivities.get( part );
        }
        return result;
    }
}
