/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.workitems.client.widget;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.drools.workbench.screens.workitems.client.resources.i18n.WorkItemsEditorConstants;
import org.drools.workbench.screens.workitems.model.WorkItemDefinitionElements;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.uberfire.client.mvp.UberView;

@Dependent
public class WorkItemDefinitionElementsBrowser extends Composite implements HasWorkItemDefinitionElements,
                                                                            UberView<WorkItemDefinitionElementSelectedListener> {

    @Inject
    private WorkItemMetaDataLoader metaDataLoader;

    private final Container container = new Container() {{
        setFluid( true );
    }};

    private final ListBox imagesList = new ListBox() {{
        addStyleName( ColumnSize.MD_12.getCssName() );
    }};

    private final FlowPanel elements = new FlowPanel() {{
        getElement().getStyle().setPaddingBottom( 5, Style.Unit.PX );
    }};

    private WorkItemDefinitionElementSelectedListener presenter;

    public WorkItemDefinitionElementsBrowser() {
        final Heading label = new Heading( HeadingSize.H4, WorkItemsEditorConstants.INSTANCE.BrowserTitle() );
        label.getElement().getStyle().setTextAlign( Style.TextAlign.CENTER );
        label.addStyleName( ColumnSize.MD_12.getCssName() );
        container.add( new Row() {{
            add( label );
        }} );

        container.add( new Row() {{
            add( elements );
        }} );

        //Images
        container.add( new Row() {{
            add( imagesList );
        }} );
        imagesList.setVisibleItemCount( 1 );
        imagesList.setSelectedIndex( 0 );

        imagesList.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                elementSelected( imagesList.getItemText( imagesList.getSelectedIndex() ),
                                 imagesList.getValue( imagesList.getSelectedIndex() ) );
            }
        } );

        initWidget( container );
    }

    @PostConstruct
    public void loadMetaData() {
        metaDataLoader.loadMetaContent( this );
    }

    @Override
    public void init( final WorkItemDefinitionElementSelectedListener presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setDefinitionElements( final WorkItemDefinitionElements metaData ) {
        for ( final Map.Entry<String, String> entry : metaData.getDefinitionElements().entrySet() ) {
            final String description = getButtonDescription( entry.getKey() );
            elements.add( new Button( description ) {{
                setBlock( true );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        elementSelected( description,
                                         entry.getValue() );
                    }
                } );
            }} );
        }
    }

    private String getButtonDescription( String buttonKey ) {
        if ( buttonKey == null || buttonKey.trim().length() == 0 ) {
            return "";
        } else if ( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DEFINITION.equals( buttonKey ) ) {
            return WorkItemsEditorConstants.INSTANCE.Definition();
        } else if ( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DISPLAY_NAME.equals( buttonKey ) ) {
            return WorkItemsEditorConstants.INSTANCE.DisplayName();
        } else if ( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_PARAMETER.equals( buttonKey ) ) {
            return WorkItemsEditorConstants.INSTANCE.Parameter();
        } else if ( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_RESULT.equals( buttonKey ) ) {
            return WorkItemsEditorConstants.INSTANCE.Result();
        } else if( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_CUSTOM_EDITOR.equals( buttonKey ) ) {
            return WorkItemsEditorConstants.INSTANCE.CustomEditor();
        } else if( WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_PARAMETER_VALUES.equals( buttonKey ) ) {
            return WorkItemsEditorConstants.INSTANCE.ParameterValues();
        } else if (WorkItemsEditorService.WORK_ITEMS_EDITOR_SETTINGS_DEFAULT_HANDLER.equals(buttonKey)) {
            return WorkItemsEditorConstants.INSTANCE.DefaultHandler();
        }
        return buttonKey;
    }

    public void setImages( final List<String> workItemImages ) {
        imagesList.clear();
        imagesList.addItem( WorkItemsEditorConstants.INSTANCE.ChooseIcon() );
        for ( String workItemImage : workItemImages ) {
            imagesList.addItem( workItemImage );
        }
    }

    private void elementSelected( final String title,
                                  final String value ) {
        presenter.onElementSelected( title,
                                     value );
    }

}
