/*
 * Copyright 2010 JBoss Inc
 *
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
 */

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A response packet that is sent back to the client.
 * messageType is to allow filtering on the client, message data itself contains the message.
 */
public class PushResponse implements IsSerializable {

    public PushResponse() {
        this("", "");
    }
    public PushResponse(String key, String message) {
        this.messageType = key;
        this.message = message;
    }
    public String messageType;
    public String message;

}
