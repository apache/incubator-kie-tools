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


package org.kie.workbench.common.stunner.core.documentation.service;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.model.DiagramDocumentation;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.kie.workbench.common.stunner.core.documentation.model.HTMLDocumentationTemplate;

public interface DiagramDocumentationService<D extends Diagram, R extends DiagramDocumentation,
        T extends HTMLDocumentationTemplate, P extends DocumentationOutput> {

    /**
     * Process the diagram generating the documentation output bean.
     * @param diagram
     * @return
     */
    R processDocumentation(D diagram);

    /**
     * Returns the template to be used to build the serialized documentation.
     * @return
     */
    T getDocumentationTemplate();

    /**
     * Generates the documentation serialized output based on the given template.
     * @param template
     * @param diagramDocumentation
     * @return
     */
    DocumentationOutput buildDocumentation(T template, R diagramDocumentation);

    /**
     * This is the method that executed the full documentations process.
     * @param diagram
     * @return the processed documentation output
     */
    DocumentationOutput generate(D diagram);
}