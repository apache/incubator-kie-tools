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
package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import static org.uberfire.ext.plugin.client.perspective.editor.layout.editor.ScreenLayoutDragComponent.PLACE_NAME_PARAMETER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.plugin.client.validation.NameValidator;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

public class EditScreen
        extends BaseModal {

    public static String PROPERTY_EDITOR_KEY = "LayoutEditor";

    private final ModalConfigurationContext configContext;

    @UiField
    TextBox key;

    @UiField
    FormGroup paramKeyControlGroup;

    @UiField
    HelpBlock paramKeyInline;

    @UiField
    TextBox value;

    @UiField
    PropertyEditorWidget propertyEditor;

    private Boolean revertChanges = Boolean.TRUE;

    private Map<String, String> lastParametersSaved = new HashMap<String, String>();
    private List<String> availableWorkbenchScreensIds = new ArrayList<String>( );

    interface Binder
            extends
            UiBinder<Widget, EditScreen> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public EditScreen( ModalConfigurationContext configContext ) {
        getScreensId();
        this.configContext = configContext;
        setTitle( CommonConstants.INSTANCE.EditComponent() );
        setBody( uiBinder.createAndBindUi( EditScreen.this ) );
        propertyEditor.handle( generateEvent( generateScreenSettingsCategory() ) );
        saveOriginalState();
        add( new ModalFooterOKCancelButtons(
                        new Command() {
                            @Override
                            public void execute() {
                                okButton();
                            }
                        },
                        new Command() {
                            @Override
                            public void execute() {
                                cancelButton();
                            }
                        }
                )
        );
        addHiddlenHandler();

    }

    private void getScreensId() {
        final ActivityBeansInfo activityBeansInfo = getActivityBeansInfo();
        availableWorkbenchScreensIds = activityBeansInfo.getAvailableWorkbenchScreensIds();
    }


    private void saveOriginalState() {
        lastParametersSaved = new HashMap<String, String>();
        Map<String, String> layoutComponentProperties = configContext.getComponentProperties();
        for ( String key : layoutComponentProperties.keySet() ) {
            lastParametersSaved.put( key, layoutComponentProperties.get( key ) );
        }
    }

    private void addHiddlenHandler() {
        addHiddenHandler( new ModalHiddenHandler() {
            @Override
            public void onHidden( ModalHiddenEvent hiddenEvent ) {
                if ( userPressCloseOrCancel() ) {
                    revertChanges();
                }
            }
        } );
    }

    private void revertChanges() {
        configContext.resetComponentProperties();
        for ( String key : lastParametersSaved.keySet() ) {
            configContext.setComponentProperty( key, lastParametersSaved.get( key ) );
        }
    }

    private boolean userPressCloseOrCancel() {
        return revertChanges;
    }

    @Override
    public void show() {
        super.show();
    }

    void okButton() {

        revertChanges = Boolean.FALSE;

        // Make sure a default screen is set before finish
        if ( configContext.getComponentProperty( PLACE_NAME_PARAMETER ) == null ) {
            if ( !availableWorkbenchScreensIds.isEmpty() ) {
                configContext.setComponentProperty( PLACE_NAME_PARAMETER, availableWorkbenchScreensIds.get( 0 ) );
                configContext.configurationFinished();
            } else {
                // If no screens are available then cancel
                configContext.configurationCancelled();
            }
        } else {
            configContext.configurationFinished();
        }
        super.hide();
    }

    void cancelButton() {
        super.hide();
        configContext.configurationCancelled();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @UiHandler("add")
    void add( final ClickEvent event ) {
        final PropertyEditorCategory property = addProperty();
        if ( property == null ) {
            return;
        }
        propertyEditor.handle( generateEvent( property ) );
        key.setText( "" );
        value.setText( "" );
    }

    private PropertyEditorCategory addProperty() {
        paramKeyInline.setText( "" );
        paramKeyControlGroup.setValidationState( ValidationState.NONE );

        //Check the Key is valid
        final NameValidator validator = NameValidator.parameterNameValidator();
        if ( !validator.isValid( key.getText() ) ) {
            paramKeyControlGroup.setValidationState( ValidationState.ERROR );
            paramKeyInline.setText( validator.getValidationError() );
            return null;
        }

        //Check the Key is unique
        Map<String, String> properties = configContext.getComponentProperties();
        for ( String parameterKey : properties.keySet() ) {
            if ( key.getText().equals( parameterKey ) ) {
                paramKeyControlGroup.setValidationState( ValidationState.ERROR );
                paramKeyInline.setText( CommonConstants.INSTANCE.DuplicateParameterName() );
                return null;
            }
        }

        configContext.setComponentProperty( key.getText(), value.getText() );
        return generateScreenSettingsCategory();
    }

    private PropertyEditorCategory generateScreenSettingsCategory() {

        //Override getFields() so we can remove Parameter from ScreenEditor when collection is modified by PropertiesWidget
        PropertyEditorCategory category = new PropertyEditorCategory( "Screen Configuration" ) {

            @Override
            public List<PropertyEditorFieldInfo> getFields() {
                return new ArrayList<PropertyEditorFieldInfo>( super.getFields() ) {

                    @Override
                    public boolean remove( Object o ) {
                        if ( o instanceof PropertyEditorFieldInfo ) {
                            final PropertyEditorFieldInfo info = (PropertyEditorFieldInfo) o;
                            configContext.removeComponentProperty( info.getLabel() );
                        }
                        return super.remove( o );
                    }
                };
            }
        };

        // Add the screen selector property
        final Map<String, String> parameters = configContext.getComponentProperties();
        String selectedScreenId = parameters.get( PLACE_NAME_PARAMETER );


        category.withField( new PropertyEditorFieldInfo( PLACE_NAME_PARAMETER,
                                                         selectedScreenId == null ? "" : selectedScreenId, PropertyEditorType.COMBO )
                                    .withComboValues( availableWorkbenchScreensIds )
                                    .withKey( configContext.hashCode() + PLACE_NAME_PARAMETER ) );

        // Add the rest of the screen's properties
        for ( final String key : parameters.keySet() ) {
            if ( !PLACE_NAME_PARAMETER.equals( key ) ) {
                category.withField( new PropertyEditorFieldInfo( key, parameters.get( key ), PropertyEditorType.TEXT )
                                            .withKey( configContext.hashCode() + key )
                                            .withRemovalSupported( true ) );
            }
        }

        // Ensure the screen category is always expanded after init
        propertyEditor.setLastOpenAccordionGroupTitle( category.getName() );
        return category;
    }

    private ActivityBeansInfo getActivityBeansInfo() {
        final SyncBeanDef<ActivityBeansInfo> activityBeansInfoIOCBeanDef = IOC.getBeanManager().lookupBean( ActivityBeansInfo.class );
        return activityBeansInfoIOCBeanDef.getInstance();
    }

    private PropertyEditorEvent generateEvent( PropertyEditorCategory category ) {
        PropertyEditorEvent event = new PropertyEditorEvent( PROPERTY_EDITOR_KEY, category );
        return event;
    }
}