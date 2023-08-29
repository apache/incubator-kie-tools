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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientUtils.getDefaultIconData;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class WorkItemDefinitionStandaloneClientServiceTest {

    private WorkItemDefinitionStandaloneClientService tested;
    private Promises promises;
    private WorkItemDefinitionCacheRegistry registry;

    @Before
    public void setUp() {
        promises = new SyncPromises();
        registry = new WorkItemDefinitionCacheRegistry();
        tested = spy(new WorkItemDefinitionStandaloneClientService(promises,
                                                                   registry,
                                                                   new BPMNStaticResourceContentService(promises),
                                                                   new WorkItemIconCacheImpl(new BPMNStaticResourceContentService(promises))));
    }

    @Test
    public void testGetRegistry() {
        tested.init();
        assertEquals(registry, tested.getRegistry());
    }

    @Test
    public void testLoadAllWorkItems() {
        when(tested.getPresetAsText()).thenReturn(
                "[\n" +
                        "   [\n" +
                        "    \"name\" : \"Milestone\",\n" +
                        "    \"parameters\" : [\n" +
                        "        \"Condition\" : new StringDataType()\n" +
                        "    ],\n" +
                        "    \"displayName\" : \"Milestone\",\n" +
                        "    \"icon\" : \"defaultmilestoneicon.png\",\n" +
                        "    \"category\" : \"Milestone\"\n" +
                        "    ]\n" +
                        "]"
        );
        when(tested.getMilestoneIconAsBase64()).thenReturn(BPMNStaticResourceContentService.MILESTONE_SERVICE_TASK_DATA_URI);
        call();

        List<WorkItemDefinition> items = new ArrayList<>(registry.items());
        System.out.println(items.size());
        assertEquals(6, items.size());

        WorkItemDefinition email = items.get(0);
        assertNotNull(email);
        assertEquals("Email", email.getName());
        assertEquals(BPMNStaticResourceContentService.EMAIL_SERVICE_TASK_DATA_URI, email.getIconDefinition().getIconData());

        WorkItemDefinition log = items.get(1);
        assertNotNull(log);
        assertEquals("Log", log.getName());
        assertEquals(BPMNStaticResourceContentService.LOG_SERVICE_TASK_DATA_URI, log.getIconDefinition().getIconData());

        WorkItemDefinition milestone = items.get(2);
        assertNotNull(milestone);
        assertEquals("Milestone", milestone.getName());
        assertEquals(BPMNStaticResourceContentService.MILESTONE_SERVICE_TASK_DATA_URI,
                     milestone.getIconDefinition().getIconData());
        assertEquals("defaultmilestoneicon.png",
                     milestone.getIconDefinition().getUri());

        WorkItemDefinition decisionTask = items.get(3);
        assertNotNull(decisionTask);
        assertEquals("DecisionTask", decisionTask.getName());
        assertEquals(getDefaultIconData(), decisionTask.getIconDefinition().getIconData());

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
    public void testNoWorkItemsLoadPreset() {
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
        assertEquals(0, registry.items().size());
    }

    @Test
    public void testEmptyWorkItemsLoadPreset() {
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
        assertEquals(0, registry.items().size());
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
    public void testGetPresetIcon() {
        WorkItemDefinitionStandaloneClientService service = new WorkItemDefinitionStandaloneClientService(promises,
                                                                                                          registry,
                                                                                                          new BPMNStaticResourceContentService(promises),
                                                                                                          new WorkItemIconCacheImpl(new BPMNStaticResourceContentService(promises)));
        assertEquals("getMillestoneImage",
                     service.getPresetIcon("defaultmilestoneicon.png"));
        assertEquals("",
                     service.getPresetIcon("someicon.png"));
    }

    @Test
    public void testDestroy() {
        call();
        assertEquals(5, registry.items().size());
        tested.destroy();
        assertEquals(registry, tested.getRegistry());
        assertTrue(registry.items().isEmpty());
    }

    private void call() {
        tested.init();
        tested.call(mock(Metadata.class));
    }
}
