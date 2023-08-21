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
 * Handler interface for Notify close events.
 */
public interface NotifyCloseHandler {

    /**
     * Called when Notify close event is fired.
     */
    void onClose();

    /**
     * Default Notify's close handler
     */
    static NotifyCloseHandler DEFAULT_CLOSE_HANDLER = new NotifyCloseHandler() {
        @Override
        public void onClose() {}
    };
}
