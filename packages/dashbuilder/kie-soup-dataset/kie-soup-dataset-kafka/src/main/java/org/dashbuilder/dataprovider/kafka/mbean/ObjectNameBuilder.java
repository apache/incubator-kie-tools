/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.kafka.mbean;

/**
 * Makes build object names for MBeans
 *
 */
public class ObjectNameBuilder {

    private String domain;
    private String name;
    private String type;
    private String request;
    private String delayedOperation;
    private String clientId;
    private String topic;
    private String nodeId;
    private String hyfenClientId;
    private String hyfenNodeId;
    private String partition;

    private ObjectNameBuilder(String domain) {
        this.domain = domain;
    }

    static ObjectNameBuilder create(String domain) {
        return new ObjectNameBuilder(domain);
    }

    public ObjectNameBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ObjectNameBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ObjectNameBuilder request(String request) {
        this.request = request;
        return this;
    }

    public ObjectNameBuilder delayedOperation(String delayedOperation) {
        this.delayedOperation = delayedOperation;
        return this;
    }

    public ObjectNameBuilder clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ObjectNameBuilder hyfenClientId(String clientId) {
        this.hyfenClientId = clientId;
        return this;
    }

    public ObjectNameBuilder topic(String topic) {
        this.topic = topic;
        return this;
    }

    public ObjectNameBuilder nodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public ObjectNameBuilder partition(String partition) {
        this.partition = partition;
        return this;
    }
    
    public ObjectNameBuilder hyfenNodeId(String nodeId) {
        this.hyfenNodeId = nodeId;
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder(domain + ":");
        appendFirst(sb, "type", type);
        appendIntermediary(sb, "name", name);
        appendIntermediary(sb, "request", request);
        appendIntermediary(sb, "delayedOperation", delayedOperation);
        appendIntermediary(sb, "clientId", clientId);
        appendIntermediary(sb, "topic", topic);
        appendIntermediary(sb, "nodeId", nodeId);
        appendIntermediary(sb, "client-id", hyfenClientId);
        appendIntermediary(sb, "partition", partition);
        appendIntermediary(sb, "node-id", hyfenNodeId);
        return sb.toString();
    }

    private void appendFirst(StringBuilder sb, String name, String value) {
        append(sb, name, value, false);
    }

    private void appendIntermediary(StringBuilder sb, String name, String value) {
        append(sb, name, value, true);
    }

    private void append(StringBuilder sb, String name, String value, boolean intermediary) {
        if (value == null) {
            return;
        }
        if (intermediary) {
            sb.append(",");
        }
        sb.append(name);
        sb.append("=");
        sb.append(value);
    }

}