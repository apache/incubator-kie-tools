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

package org.uberfire.ext.wires.core.client.properties;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.wires.core.api.events.ShapeSelectedEvent;
import org.uberfire.ext.wires.core.api.properties.PropertyEditorAdaptor;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen(identifier = "WiresPropertiesScreen")
public class PropertiesScreen extends Composite {

    interface ViewBinder extends UiBinder<Widget, PropertiesScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private static final String MY_ID = "WiresPropertiesScreen";

    private WiresBaseShape selectedShape;

    @UiField
    FlowPanel panel;

    @UiField
    PropertyEditorWidget propertyEditorWidget;

    @Inject
    PropertyEditorAdaptorsCache adaptors;

    @PostConstruct
    public void init() {
        super.initWidget( uiBinder.createAndBindUi( this ) );
    }

    @WorkbenchPartTitle
    @Override
    public String getTitle() {
        return "Properties Editor";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

    public void onShapeSelectedEvent( @Observes ShapeSelectedEvent event ) {
        selectedShape = event.getShape();
        propertyEditorWidget.handle( new PropertyEditorEvent( MY_ID,
                                                              getProperties( selectedShape ) ) );

    }

    protected List<PropertyEditorCategory> getProperties( final WiresBaseShape shape ) {
        final List<PropertyEditorCategory> properties = new ArrayList<PropertyEditorCategory>();
        for ( PropertyEditorAdaptor adaptor : adaptors.getAdaptors() ) {
            if ( adaptor.supports( shape ) ) {
                properties.addAll( adaptor.getProperties( shape ) );
            }
        }
        return properties;
    }

}
