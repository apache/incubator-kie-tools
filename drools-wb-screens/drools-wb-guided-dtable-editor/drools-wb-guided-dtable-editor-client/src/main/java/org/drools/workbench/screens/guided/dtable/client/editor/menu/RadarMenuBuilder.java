/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTablePinnedEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class RadarMenuBuilder implements MenuFactory.CustomMenuBuilder,
                                         RadarMenuView.Presenter {

    private RadarMenuView view;
    private GuidedDecisionTableModellerView.Presenter modeller;

    //Event to trigger redraw of Radar
    public static class UpdateRadarEvent {

        private final GuidedDecisionTableModellerView.Presenter modeller;

        public UpdateRadarEvent( final GuidedDecisionTableModellerView.Presenter modeller ) {
            this.modeller = modeller;
        }

        public GuidedDecisionTableModellerView.Presenter getModeller() {
            return modeller;
        }

    }

    @Inject
    public RadarMenuBuilder( final RadarMenuView view ) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init( this );
    }

    @Override
    public void setModeller( final GuidedDecisionTableModellerView.Presenter modeller ) {
        this.modeller = modeller;
    }

    @Override
    public void push( final MenuFactory.CustomMenuBuilder element ) {
    }

    @Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return view;
            }

            @Override
            public boolean isEnabled() {
                return view.isEnabled();
            }

            @Override
            public void setEnabled( final boolean enabled ) {
                view.setEnabled( enabled );
            }
        };
    }

    @SuppressWarnings("unused")
    public void onUpdateRadarEvent( final @Observes UpdateRadarEvent event ) {
        final GuidedDecisionTableModellerView.Presenter modeller = event.getModeller();
        if ( modeller == null ) {
            return;
        }
        if ( !modeller.equals( this.modeller ) ) {
            return;
        }
        onClick();
    }

    @SuppressWarnings("unused")
    public void onDecisionTablePinnedEvent( final @Observes DecisionTablePinnedEvent event ) {
        final GuidedDecisionTableModellerView.Presenter modeller = event.getPresenter();
        if ( modeller == null ) {
            return;
        }
        if ( !modeller.equals( this.modeller ) ) {
            return;
        }
        view.enableDrag( !event.isPinned() );
    }

    @Override
    public void onClick() {
        view.reset();
        view.setModellerBounds( modeller.getView().getBounds() );
        view.setAvailableDecisionTables( modeller.getAvailableDecisionTables() );
        view.setVisibleBounds( modeller.getView().getGridLayerView().getVisibleBounds() );
    }

    @Override
    public void onDragVisibleBounds( final double canvasX,
                                     final double canvasY ) {
        final double _canvasX = -canvasX;
        final double _canvasY = -canvasY;
        final Transform oldTransform = modeller.getView().getGridLayerView().getViewport().getTransform();
        final double scaleX = oldTransform.getScaleX();
        final double scaleY = oldTransform.getScaleY();
        final double translateX = oldTransform.getTranslateX();
        final double translateY = oldTransform.getTranslateY();
        final double dx = _canvasX - ( translateX / scaleX );
        final double dy = _canvasY - ( translateY / scaleY );
        final Transform newTransform = oldTransform.copy().translate( dx,
                                                                      dy );

        modeller.getView().getGridLayerView().getViewport().setTransform( newTransform );
        modeller.getView().getGridLayerView().batch();
    }

}
