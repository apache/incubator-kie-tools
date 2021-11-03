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

package org.uberfire.mvp;

import java.util.Map;
import java.util.Set;

import jsinterop.annotations.JsType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

/**
 * A request to navigate to a particular UberFire Workbench Place (a WorkbenchPerspective, a WorkbenchScreen, or a
 * WorkbenchEditor). Can include optional state parameters that are made available to the requested place.
 * <p>
 * Place requests can be serialized to and created from a valid URL fragment identifier (the string that goes after the
 * {@code #} in the browser's location bar).
 */
@JsType
public interface PlaceRequest {

    public static final PlaceRequest NOWHERE = new DefaultPlaceRequest("NOWHERE");

    String getIdentifier();

    void setIdentifier(String identifier);

    String getFullIdentifier();

    String getParameter(final String key,
                        final String defaultValue);

    Set<String> getParameterNames();

    Map<String, String> getParameters();

    PlaceRequest addParameter(final String name,
                              final String value);

    // TODO (UF-88) when PlaceRequest is an immutable value type, cloning will no longer be a sensible operation
    PlaceRequest clone();

    /**
     * Indicates whether or not the Workbench framework should add a browser history item when navigating to this place.
     */
    boolean isUpdateLocationBarAllowed();

    void setUpdateLocationBar(boolean updateLocationBar);
    /**
     * Returns the path associated with this {@link PlaceRequest}.
     */
    default Path getPath() {
        // TODO go over all UF public API and start using Optional
        return null;
    }

    /**
     * Invokes {@link #toString()} but exported to JavaScript so it can be invoked from different scripts.
     */
    default String asString() {
        return this.toString();
    }
}
