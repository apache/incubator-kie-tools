/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.service;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * The {@link #luceneValue} value is always set to a lowercase one-word (no -'s or _'s) value for the following reason:
 * <ol>
 * <li>Lucence is case-sensitive: let's make it easy by just always passing a lower case string for these values</li>
 * <li>Some lucene analyzers may break up tokens on the "-" or other non-word characters. That would be bad, so let's just use one-word values</li>
 * </ol>
 */
@Portable
public enum PartType {

    /*
     * Java
     */

    // (there are cases in which it's not clear whether we are referring to a field or getter/setter:
    //  TODO: this will require extra logic to deal with these cases)
    /**
     * A Java class method
     */
    METHOD("method"),

    /**
     * A Java class field
     */
    FIELD("field"),

    /*
     * DRL
     */

    /**
     * A DRL-defined enum value
     */
    DRL_ENUM_VAL("drlenumval"),
    /**
     * A NamedConsequence in a DRL
     */
    NAMED_CONSEQUENCE("namedconsequence"),

    // SHARED PART TYPES

    /**
     * A rule-flow group in a rule or process
     */
    RULEFLOW_GROUP("ruleflowgroup"),

    /**
     * An agenda group in a rule
     */
    AGENDA_GROUP("agendagroup"),

    /**
     * An activation group in a rule
     */
    ACTIVATION_GROUP("activationgroup"),

    /**
     * An entry-point in one more rules
     */
    ENTRY_POINT("entrypoint"),

    /**
     * A global (fact) in one or more rules
     */
    GLOBAL("global"),

    /*
     * BPMN2 / Processes
     */

    /**
     * A process-defined variable
     */
    VARIABLE("variable"),

    /**
     * "taskName", used to identify WorkItemHandler
     */
    TASK_NAME("taskName"),

    // SHARED PART TYPES
    /**
     * This is a shared part type
     * </p>
     * This should be used for *all* "Signal" types, including Signals, Messages, Errors, and other "signal" types that
     * the jBPM process engine internally handles as a signal.
     */
    SIGNAL("signal"),

    /*
     * Forms
     */

    /**
     * A data-holder defined in one or more forms
     */
    DATAHOLDER("dataholder"),

    /**
     * A form field
     */
    FORM_FIELD("formfield"),

    /*
     * Scorecards
     */

    /**
     * A form field
     */
    SCORECARD_MODEL_NAME("scorecardModelName"),

    /*
     * Paths
     */

    /**
     * Generic path reference
     */
    PATH("path");


    private final String luceneValue;

    private PartType(String value) {
        this.luceneValue = value;
    }

    @Override
    public String toString() {
        return this.luceneValue;
    }

    public static PartType getPartTypeFromAttribueDescrName(String name) {
        switch(name) {
             // Java
            case "method":
                return PartType.METHOD;
            case "field":
                return PartType.FIELD;

             // Rule
            case "ruleflow-group":
                return PartType.RULEFLOW_GROUP;
            case "agenda-group":
                return PartType.AGENDA_GROUP;
            case "activation-group":
                return PartType.ACTIVATION_GROUP;
            case "entry-point":
                return PartType.ENTRY_POINT;

             // Process
            default:
                throw new IllegalStateException("Unknown PartType: " + name );
        }
    }
}