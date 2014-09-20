/*
 * Copyright 2012 JBoss Inc
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

package org.kie.uberfire.plugin.client.editor;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavPills;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.ace.AceEditor;
import org.kie.uberfire.client.ace.AceEditorMode;
import org.kie.uberfire.client.ace.AceEditorTheme;
import org.kie.uberfire.plugin.client.code.CodeElement;
import org.kie.uberfire.plugin.client.type.PerspectivePluginResourceType;
import org.kie.uberfire.plugin.client.widget.split.HorizontalSplit;
import org.kie.uberfire.plugin.model.CodeType;
import org.kie.uberfire.plugin.model.Plugin;
import org.kie.uberfire.plugin.model.PluginContent;
import org.kie.uberfire.plugin.model.PluginSimpleContent;
import org.kie.uberfire.plugin.model.PluginType;
import org.kie.uberfire.plugin.service.PluginServices;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.uberfire.plugin.client.code.CodeList.*;

@Dependent
@WorkbenchEditor(identifier = "Perspective PlugIn Editor", supportedTypes = { PerspectivePluginResourceType.class }, priority = Integer.MAX_VALUE)
public class PerspectiveEditor
        extends Composite
        implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, PerspectiveEditor> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    FlowPanel htmlPanel;

    @UiField
    FlowPanel content;

    @UiField
    HorizontalSplit leftHorizontalSplit;

    @UiField
    FlowPanel topArea;

    @UiField
    FlowPanel topContent;

    @UiField
    FlowPanel bottomArea;

    @UiField
    FlowPanel bottomContent;

    @UiField
    NavPills lifecycleHolder;

    @UiField
    Dropdown lifecycles;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private PlaceManager placeManager;

    private PlaceRequest place;

    private Plugin plugin;

    private final AceEditor templateEditor = new AceEditor();
    private final AceEditor jsEditor = new AceEditor();

    private Map<CodeType, String> codeMap = new HashMap<CodeType, String>();
    private CodeType currentElement = null;
    private PluginContent pluginContent;

    final Command editorResizing = new Command() {
        @Override
        public void execute() {
            templateEditor.redisplay();

            Double editorHeight = 100 - ( ( (double) lifecycleHolder.getOffsetHeight() / bottomArea.getOffsetHeight() ) * 100 );
            if ( editorHeight.equals( Double.NaN ) || editorHeight.doubleValue() <= 0d ) {
                return;
            }
            jsEditor.setHeight( editorHeight + "%" );
            jsEditor.redisplay();

        }
    };

    final ParameterizedCommand<CodeType> codeChange = new ParameterizedCommand<CodeType>() {
        @Override
        public void execute( final CodeType parameter ) {
            codeMap.put( currentElement, jsEditor.getText() );
            currentElement = parameter;
            final String content = codeMap.get( currentElement );
            if ( content != null ) {
                jsEditor.setText( content );
            } else {
                jsEditor.setText( "" );
            }
            jsEditor.setFocus();
        }
    };

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        htmlPanel.getElement().getStyle().setBackgroundColor( "#F6F6F6" );

        setup( MAIN, DIVIDER, ON_OPEN, ON_CLOSE, ON_SHUTDOWN, DIVIDER, PANEL_TYPE );

        leftHorizontalSplit.init( topArea, bottomArea, content, editorResizing );

        setupEditor( templateEditor, topContent );
        setupEditor( jsEditor, bottomContent );

        templateEditor.startEditor();
        templateEditor.setMode( AceEditorMode.JAVASCRIPT );
        templateEditor.setTheme( AceEditorTheme.CHROME );

        jsEditor.startEditor();
        jsEditor.setMode( AceEditorMode.JAVASCRIPT );
        jsEditor.setTheme( AceEditorTheme.CHROME );
    }

    @OnStartup
    public void onStartup( final Path path,
                           final PlaceRequest place ) {
        pluginServices.call( new RemoteCallback<PluginContent>() {
            @Override
            public void callback( final PluginContent response ) {
                setupContent( response );
            }
        } ).getPluginContent( path );
        plugin = new Plugin( place.getParameter( "name", "" ), PluginType.SCREEN, path );
        this.place = place;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Perspective PlugIn Editor [" + plugin.getName() + "]";
    }

    @WorkbenchMenu
    public Menus getMenu() {
        return new PluginsCommonMenu().build( new Command() {
            @Override
            public void execute() {
                final PluginSimpleContent content = new PluginSimpleContent( pluginContent, getTemplate(), getCodeMap() );

                pluginServices.call().save( content );
            }
        }, new Command() {
            @Override
            public void execute() {
                pluginServices.call().delete( plugin );
                placeManager.forceClosePlace( place );
            }
        } );
    }

    public Map<CodeType, String> getCodeMap() {
        codeMap.put( currentElement, jsEditor.getText() );
        return codeMap;
    }

    public String getTemplate() {
        return templateEditor.getText();
    }

    private void setup( final CodeElement... elements ) {
        lifecycles.setIcon( elements[ 0 ].getIcon() );
        lifecycles.setText( elements[ 0 ].toString() );
        currentElement = elements[ 0 ].getType();

        for ( final CodeElement element : elements ) {
            element.addNav( lifecycles, codeChange );
        }
    }

    private void setupContent( final PluginContent pluginContent ) {

        for ( final Map.Entry<CodeType, String> entry : pluginContent.getCodeMap().entrySet() ) {
            codeMap.put( entry.getKey(), entry.getValue() );
        }

        jsEditor.setText( codeMap.get( currentElement ) );
        templateEditor.setText( pluginContent.getTemplate() );

        this.pluginContent = pluginContent;
    }

    private void setupEditor( final AceEditor editor,
                              final FlowPanel content ) {
        editor.setWidth( "100%" );
        editor.setHeight( "100%" );

        content.add( editor );
    }

    @Override
    public void onResize() {
        htmlPanel.setHeight( getParent().getParent().getOffsetHeight() + "px" );
        leftHorizontalSplit.getElement().getStyle().setTop( topArea.getOffsetHeight() - 6, Style.Unit.PX );
        editorResizing.execute();
    }
}