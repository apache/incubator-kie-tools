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

package org.guvnor.asset.management.client.editors.repository.wizard;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.Wizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;

public class WizardTestUtils {

    public static void assertPageComplete(final boolean expectedResult,
                                          WizardPage page) {
        page.isComplete(new Callback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                assertEquals(expectedResult,
                             result);
            }
        });
    }

    public static void assertWizardComplete(final boolean expectedResult,
                                            Wizard wizard) {
        wizard.isComplete(new Callback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                assertEquals(expectedResult,
                             result);
            }
        });
    }

    public static class WizardPageStatusChangeEventMock implements Event<WizardPageStatusChangeEvent> {

        protected List<WizardPageStatusChangeHandler> eventHandlers = new ArrayList<WizardPageStatusChangeHandler>();

        @Override
        public void fire(WizardPageStatusChangeEvent wizardPageStatusChangeEvent) {
            for (WizardPageStatusChangeHandler eventHandler : eventHandlers) {
                eventHandler.handleEvent(wizardPageStatusChangeEvent);
            }
        }

        @Override
        public Event<WizardPageStatusChangeEvent> select(Annotation... annotations) {
            return null;
        }

        @Override
        public <U extends WizardPageStatusChangeEvent> Event<U> select(Class<U> aClass,
                                                                       Annotation... annotations) {
            return null;
        }

        public void addEventHandler(WizardPageStatusChangeHandler eventHandler) {
            eventHandlers.add(eventHandler);
        }
    }

    public interface WizardPageStatusChangeHandler {

        void handleEvent(WizardPageStatusChangeEvent event);
    }

    public static class NotificationEventMock implements Event<NotificationEvent> {

        protected List<NotificationEventHandler> eventHandlers = new ArrayList<NotificationEventHandler>();

        @Override
        public void fire(NotificationEvent notificationEvent) {
            for (NotificationEventHandler eventHandler : eventHandlers) {
                eventHandler.handleEvent(notificationEvent);
            }
        }

        @Override
        public Event<NotificationEvent> select(Annotation... annotations) {
            return null;
        }

        @Override
        public <U extends NotificationEvent> Event<U> select(Class<U> aClass,
                                                             Annotation... annotations) {
            return null;
        }

        public void addEventHandler(NotificationEventHandler eventHandler) {
            eventHandlers.add(eventHandler);
        }
    }

    public interface NotificationEventHandler {

        void handleEvent(NotificationEvent notificationEvent);
    }
}
