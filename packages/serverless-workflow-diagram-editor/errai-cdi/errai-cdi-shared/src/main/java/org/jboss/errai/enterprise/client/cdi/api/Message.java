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

import java.util.Map;

import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.ResourceProvider;

public interface Message {

    /**
     * Sets the subject of this message, which is the intended recipient, and returns the message.
     *
     * @param subject
     *          the intended recipient of the message.
     * @return this message.
     */
    public Message toSubject(String subject);

    /**
     * Returns the message's subject.
     *
     * @return this message's subject (its intended recipient).
     */
    public String getSubject();

    /**
     * Sets the command type for this message. Command is an optional extension for creating services that can respond to
     * different specific commands.
     *
     * @param type
     *          <tt>String</tt> representation of a command type.
     * @return this message.
     */
    public Message command(String type);

    /**
     * Set the command type for this message. Command is an optional extension for creating services that can respond to
     * different specific commands.
     *
     * @param type
     *          <tt>Enum</tt> representation of a command type.
     * @return this message.
     */
    public Message command(Enum<?> type);

    /**
     * Returns the command type for this message as a <tt>String</tt>.
     *
     * @return the command type.
     */
    public String getCommandType();

    /**
     * Sets a Message part to the specified value.
     *
     * @param part
     *          The <tt>String</tt> name of the message part.
     * @param value
     *          The value to set the part to.
     * @return this message.
     */
    public Message set(String part, Object value);

    /**
     * Sets a Message part to the specified value.
     *
     * @param part
     *          The <tt>Enum</tt> representation of the message part.
     * @param value
     *          the value to set the part to.
     * @return this message.
     */
    public Message set(Enum<?> part, Object value);

    /**
     * Adds or replaces a message part whose value is recomputed every time the message is sent.
     *
     * @param part
     *          The name of the message part.
     * @param provider
     *          a callback that provides the value of the message part on demand. Will be called every time this message
     *          is sent.
     * @return this message.
     */
    public Message setProvidedPart(String part, ResourceProvider<?> provider);

    /**
     * Adds or replaces a message part whose value is recomputed every time the message is sent.
     *
     * @param part
     *          The <tt>Enum</tt> representation of the message part.
     * @param provider
     *          a callback that provides the value of the message part on demand. Will be called every time this message
     *          is sent.
     * @return this message.
     */
    public Message setProvidedPart(Enum<?> part, ResourceProvider<?> provider);

    /**
     * Checks if message contains the specified part.
     *
     * @param part
     *          <tt>String</tt> part to check for.
     * @return true if message contains {@code part}.
     */
    public boolean hasPart(String part);

    /**
     * Checks if message contains the specified part.
     *
     * @param part
     *          <tt>Enum</tt> part to check for.
     * @return true if message contains {@code part}.
     */
    public boolean hasPart(Enum<?> part);

    /**
     * Removes specified part from the message.
     *
     * @param part
     *          the part to remove.
     */
    public void remove(String part);

    /**
     * Removes specified part from the message.
     *
     * @param part
     *          the part to remove.
     */
    public void remove(Enum<?> part);

    /**
     * Copies a part of this message to another message.
     *
     * @param part
     *          the part of this message to copy.
     * @param m
     *          the message to copy the part to. Must not be null.
     * @return this message
     */
    public Message copy(String part, Message m);

    /**
     * Copies a part of this message to another message.
     *
     * @param part
     *          the part of this message to copy.
     * @param m
     *          the message to copy the part to. Must not be null.
     * @return this message
     */
    public Message copy(Enum<?> part, Message m);

    /**
     * Sets the message to contain the specified parts. This overwrites any
     * existing message contents.
     * <p>
     * Implementations may or may not create a defensive copy of the given map.
     * Check their documentation to be sure.
     *
     * @param parts
     *          Parts to be used in the message. Must not be null.
     * @return this message
     */
    public Message setParts(Map<String, Object> parts);

    /**
     * Copies in a set of message parts from the provided map.
     *
     * @param parts
     *          Parts to be added to the message. Must not be null.
     * @return this message
     */
    public Message addAllParts(Map<String, Object> parts);

    /**
     * Copies in a set of provided message parts from the provided maps
     *
     * @param provided
     *          provided parts to be added to the message
     * @return this message
     */
    public Message addAllProvidedParts(Map<String, ResourceProvider<?>> provided);

    /**
     * Returns a Map of all the specified parts.
     *
     * @return a Map of the message parts.
     */
    public Map<String, Object> getParts();

    /**
     * Returns a Map of all provided parts.
     *
     * @return a Map of the provided message parts.
     */
    public Map<String, ResourceProvider<?>> getProvidedParts();

    /**
     * Adds the Map of resources to the message.
     *
     * @param resources
     *          Map of resource
     */
    public void addResources(Map<String, ?> resources);

    /**
     * Sets a transient resource. A resource is not transmitted beyond the current bus scope. It can be used for managing
     * the lifecycle of a message within a bus.
     *
     * @param key
     *          Name of resource.
     * @param res
     *          Instance of resource.
     * @return this message
     */
    public Message setResource(String key, Object res);

    /**
     * Obtains a transient resource based on the specified key.
     *
     * @param type
     *          type of resource.
     * @param key
     *          the name of the resource
     * @return Instance of resource.
     */
    public <T> T getResource(Class<T> type, String key);

    /**
     * Returns true if the specified transient resource is present.
     *
     * @param key
     *          Name of resource.
     * @return boolean value indicating if the specified resource is present in the message.
     */
    public boolean hasResource(String key);

    /**
     * Copies a transient resource to this message from the specified message.
     *
     * @param key  Name of resource.
     * @param m  Message to copy from.
     * @return this message.
     */
    public Message copyResource(String key, Message m);

    /**
     * Sets the error callback for this message.
     *
     * @param callback  error callback.
     * @return this message.
     */
    public Message errorsCall(ErrorCallback callback);

    /**
     * Gets the error callback for this message.
     *
     * @return the error callback
     */
    public ErrorCallback<Message> getErrorCallback();

    public <T> T getValue(Class<T> type);

    /**
     * Gets the specified message part in the specified type. A <tt>ClassCastException</tt> is thrown if the value cannot
     * be coerced to the specified type.
     *
     * @param type
     *          Type to be returned.
     * @param part
     *          Message part.
     * @param <T>
     *          Type to be returned.
     * @return Value in the specified type.
     */
    public <T> T get(Class<T> type, String part);

    /**
     * Gets the specified message part in the specified type. A <tt>ClassCastException</tt> is thrown if the value cannot
     * be coerced to the specified type.
     *
     * @param type
     *          Type to be returned.
     * @param part
     *          Message part.
     * @param <T>
     *          Type to be returned.
     * @return Value in the specified type.
     */
    public <T> T get(Class<T> type, Enum<?> part);

    /**
     * Commits the message in its current structure. After this method is called, there is no guarantee that any changes
     * in the message will be communicated across the bus. In fact, modifying the message after calling commit() may
     * create a corrupt payload. In theory, you should never call this method. It's called by the message bus immediately
     * before transmission.
     */
    public void commit();
}