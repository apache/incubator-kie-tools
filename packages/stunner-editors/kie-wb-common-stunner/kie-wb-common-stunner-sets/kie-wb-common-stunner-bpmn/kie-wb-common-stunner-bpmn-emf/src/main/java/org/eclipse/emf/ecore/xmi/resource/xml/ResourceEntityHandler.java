/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.eclipse.emf.ecore.xmi.resource.xml;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

public class ResourceEntityHandler extends URIHandler {

    protected String entityName;
    protected int count = 1;
    protected Map<String, String> nameToValueMap = new LinkedHashMap<String, String>();
    protected Map<String, String> valueToNameMap = new LinkedHashMap<String, String>();

    /**
     * Creates an instance that will generate entity names based on the given entity name
     * followed by the {@link #count} starting from 1 to ensure uniqueness.
     * @param entityName
     */
    public ResourceEntityHandler(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Returns the given URI.
     * @param uri the URI to deresolve.
     */
    @Override
    public URI deresolve(URI uri) {
        return uri;
    }

    /**
     * Returns the deresolved URI by calling super.
     * @param uri the URI to deresolve.
     */
    protected URI doDeresolve(URI uri) {
        return super.deresolve(uri);
    }

    /**
     * Sets the base URI and if the URI has changed,
     * resolves all the entity URIs values against the old base URI
     * and then deresolves them against the new baseURI.
     * @param uri the new base URI.
     */
    @Override
    public void setBaseURI(URI uri) {
        boolean unchanged = uri == null ? baseURI == null : uri.equals(baseURI);
        if (!unchanged) {
            for (Map.Entry<String, String> entry : nameToValueMap.entrySet()) {
                entry.setValue(resolve(URI.createURI(entry.getValue())).toString());
            }
        }
        doSetBaseURI(uri);
        if (!unchanged) {
            valueToNameMap.clear();
            for (Map.Entry<String, String> entry : nameToValueMap.entrySet()) {
                valueToNameMap.put(entry.getValue(), entry.getKey());
                entry.setValue(doDeresolve(URI.createURI(entry.getValue())).toString());
            }
        }
    }

    /**
     * Sets the base URI by calling super.
     * @param uri
     */
    protected void doSetBaseURI(URI uri) {
        super.setBaseURI(uri);
    }

    /**
     * Resets the handler to its initial state.
     */
    public void reset() {
        valueToNameMap.clear();
        nameToValueMap.clear();
        count = 1;
    }

    public String getEntityName(String entityValue) {
        String result = valueToNameMap.get(entityValue);
        if (result == null) {
            result = generateEntityName(entityValue);
            if (result != null) {
                valueToNameMap.put(entityValue, result);
                nameToValueMap.put(result, entityValue);
            }
        }
        return result;
    }

    /**
     * Generates a new unique entity name for the given entity value.
     * @param entityValue the value for which an entity name is needed.
     * @return a new unique entity name.
     */
    protected String generateEntityName(String entityValue) {
        for (String result = entityName + count; ; result = entityName + ++count) {
            if (!nameToValueMap.containsKey(result)) {
                return result;
            }
        }
    }

    public void handleEntity(String entityName, String entityValue) {
        nameToValueMap.put(entityName, entityValue);
        valueToNameMap.put(entityValue, entityName);
    }

    public Map<String, String> getNameToValueMap() {
        // Deresolve all the URI against the current base URI.
        //
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : nameToValueMap.entrySet()) {
            result.put(entry.getKey(), doDeresolve(URI.createURI(entry.getValue())).toString());
        }
        return result;
    }
}
