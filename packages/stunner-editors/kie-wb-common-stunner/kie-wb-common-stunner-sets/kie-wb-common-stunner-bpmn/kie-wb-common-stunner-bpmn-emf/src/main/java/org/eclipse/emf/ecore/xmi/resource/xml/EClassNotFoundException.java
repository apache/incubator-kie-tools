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

import org.eclipse.emf.ecore.EFactory;

public class EClassNotFoundException extends XMIException {

    private static final long serialVersionUID = 1L;

    protected String className;
    protected transient EFactory factory;

    public EClassNotFoundException(String name, EFactory factory, String location, int line, int column) {
        super("Class '" + name + "' is not found or is abstract.", location, line, column);
        className = name;
        this.factory = factory;
    }

    public String getName() {
        return className;
    }

    public EFactory getFactory() {
        return factory;
    }
}
