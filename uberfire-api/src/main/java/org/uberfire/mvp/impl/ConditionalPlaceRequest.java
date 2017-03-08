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

import java.util.Map;
import java.util.function.Predicate;

import org.uberfire.mvp.PlaceRequest;

public class ConditionalPlaceRequest extends DefaultPlaceRequest {

    private PlaceRequest orElsePlaceRequest;
    private Predicate<PlaceRequest> predicate;

    public ConditionalPlaceRequest() {
        super();
    }

    /**
     * A predicate applied to determine if this place request
     * should be used or the alternative specified by #orElse
     * @param identifier The place ID, or an empty string for the default place.
     */
    public ConditionalPlaceRequest(final String identifier) {
        super(identifier);
    }

    /**
     * Creates a conditional place request for the given place ID with the given
     * state parameters for that place.
     * @param identifier The place ID, or an empty string for the default place.
     * @param parameters Place-specific parameters to pass to the place. Must not be null.
     */
    public ConditionalPlaceRequest(final String identifier,
                                   final Map<String, String> parameters) {
        super(identifier,
              parameters);
    }

    /**
     * Creates a place request for the given place ID with the given
     * state parameters for that place.
     * @param predicate Represents if default place request should be used.
     * If false, the place request will be orElsePlaceRequest.
     */
    public ConditionalPlaceRequest when(Predicate<PlaceRequest> predicate) {
        this.predicate = predicate;
        return this;
    }

    /**
     * Creates a place request for the given place ID with the given
     * state parameters for that place.
     * @param orElsePlaceRequest alternative place request.
     */
    public PlaceRequest orElse(PlaceRequest orElsePlaceRequest) {
        this.orElsePlaceRequest = orElsePlaceRequest;
        return this;
    }

    /**
     * Return the place request of this conditional PlaceRequest.
     * If there is no predicate or orElsePlaceRequest returns the
     * default place request.
     */
    public PlaceRequest resolveConditionalPlaceRequest() {
        if (invalidConditionalPlaceRequest()) {
            return this;
        }
        if (predicate == null || predicate.test(this)) {
            return this;
        } else {
            return resolve();
        }
    }

    private boolean invalidConditionalPlaceRequest() {
        return predicate == null || orElsePlaceRequest == null;
    }

    protected PlaceRequest resolve() {
        if (orElsePlaceRequest instanceof ConditionalPlaceRequest) {
            return ((ConditionalPlaceRequest) orElsePlaceRequest).resolveConditionalPlaceRequest();
        } else {
            return orElsePlaceRequest;
        }
    }

    @Override
    public String getIdentifier() {
        if (invalidConditionalPlaceRequest()) {
            return identifier;
        }

        if (predicate == null || predicate.test(this)) {
            return identifier;
        } else {
            return resolve().getIdentifier();
        }
    }

    @Override
    public Map<String, String> getParameters() {
        if (invalidConditionalPlaceRequest()) {
            return parameters;
        }

        if (predicate == null || predicate.test(this)) {
            return parameters;
        } else {
            return resolve().getParameters();
        }
    }

    @Override
    public PlaceRequest clone() {
        return new ConditionalPlaceRequest(identifier,
                                           parameters).when(predicate).orElse(orElsePlaceRequest);
    }

    /**
     * A conditional place request should be resolved before being compared to another default place request.
     * @param o A default or conditional place request.
     * @return True if the resolved conditional place request equals to the default place request or resolved
     * conditional place request passed, and false otherwise.
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof DefaultPlaceRequest)) {
            return false;
        }

        if (this == o) {
            return true;
        }

        PlaceRequest that = (PlaceRequest) o;
        if (o instanceof ConditionalPlaceRequest) {
            that = ((ConditionalPlaceRequest) o).resolveConditionalPlaceRequest();
        }

        if (invalidConditionalPlaceRequest()) {
            return super.equals(that);
        }

        if (predicate == null || predicate.test(this)) {
            return super.equals(that);
        } else {
            return resolve().equals(that);
        }
    }

    @Override
    public int hashCode() {
        if (invalidConditionalPlaceRequest()) {
            return super.hashCode();
        }

        if (predicate == null || predicate.test(this)) {
            return super.hashCode();
        } else {
            return resolve().hashCode();
        }
    }

    @Override
    public String toString() {
        if (predicate == null || predicate.test(this)) {
            return super.toString();
        } else {
            return resolve().toString();
        }
    }
}
