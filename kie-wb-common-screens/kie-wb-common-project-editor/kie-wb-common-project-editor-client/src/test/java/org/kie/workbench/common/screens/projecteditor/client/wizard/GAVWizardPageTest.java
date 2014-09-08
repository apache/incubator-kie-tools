/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.wizard;

import org.guvnor.common.services.project.client.POMEditorPanel;
import org.guvnor.common.services.project.model.POM;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.kie.uberfire.client.wizards.WizardPageStatusChangeEvent;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;

import javax.enterprise.event.Event;
import java.lang.annotation.Annotation;

import static org.mockito.Mockito.*;

public class GAVWizardPageTest {

    private POMEditorPanel pomEditor;
    private GAVWizardPage page;
    private GAVWizardPageView view;

    @Before
    public void setUp() throws Exception {
        pomEditor = mock(POMEditorPanel.class);
        view = mock(GAVWizardPageView.class);
        page = new GAVWizardPage(
                pomEditor,
                view,
                new WizardPageStatusChangeEventMock(),
                new ProjectScreenServiceMock());
    }

    @Test
    public void testPomsWithParentDataDisableFieldsParentNotSet() throws Exception {
        page.setPom(new POM());

        verify(pomEditor, never()).disableGroupID(anyString());
        verify(pomEditor, never()).disableVersion(anyString());
    }

    @Test
    public void testPomsWithParentDataDisableFieldsParentSet() throws Exception {
        when(view.InheritedFromAParentPOM()).thenReturn("InheritedFromAParentPOM");
        POM pom = new POM();
        pom.getGav().setGroupId("supergroup");
        page.setPom(pom);

        verify(pomEditor).disableGroupID("InheritedFromAParentPOM");
        verify(pomEditor).disableVersion("InheritedFromAParentPOM");
    }

    private class WizardPageStatusChangeEventMock
            implements Event<WizardPageStatusChangeEvent> {
        @Override
        public void fire(WizardPageStatusChangeEvent wizardPageStatusChangeEvent) {

        }

        @Override
        public Event<WizardPageStatusChangeEvent> select(Annotation... annotations) {
            return null;
        }

        @Override
        public <U extends WizardPageStatusChangeEvent> Event<U> select(Class<U> uClass, Annotation... annotations) {
            return null;
        }
    }

    private class ProjectScreenServiceMock
            implements Caller<ProjectScreenService> {
        @Override
        public ProjectScreenService call() {
            return null;
        }

        @Override
        public ProjectScreenService call(RemoteCallback<?> remoteCallback) {
            return null;
        }

        @Override
        public ProjectScreenService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
            return null;
        }
    }
}
