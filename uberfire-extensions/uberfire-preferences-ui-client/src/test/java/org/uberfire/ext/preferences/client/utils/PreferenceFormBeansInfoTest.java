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

package org.uberfire.ext.preferences.client.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Named;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.preferences.client.annotations.PreferenceForm;
import org.uberfire.preferences.shared.bean.BasePreference;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PreferenceFormBeansInfoTest {

    @Mock
    private SyncBeanManager syncBeanManager;

    @Mock
    private ActivityBeansCache activityBeansCache;

    private ActivityBeansInfo activityBeansInfo;

    private PreferenceFormBeansInfo preferenceFormBeansInfo;

    @Before
    public void setup() {
        activityBeansInfo = new ActivityBeansInfo(syncBeanManager, activityBeansCache);
        preferenceFormBeansInfo = new PreferenceFormBeansInfo(activityBeansInfo);
        when(syncBeanManager.lookupBeans(WorkbenchScreenActivity.class))
                .thenReturn(generateBeansList());
    }

    @Test
    public void getPreferenceFormForTest() {
        assertEquals("MyPreference1Form",
                     preferenceFormBeansInfo.getPreferenceFormFor("MyPreference1"));
        assertEquals("MyPreference2Form",
                     preferenceFormBeansInfo.getPreferenceFormFor("MyPreference2"));
        assertEquals("MyPreference3Form",
                     preferenceFormBeansInfo.getPreferenceFormFor("MyPreference3"));
    }

    private Collection<SyncBeanDef<WorkbenchScreenActivity>> generateBeansList() {
        Collection<SyncBeanDef<WorkbenchScreenActivity>> beans = new ArrayList<SyncBeanDef<WorkbenchScreenActivity>>();

        beans.add(generateBeanDef(null));
        beans.add(generateBeanDef("MyPreference1"));
        beans.add(generateBeanDef("MyPreference2"));
        beans.add(generateBeanDef("MyPreference3"));

        return beans;
    }

    private SyncBeanDef<WorkbenchScreenActivity> generateBeanDef(final String preferenceIdentifier) {
        return new SyncBeanDef<WorkbenchScreenActivity>() {
            @Override
            public Class<WorkbenchScreenActivity> getType() {
                return null;
            }

            @Override
            public Class<?> getBeanClass() {
                return null;
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return null;
            }

            @Override
            public WorkbenchScreenActivity getInstance() {
                return null;
            }

            @Override
            public WorkbenchScreenActivity newInstance() {
                return null;
            }

            @Override
            public Set<Annotation> getQualifiers() {
                final HashSet<Annotation> annotations = new HashSet<Annotation>();

                if (preferenceIdentifier != null) {
                    annotations.add(new PreferenceForm() {
                        @Override
                        public Class<? extends Annotation> annotationType() {
                            return PreferenceForm.class;
                        }

                        @Override
                        public String value() {
                            return preferenceIdentifier;
                        }
                    });

                    annotations.add(new Named() {
                        @Override
                        public Class<? extends Annotation> annotationType() {
                            return Named.class;
                        }

                        @Override
                        public String value() {
                            return preferenceIdentifier + "Form";
                        }
                    });
                }

                return annotations;
            }

            @Override
            public boolean matches(Set<Annotation> annotations) {
                return false;
            }

            @Override
            public String getName() {
                if (preferenceIdentifier != null) {
                    return preferenceIdentifier + "Form";
                } else {
                    return null;
                }
            }

            @Override
            public boolean isActivated() {
                return false;
            }

            @Override
            public boolean isAssignableTo(Class<?> type) {
                return WorkbenchScreenActivity.class.equals(type);
            }
        };
    }

    class MyPreference1 implements BasePreference<MyPreference1> {

    }

    class MyPreference2 implements BasePreference<MyPreference2> {

    }

    class MyPreference3 implements BasePreference<MyPreference3> {

    }
}
