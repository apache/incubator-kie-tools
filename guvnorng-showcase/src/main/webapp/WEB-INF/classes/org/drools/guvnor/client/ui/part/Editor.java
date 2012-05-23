/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.ui.part;

import org.drools.guvnor.client.ui.EditorInput;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.framework.RequestDispatcher;

public abstract class Editor extends WorkbenchPart {

    protected EditorInput input = null;
    private RequestDispatcher dispatcher = ErraiBus.getDispatcher();

    public String getId() {
        return getName() + "|" + input.getId();
    }

    public void init(final EditorInput input) {

        if (input != null) {
            bus.unsubscribeAll("Save_" + input.getId());
            bus.unsubscribeAll("IsDirty_" + input.getId());
        }

        this.input = input;

        bus.subscribe("Save_" + input.getId(), new MessageCallback() {
            @Override
            public void callback(Message message) {
                doSave();
            }
        });

        bus.subscribe("ForceSave", new MessageCallback() {
            @Override
            public void callback(Message message) {
                doSave();
            }
        });

        bus.subscribe("IsDirty_" + input.getId(), new MessageCallback() {
            @Override
            public void callback(Message message) {
                MessageBuilder.createMessage()
                        .toSubject("IsDirtyService")
                        .signalling()
                        .with("id", input.getId())
                        .with("is_dirty", isDirty())
                        .noErrorHandling()
                        .sendNowWith(dispatcher);
            }
        });
    }

    public EditorInput getInput() {
        return input;
    }

    public abstract void doSave();

    public abstract boolean isDirty();
}
