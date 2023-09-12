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


package org.kie.workbench.common.stunner.core.client.shape.view.event;

import java.util.LinkedList;
import java.util.List;

public class NativeHandlerRegistration {

    private final List<NativeHandler> handler_list = new LinkedList<>();

    public NativeHandlerRegistration() {
    }

    public final int size() {
        return this.handler_list.size();
    }

    public final boolean isEmpty() {
        return this.handler_list.isEmpty();
    }

    private void clear() {
        int size = this.size();
        for (int i = 0; i < size; ++i) {
            handler_list.get(i).removeHandler();
        }
        handler_list.clear();
    }

    public final NativeHandler register(final NativeHandler handler) {
        if (null != handler && !this.handler_list.contains(handler)) {
            this.handler_list.add(handler);
        }
        return handler;
    }

    public final boolean isRegistered(final NativeHandler handler) {
        return null != handler && this.size() > 0 && this.handler_list.contains(handler);
    }

    public final NativeHandlerRegistration deregister(final NativeHandler handler) {
        if (null != handler) {
            if (this.size() > 0 && this.handler_list.contains(handler)) {
                this.handler_list.remove(handler);
            }
            handler.removeHandler();
        }
        return this;
    }

    public void removeHandler() {
        clear();
    }
}
