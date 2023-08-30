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


public interface MessageCallback {

    /**
     * Called by the Message Bus every time it processes a message with the
     * subject this callback is registered for.
     *
     * @param message
     *          The message on the bus. Avoid making changes to this object,
     *          because it will continue to be reused by the framework and the
     *          same Message instance will be passed to other callbacks.
     */
    public void callback(Message message);
}