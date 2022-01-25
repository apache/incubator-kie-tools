/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.reflections.scanners;

import org.jboss.errai.reflections.util.FilterBuilder;

import java.util.List;

/** scans for superclass and interfaces of a class, allowing a reverse lookup for subtypes */
public class SubTypesScanner extends AbstractScanner {

    {
        filterResultsBy(new FilterBuilder().exclude(Object.class.getName())); //exclude direct Object subtypes by default
    }

    @SuppressWarnings({"unchecked"})
    public void scan(final Object cls) {
		String className = getMetadataAdapter().getClassName(cls);
		String superclass = getMetadataAdapter().getSuperclassName(cls);

        if (acceptResult(superclass)) {
            getStore().put(superclass, className);
        }

		for (String anInterface : (List<String>) getMetadataAdapter().getInterfacesNames(cls)) {
			if (acceptResult(anInterface)) {
                getStore().put(anInterface, className);
            }
        }
    }
}
