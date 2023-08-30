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
import org.eclipse.emf.ecore.EStructuralFeature;

public class IllegalValueException extends XMIException {

    private static final long serialVersionUID = 1L;

    protected transient EObject object;
    protected transient EStructuralFeature feature;
    protected transient Object value;

    public IllegalValueException
            (EObject object, EStructuralFeature feature, Object value, Exception emfException, String location, int line, int column) {
        super("Value '" + value + "' is not legal.", emfException, location, line, column);
        this.object = object;
        this.feature = feature;
        this.value = value;
    }

    public EObject getObject() {
        return object;
    }

    public EStructuralFeature getFeature() {
        return feature;
    }

    public Object getValue() {
        return value;
    }
}
