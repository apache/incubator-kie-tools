/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.displayer.external;

import java.util.Map;
import java.util.Optional;

import jsinterop.base.Js;

/**
 * Helper to deal with External Component Messages.
 *
 */
public class ExternalComponentMessageHelper {

    /**
     * Message when a requested function is not found
     */
    public static final String FUNCTION_NOT_FOUND = "Function not found.";

    /**
     * Message when a function is executed with success
     */
    public static final String FUNCTION_CALL_SUCESS = "Function called with success";

    /**
     * Message when a message is of type FUNCTION_CALL, but the request to call the function is not found.
     */
    public static final String FUNCTION_CALL_REQUEST_NOT_FOUND = "You must provide a function request to call a function.";

    /**
     * Default message when there was an error executing the function.
     */
    public static final String FUNCTION_EXECUTION_ERROR_FOUND = "There was an error executing the function";

    /**
     * The property that should be used by components to find its unique ID during Runtime.
     */
    static final String COMPONENT_RUNTIME_ID_PROP = "component_id";

    /**
     * Inbound property to define the dataset to be consumed by components.
     */
    static final String DATA_SET_PROP = "dataSet";

    /**
     * Outbound property to define the filter request. Should be used with messages of type Filter Request.
     */
    static final String FILTER_PROP = "filter";

    /**
     * Outbound property to define the name of a function to be invoked.
     */
    static final String FUNCTION_CALL_PROP = "functionCallRequest";

    /**
     * Inbound property to define the result of a function call.
     */
    static final String FUNCTION_RESPONSE_PROP = "functionResponse";

    /**
     * Inbound property to define the result of a function call.
     */
    static final String CONFIGURATION_ISSUE_PROP = "configurationIssue";

    /**
     * Extract filter request from messages coming from the component.
     * 
     * @param message
     * The message sent by the component.
     * @return
     * An optional containing the filter extracted from the message.
     */
    public Optional<ExternalFilterRequest> filterRequest(ExternalComponentMessage message) {
        Object filterProp = message.getProperty(FILTER_PROP);
        if (filterProp != null) {
            ExternalFilterRequest filterRequest = Js.cast(filterProp);
            return Optional.ofNullable(filterRequest);
        }
        return Optional.empty();
    }


    /**
     * Builds a message that contains dataset and component properties.
     * @param ds
     * The dataset that will be sent to client
     * @param componentProperties
     * All the component properties
     * @return
     * The message ready to be sent to the user.
     */
    public ExternalComponentMessage newDataSetMessage(ExternalDataSet ds, Map<String, Object> componentProperties) {
        ExternalComponentMessage message = ExternalComponentMessage.create(ExternalComponentMessageType.DATASET.name(), componentProperties);
        message.setProperty(DATA_SET_PROP, ds);
        return message;
    }

    /**
     * Creates a message of type INIT, which is the first message sent to the component.
     * @param componentProperties
     * The initial properties.
     * @return
     * The message ready to be sent.
     */
    public ExternalComponentMessage newInitMessage(Map<String, Object> componentProperties) {
        return ExternalComponentMessage.create(ExternalComponentMessageType.INIT.name(), componentProperties);
    }

    /**
     * 
     * Add the component runtime ID to a message
     * @param message
     * The message which the ID should be set
     * @param componentId
     * The component unique ID. It should be used by components to send messages to DB.
     */
    public void withId(ExternalComponentMessage message, String componentId) {
        message.setProperty(COMPONENT_RUNTIME_ID_PROP, componentId);
    }

    /**
     * Extract the component Id from a message.
     * @param message
     * The target message to have the component id removed.
     * @return
     * An optional containing the component id.
     */
    public Optional<String> getComponentId(ExternalComponentMessage message) {
        return getValue(COMPONENT_RUNTIME_ID_PROP, message);
    }

    public Optional<String> getConfigurationIssue(ExternalComponentMessage message) {
        return getValue(CONFIGURATION_ISSUE_PROP, message);
    }

    /**
     * 
     * Check if the given message is an INIT message.
     * @param message
     * The message to be checked.
     * @return
     * true if it is a message of type INIT.
     */
    public boolean isInit(ExternalComponentMessage message) {
        String type = verifyTypeNotNull(message);
        return ExternalComponentMessageType.INIT.name().equals(type);
    }

    public ExternalComponentMessageType messageType(ExternalComponentMessage message) {
        String type = verifyTypeNotNull(message);
        return ExternalComponentMessageType.valueOf(type);
    }

    private String verifyTypeNotNull(ExternalComponentMessage message) {
        String type = message.getType();
        if (type == null) {
            throw new IllegalArgumentException("Message type cannot be null.");
        }
        return type;
    }

    private Optional<String> getValue(String valueKey, ExternalComponentMessage message) {
        Object valueObj = message.getProperty(valueKey);
        if (valueObj != null) {
            String componentId = valueObj.toString();
            return Optional.ofNullable(componentId);
        }
        return Optional.empty();
    }

}