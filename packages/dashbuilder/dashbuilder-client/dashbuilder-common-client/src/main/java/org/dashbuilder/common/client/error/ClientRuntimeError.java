/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.common.client.error;

import org.dashbuilder.common.client.StringUtils;

/**
 * Class that wraps errors either captured or generated on client runtime.
 */
public class ClientRuntimeError {

    private String message;
    private Throwable throwable;

    public ClientRuntimeError(String message) {
        this(message, null);
    }

    public ClientRuntimeError(Throwable e) {
        this(null, e);
    }

    public ClientRuntimeError(String message, Throwable e) {
        this.message = message;
        this.throwable = e;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getMessage() {
        if (!StringUtils.isBlank(message)) {
            return message;
        }

        Throwable root = getRootCause();
        return root.toString();
    }

    public String getCause() {
        Throwable root = getRootCause();
        if (root == null || getMessage().equals(root.toString())) {
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
        return getMessage() + (cause != null ? " (cause: " + cause + ")" : "");
    }
}
