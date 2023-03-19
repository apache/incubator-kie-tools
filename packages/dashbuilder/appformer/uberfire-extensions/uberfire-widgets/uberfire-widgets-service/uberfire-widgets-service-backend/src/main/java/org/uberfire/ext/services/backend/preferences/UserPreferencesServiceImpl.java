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
package org.uberfire.ext.services.backend.preferences;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.services.shared.preferences.UserPreference;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;

// TODO: Remove when cleaning user preference
@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

    @Override
    public void saveUserPreferences(UserPreference preferences) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public UserPreference loadUserPreferences(String key, UserPreferencesType type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserPreference loadUserPreferences(UserPreference preferences) {
        // TODO Auto-generated method stub
        return null;
    }
}
