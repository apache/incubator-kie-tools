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


package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.commons.data.Pair;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ActivityBeansCacheUnitTestWrapper extends ActivityBeansCache {


    private String idMock;
    private SyncBeanDef mockDef;
    private Activity activity;
    private Collection<SyncBeanDef<Activity>> availableActivities = new HashSet<SyncBeanDef<Activity>>();
    private Pair<Integer, List<String>> metaInfo;

    public ActivityBeansCacheUnitTestWrapper() {
        mockDef = mock(SyncBeanDef.class);
        idMock = "mockDef1";
        when(mockDef.getName()).thenReturn(idMock);
        availableActivities.add(mockDef);
    }


    @Override
    Collection<SyncBeanDef<Activity>> getAvailableActivities() {
        return availableActivities;
    }

    public SyncBeanDef getMockDef() {
        return mockDef;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getIdMock() {
        return idMock;
    }

    public void duplicateActivity() {
        SyncBeanDef duplicateMockDef = mock(SyncBeanDef.class);
        when(duplicateMockDef.getName()).thenReturn(idMock);
        availableActivities.add(duplicateMockDef);
    }

    public void mockActivityBehaviour() {
        metaInfo = mock(Pair.class);
        when(metaInfo.getK1()).thenReturn(new Integer(1));
        when(metaInfo.getK2()).thenReturn(new ArrayList<String>());
    }
}
