/*
 * Copyright 2015 JBoss Inc
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
package org.uberfire.ext.wires.bpmn.backend;

import org.uberfire.ext.wires.bpmn.api.model.impl.BpmnDiagram;

public class BpmnPersistence {

    private static final BpmnPersistence INSTANCE = new BpmnPersistence();

    protected BpmnPersistence() {
    }

    public static BpmnPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal( final BpmnDiagram model ) {
        //TODO {manstis} Save the model as XML
        final StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    public BpmnDiagram unmarshal( final String content ) {
        //TODO {manstis} Load the model from XML
        final BpmnDiagram model = new BpmnDiagram();
        return model;
    }

}
