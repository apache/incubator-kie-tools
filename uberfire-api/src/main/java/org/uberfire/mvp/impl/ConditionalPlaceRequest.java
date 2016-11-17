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
package org.uberfire.mvp.impl;

import org.uberfire.mvp.PlaceRequest;

import java.util.Map;
import java.util.function.Predicate;

public class ConditionalPlaceRequest extends DefaultPlaceRequest {


    private PlaceRequest orElsePlaceRequest;
    private Predicate<PlaceRequest> predicate;


    public ConditionalPlaceRequest() {
        super();
    }

    /**
     * A predicate applied to determine if this place request
     * should be used or the alternative specified by #orElse
     *
     * @param identifier The place ID, or an empty string for the default place.
     */
    public ConditionalPlaceRequest( final String identifier ) {
        super( identifier );
    }

    /**
     * Creates a conditional place request for the given place ID with the given
     * state parameters for that place.
     *
     * @param identifier The place ID, or an empty string for the default place.
     * @param parameters Place-specific parameters to pass to the place. Must not be null.
     */
    public ConditionalPlaceRequest( final String identifier,
                                    final Map<String, String> parameters ) {
        super( identifier, parameters );
    }

    /**
     * Creates a place request for the given place ID with the given
     * state parameters for that place.
     *
     * @param predicate Represents if default place request should be used.
     *                  If false, the place request will be orElsePlaceRequest.
     */
    public ConditionalPlaceRequest when( Predicate<PlaceRequest> predicate ) {
        this.predicate = predicate;
        return this;
    }

    /**
     * Creates a place request for the given place ID with the given
     * state parameters for that place.
     *
     * @param orElsePlaceRequest alternative place request.
     */
    public PlaceRequest orElse( PlaceRequest orElsePlaceRequest ) {
        this.orElsePlaceRequest = orElsePlaceRequest;
        return this;
    }

    /**
     * Return the place request of this conditional PlaceRequest.
     * If there is no predicate or orElsePlaceRequest returns the
     * default place request.
     */
    public PlaceRequest resolveConditionalPlaceRequest() {
        if ( invalidPlaceRequest() ) {
            return this;
        }
        if ( predicate.test( this ) ) {
            return this;
        } else {
            return resolve();
        }
    }

    private boolean invalidPlaceRequest() {
        return predicate == null || orElsePlaceRequest == null;
    }

    private PlaceRequest resolve() {
        if ( orElsePlaceRequest instanceof ConditionalPlaceRequest ) {
            return ( ( ConditionalPlaceRequest ) orElsePlaceRequest ).resolveConditionalPlaceRequest();
        } else {
            return orElsePlaceRequest;
        }
    }

    @Override
    public String toString() {
        return "ConditionalPlaceRequest " + super.toString();
    }

}
