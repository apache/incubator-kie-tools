/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBeanFactoryTest {

    @Mock
    private StaticWorkbenchPanelPresenter staticWorkbenchPanelPresenter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SyncBeanManager iocManager;

    @InjectMocks
    private DefaultBeanFactory beanFactory;

    @Before
    public void setup() {
        when(iocManager.lookupBean(StaticWorkbenchPanelPresenter.class).getInstance()).thenReturn(staticWorkbenchPanelPresenter);
    }

    @Test
    public void testNewTemplateWorkbenchPartPresenter() {
        beanFactory.newWorkbenchPart(null,
                                     null);
        verify(iocManager.lookupBean(StaticWorkbenchPanelPresenter.class)).getInstance();
    }
}
