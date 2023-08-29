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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Message;

public class CorrelationWriterData {

    private final String correlationPropertyID;
    private final Message message;
    private final FormalExpression messagePath;
    private final FormalExpression dataPath;

    public CorrelationWriterData(final String correlationPropertyID,
                                 final Message message,
                                 final FormalExpression messagePath,
                                 final FormalExpression dataPath) {
        this.correlationPropertyID = correlationPropertyID;
        this.message = message;
        this.messagePath = messagePath;
        this.dataPath = dataPath;
    }

    public String getCorrelationPropertyID() {
        return correlationPropertyID;
    }

    public Message getMessage() {
        return message;
    }

    public FormalExpression getMessagePath() {
        return messagePath;
    }

    public FormalExpression getDataPath() {
        return dataPath;
    }
}
