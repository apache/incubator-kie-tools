/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.shared.enums;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface EnumDropdownService {

    /**
     * Load drop-down data. This can be either a static list or a dynamic list from a server-side helper
     * class defined in the enum definition. For dynamic enums the resource path is used to resolve a Project
     * to get the Project Classloader to resolve the helper class that can either be in the containers
     * classpath (WEB-INF/lib) or a Project dependency.
     * @param resource The resource needing the drop-down
     * @param valuePairs key=value pairs to be interpolated into the expression.
     * @param expression The expression, which will then be eval'ed to generate a String[]
     */
    public String[] loadDropDownExpression( final Path resource,
                                            final String[] valuePairs,
                                            String expression );

}
