/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.shared.mvp;

import java.util.Map;
import java.util.Set;

import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

public interface PlaceRequest {

    public static final PlaceRequest NOWHERE = new DefaultPlaceRequest( "NOWHERE" );

    String getIdentifier();

    String getFullIdentifier();

    String getParameter( final String key,
                         final String defaultValue );

    Set<String> getParameterNames();

    Map<String, String> getParameters();

    PlaceRequest addParameter( final String name,
                               final String value );

    PlaceRequest getPlace();

    PlaceRequest clone();

}
