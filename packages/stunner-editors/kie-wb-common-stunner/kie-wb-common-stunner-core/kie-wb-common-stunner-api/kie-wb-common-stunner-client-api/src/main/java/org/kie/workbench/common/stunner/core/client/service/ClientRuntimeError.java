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

package org.kie.workbench.common.stunner.core.client.service;

/**
 * Class that wraps errors either captured or generated on client runtime.
 */
public class ClientRuntimeError {

    private final String errorTitle;
    private final String errorContent;
    private final String errorMessage;
    private final Throwable throwable;

    public ClientRuntimeError(final String errorMessage) {
        this(null, null, errorMessage,
             null);
    }

    public ClientRuntimeError(final Throwable e) {
        this(null,
                null,
             null,
             e);
    }

    public ClientRuntimeError(final String errorMessage,
                              final Throwable e) {
        this(null,
                null,
                errorMessage,
                e);
    }

    public ClientRuntimeError(final String errorTitle,
                              final String errorContent,
                              final String errorMessage,
                              final Throwable e) {
        this.errorTitle = errorTitle;
        this.errorContent = errorContent;
        this.errorMessage = errorMessage;
        this.throwable = e;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getErrorMessage() {
        if (null != errorMessage && !errorMessage.trim().isEmpty()) {
            return errorMessage;
        }
        Throwable root = getRootCause();
        return root.toString();
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public String getErrorContent() {
        return errorContent;
    }

    public String getCause() {
        Throwable root = getRootCause();
        if (root == null || getErrorMessage().equals(root.toString())) {
            return null;
        } else {
            return root.getMessage();
        }
    }

    public Throwable getRootCause() {
        Throwable target = throwable;
        while (true) {
            if (target == null || target.getCause() == null) {
                return target;
            } else {
                target = target.getCause();
            }
        }
    }

    public String toString() {
        String cause = getCause();
        return getErrorMessage() + (cause != null ? " (cause: " + cause + ")" : "");
    }
}
