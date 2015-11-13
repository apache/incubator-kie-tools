/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.widget.plugin;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.NavPills;
import org.uberfire.ext.plugin.client.code.CodeElement;
import org.uberfire.ext.plugin.client.widget.media.MediaLibraryWidget;
import org.uberfire.ext.plugin.client.widget.split.HorizontalSplit;
import org.uberfire.ext.plugin.client.widget.split.VerticalSplit;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.plugin.model.PluginContent;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class GeneralPluginEditor extends Composite implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, GeneralPluginEditor> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    FlowPanel content;

    @UiField
    VerticalSplit verticalSplit;

    @UiField
    HorizontalSplit leftHorizontalSplit;

    @UiField
    HorizontalSplit rightHorizontalSplit;

    @UiField
    FlowPanel leftArea;

    @UiField
    FlowPanel leftTopArea;

    @UiField
    FlowPanel leftTopContent;

    @UiField
    FlowPanel leftBottomArea;

    @UiField
    FlowPanel leftBottomContent;

    @UiField
    FlowPanel rightArea;

    @UiField
    FlowPanel rightTopArea;

    @UiField
    FlowPanel rightTopContent;

    @UiField
    FlowPanel rightBottomArea;

    @UiField
    NavPills lifecycleHolder;

    @UiField
    Button lifecycle;

    @UiField
    DropDownMenu lifecycles;

    @UiField
    FlowPanel rightBottomContent;

    @Inject
    private MediaLibraryWidget mediaLibraryWidget;

    private final AceEditor templateEditor = new AceEditor();
    private final AceEditor cssEditor = new AceEditor();
    private final AceEditor jsEditor = new AceEditor();

    private Map<CodeType, String> codeMap = new HashMap<CodeType, String>();
    private CodeType currentElement = null;
    private PluginContent pluginContent;

    final Command editorResizing = new Command() {
        @Override
        public void execute() {
            templateEditor.redisplay();
            cssEditor.redisplay();

            Double editorHeight = 100 - ( ( (double) lifecycleHolder.getOffsetHeight() / leftBottomArea.getOffsetHeight() ) * 100 );
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
            codeMap.put( currentElement,
                         jsEditor.getText() );
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

        rightBottomContent.add( mediaLibraryWidget );

        verticalSplit.init( leftArea,
                            rightArea,
                            content,
                            editorResizing );
        leftHorizontalSplit.init( leftTopArea,
                                  leftBottomArea,
                                  content,
                                  editorResizing );
        rightHorizontalSplit.init( rightTopArea,
                                   rightBottomArea,
                                   content,
                                   editorResizing );

        setupEditor( templateEditor,
                     leftTopContent );
        setupEditor( cssEditor,
                     rightTopContent );
        setupEditor( jsEditor,
                     leftBottomContent );

        templateEditor.startEditor();
        templateEditor.setMode( AceEditorMode.HTML );
        templateEditor.setTheme( AceEditorTheme.CHROME );

        cssEditor.startEditor();
        cssEditor.setMode( AceEditorMode.CSS );
        cssEditor.setTheme( AceEditorTheme.CHROME );

        jsEditor.startEditor();
        jsEditor.setMode( AceEditorMode.JAVASCRIPT );
        jsEditor.setTheme( AceEditorTheme.CHROME );
    }

    public void setup( final CodeElement... elements ) {
        lifecycle.setIcon( elements[ 0 ].getIcon() );
        lifecycle.setText( elements[ 0 ].toString() );
        currentElement = elements[ 0 ].getType();

        for ( final CodeElement element : elements ) {
            element.addNav( lifecycles,
                            lifecycle,
                            codeChange );
        }
    }

    public void setupContent( final PluginContent pluginContent,
                              final ParameterizedCommand<Media> onMediaDelete ) {

        codeMap.clear();

        for ( final Map.Entry<CodeType, String> entry : pluginContent.getCodeMap().entrySet() ) {
            codeMap.put( entry.getKey(),
                         entry.getValue() );
        }

        jsEditor.setText( codeMap.get( currentElement ) );
        templateEditor.setText( pluginContent.getTemplate() );
        cssEditor.setText( pluginContent.getCss() );

        mediaLibraryWidget.setup( pluginContent.getName(),
                                  pluginContent.getMediaLibrary(),
                                  onMediaDelete );

        this.pluginContent = pluginContent;
    }

    private void setupEditor( final AceEditor editor,
                              final FlowPanel content ) {
        editor.setWidth( "100%" );
        editor.setHeight( "100%" );

        content.add( editor );
    }

    public PluginContent getContent() {
        return pluginContent;
    }

    public boolean isDirty() {
        return false;
    }

    public Map<CodeType, String> getCodeMap() {
        codeMap.put( currentElement, jsEditor.getText() );
        return codeMap;
    }

    public String getTemplate() {
        return templateEditor.getText();
    }

    public String getCss() {
        return cssEditor.getText();
    }

    @Override
    public void onResize() {
        getParent().getElement().getStyle().setBackgroundColor( "#F6F6F6" );
        content.getElement().getStyle().setTop( 60, Style.Unit.PX );
        verticalSplit.getElement().getStyle().setLeft( leftArea.getOffsetWidth() - 3, Style.Unit.PX );
        leftHorizontalSplit.getElement().getStyle().setTop( leftTopArea.getOffsetHeight() - 6, Style.Unit.PX );
        rightHorizontalSplit.getElement().getStyle().setTop( rightTopArea.getOffsetHeight() - 6, Style.Unit.PX );
        editorResizing.execute();
    }
}