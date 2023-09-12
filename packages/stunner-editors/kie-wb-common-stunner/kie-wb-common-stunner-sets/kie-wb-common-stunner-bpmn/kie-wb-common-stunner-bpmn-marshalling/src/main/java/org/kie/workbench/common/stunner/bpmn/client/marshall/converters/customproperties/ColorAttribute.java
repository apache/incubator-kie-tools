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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import org.omg.spec.bpmn.non.normative.color.ColorPackage;

public class ColorAttribute {

    private static final String colorns = ColorPackage.eNS_URI;

    public static final AttributeDefinition<String> BgActivities = new StringAttribute(colorns, "background-color", "#fafad2");
    public static final AttributeDefinition<String> BgEvents = new StringAttribute(colorns, "background-color", "#f5deb3");
    public static final AttributeDefinition<String> BgStartEvents = new StringAttribute(colorns, "background-color", "#9acd32");
    public static final AttributeDefinition<String> BgEndEvents = new StringAttribute(colorns, "background-color", "#ff6347");
    public static final AttributeDefinition<String> BgDataObjects = new StringAttribute(colorns, "background-color", "#C0C0C0");
    public static final AttributeDefinition<String> BgCatchingEvents = new StringAttribute(colorns, "background-color", "#f5deb3");
    public static final AttributeDefinition<String> BgThrowingEvents = new StringAttribute(colorns, "background-color", "#8cabff");
    public static final AttributeDefinition<String> BgGateways = new StringAttribute(colorns, "background-color", "#f0e68c");
    public static final AttributeDefinition<String> BgSwimlanes = new StringAttribute(colorns, "background-color", "#ffffff");
    public static final AttributeDefinition<String> DefaultBr = new StringAttribute(colorns, "border-color", "#000000");
    public static final AttributeDefinition<String> BrCatchingEvents = new StringAttribute(colorns, "border-color", "#a0522d");
    public static final AttributeDefinition<String> BrThrowingEvents = new StringAttribute(colorns, "border-color", "#008cec");
    public static final AttributeDefinition<String> BrGateways = new StringAttribute(colorns, "border-color", "#a67f00");
    public static final AttributeDefinition<String> DefaultFont = new StringAttribute(colorns, "font-color", "#000000");
    public static final AttributeDefinition<String> DefaultSeqwuenceFlow = new StringAttribute(colorns, "border-color", "#000000");
}
