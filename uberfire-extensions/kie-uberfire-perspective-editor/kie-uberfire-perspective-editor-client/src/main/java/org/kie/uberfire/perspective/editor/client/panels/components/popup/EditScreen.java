/*
* Copyright 2013 JBoss Inc
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
package org.kie.uberfire.perspective.editor.client.panels.components.popup;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditorUI;
import org.kie.uberfire.perspective.editor.model.ScreenParameter;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;
import org.kie.uberfire.properties.editor.client.PropertyEditorWidget;
import org.kie.uberfire.properties.editor.model.PropertyEditorCategory;
import org.kie.uberfire.properties.editor.model.PropertyEditorEvent;
import org.kie.uberfire.properties.editor.model.PropertyEditorFieldInfo;
import org.kie.uberfire.properties.editor.model.PropertyEditorType;
import org.kie.uberfire.properties.editor.model.validators.PropertyFieldValidator;

public class EditScreen
        extends PopupPanel {

    private final EditorWidget parent;

    @UiField
    Modal popup;

    @UiField
    TextBox key;

    @UiField
    TextBox value;

    @UiField
    PropertyEditorWidget propertyEditor;

    interface Binder
            extends
            UiBinder<Widget, EditScreen> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public EditScreen( EditorWidget parent ) {
        setWidget( uiBinder.createAndBindUi( this ) );
        this.parent = parent;
        propertyEditor.setLastOpenAccordionGroupTitle( "Screen Editors" );
        propertyEditor.handle( generateEvent( defaultScreenProperties() ) );
    }

    public void show() {
        popup.show();
    }

    @UiHandler("close")
    void close( final ClickEvent event ) {
        popup.hide();
    }

    @UiHandler("add")
    void add( final ClickEvent event ) {
        propertyEditor.setLastOpenAccordionGroupTitle( "Screen Editors" );
        propertyEditor.handle( generateEvent( addProperty() ) );
        key.setText( "" );
        value.setText( "" );
    }

    private PropertyEditorCategory addProperty() {
        PerspectiveEditorUI perspectiveEditor = getPerspectiveEditor();
        perspectiveEditor.addParameter( parent.hashCode() + "", new ScreenParameter( key.getText(), value.getText() ) );
        return defaultScreenProperties();
    }

    private PropertyEditorCategory defaultScreenProperties() {
        PerspectiveEditorUI perspectiveEditor = getPerspectiveEditor();
        Map<String, String> screenProperties = perspectiveEditor.getScreenProperties( parent.hashCode() + "" );

        PropertyEditorCategory category = new PropertyEditorCategory( "Screen Editors" );
        for ( String key : screenProperties.keySet() ) {
            category.withField(
                    new PropertyEditorFieldInfo( key, screenProperties.get( key ), PropertyEditorType.TEXT )
                            .withKey( parent.hashCode() + "" ).withValidators( new PropertyFieldValidator() {
                        @Override
                        public boolean validate( Object value ) {
                            return true;
                        }

                        @Override
                        public String getValidatorErrorMessage() {
                            return "";
                        }
                    } ) );
        }

        return category;
    }

    private PerspectiveEditorUI getPerspectiveEditor() {
        SyncBeanManager beanManager = IOC.getBeanManager();
        IOCBeanDef<PerspectiveEditorUI> perspectiveEditorIOCBeanDef = beanManager.lookupBean( PerspectiveEditorUI.class );
        return perspectiveEditorIOCBeanDef.getInstance();
    }

    private PropertyEditorEvent generateEvent( PropertyEditorCategory category ) {
        PropertyEditorEvent event = new PropertyEditorEvent( PerspectiveEditorUI.PROPERTY_EDITOR_KEY, category );
        return event;
    }

}
