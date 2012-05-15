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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.framework.MessageBus;

public abstract class WorkbenchPart extends AbstractActivity {

    protected final MessageBus bus = ErraiBus.get();

    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        createPartControl(panel);
        init();
    }

    public void init() {
        bus.subscribe("Focus_" + getId(), new MessageCallback() {
            @Override
            public void callback(Message message) {
                setFocus();
            }
        });
    }

    public abstract void createPartControl(AcceptsOneWidget container);

    public String getId() {
        return getName();
    }

    public abstract String getName();

    public String getTitle() {
        return "";
    }

    public String getTitleToolTip() {
        return "";
    }

    public ImageResource getTitleImage() {
        return null;
    }

    public void onStop() {
        bus.unsubscribeAll("Focus_" + getId());
        dispose();
    }

    public abstract void dispose();

    public abstract void setFocus();
}
