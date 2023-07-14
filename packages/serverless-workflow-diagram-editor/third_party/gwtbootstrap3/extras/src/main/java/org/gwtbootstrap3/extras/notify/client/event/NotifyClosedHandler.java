package org.gwtbootstrap3.extras.notify.client.event;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2015 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Handler interface for Notify closed events.
 */
public interface NotifyClosedHandler {

    /**
     * Called when Notify closed event is fired.
     */
    void onClosed();

    /**
     * Default Notify's closed handler
     */
    static NotifyClosedHandler DEFAULT_CLOSED_HANDLER = new NotifyClosedHandler() {
        @Override
        public void onClosed() {}
    };
}
