/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.events;

import org.uberfire.workbench.events.UberFireEvent;

/**
 * An error to be handled from a given context.
 * 
 * @since 0.8.0
 */
public class OnErrorEvent extends ContextualEvent implements UberFireEvent {

    private Throwable cause;
    private String message;

    public OnErrorEvent(Object context) {
        super(context);
    }

    public OnErrorEvent(Object context, Throwable cause) {
        super(context);
        this.cause = cause;
    }

    public OnErrorEvent(Object context, String message) {
        super(context);
        this.message = message;
    }

    public Throwable getCause() {
        return cause;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorEvent [context=" + getContext() + "]";
    }

}
