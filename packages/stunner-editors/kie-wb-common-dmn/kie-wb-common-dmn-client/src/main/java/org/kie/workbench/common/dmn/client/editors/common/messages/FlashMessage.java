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

package org.kie.workbench.common.dmn.client.editors.common.messages;

import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.UberFireEvent;

public class FlashMessage implements UberFireEvent {

    private final String strongMessage;

    private final String regularMessage;

    private final Type type;

    private final String elementSelector;

    private final Command onSuccess;

    private final Command onError;

    public FlashMessage(final Type type,
                        final String strongMessage,
                        final String regularMessage,
                        final String elementSelector,
                        final Command onSuccess,
                        final Command onError) {
        this.type = type;
        this.strongMessage = strongMessage;
        this.regularMessage = regularMessage;
        this.elementSelector = elementSelector;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    public FlashMessage(final Type type,
                        final String strongMessage,
                        final String regularMessage,
                        final String elementSelector) {
        this(type, strongMessage, regularMessage, elementSelector, () -> { /* Nothing. */ }, () -> { /* Nothing. */ });
    }

    public FlashMessage(final Type type,
                        final String strongMessage,
                        final String regularMessage) {
        this(type, strongMessage, regularMessage, "", () -> { /* Nothing. */ }, () -> { /* Nothing. */ });
    }

    public String getStrongMessage() {
        return strongMessage;
    }

    public String getRegularMessage() {
        return regularMessage;
    }

    public Type getType() {
        return type;
    }

    public String getElementSelector() {
        return elementSelector;
    }

    public Command getOnSuccess() {
        return onSuccess;
    }

    public Command getOnError() {
        return onError;
    }

    public enum Type {
        ERROR,
        WARNING,
        SUCCESS
    }
}
