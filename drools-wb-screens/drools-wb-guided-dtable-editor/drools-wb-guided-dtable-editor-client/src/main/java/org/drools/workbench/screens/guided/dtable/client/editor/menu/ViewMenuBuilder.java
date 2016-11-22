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
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTablePinnedEvent;
import org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi.DecisionTableSelectedEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@Dependent
public class ViewMenuBuilder extends BaseMenu implements MenuFactory.CustomMenuBuilder,
                                                         ViewMenuView.Presenter {

    public interface SupportsZoom {

        void setZoom( final int zoomLevel );

    }

    public interface HasMergedView {

        void setMerged( final boolean merged );

        boolean isMerged();

    }

    public interface HasAuditLog {

        void showAuditLog();

    }

    private ViewMenuView view;
    private GuidedDecisionTableModellerView.Presenter modeller;

    @Inject
    public ViewMenuBuilder( final ViewMenuView view ) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init( this );
        view.setZoom125( false );
        view.setZoom100( true );
        view.setZoom75( false );
        view.setZoom50( false );
        view.enableToggleMergedStateMenuItem( false );
        view.enableViewAuditLogMenuItem( false );
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

    @Override
    public void onDecisionTableSelectedEvent( final @Observes DecisionTableSelectedEvent event ) {
        super.onDecisionTableSelectedEvent( event );
    }

    @Override
    public void initialise() {
        if ( activeDecisionTable == null || !activeDecisionTable.getAccess().isEditable() ) {
            view.enableToggleMergedStateMenuItem( false );
            view.enableViewAuditLogMenuItem( false );
            view.setMerged( false );
        } else {
            view.enableToggleMergedStateMenuItem( true );
            view.enableViewAuditLogMenuItem( true );
            view.setMerged( activeDecisionTable.isMerged() );
        }
    }

    public void onDecisionTablePinnedEvent( final @Observes DecisionTablePinnedEvent event ) {
        final GuidedDecisionTableModellerView.Presenter modeller = event.getPresenter();
        if ( modeller == null ) {
            return;
        }
        if ( !modeller.equals( this.modeller ) ) {
            return;
        }
        view.enableZoom( !event.isPinned() );
    }

    @Override
    public void onZoom( final int zoom ) {
        modeller.setZoom( zoom );
        view.setZoom125( false );
        view.setZoom100( false );
        view.setZoom75( false );
        view.setZoom50( false );
        switch ( zoom ) {
            case 125:
                view.setZoom125( true );
                break;
            case 100:
                view.setZoom100( true );
                break;
            case 75:
                view.setZoom75( true );
                break;
            case 50:
                view.setZoom50( true );
                break;
        }
    }

    @Override
    public void onToggleMergeState() {
        if ( activeDecisionTable != null ) {
            final boolean newMergeState = !activeDecisionTable.isMerged();
            activeDecisionTable.setMerged( newMergeState );
            view.setMerged( newMergeState );
        }
    }

    @Override
    public void onViewAuditLog() {
        if ( activeDecisionTable != null ) {
            activeDecisionTable.showAuditLog();
        }
    }

}
