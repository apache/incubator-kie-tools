/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.workbench;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.Cookies;

/**
 * Created by Cristiano Nicolai.
 */
@ApplicationScoped
public class UserPreferencesImpl implements UserPreferences {

    public static final String USER_COOKIE_WORKBENCH_MODE = "uberfire.user.workbench.mode";

    private final Map<String, String> preferences = new HashMap<String, String>();

    @PostConstruct
    protected void setup() {
        if ( Cookies.isCookieEnabled() ) {
            preferences.put( USER_COOKIE_WORKBENCH_MODE, Cookies.getCookie( USER_COOKIE_WORKBENCH_MODE ) );
        }
    }

    @Override
    public void setUseWorkbenchInStandardMode( boolean workbenchInStandardMode ) {
        if ( Cookies.isCookieEnabled() ) {
            Cookies.setCookie( USER_COOKIE_WORKBENCH_MODE, String.valueOf( workbenchInStandardMode ) );
        }
        preferences.put( USER_COOKIE_WORKBENCH_MODE, String.valueOf( workbenchInStandardMode ) );
    }

    @Override
    public boolean isUseWorkbenchInStandardMode() {
        final String mode = preferences.get( USER_COOKIE_WORKBENCH_MODE );
        if ( mode == null ) {
            return true;
        } else {
            return Boolean.valueOf( mode );
        }
    }
}
