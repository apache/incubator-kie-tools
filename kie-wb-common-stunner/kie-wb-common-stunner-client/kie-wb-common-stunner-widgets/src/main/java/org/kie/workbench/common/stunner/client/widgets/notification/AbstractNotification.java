/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.notification;

public abstract class AbstractNotification<S, C> implements Notification<S, C> {

    private final String uuid;
    private final Type type;
    private final S source;
    private final C context;

    public AbstractNotification(final String uuid,
                                final Type type,
                                final S source,
                                final C context) {
        this.uuid = uuid;
        this.type = type;
        this.source = source;
        this.context = context;
    }

    @Override
    public String getNotificationUUID() {
        return uuid;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public S getSource() {
        return source;
    }

    @Override
    public C getContext() {
        return context;
    }
}
