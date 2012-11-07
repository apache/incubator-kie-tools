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

package org.uberfire.shared.mvp.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.shared.mvp.PlaceRequest;

@Portable
public class PassThroughPlaceRequest extends DefaultPlaceRequest {

    private final Map<String, Object> passThroughParameters = new HashMap<String, Object>();

    public PassThroughPlaceRequest() {
        super();
    }

    public PassThroughPlaceRequest(final String identifier) {
        super( identifier );
    }

   	//TODO: Throw ValueFormatException if conversion to a String is not possible
    public String getPassThroughParameterString(final String key,
                                                final String defaultValue) {
    	Object value = null;

        if ( passThroughParameters != null ) {
            value = passThroughParameters.get( key );
        }

        if ( value == null ) {
            value = defaultValue;
        }
        return (String)value;
    }

	public Object getPassThroughParameter(final String key,
			final Object defaultValue) {
		Object value = null;

		if (passThroughParameters != null) {
			value = passThroughParameters.get(key);
		}

		if (value == null) {
			value = defaultValue;
		}
		return value;
	} 
    
    public Set<String> getPassThroughParameterNames() {
        return passThroughParameters.keySet();
    }

    public Map<String, Object> getPassThroughParameters() {
        return passThroughParameters;
    }

    public PlaceRequest addPassThroughParameter(final String name,
                                                final Object value) {
        this.passThroughParameters.put( name,
                                        value );
        return this;
    }

}
