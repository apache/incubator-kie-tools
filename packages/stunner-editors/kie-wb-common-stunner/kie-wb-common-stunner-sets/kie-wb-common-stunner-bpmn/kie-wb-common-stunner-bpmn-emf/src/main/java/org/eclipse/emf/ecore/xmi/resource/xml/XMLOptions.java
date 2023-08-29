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

import java.util.Map;

import org.eclipse.emf.common.util.URI;

public class XMLOptions {

    // protected EcoreBuilder ecoreBuilder;

    protected Map<String, URI> externalSchemaLocation;

    protected boolean anyXML;

    protected boolean processSchemaLocations;

    /*public EcoreBuilder getEcoreBuilder()
    {
        return ecoreBuilder;
    }*/

    public Map<String, URI> getExternalSchemaLocations() {
        return externalSchemaLocation;
    }

    public boolean isProcessAnyXML() {
        return anyXML;
    }

    public boolean isProcessSchemaLocations() {
        return processSchemaLocations;
    }

    /*public void setEcoreBuilder(EcoreBuilder ecoreBuilder)
    {
        this.ecoreBuilder = ecoreBuilder;
    }*/

    public void setExternalSchemaLocations(Map<String, URI> schemaLocations) {
        this.externalSchemaLocation = schemaLocations;
    }

    public void setProcessAnyXML(boolean anyXML) {
        this.anyXML = anyXML;
    }

    public void setProcessSchemaLocations(boolean processSchemaLocations) {
        this.processSchemaLocations = processSchemaLocations;
    }

    @Override
    public int hashCode() {
        int hashCode = externalSchemaLocation != null ? externalSchemaLocation.hashCode() : 0;
        /*hashCode ^= (ecoreBuilder != null) ? ecoreBuilder.hashCode() : 0;*/
        return hashCode + (anyXML ? 1 : 0) + (processSchemaLocations ? 2 : 0);
    }
}
