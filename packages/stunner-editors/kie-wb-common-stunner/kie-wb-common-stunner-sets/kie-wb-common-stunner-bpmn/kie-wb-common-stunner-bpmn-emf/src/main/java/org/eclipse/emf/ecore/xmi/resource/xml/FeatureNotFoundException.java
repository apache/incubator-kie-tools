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

import org.eclipse.emf.ecore.EObject;

public class FeatureNotFoundException extends XMIException {

    private static final long serialVersionUID = 1L;

    protected String featureName;
    protected transient EObject object;

    public FeatureNotFoundException(String name, EObject object, String location, int line, int column) {
        super("Feature '" + name + "' not found.", location, line, column);
        featureName = name;
        this.object = object;
    }

    public String getName() {
        return featureName;
    }

    public EObject getObject() {
        return object;
    }
}
