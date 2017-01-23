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

package org.uberfire.ext.preferences.client.central.form;

import java.util.Map;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@WorkbenchScreen(identifier = DefaultPreferenceForm.IDENTIFIER)
public class DefaultPreferenceForm {

    public static final String IDENTIFIER = "org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm";

    public interface View extends UberElement<DefaultPreferenceForm>,
                                  IsElement {

    }

    private TranslationService translationService;

    private final View view;

    private String id;

    private String title;

    private BasePreferencePortable<?> preference;

    private PropertyEditorCategory category;

    private PreferenceHierarchyElement<?> hierarchyElement;

    @Inject
    public DefaultPreferenceForm( final View view,
                                  final TranslationService translationService ) {
        this.view = view;
        this.translationService = translationService;
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        id = placeRequest.getParameter( "id", null );
        title = placeRequest.getParameter( "title", null );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsElement getView() {
        return view;
    }

    public void hierarchyItemFormInitializationEvent( @Observes HierarchyItemFormInitializationEvent event ) {
        if ( preference == null && event.getItemId().equals( id ) ) {
            preference = event.getPreference();
            hierarchyElement = event.getHierarchyElement();
            view.init( this );
        }
    }

    public PropertyEditorEvent generatePropertyEditorEvent() {
        if ( category == null ) {
            createPropertiesEditorCategory();
        }

        PropertyEditorEvent event = new PropertyEditorEvent( id, category );
        return event;
    }

    private void createPropertiesEditorCategory() {
        category = new PropertyEditorCategory( "Properties" );
        category.setIdEvent( id );

        for ( Map.Entry<String, PropertyFormType> property : preference.getPropertiesTypes().entrySet() ) {
            final String propertyName = property.getKey();
            final PropertyEditorType propertyType = getPropertyEditorType( property.getValue() );
            final Object propertyValue = preference.get( propertyName );

            final PropertyEditorFieldInfo fieldInfo = new PropertyEditorFieldInfo( translationService.format( hierarchyElement.getBundleKeyByProperty().get( property.getKey() ) ),
                                                                                   propertyValue != null ? propertyValue.toString() : "",
                                                                                   propertyType );
            category.withField( fieldInfo.withKey( propertyName ) );
        }
    }

    public void propertyChanged( @Observes PropertyEditorChangeEvent event ) {
        if ( event.getProperty().getEventId().equals( id ) ) {
            final String propertyName = event.getProperty().getKey();
            final PropertyFormType propertyType = preference.getPropertyType( propertyName );
            final Object newValue = propertyType.fromString( event.getNewValue() );

            preference.set( propertyName, newValue );
        }
    }

    public void saveEvent( @Observes PreferencesCentralSaveEvent event ) {
        createPropertiesEditorCategory();
        view.init( this );
    }

    public void undoEvent( @Observes PreferencesCentralUndoChangesEvent event ) {
        category.undo();
        view.init( this );
    }

    public BasePreferencePortable<?> getPreference() {
        return preference;
    }

    public PropertyEditorType getPropertyEditorType( PropertyFormType propertyFormType ) {
        return PropertyEditorType.valueOf( propertyFormType.name() );
    }
}