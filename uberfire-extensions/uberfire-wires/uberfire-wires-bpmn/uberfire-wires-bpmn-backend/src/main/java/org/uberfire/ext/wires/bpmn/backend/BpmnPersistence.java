/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import com.thoughtworks.xstream.XStream;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;

public class BpmnPersistence {

    private static final BpmnPersistence INSTANCE = new BpmnPersistence();

    private XStream xs = new XStream();

    protected BpmnPersistence() {
    }

    public static BpmnPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal( final ProcessNode process ) {
        return xs.toXML( process );
    }

    public ProcessNode unmarshal( final String content ) {
        final ProcessNode process = (ProcessNode) xs.fromXML( content );
        return process;
    }

}
