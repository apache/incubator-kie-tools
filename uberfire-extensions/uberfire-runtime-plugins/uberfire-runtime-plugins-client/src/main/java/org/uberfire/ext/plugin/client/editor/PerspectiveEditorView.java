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

package org.uberfire.ext.plugin.client.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavPills;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;
import org.uberfire.ext.plugin.client.code.CodeElement;
import org.uberfire.ext.plugin.client.widget.split.HorizontalSplit;
import org.uberfire.ext.plugin.model.*;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import java.util.HashMap;
import java.util.Map;

import static org.uberfire.ext.plugin.client.code.CodeList.*;

@Dependent
public class PerspectiveEditorView
        extends BaseEditorViewImpl
        implements UberView<PerspectiveEditorPresenter>,
        Editor<RuntimePlugin>, RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, PerspectiveEditorView> {

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

    protected final AceEditor templateEditor = new AceEditor();
    protected final AceEditor jsEditor = new AceEditor();

    protected Map<CodeType, String> codeMap = new HashMap<CodeType, String>();
    protected CodeType currentElement = null;

    protected PluginContent pluginContent;

    private PerspectiveEditorPresenter presenter;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final PerspectiveEditorPresenter presenter ) {
        this.presenter = presenter;

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


    final Command editorResizing = new Command() {
        @Override
        public void execute() {
            templateEditor.redisplay();

            Double editorHeight = 100 - ( ( ( double ) lifecycleHolder.getOffsetHeight() / bottomArea.getOffsetHeight() ) * 100 );
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

    private void setup( final CodeElement... elements ) {
        lifecycles.setIcon( elements[ 0 ].getIcon() );
        lifecycles.setText( elements[ 0 ].toString() );
        currentElement = elements[ 0 ].getType();

        for ( final CodeElement element : elements ) {
            element.addNav( lifecycles, codeChange );
        }
    }

    protected void setupContent( final PluginContent pluginContent ) {

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

    public String getTemplate() {
        return templateEditor.getText();
    }

    public Map<CodeType, String> getCodeMap() {
        codeMap.put( currentElement, jsEditor.getText() );
        return codeMap;
    }

    public PluginSimpleContent getContent() {
        return new PluginSimpleContent( pluginContent, getTemplate(), getCodeMap() );
    }

    @Override
    public void onResize() {
        htmlPanel.setHeight( getParent().getParent().getOffsetHeight() + "px" );
        leftHorizontalSplit.getElement().getStyle().setTop( topArea.getOffsetHeight() - 6, Style.Unit.PX );
        editorResizing.execute();
    }
}