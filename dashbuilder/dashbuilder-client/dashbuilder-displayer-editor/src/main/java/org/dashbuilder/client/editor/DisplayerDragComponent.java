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
package org.dashbuilder.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.editor.resources.i18n.Constants;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.PerspectiveCoordinator;
import org.dashbuilder.displayer.client.widgets.DisplayerEditorPopup;
import org.dashbuilder.displayer.client.widgets.DisplayerViewer;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;

@Dependent
public class DisplayerDragComponent implements LayoutDragComponent, HasModalConfiguration {

    SyncBeanManager beanManager;
    DisplayerViewer viewer;
    PlaceManager placeManager;
    PerspectiveCoordinator perspectiveCoordinator;
    DisplayerSettingsJSONMarshaller marshaller;

    @Inject
    public DisplayerDragComponent(SyncBeanManager beanManager,
                                  DisplayerViewer viewer,
                                  PlaceManager placeManager,
                                  PerspectiveCoordinator perspectiveCoordinator) {

        this.beanManager = beanManager;
        this.viewer = viewer;
        this.placeManager = placeManager;
        this.perspectiveCoordinator = perspectiveCoordinator;
        this.marshaller = DisplayerSettingsJSONMarshaller.get();
    }

    public DisplayerType getDisplayerType() {
        return null;
    }

    public DisplayerSubType getDisplayerSubType() {
        return null;
    }

    @Override
    public String getDragComponentTitle() {
        return Constants.INSTANCE.DisplayerComponent();
    }

    @Override
    public IsWidget getPreviewWidget( final RenderingContext ctx ) {
        return getShowWidget( ctx );
    }

    @Override
    public IsWidget getShowWidget( final RenderingContext ctx ) {
        Map<String, String> properties = ctx.getComponent().getProperties();
        String json = properties.get( "json" );
        if ( json == null ) {
            return null;
        }

        final DisplayerSettings settings = marshaller.fromJsonString( json );
        viewer.removeFromParent();
        viewer.init( settings );
        viewer.addAttachHandler(attachEvent -> {
            if (attachEvent.isAttached()) {
                final int offsetWidth = ctx.getContainer().getOffsetWidth();
                int containerWidth = offsetWidth > 40 ?  offsetWidth - 40 : 0;
                adjustSize( settings, containerWidth );
                Displayer displayer = viewer.draw();
                perspectiveCoordinator.addDisplayer( displayer );
            }
        });
        int containerWidth = ctx.getContainer().getOffsetWidth() - 40;
        adjustSize(settings, containerWidth);
        Displayer displayer = viewer.draw();
        perspectiveCoordinator.addDisplayer(displayer);
        return viewer;
    }

    @Override
    public Modal getConfigurationModal( final ModalConfigurationContext ctx ) {
        return buildEditorPopUp(ctx);
    }

    protected DisplayerEditorPopup buildEditorPopUp(final ModalConfigurationContext ctx) {
        Map<String, String> properties = ctx.getComponentProperties();
        String json = properties.get( "json" );
        DisplayerSettings settings = json != null ? marshaller.fromJsonString( json ) : initialSettings(ctx);
        DisplayerEditorPopup editor = beanManager.lookupBean( DisplayerEditorPopup.class ).newInstance();

        // For brand new components set the default type/subtype to create
        if (settings == null) {
            if (getDisplayerType() != null) {
                editor.setDisplayerType(getDisplayerType());
            }
            if (getDisplayerSubType() != null) {
                editor.setDisplayerSubType(getDisplayerSubType());
            }
        }

        editor.init( settings );
        editor.setOnSaveCommand( getSaveCommand( editor, ctx ) );
        editor.setOnCloseCommand( getCloseCommand( editor, ctx ) );
        return editor;
    }

    protected DisplayerSettings initialSettings(final ModalConfigurationContext ct) {
        return null;
    }

    protected Command getSaveCommand( final DisplayerEditorPopup editor, final ModalConfigurationContext ctx ) {
        return () -> {
            String json = marshaller.toJsonString( editor.getDisplayerSettings() );
            ctx.setComponentProperty( "json", json );
            ctx.configurationFinished();
            beanManager.destroyBean( editor );
        };
    }

    protected Command getCloseCommand( final DisplayerEditorPopup editor, final ModalConfigurationContext ctx ) {
        return () -> {
            ctx.configurationCancelled();
            beanManager.destroyBean( editor );
        };
    }

    protected void adjustSize( DisplayerSettings settings, int containerWidth ) {
        int displayerWidth = settings.getChartWidth();
        int tableWidth = settings.getTableWidth();
        if ( containerWidth > 0 && displayerWidth > containerWidth ) {
            int ratio = containerWidth * 100 / displayerWidth;
            settings.setChartWidth( containerWidth );
            settings.setChartHeight( settings.getChartHeight() * ratio / 100 );
        }
        if ( tableWidth == 0 || tableWidth > containerWidth ) {
            settings.setTableWidth( containerWidth > 20 ? containerWidth - 20 : 0 );
        }
    }
}
