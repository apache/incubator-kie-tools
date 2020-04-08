/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.kogito.client.services.util.impl.WorkItemIconCacheImpl;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientUtils.getDefaultIconData;
import static org.mockito.Mockito.mock;

@RunWith(GwtMockitoTestRunner.class)
public class WorkItemDefinitionStandaloneClientServiceTest {

    private WorkItemDefinitionStandaloneClientService tested;
    private Promises promises;
    private WorkItemDefinitionCacheRegistry registry;

    @Before
    public void setUp() {
        promises = new SyncPromises();
        registry = new WorkItemDefinitionCacheRegistry();
        tested = new WorkItemDefinitionStandaloneClientService(promises,
                                                               registry,
                                                               new BPMNStaticResourceContentService(promises),
                                                               new WorkItemIconCacheImpl(new BPMNStaticResourceContentService(promises)));
    }

    @Test
    public void testGetRegistry() {
        tested.init();
        assertEquals(registry, tested.getRegistry());
    }

    @Test
    public void testLoadAllWorkItems() {
        call();

        List<WorkItemDefinition> items = new ArrayList<>(registry.items());
        assertEquals(6, items.size());

        WorkItemDefinition email = items.get(0);
        assertNotNull(email);
        assertEquals("Email", email.getName());
        assertEquals(BPMNStaticResourceContentService.EMAIL_SERVICE_TASK_DATA_URI, email.getIconDefinition().getIconData());

        WorkItemDefinition log = items.get(1);
        assertNotNull(log);
        assertEquals("Log", log.getName());
        assertEquals(BPMNStaticResourceContentService.LOG_SERVICE_TASK_DATA_URI, log.getIconDefinition().getIconData());

        WorkItemDefinition decisionTask = items.get(2);
        assertNotNull(decisionTask);
        assertEquals("DecisionTask", decisionTask.getName());
        assertEquals(getDefaultIconData(), decisionTask.getIconDefinition().getIconData());

        WorkItemDefinition milestone = items.get(3);
        assertNotNull(milestone);
        assertEquals("Milestone", milestone.getName());
        assertEquals(getDefaultIconData(), milestone.getIconDefinition().getIconData());

        WorkItemDefinition brTask = items.get(4);
        assertNotNull(brTask);
        assertEquals("BusinessRuleTask", brTask.getName());
        assertEquals(getDefaultIconData(), brTask.getIconDefinition().getIconData());

        WorkItemDefinition anotherLog = items.get(5);
        assertNotNull(anotherLog);
        assertEquals("AnotherLog", anotherLog.getName());
        assertEquals(BPMNStaticResourceContentService.LOG_SERVICE_TASK_DATA_URI, anotherLog.getIconDefinition().getIconData());
    }

    @Test
    public void testLoadNoWorkItems() {
        tested = new WorkItemDefinitionStandaloneClientService(promises,
                                                               registry,
                                                               new ResourceContentService() {

                                                                   @Override
                                                                   public Promise<String> get(String uri) {
                                                                       return promises.resolve();
                                                                   }

                                                                   @Override
                                                                   public Promise<String> get(String uri,
                                                                                              ResourceContentOptions options) {
                                                                       return get(uri);
                                                                   }

                                                                   @Override
                                                                   public Promise<String[]> list(String pattern) {
                                                                       return promises.resolve(new String[0]);
                                                                   }

                                                                   @Override
                                                                   public Promise<String[]> list(String pattern, ResourceListOptions options) {
                                                                       return promises.resolve(new String[0]);
                                                                   }
                                                               },
                                                               new WorkItemIconCacheImpl(new BPMNStaticResourceContentService(promises)));
        call();
        assertTrue(registry.items().isEmpty());
    }

    @Test
    public void testLoadEmptyWorkItems() {
        tested = new WorkItemDefinitionStandaloneClientService(promises,
                                                               registry,
                                                               new ResourceContentService() {

                                                                   @Override
                                                                   public Promise<String> get(String uri) {
                                                                       return promises.resolve();
                                                                   }

                                                                   @Override
                                                                   public Promise<String> get(String uri,
                                                                                              ResourceContentOptions options) {
                                                                       return get(uri);
                                                                   }

                                                                   @Override
                                                                   public Promise<String[]> list(String pattern) {
                                                                       return promises.resolve(new String[]{""});
                                                                   }

                                                                   @Override
                                                                   public Promise<String[]> list(String pattern, ResourceListOptions options) {
                                                                       return promises.resolve(new String[0]);
                                                                   }
                                                               },
                                                               new WorkItemIconCacheImpl(new BPMNStaticResourceContentService(promises)));
        call();
        assertTrue(registry.items().isEmpty());
    }

    @Test
    public void testIconDataUri() {
        final String testData = "testData";
        final String iconDataUri = WorkItemDefinitionStandaloneClientService.iconDataUri("test.png", testData);
        final String badUri = WorkItemDefinitionStandaloneClientService.iconDataUri("bad uri", testData);
        assertEquals("data:image/png;base64, testData", iconDataUri);
        assertEquals(testData, badUri);
    }

    @Test
    public void testIsDataUri() {
        boolean notDataUri = WorkItemDefinitionStandaloneClientService.isIconDataUri("test");
        boolean dataUri = WorkItemDefinitionStandaloneClientService.isIconDataUri("data:test");
        assertFalse(notDataUri);
        assertTrue(dataUri);
    }

    @Test
    public void testDestroy() {
        call();
        assertEquals(6, registry.items().size());
        tested.destroy();
        assertEquals(registry, tested.getRegistry());
        assertTrue(registry.items().isEmpty());
    }

    private void call() {
        tested.init();
        tested.call(mock(Metadata.class));
    }
}
