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

package org.uberfire.client.screens.properties.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.properties.editor.model.PropertyEditorEvent;
import org.uberfire.properties.editor.service.BeanPropertyEditorBuilderService;

@Dependent
@WorkbenchScreen(identifier = "PropertyEditorClientScreen")
public class PropertyEditorClientScreen
        extends Composite
        implements RequiresResize {

    public static final String MY_ID = "PropertyClientScreen";

    interface ViewBinder
            extends
            UiBinder<Widget, PropertyEditorClientScreen> {

    }

    @Inject
    Event<PropertyEditorEvent> event;

    @Inject
    private Caller<BeanPropertyEditorBuilderService> beanPropertyEditorBuilderCaller;

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "PropertyClientScreen";
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }

    @UiHandler("launchDemo")
    public void onClickLaunchUnknownPlace( final ClickEvent e ) {
        event.fire( new PropertyEditorEvent( MY_ID, PropertyEditorClientScreenHelper.createProperties() ) );
    }

    public void propertyEditorChangeEvent( @Observes PropertyEditorChangeEvent event ) {
        if ( isMyPropertyEvent( event ) ) {
            Window.alert( "Msg from property editor: Changed: " + event.getProperty().getLabel() + " - new value: " + event.getNewValue() );
        }
    }

    private boolean isMyPropertyEvent( PropertyEditorChangeEvent event ) {
        return true;
    }
}