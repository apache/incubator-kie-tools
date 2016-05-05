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

import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.server.UserServicesBackendImpl;
import org.uberfire.ext.services.shared.preferences.UserPreference;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@Service
public class UserPreferencesServiceImpl implements UserPreferencesService {

    @Inject
    private UserServicesBackendImpl userServicesBackend;

    @Inject
    private User identity;

    @Inject
    @Named("configIO")
    private IOService ioServiceConfig;

    private XStream xs = new XStream();

    @Override
    public void saveUserPreferences( final UserPreference preferences ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier(),
                                                                    preferences.getType().getExt(),
                                                                    preferences.getPreferenceKey() );
        saveUserPreferences( preferences,
                             preferencesPath );
    }

    @Override
    public UserPreference loadUserPreferences( final String key,
                                               final UserPreferencesType type ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier(),
                                                                    type.getExt(),
                                                                    key );
        return loadUserPreferences( preferencesPath );
    }

    private void saveUserPreferences( final UserPreference preferences,
                                      final Path path ) {
        try {
            ioServiceConfig.startBatch( path.getFileSystem() );
            ioServiceConfig.write( path, xs.toXML( preferences ) );

        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        } finally {
            ioServiceConfig.endBatch();
        }
    }

    private UserPreference loadUserPreferences( final Path path ) {
        try {
            if ( ioServiceConfig.exists( path ) ) {
                final String xml = ioServiceConfig.readAllString( path );
                return (UserPreference) xs.fromXML( xml );
            }

        } catch ( final Exception e ) {
            throw new RuntimeException( e );
        }
        return null;
    }

    @Override
    public UserPreference loadUserPreferences( final UserPreference preferences ) {
        final Path preferencesPath = userServicesBackend.buildPath( identity.getIdentifier(),
                                                                    preferences.getType().getExt(),
                                                                    preferences.getPreferenceKey() );
        return loadUserPreferences( preferencesPath );
    }

}
