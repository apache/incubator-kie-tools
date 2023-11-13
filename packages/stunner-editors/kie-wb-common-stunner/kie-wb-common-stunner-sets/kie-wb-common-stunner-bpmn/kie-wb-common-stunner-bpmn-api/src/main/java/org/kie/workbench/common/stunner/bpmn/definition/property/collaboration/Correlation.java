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


package org.kie.workbench.common.stunner.bpmn.definition.property.collaboration;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Portable
@Bindable
public class Correlation {

    private static final String EMPTY_STRING = "";

    protected static final String DELIMITER = "|";
    protected static final String IDENTIFIER = "correlation";
    protected static final int VALID_CORRELATION_LENGTH = 5;

    private String id;

    private String name;

    private String propertyId;

    private String propertyName;

    private String propertyType;

    public Correlation() {
        this(EMPTY_STRING,
             EMPTY_STRING,
             EMPTY_STRING,
             EMPTY_STRING,
             EMPTY_STRING);
    }

    public Correlation(@MapsTo("id") final String id,
                       @MapsTo("name") final String name,
                       @MapsTo("propertyId") final String propertyId,
                       @MapsTo("propertyName") final String propertyName,
                       @MapsTo("propertyType") final String propertyType) {
        this.id = id;
        this.name = name;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    public static Boolean isValidString(String correlationValue) {
        String[] correlationParts = splitCorrelationString(correlationValue);

        if (correlationParts.length != VALID_CORRELATION_LENGTH) {
            return false;
        }

        if (!correlationParts[0].equals(IDENTIFIER)) {
            return false;
        }

        if (correlationParts[1].isEmpty()) {
            return false;
        }

        if (correlationParts[2].isEmpty()) {
            return false;
        }

        if (correlationParts[3].isEmpty()) {
            return false;
        }

        if (correlationParts[4].isEmpty()) {
            return false;
        }

        if (correlationParts[5].isEmpty()) {
            return false;
        }

        return true;
    }

    public static Correlation fromString(String correlationValue) throws Exception {
        if (!isValidString(correlationValue)) {
            throw new Exception("The value: " + correlationValue + " is not a valid Correlation.");
        }

        String[] correlationParts = splitCorrelationString(correlationValue);
        Correlation correlation = new Correlation(correlationParts[1],
                                                  correlationParts[2],
                                                  correlationParts[3],
                                                  correlationParts[4],
                                                  correlationParts[5]);

        return correlation;
    }

    private static String[] splitCorrelationString(String correlationValue) {
        return correlationValue.split("\\" + DELIMITER);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(final String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(final String propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public String toString() {
        return IDENTIFIER + DELIMITER +
                id + DELIMITER +
                name + DELIMITER +
                propertyId + DELIMITER +
                propertyName + DELIMITER +
                propertyType + DELIMITER;
    }
}