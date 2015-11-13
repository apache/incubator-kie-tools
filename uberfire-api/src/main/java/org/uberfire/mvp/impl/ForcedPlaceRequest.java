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

package org.uberfire.mvp.impl;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ForcedPlaceRequest
        extends DefaultPlaceRequest {

    public ForcedPlaceRequest() {
        super( "" );
    }

    public ForcedPlaceRequest( final String identifier ) {
        super( identifier );
    }

    public ForcedPlaceRequest( final String identifier,
                               final Map<String, String> parameters ) {
        super( identifier,
               parameters );
    }

    /**
     * Creates a place request for the given place ID, with the given state parameters for that place, and the given
     * preference of whether or not the browser's location bar should be updated.
     * @param identifier The place ID, or an empty string for the default place.
     * @param parameters Place-specific parameters to pass to the place. Must not be null.
     * @param updateLocationBar If true, the browser's history will be updated with this place request. If false, the location bar
     * will not be modified as a result of this place request.
     */
    public ForcedPlaceRequest( final String identifier,
                               final Map<String, String> parameters,
                               final boolean updateLocationBar ) {
        super( identifier,
               parameters,
               updateLocationBar );
    }

}
