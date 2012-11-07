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

public interface PlaceRequest {

    public String getIdentifier();

    public String getFullIdentifier();

    //TODO: add other getParameter methods if necessary, eg, getParameterDate, getParameterLong etc
    public String getParameterString(String key,
                                     String defaultValue);
    
    public Object getParameter(String key,
                               Object defaultValue);
    
    public Set<String> getParameterNames();

    public Map<String, Object> getParameters();

    public PlaceRequest addParameter(String name,
                                     String value);
    
    public PlaceRequest addParameter(String name,
                                     Object value);
    
    public PlaceRequest getPlace();

}
