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

package org.uberfire.client.mvp;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.category.Undefined;

import static org.mockito.Mockito.*;


public class ActivityBeansCacheUnitTestWrapper extends ActivityBeansCache {


    @IsSplashScreen
    @ApplicationScoped
    private static class SplashScreenForTesting {

    }

    @IsClientEditor
    private static class ClientEditor {

    }

    private String idMock;
    private SyncBeanDef mockDef;
    private Activity activity;
    private Collection<SyncBeanDef<Activity>> availableActivities = new HashSet<SyncBeanDef<Activity>>();
    private Pair<Integer, List<String>> metaInfo;

    public ActivityBeansCacheUnitTestWrapper() {
        this.resourceTypeManagerCache = new ResourceTypeManagerCache(new CategoriesManagerCache(new Undefined()));
        mockDef = mock(SyncBeanDef.class);
        idMock = "mockDef1";
        when(mockDef.getName()).thenReturn(idMock);
        when(mockDef.getBeanClass()).thenReturn(this.getClass());
        availableActivities.add(mockDef);
    }

    @Override
    void registerGwtEditorProvider() {
        //do nothing
    }

    @Override
    void registerGwtClientBean(String id, SyncBeanDef<Activity> activityBean) {
        //do nothing
    }

    public void mockSplashScreenBehaviour() {
        Set<Annotation> annotations = new HashSet<Annotation>(Arrays.asList(SplashScreenForTesting.class.getAnnotations()));
        when(mockDef.getQualifiers()).thenReturn(annotations);

        activity = mock(AbstractSplashScreenActivity.class);
        when(mockDef.getInstance()).thenReturn(activity);
    }

    public void mockClientEditorBehaviour() {
        Set<Annotation> annotations = new HashSet<Annotation>(Arrays.asList(ClientEditor.class.getAnnotations()));
        when(mockDef.getQualifiers()).thenReturn(annotations);

        activity = mock(WorkbenchClientEditorActivity.class);
        when(mockDef.getInstance()).thenReturn(activity);
        when(activity.getIdentifier()).thenReturn(idMock);
    }

    public void createActivitiesAndMetaInfo(int priority1,
                                            int priority2) {
        resourceTypeManagerCache.addResourceActivity(new ActivityAndMetaInfo(null,
                                                                             null,
                                                                             priority1,
                                                                             new ArrayList()));
        resourceTypeManagerCache.addResourceActivity(new ActivityAndMetaInfo(null,
                                                                             null,
                                                                             priority2,
                                                                             new ArrayList()));
    }

    public ResourceTypeManagerCache getResourceTypeManagerCache() {
        return this.resourceTypeManagerCache;
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

    @Override
    Pair<Integer, List<String>> generateActivityMetaInfo(SyncBeanDef<Activity> activityBean) {
        return metaInfo;
    }

    public void mockActivityBehaviour() {
        metaInfo = mock(Pair.class);
        when(metaInfo.getK1()).thenReturn(new Integer(1));
        when(metaInfo.getK2()).thenReturn(new ArrayList<String>());
    }
}
