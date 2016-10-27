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

package org.kie.workbench.common.stunner.client.widgets.navigation.navigator.shapesets;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.NavigatorItemView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class ShapeSetNavigatorItemImpl implements IsWidget, ShapeSetNavigatorItem {

    private static Logger LOGGER = Logger.getLogger( ShapeSetNavigatorItemImpl.class.getName() );

    ShapeManager shapeManager;
    DefinitionManager definitionManager;
    NavigatorItemView<NavigatorItem> view;

    private String uuid;
    private Command callback;

    @Inject
    public ShapeSetNavigatorItemImpl( final ShapeManager shapeManager,
                                      final DefinitionManager definitionManager,
                                      final NavigatorItemView<NavigatorItem> view ) {
        this.shapeManager = shapeManager;
        this.definitionManager = definitionManager;
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

    public void show( final ShapeSet shapeSet,
                      final int widthInPx,
                      final int heightInPx,
                      final Command callback ) {
        this.callback = callback;
        this.uuid = shapeSet.getId();
        final String defSetId = shapeSet.getDefinitionSetId();
        final SafeUri thumbUri = shapeManager.getThumbnail( defSetId );
        final Object defSet = definitionManager.definitionSets().getDefinitionSetById( defSetId );
        final String description = definitionManager.adapters().forDefinitionSet().getDescription( defSet );
        view
                .setUUID( uuid )
                .setItemTitle( description )
                .setThumbUri( thumbUri );
        view.setItemPxSize( widthInPx, heightInPx );

    }

    @Override
    public String getName() {
        return uuid;
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

    private void log( final Level level, final String message ) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log( level, message );
        }
    }

}
