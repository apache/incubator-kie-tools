/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserWorkbenchPreferences;

/**
 * Definition of Handler to support add new menu of workbench configuration
 */
public abstract class WorkbenchConfigurationHandler {

    private List<Pair<String, ? extends Composite>> widgetList;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    private UserWorkbenchPreferences preference;

    protected abstract void initHandler();

    /**
     * A description of the new workbench configuration menu
     * @return
     */
    public abstract String getDescription();

    /**
     * An entry-point for workbench configuration
     * @param isInit, is initialization
     */
    public abstract void configurationSetting( boolean isInit );

    /**
     * Return a List of Widgets that the NewWorkbenchConfigurationHandler can use to gather additional parameters for the
     * new workbench configuration. The List is of Pairs, where each Pair consists of a String caption and IsWidget editor.
     * @return null if no extension is provided
     */
    public List<Pair<String, ? extends Composite>> getExtensions() {
        if ( widgetList == null || widgetList.size() == 0 ) {
            widgetList = new ArrayList<Pair<String, ? extends Composite>>();
        }
        return this.widgetList;
    }

    /**
     * Provide NewWorkbenchConfigurationHandler with the ability to load UserWorkbenchPreferences from git repository
     */
    public void loadUserWorkbenchPreferences() {
        preferencesService.call( new RemoteCallback<UserWorkbenchPreferences>() {

            @Override
            public void callback( final UserWorkbenchPreferences response ) {
                if ( response != null ) {
                    setPreference( response );
                    setDefaultConfigurationValues( response );
                    configurationSetting( true );
                }
            }
        } ).loadUserPreferences( new UserWorkbenchPreferences( "default" ) );

    }

    /**
     * Provide NewWorkbenchConfigurationHandler with the ability to store UserWorkbenchPreferences into GIT repository
     */
    public void saveUserWorkbenchPreferences() {
        UserWorkbenchPreferences preferences = getSelectedUserWorkbenchPreferences();
        preferencesService.call( new RemoteCallback<Void>() {

            @Override
            public void callback( Void response ) {

            }
        } ).saveUserPreferences( preferences );
    }

    /**
     * Set default configuration value 
     * @param response: user preferences from GIT repository
     */
    protected abstract void setDefaultConfigurationValues( final UserWorkbenchPreferences response );

    /** get currently user preferences from selected item widget 
     * @return
     */
    protected abstract UserWorkbenchPreferences getSelectedUserWorkbenchPreferences();

    /**
     * get specific widget by using widget name
     * @param name : specific widget name
     * @return
     */
    public Composite getWidgetByName( final String name ) {
        for ( Pair<String, ? extends Composite> pair : widgetList ) {
            if ( pair.getK1().equals( name ) ) {
                return pair.getK2();
            }
        }
        return null;
    }

    public UserWorkbenchPreferences getPreference() {
        return preference;
    }

    public void setPreference( final UserWorkbenchPreferences response ) {
        this.preference = response;
    }
}
