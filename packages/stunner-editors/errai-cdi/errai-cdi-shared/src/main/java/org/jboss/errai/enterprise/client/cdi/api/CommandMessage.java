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


package org.jboss.errai.enterprise.client.cdi.api;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.ResourceProvider;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.common.client.types.TypeHandlerFactory;

public class CommandMessage implements Message {

    transient final Map<String, ResourceProvider<?>> providedParts;
    final Map<String, Object> parts;
    transient Map<String, Object> resources;
    ErrorCallback errorsCall;
    int routingFlags;

    /**
     * Creates a new CommandMessage with no parts and no provided parts.
     *
     * @return a new instance of CommandMessage.
     */
    public static CommandMessage create() {
        return new CommandMessage();
    }

    public static CommandMessage createWithParts(final Map<String, Object> parts) {
        return new CommandMessage(Assert.notNull(parts));
    }

    @SuppressWarnings("unchecked")
    public static CommandMessage createWithPartsFromRawMap(final Map parts) {
        return new CommandMessage(parts);
    }

    public static CommandMessage createWithParts(final Map<String, Object> parts, final int flags) {
        return new CommandMessage(Assert.notNull(parts), flags);
    }

    public static CommandMessage createWithParts(final Map<String, Object> parts, final Map<String, ResourceProvider<?>> provided) {
        return new CommandMessage(parts, provided);
    }

    CommandMessage() {
        this.parts = new HashMap<String, Object>();
        this.providedParts = new HashMap<String, ResourceProvider<?>>(5);
    }

    private CommandMessage(final Map<String, Object> parts) {
        this.parts = parts;
        this.providedParts = new HashMap<String, ResourceProvider<?>>(0);
    }

    public CommandMessage(final Map<String, Object> parts, final int routingFlags) {
        this.parts = parts;
        this.routingFlags = routingFlags;
        this.providedParts = new HashMap<String, ResourceProvider<?>>(5);
    }

    private CommandMessage(final Map<String, Object> parts, final Map<String, ResourceProvider<?>> providers) {
        this.parts = parts;
        this.providedParts = providers;
    }

    @Override
    public String getCommandType() {
        return (String) parts.get(MessageParts.CommandType.name());
    }

    @Override
    public String getSubject() {
        return String.valueOf(parts.get(MessageParts.ToSubject.name()));
    }

    @Override
    public Message toSubject(final String subject) {
        parts.put(MessageParts.ToSubject.name(), subject);
        return this;
    }

    @Override
    public Message command(final Enum<?> type) {
        parts.put(MessageParts.CommandType.name(), type.name());
        return this;
    }

    @Override
    public Message command(final String type) {
        parts.put(MessageParts.CommandType.name(), type);
        return this;
    }

    @Override
    public Message set(final Enum<?> part, final Object value) {
        return set(part.name(), value);
    }

    @Override
    public Message set(final String part, final Object value) {
        parts.put(part, value);
        return this;
    }

    @Override
    public Message setProvidedPart(final String part, final ResourceProvider<?> provider) {
        providedParts.put(part, provider);
        return this;
    }

    @Override
    public Message setProvidedPart(final Enum<?> part, final ResourceProvider<?> provider) {
        return setProvidedPart(part.name(), provider);
    }

    @Override
    public void remove(final String part) {
        parts.remove(part);
    }

    @Override
    public void remove(final Enum<?> part) {
        parts.remove(part.name());
    }

    @Override
    public Message copy(final Enum<?> part, final Message message) {
        set(part, message.get(Object.class, part));
        return this;
    }

    @Override
    public Message copy(final String part, final Message message) {
        set(part, message.get(Object.class, part));
        return this;
    }

    @Override
    public <T> T getValue(Class<T> type) {
        return get(type, MessageParts.Value);
    }

    @Override
    @SuppressWarnings({ "UnusedDeclaration" })
    public <T> T get(final Class<T> type, final Enum<?> part) {
        return get(type, part.toString());
    }

    @Override
    @SuppressWarnings({ "UnusedDeclaration" })
    public <T> T get(final Class<T> type, final String part) {
        final Object value = parts.get(part);
        return value == null ? null : TypeHandlerFactory.convert(value.getClass(), type, value);
    }

    @Override
    public boolean hasPart(final Enum<?> part) {
        return hasPart(part.name());
    }

    @Override
    public boolean hasPart(final String part) {
        return parts.containsKey(part);
    }

    @Override
    public Map<String, Object> getParts() {
        return parts;
    }

    @Override
    public Map<String, ResourceProvider<?>> getProvidedParts() {
        return providedParts;
    }

    @Override
    public Message setParts(final Map<String, Object> parts) {
        parts.clear();
        parts.putAll(parts);
        return this;
    }

    @Override
    public Message addAllParts(final Map<String, Object> parts) {
        this.parts.putAll(parts);
        return this;
    }

    @Override
    public Message addAllProvidedParts(final Map<String, ResourceProvider<?>> parts) {
        this.providedParts.putAll(parts);
        return this;
    }

    @Override
    public Message setResource(final String key, final Object res) {
        if (this.resources == null)
            this.resources = new HashMap<String, Object>();
        this.resources.put(key, res);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getResource(final Class<T> type, final String key) {
        return (T) (this.resources == null ? null : this.resources.get(key));
    }

    @Override
    public Message copyResource(final String key, final Message copyFrom) {
        if (!copyFrom.hasResource(key)) {
            throw new RuntimeException("Cannot copy resource '" + key + "': no such resource.");
        }
        setResource(key, copyFrom.getResource(Object.class, key));
        return this;
    }

    @Override
    public Message errorsCall(final ErrorCallback callback) {
        if (this.errorsCall != null) {
            throw new RuntimeException("An ErrorCallback is already registered");
        }
        this.errorsCall = callback;
        return this;
    }

    @Override
    public ErrorCallback getErrorCallback() {
        return errorsCall;
    }

    @Override
    public boolean hasResource(final String key) {
        return this.resources != null && this.resources.containsKey(key);
    }

    @Override
    public void addResources(final Map<String, ?> resources) {
        if (this.resources == null) {
            this.resources = new HashMap<String, Object>(resources);
        }
        else {
            this.resources.putAll(resources);
        }
    }

    @Override
    public void commit() {
        if (!providedParts.isEmpty()) {
            for (final Map.Entry<String, ResourceProvider<?>> entry : providedParts.entrySet())
                set(entry.getKey(), entry.getValue().get());
        }
    }

    @Override
    public String toString() {
        return buildDescription();
    }

    private String buildDescription() {
        final StringBuilder append = new StringBuilder();
        boolean f = false;
        for (final Map.Entry<String, Object> entry : parts.entrySet()) {
            if (f)
                append.append(", ");
            append.append(entry.getKey()).append("=").append(String.valueOf(entry.getValue()));
            f = true;
        }
        return append.toString();
    }
}