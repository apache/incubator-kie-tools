/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.event.Event;

import org.assertj.core.api.Assertions;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.util.GWTEditorNativeRegister;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.workbench.category.Category;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceTypeManagerCacheTest {

    public static final String MODEL_CATEGORY = "MODEL";
    public static final String MODEL_TYPE = "java";

    public static final String FORM_CATEGORY = "FORM";
    public static final String FORM_TYPE = "frm";

    public static final String ANY_RESOURCE = "any.resource";

    @Mock
    private SyncBeanManager iocManager;

    @Mock
    private Event<NewPerspectiveEvent> newPerspectiveEventEvent;

    @Mock
    private Event<NewWorkbenchScreenEvent> newWorkbenchScreenEvent;

    @Mock
    private CategoriesManagerCache categoriesManagerCache;

    private ResourceTypeManagerCache resourceTypeManagerCache;

    @Mock
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    @Mock
    private GWTEditorNativeRegister gwtEditorNativeRegister;

    private ActivityBeansCache activityBeansCache;

    private EditorDef defaultEditorDef;
    
    private EditorDef modelEditorDef;

    private EditorDef formEditorDef;

    private List<Class> experimentalTestActivities = new ArrayList<>();

    @Before
    public void setUp() {
        experimentalTestActivities.add(ModelEditorActivity.class);
        experimentalTestActivities.add(FormEditorActivity.class);
        experimentalTestActivities.add(DefaultEditorActivity.class);

        when(experimentalActivitiesAuthorizationManager.authorizeActivityClass(any())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                Class type = (Class) invocationOnMock.getArguments()[0];
                return experimentalTestActivities.contains(type);
            }
        });

        resourceTypeManagerCache = new ResourceTypeManagerCache(categoriesManagerCache);

        activityBeansCache = new ActivityBeansCache(iocManager,
                                                    newPerspectiveEventEvent,
                                                    newWorkbenchScreenEvent,
                                                    resourceTypeManagerCache,
                                                    experimentalActivitiesAuthorizationManager,
                                                    gwtEditorNativeRegister);

        modelEditorDef = registerResourceType(MODEL_CATEGORY, ModelEditorActivity.class, MODEL_TYPE, "1");
        formEditorDef = registerResourceType(FORM_CATEGORY, FormEditorActivity.class, FORM_TYPE, "2");
    }

    private void registerDefaultResourceType() {
        ClientResourceType anyResourceType = mock(ClientResourceType.class);
        when(anyResourceType.accept(any())).thenReturn(true);

        defaultEditorDef = registerResourceType(anyResourceType, DefaultEditorActivity.class, "", "-1", null);
    }
    
    private EditorDef registerResourceType(ClientResourceType resourceType, Class<?> editorActivityClass, String type, String priority, Category category) {
        SyncBeanDef<ClientResourceType> typeBeanDef = mock(SyncBeanDef.class);
        when(typeBeanDef.getInstance()).thenReturn(resourceType);
        Collection<SyncBeanDef> resourceTypeBeans = Arrays.asList(typeBeanDef);

        when(iocManager.lookupBeans(eq(type))).thenReturn(resourceTypeBeans);

        SyncBeanDef editorActivityDef = mock(SyncBeanDef.class);
        when(editorActivityDef.getBeanClass()).thenReturn(editorActivityClass);
        when(editorActivityDef.getName()).thenReturn(type);

        activityBeansCache.addNewEditorActivity(editorActivityDef, priority, type);

        return new EditorDef(editorActivityDef, resourceType, category);
    }

    private EditorDef registerResourceType(String categoryName, Class<?> editorActivityClass, String type, String priority) {
        Category category = mock(Category.class);
        when(category.getName()).thenReturn(categoryName);

        ClientResourceType resourceType = mock(ClientResourceType.class);
        when(resourceType.getCategory()).thenReturn(category);
        when(resourceType.accept(any(Path.class))).thenAnswer((Answer<Boolean>) invocationOnMock -> {
            Path path = (Path) invocationOnMock.getArguments()[0];

            return path.getFileName().endsWith(type);
        });

        return registerResourceType(resourceType, editorActivityClass, type, priority, category);
    }

    @Test
    public void testGetResourceTypeDefinitions() {
        Category process = mock(Category.class);

        when(process.getName()).thenReturn("PROCESS");

        Assertions.assertThat(resourceTypeManagerCache.getResourceTypeDefinitionsByCategory(process))
                .isEmpty();

        Assertions.assertThat(resourceTypeManagerCache.getResourceTypeDefinitionsByCategory(modelEditorDef.getCategory()))
                .hasSize(1)
                .containsExactly(modelEditorDef.getResourceType());

        Assertions.assertThat(resourceTypeManagerCache.getResourceTypeDefinitionsByCategory(formEditorDef.getCategory()))
                .hasSize(1)
                .containsExactly(formEditorDef.getResourceType());
    }

    @Test
    public void testGetUnknownEditorByPathWithoutDefaultEditor() {
        Path path = mock(Path.class);
        when(path.getFileName()).thenReturn(ANY_RESOURCE);

        Assertions.assertThatThrownBy(() -> {
            activityBeansCache.getActivity(path);
        }).isInstanceOf(ActivityBeansCache.EditorResourceTypeNotFound.class);
    }

    @Test
    public void testGetUnknownEditorByPathWithDefaultEditor() {
        registerDefaultResourceType();

        Path path = mock(Path.class);
        when(path.getFileName()).thenReturn(ANY_RESOURCE);

        Assertions.assertThat(activityBeansCache.getActivity(path))
                .isNotNull()
                .isEqualTo(defaultEditorDef.getEditorActivityBeanDef());

    }

    @Test
    public void testGetEditorByPath() {
        Path path = mock(Path.class);

        when(path.getFileName()).thenReturn("any." + MODEL_TYPE);

        Assertions.assertThat(activityBeansCache.getActivity(path))
                .isNotNull()
                .isEqualTo(modelEditorDef.getEditorActivityBeanDef());

        when(path.getFileName()).thenReturn("any." + FORM_TYPE);

        Assertions.assertThat(activityBeansCache.getActivity(path))
                .isNotNull()
                .isEqualTo(formEditorDef.getEditorActivityBeanDef());

    }

    @Test
    public void testGetDefaultEditoFromDisabledExperimentalEditorByPath() {
        Path path = mock(Path.class);

        when(path.getFileName()).thenReturn("any." + FORM_TYPE);

        registerDefaultResourceType();

        // Removing this will make Experimental FormEditorActivity disabled
        experimentalTestActivities.remove(FormEditorActivity.class);

        Assertions.assertThat(activityBeansCache.getActivity(path))
                .isNotNull()
                .isEqualTo(defaultEditorDef.getEditorActivityBeanDef());
    }
    
        
    public class EditorDef {
        private SyncBeanDef editorActivity;
        private ClientResourceType resourceType;
        private Category category;

        public EditorDef(SyncBeanDef editorActivity, ClientResourceType resourceType, Category category) {
            this.editorActivity = editorActivity;
            this.resourceType = resourceType;
            this.category = category;
        }

        public SyncBeanDef getEditorActivityBeanDef() {
            return editorActivity;
        }

        public ClientResourceType getResourceType() {
            return resourceType;
        }

        public Category getCategory() {
            return category;
        }
    }

    private class ModelEditorActivity {}

    private class FormEditorActivity {}

    private class DefaultEditorActivity {}
}