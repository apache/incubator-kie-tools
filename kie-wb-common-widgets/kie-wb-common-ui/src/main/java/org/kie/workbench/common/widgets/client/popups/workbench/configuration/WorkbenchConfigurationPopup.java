/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.widgets.client.popups.workbench.configuration;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.workbench.configuration.ContextualView;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserWorkbenchPreferences;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ForcedPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.widgets.client.workbench.configuration.ContextualView.*;

@Dependent
public class WorkbenchConfigurationPopup extends BaseModal {

    interface WorkbenchConfigurationPopupImplBinder extends UiBinder<Widget, WorkbenchConfigurationPopup> {

    }

    private static WorkbenchConfigurationPopupImplBinder uiBinder = GWT.create( WorkbenchConfigurationPopupImplBinder.class );

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    @Inject
    protected Event<NotificationEvent> notification;

    @UiField
    PropertyEditorComboBox languageListItems;

    @UiField
    PropertyEditorComboBox multipleModeItems;

    @UiField
    PropertyEditorItemLabel languageListItemsLabel;

    @UiField
    PropertyEditorItemLabel multipleModeItemsLabel;

    @Inject
    private PerspectiveManager perspectiveManager;

    @Inject
    private ContextualView contextualView;

    @Inject
    protected PlaceManager placeManager;

    private CommonConstants constants = GWT.create( CommonConstants.class );
    private Map<String, String> languageMap = new HashMap<String, String>();
    private Map<String, String> viewModeMap = new HashMap<String, String>();

    public WorkbenchConfigurationPopup() {
        setTitle( constants.Workbench_Settings() );
        add( new ModalBody(){{
            add( uiBinder.createAndBindUi( WorkbenchConfigurationPopup.this ) );
        }} );
        add( new ModalFooterOKCancelButtons( new Command() {

            @Override
            public void execute() {
                onOk();
                hide();
            }
        }, new Command() {

            @Override
            public void execute() {
                hide();
            }
        }
        ) );

        languageMap.put( "default",
                         constants.English() );
        languageMap.put( "zh_CN",
                         constants.Chinese() );
        languageMap.put( "de",
                         constants.German() );
        languageMap.put( "es",
                         constants.Spanish() );
        languageMap.put( "fr",
                         constants.French() );
        languageMap.put( "ja",
                         constants.Japanese() );
        languageMap.put( "pt_BR",
                         constants.Portuguese() );

        viewModeMap.put( ADVANCED_MODE,
                         constants.Advanced() );
        viewModeMap.put( BASIC_MODE,
                         constants.Basic() );
    }

    @AfterInitialization
    public void setup() {
        languageListItemsLabel.setText( constants.Language() );
        multipleModeItemsLabel.setText( constants.View_Mode() );
        setLanguageListItems();
        setMultipleModeItems( multipleModeItems );
        preferencesService.call( new RemoteCallback<UserWorkbenchPreferences>() {

            @Override
            public void callback( final UserWorkbenchPreferences response ) {
                if ( response != null ) {
                    languageListItems.setSelectItemByText( languageMap.get( response.getLanguage() ) );
                    multipleModeItems.setSelectItemByText( viewModeMap.get( response.getViewMode( ContextualView.ALL_PERSPECTIVES ) ) );
                    for ( Map.Entry<String, String> entry : response.getPerspectiveViewMode().entrySet() ) {
                        contextualView.setViewMode( entry.getKey(), entry.getValue() );
                    }
                    refresh( response.getLanguage(), response.getViewMode( ContextualView.ALL_PERSPECTIVES ), response );
                }
            }
        }, new DefaultErrorCallback() ).loadUserPreferences( new UserWorkbenchPreferences( "default" ) );
    }

    public void onOk() {
        final Pair<String, String> selectedLanguage = languageListItems.getSelectedPair( languageListItems.getSelectedIndex() );
        final Pair<String, String> selectedViewMode = multipleModeItems.getSelectedPair( multipleModeItems.getSelectedIndex() );
        refresh( selectedLanguage.getK2(),
                 selectedViewMode.getK2(),
                 null );
        saveUserWorkbenchPreferences( selectedLanguage.getK2(),
                                      selectedViewMode.getK2() );
    }

    private void saveUserWorkbenchPreferences( final String language,
                                               final String viewMode ) {
        UserWorkbenchPreferences preferences = new UserWorkbenchPreferences( language );
        preferences.setViewMode( ContextualView.ALL_PERSPECTIVES,
                                 viewMode );
        preferencesService.call( new RemoteCallback<Void>() {

            @Override
            public void callback( Void response ) {

            }
        } ).saveUserPreferences( preferences );
    }

    private void refresh( final String selectedLanguageItem,
                          final String selectedMultipleMode,
                          final UserWorkbenchPreferences response ) {
        boolean refreshPerspectiveFlag = true;
        boolean refreshWorkbenchFlag = true;
        if ( selectedLanguageItem.equals( LocaleInfo.getCurrentLocale().getLocaleName() ) ) {
            refreshWorkbenchFlag = false;
        }
        if ( selectedMultipleMode.equals( contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES ) ) ) {
            refreshPerspectiveFlag = false;
        }
        switchMode( refreshPerspectiveFlag,
                    refreshWorkbenchFlag );
        refreshWorkbench( selectedLanguageItem,
                          refreshWorkbenchFlag,
                          response );
    }

    private void setLanguageListItems() {
        languageListItems.clear();
        String[] languages = LocaleInfo.getAvailableLocaleNames();
        for ( String language : languages ) {
            languageListItems.addItem( Pair.newPair( languageMap.get( language ),
                                                     language ) );
        }
    }

    private void setCurrentLanguage( final String languageName,
                                     final UserWorkbenchPreferences response ) {
        String isRefresh = Window.Location.getParameter( "isRefresh" );
        if ( response != null && ( isRefresh == null || isRefresh.equals( "" ) ) ) {
            Window.Location.assign( Window.Location.createUrlBuilder()
                                            .removeParameter( LocaleInfo.getLocaleQueryParam() )
                                            .setParameter( LocaleInfo.getCurrentLocale().getLocaleQueryParam(),
                                                           languageName )
                                            .setParameter( "isRefresh",
                                                           "false" )
                                            .buildString() );
        } else if ( response == null ) {
            Window.Location.assign( Window.Location.createUrlBuilder()
                                            .removeParameter( LocaleInfo.getLocaleQueryParam() )
                                            .setParameter( LocaleInfo.getCurrentLocale().getLocaleQueryParam(),
                                                           languageName )
                                            .buildString() );
        }
    }

    private void refreshWorkbench( final String selectedLanguageItem,
                                   final boolean refreshWorkbenchFlag,
                                   final UserWorkbenchPreferences response ) {
        if ( refreshWorkbenchFlag == false ) {
            return;
        }
        if ( selectedLanguageItem == null || selectedLanguageItem.equals( "" ) ) {
            showFieldEmptyWarning();
        } else {
            setCurrentLanguage( selectedLanguageItem,
                                response );
        }
    }

    private void setMultipleModeItems( final PropertyEditorComboBox multipleModeItems ) {
        multipleModeItems.clear();
        multipleModeItems.addItem( Pair.newPair( viewModeMap.get( BASIC_MODE ),
                                                 BASIC_MODE ) );
        multipleModeItems.addItem( Pair.newPair( viewModeMap.get( ADVANCED_MODE ),
                                                 ADVANCED_MODE ) );
    }

    private void switchMode( final boolean refreshPerspectiveFlag,
                             final boolean refreshWorkbenchFlag ) {
        String isRefresh = Window.Location.getParameter( "isRefresh" );
        if ( refreshPerspectiveFlag && ( !refreshWorkbenchFlag || ( refreshWorkbenchFlag && isRefresh.equals( "false" ) ) ) ) {
            String modeName = contextualView.getViewMode( ContextualView.ALL_PERSPECTIVES );
            if ( modeName.equals( BASIC_MODE ) ) {
                contextualView.setViewMode( ContextualView.ALL_PERSPECTIVES,
                                            ADVANCED_MODE );
            } else {
                contextualView.setViewMode( ContextualView.ALL_PERSPECTIVES,
                                            BASIC_MODE );
            }
            refreshPerspective();
        }
    }

    private void refreshPerspective() {
        final PerspectiveActivity currentPerspective = perspectiveManager.getCurrentPerspective();
        perspectiveManager.removePerspectiveStates( new org.uberfire.mvp.Command() {

            @Override
            public void execute() {
                if ( currentPerspective != null ) {
                    final PlaceRequest pr = new ForcedPlaceRequest( currentPerspective.getIdentifier(),
                                                                    currentPerspective.getPlace().getParameters() );
                    placeManager.goTo( pr );
                }
            }
        } );
    }

    private void showFieldEmptyWarning() {
        ErrorPopup.showMessage( constants.PleaseSetAName() );
    }
}
