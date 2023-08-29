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


package org.kie.workbench.common.stunner.bpmn.client.marshall;

public final class MarshallingMessageKeys {

    public static final String boundaryIgnored = "MarshallingMessage.boundaryIgnored";
    public static final String associationIgnored = "MarshallingMessage.associationIgnored";
    public static final String sequenceFlowIgnored = "MarshallingMessage.sequenceFlowIgnored";

    public static final String collapsedElementExpanded = "MarshallingMessage.collapsedElementExpanded";

    public static final String ignoredElement = "MarshallingMessage.ignoredElement";
    public static final String ignoredUnknownElement = "MarshallingMessage.ignoredUnknownElement";

    public static final String childLaneSetConverted = "MarshallingMessage.childLaneSetConverted";
    public static final String convertedElement = "MarshallingMessage.convertedElement";

    public static final String elementFailure = "MarshallingMessage.elementFailure";
}
