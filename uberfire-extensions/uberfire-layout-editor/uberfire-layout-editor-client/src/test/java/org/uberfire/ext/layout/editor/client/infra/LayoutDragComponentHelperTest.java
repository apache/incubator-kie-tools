/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.layout.editor.client.infra;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.components.container.Container;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LayoutDragComponentHelperTest {

    @Mock
    private SyncBeanManager beanManager;

    @Mock
    private ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService;

    private LayoutDragComponentHelper helper;
    private SyncBeanDef<LayoutDragComponent> beanDefMock;

    @Before
    public void initialize() {
        helper = createLayoutDragMock();

        beanDefMock = mock(SyncBeanDef.class);
        Collection<SyncBeanDef<LayoutDragComponent>> beanDefs = Arrays.asList(beanDefMock);
        when(beanManager.lookupBeans(LayoutDragComponent.class)).thenReturn(beanDefs);
    }

    @Test
    public void lookupBeanShouldRespectBeanScope() {
        helper.lookupDragTypeBean(LayoutDragComponentMock.class.getName());
        verify(beanDefMock).getInstance();
        verify(beanDefMock, never()).newInstance();
    }

    private LayoutDragComponentHelper createLayoutDragMock() {
        return new LayoutDragComponentHelper(beanManager, experimentalFeaturesRegistryService) {
            @Override
            Predicate<SyncBeanDef<LayoutDragComponent>> syncBeanDefBeanClassNamePredicate(String dragTypeClassName) {
                return s -> true;
            }

            @Override
            boolean isAnEnabledExperimentalFeature(SyncBeanDef<LayoutDragComponent> beanDef) {
                return true;
            }
        };
    }

    public static class LayoutDragComponentMock implements LayoutDragComponent {

        @Override
        public String getDragComponentTitle() {
            return null;
        }

        @Override
        public IsWidget getPreviewWidget(RenderingContext ctx) {
            return null;
        }

        @Override
        public IsWidget getShowWidget(RenderingContext ctx) {
            return null;
        }
    }
}