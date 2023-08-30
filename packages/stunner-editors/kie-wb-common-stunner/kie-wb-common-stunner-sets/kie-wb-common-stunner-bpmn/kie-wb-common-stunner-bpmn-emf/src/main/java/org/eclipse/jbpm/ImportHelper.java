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


/**
 * Provides static helper methods for dealing with imports.
 *
 */
package org.eclipse.jbpm;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Import;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class ImportHelper {

    /**
     * Searches the resource's {@linkplain Resource#getContents() contents} for the root Definitions object.
     * @param resource The resource to search.
     * @return The root Definitions object contained in the resource. Either the first
     * object of type Definitions in the {@linkplain Resource#getContents() contents} of the resource or
     * the value of the {@linkplain DocumentRoot#getDefinitions() Definitions reference} of the first
     * DocumentRoot object. <code>null</code>, if no Definitions is found.
     */
    public static Definitions getDefinitions(Resource resource) {
        for (EObject eobj : resource.getContents()) {
            if (eobj instanceof Definitions) {
                return (Definitions) eobj;
            } else if (eobj instanceof DocumentRoot) {
                return ((DocumentRoot) eobj).getDefinitions();
            }
        }
        return null;
    }

    /**
     * Looks up the list of import elements in the given Definitions object for an import of the given namespace.
     * @param definitions The Definitions object to search for an import element.
     * @param namespace The namespace to look for in {@link Import#getNamespace()}.
     * @return The first import element in {@link Definitions#getImports()} with {@link Import#getNamespace()}
     * equal to the given namespace.
     */
    public static Import findImportForNamespace(Definitions definitions, String namespace) {
        for (Import imp : definitions.getImports()) {
            if (namespace.equals(imp.getNamespace())) {
                // TODO: Also check that imp.getType() is BPMN
                return imp;
            }
        }
        return null;
    }

    /**
     * Looks up the list of import elements in the given Definitions object for an import of the given location.
     * <p>
     * The location values of the import elements in the Definitions parameter are resolved against the
     * absolute URI of the resource that contains the Definitions object. The result is compared against
     * the absolute form of location.
     * @param referencingModel The Definitions object to search for an import element.
     * @param location The location to look for in {@link Import#getLocation()}.
     * @return The import element with a matching location value, or <code>null</code>, if none is found.
     */
    public static Import findImportForLocation(Definitions referencingModel, URI location) {
        URI referencingURI = referencingModel.eResource().getURI();
        URI referencedURI = location;
        for (Import imp : referencingModel.getImports()) {
            if (imp.getLocation() != null) {
                URI importUri = URI.createURI(imp.getLocation()).resolve(referencingURI);
                if (importUri.equals(referencedURI)) {
                    // TODO: Also check that imp.getType() is BPMN
                    return imp;
                }
            }
        }
        return null;
    }
}
