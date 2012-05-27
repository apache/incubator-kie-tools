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

package org.drools.guvnor.client.mvp;

import com.google.gwt.place.shared.Place;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class PlaceRequest extends Place {

    private final String nameToken;

    private final Map<String, String> parameters = new HashMap<String, String>();


    public PlaceRequest(String nameToken) {
        this.nameToken = nameToken;
    }


    public String getNameToken() {
        return nameToken;
    }

    public String getFullToken() {
        StringBuilder token = new StringBuilder();
        token.append(this.getNameToken());

        if (this.getParameterNames().size() > 0) {
            token.append("?");
        }
        for (String name : this.getParameterNames()) {
            token.append(name).append("=")
                    .append(this.getParameter(name, null));
            token.append("&");
        }
        
        if(token.length() != 0 && token.lastIndexOf("&")+1 == token.length()) {
            token.deleteCharAt(token.length());           
        }

        return token.toString();
    }
    
    public String getParameter(String key, String defaultValue) {
        String value = null;

        if (parameters != null) {
            value = parameters.get(key);
        }

        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
    
    public Set<String> getParameterNames() {
        return parameters.keySet();
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public PlaceRequest parameter(String name, String value) {
        this.parameters.put(name, value);
        return this;
    }

    public PlaceRequest getPlace() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaceRequest placeRequest = (PlaceRequest) o;
        return getFullToken().equals(placeRequest.getFullToken());
    }

    @Override
    public int hashCode() {
        return getFullToken() != null ? getFullToken().hashCode() : 0;
    }

}
