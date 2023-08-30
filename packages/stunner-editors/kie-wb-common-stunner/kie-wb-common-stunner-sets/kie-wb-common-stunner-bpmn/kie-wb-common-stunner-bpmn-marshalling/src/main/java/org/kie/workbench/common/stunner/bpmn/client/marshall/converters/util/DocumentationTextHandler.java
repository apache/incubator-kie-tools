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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util;

import org.eclipse.bpmn2.Documentation;
import org.eclipse.emf.ecore.util.FeatureMap;

public class DocumentationTextHandler extends AbstractConverterHandler {

    private final Documentation documentation;

    public static DocumentationTextHandler of(Documentation documentation) {
        return new DocumentationTextHandler(documentation);
    }

    private DocumentationTextHandler(Documentation documentation) {
        this.documentation = documentation;
    }

    public String getText() {
        return get_3_6();
    }

    public void setText(String newText) {
        set(newText);
    }

    protected FeatureMap getMixed() {
        return documentation.getMixed();
    }
}
