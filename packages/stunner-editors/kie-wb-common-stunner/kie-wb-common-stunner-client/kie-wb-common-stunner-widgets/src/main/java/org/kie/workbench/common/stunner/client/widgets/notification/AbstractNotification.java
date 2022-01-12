/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.notification;

public abstract class AbstractNotification<S> implements Notification<S, NotificationContext> {

    private final Type type;
    private final String message;
    private final NotificationContext context;

    AbstractNotification(final NotificationContext context,
                         final Type type,
                         final String message) {
        this.type = type;
        this.message = message;
        this.context = context;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public NotificationContext getContext() {
        return context;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
