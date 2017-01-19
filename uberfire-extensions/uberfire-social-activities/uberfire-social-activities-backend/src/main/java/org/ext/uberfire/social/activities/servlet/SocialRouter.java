/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.servlet;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialAdapterRepositoryAPI;

@ApplicationScoped
public class SocialRouter {

    @Inject
    SocialAdapterRepositoryAPI socialAdapterRepositoryAPI;

    private Map<Class, SocialAdapter> socialAdapters;

    @PostConstruct
    public void setup() {
        socialAdapters = socialAdapterRepositoryAPI.getSocialAdapters();
    }

    public SocialRouter() {

    }

    String extractPath( String path ) {
        if ( path.length() > 0 && isSlashFirstChar( path ) ) {
            String newPath = path.substring( 1 );
            return newPath;
        }
        throw new NotASocialPathException("Path " + path + " is not valid in this context.");
    }

    private boolean isSlashFirstChar( String path ) {
        return path.substring( 0, 1 ).equalsIgnoreCase( "/" );
    }

    public SocialAdapter getSocialAdapterByPath( String path ) throws SocialAdapterNotFound {
        String newPath = extractPath( path );
        return getSocialAdapter( newPath );
    }

    public SocialAdapter getSocialAdapter( String adapterName ) {
        for ( Map.Entry<Class, SocialAdapter> entry : getSocialAdapters().entrySet() ) {
            SocialAdapter socialAdapter = entry.getValue();
            if ( thereIsASocialAdapter( socialAdapter ) && nameIsThisSocialAdapter( adapterName, socialAdapter ) ) {
                return socialAdapter;
            }
        }
        throw new SocialAdapterNotFound();
    }

    private boolean nameIsThisSocialAdapter( String newPath,
                                             SocialAdapter socialAdapter ) {
        return socialAdapter.socialEventType().toString().equalsIgnoreCase( newPath );
    }

    private boolean thereIsASocialAdapter( SocialAdapter socialAdapter ) {
        return socialAdapter != null && socialAdapter.socialEventType() != null;
    }

    public Map<Class, SocialAdapter> getSocialAdapters() {
        return socialAdapters;
    }

    public class SocialAdapterNotFound extends RuntimeException {

    }

    private class NotASocialPathException extends RuntimeException {

        public NotASocialPathException() {
        }

        public NotASocialPathException(String message) {
            super(message);
        }
    }
}
