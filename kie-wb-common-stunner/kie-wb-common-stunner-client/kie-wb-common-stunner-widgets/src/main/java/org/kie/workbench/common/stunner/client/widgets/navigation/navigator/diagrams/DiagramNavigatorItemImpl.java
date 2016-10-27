/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.navigation.navigator.diagrams;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.NavigatorItemView;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.lookup.diagram.DiagramRepresentation;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class DiagramNavigatorItemImpl implements IsWidget, DiagramNavigatorItem {

    private static Logger LOGGER = Logger.getLogger( DiagramNavigatorItemImpl.class.getName() );

    ShapeManager shapeManager;
    NavigatorItemView<NavigatorItem> view;

    private String name;
    private Command callback;

    @Inject
    public DiagramNavigatorItemImpl( final ShapeManager shapeManager,
                                     final NavigatorItemView<NavigatorItem> view ) {
        this.shapeManager = shapeManager;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void show( final DiagramRepresentation diagramRepresentation,
                      final int widthInPx,
                      final int heightInPx,
                      final Command callback ) {
        this.callback = callback;
        this.name = diagramRepresentation.getName();
        view
                .setUUID( name )
                .setItemTitle( diagramRepresentation.getTitle() );
        final String thumbData = diagramRepresentation.getThumbImageData();
        if ( isEmpty( thumbData ) ) {
            final String defSetId = diagramRepresentation.getDefinitionSetId();
            final SafeUri thumbUri = shapeManager.getThumbnail( defSetId );
            view.setThumbUri( thumbUri );

        } else {
            view.setThumbData( thumbData );
        }
        view.setItemPxSize( widthInPx, heightInPx );

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public NavigatorItemView getView() {
        return view;
    }

    @Override
    public void onItemSelected() {
        if ( null != callback ) {
            callback.execute();
        }
    }

    private boolean isEmpty( final String s ) {
        return s == null || s.trim().length() == 0;
    }

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
