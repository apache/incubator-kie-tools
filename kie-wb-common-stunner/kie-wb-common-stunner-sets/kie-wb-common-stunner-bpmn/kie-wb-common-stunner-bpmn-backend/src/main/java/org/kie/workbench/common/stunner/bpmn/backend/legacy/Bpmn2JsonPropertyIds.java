/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.legacy;

/**
 * Constants of BPMN 2.0 elements properties to JSON.
 */
public final class Bpmn2JsonPropertyIds {

    public static final String NAMESPACES = "namespaces";
    public static final String TARGETNAMESPACE = "targetnamespace";
    public static final String EXPRESSIONLANGUAGE = "expressionlanguage";
    public static final String CONDITIONEXPRESSIONLANGUAGE = "conditionexpressionlanguage";
    public static final String TYPELANGUAGE = "typelanguage";
    public static final String CREATIONDATE = "creationdate";
    public static final String ID = "id";
    public static final String PROCESSN = "processn";
    public static final String ADHOCPROCESS = "adhocprocess";
    public static final String GLOBALS = "globals";
    public static final String CURRENCY = "currency";
    public static final String TIMEUNIT = "timeunit";
    public static final String IMPORTS = "imports";
    public static final String DATAOUTPUT = "dataoutput";
    public static final String DATAOUTPUTASSOCIATIONS = "dataoutputassociations";
    public static final String TIMEDATE = "timedate";
    public static final String TIMEDURATION = "timeduration";
    public static final String TIMECYCLE = "timecycle";
    public static final String TIMECYCLELANGUAGE = "timecyclelanguage";
    public static final String TIMERSETTINGS = "timersettings";
    public static final String SIGNALREF = "signalref";
    public static final String ERRORREF = "errorref";
    public static final String CONDITIONLANGUAGE = "conditionlanguage";
    public static final String ESCALATIONCODE = "escalationcode";
    public static final String MESSAGEREF = "messageref";
    public static final String ACTIVITYREF = "activityref";
    public static final String SIGNALSCOPE = "signalscope";
    public static final String BORDERCOLOR = "bordercolor";
    public static final String FONTSIZE = "fontsize";
    public static final String FONTCOLOR = "fontcolor";
    public static final String ISSELECTABLE = "isselectable";
    public static final String ISINTERRUPTING = "isinterrupting";
    public static final String BOUNDARYCANCELACTIVITY = "boundarycancelactivity";
    public static final String INDEPENDENT = "independent";
    public static final String WAITFORCOMPLETION = "waitforcompletion";
    public static final String CALLEDELEMENT = "calledelement";
    public static final String ISASYNC = "isasync";
    public static final String SCRIPT_LANGUAGE = "script_language";
    public static final String ONENTRYACTIONS = "onentryactions";
    public static final String ONEXITACTIONS = "onexitactions";
    public static final String EXECUTABLE = "executable";
    public static final String PACKAGE = "package";
    public static final String VARDEFS = "vardefs";
    public static final String LANES = "lanes";
    public static final String VERSION = "version";
    public static final String AUTHOR = "author";
    public static final String LANGUAGE = "language";
    public static final String MODIFICATIONDATE = "modificationdate";
    public static final String NAME = "name";
    public static final String DOCUMENTATION = "documentation";
    public static final String AUDITING = "auditing";
    public static final String MONITORING = "monitoring";
    public static final String CONDITIONTYPE = "conditiontype";
    public static final String CONDITIONEXPRESSION = "conditionexpression";
    public static final String ISIMMEDIATE = "isimmediate";
    public static final String SHOWDIAMONDMARKER = "showdiamondmarker";
    public static final String EVENTDEFINITIONREF = "eventdefinitionref";
    public static final String EVENTDEFINITIONS = "eventdefinitions";
    public static final String DATAINPUTASSOCIATIONS = "datainputassociations";
    public static final String DATAINPUT = "datainput";
    public static final String INPUTSET = "inputset";
    public static final String BGCOLOR = "bgcolor";
    public static final String TRIGGER = "trigger";
    public static final String MEAN = "mean";
    public static final String DISTRIBUTIONTYPE = "distributiontype";
    public static final String WAITTIME = "waittime";
    public static final String STANDARDDEVIATION = "standarddeviation";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String TYPE = "type";
    public static final String PRIORITY = "priority";
    public static final String MULTIPLEINSTANCECOMPLETIONCONDITION = "multipleinstancecompletioncondition";
    public static final String MULTIPLEINSTANCECOLLECTIONOUTPUT = "multipleinstancecollectionoutput";
    public static final String MULTIPLEINSTANCECOLLECTIONINPUT = "multipleinstancecollectioninput";
    public static final String MULTIPLEINSTANCEDATAOUTPUT = "multipleinstancedataoutput";
    public static final String MULTIPLEINSTANCEDATAINPUT = "multipleinstancedatainput";
    public static final String MITRIGGER = "mitrigger";
    public static final String ASSIGNMENTS = "assignments";
    public static final String ADHOCCOMPLETIONCONDITION = "adhoccompletioncondition";
    public static final String ADHOCORDERING = "adhocordering";
    public static final String INPUT_OUTPUT = "input_output";
    public static final String CUSTOMTYPE = "customtype";
    public static final String STANDARDTYPE = "standardtype";
    public static final String DATAOUTPUTSET = "dataoutputset";
    public static final String DATAINPUTSET = "datainputset";

    //prevents creation of instances
    private Bpmn2JsonPropertyIds() {
    }
}
