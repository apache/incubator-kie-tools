/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.perspective.generator;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorActivity;
import org.uberfire.ext.plugin.client.perspective.editor.generator.PerspectiveEditorScreenActivity;
import org.uberfire.jsbridge.client.cdi.SingletonBeanDefinition;

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

@ApplicationScoped
public class RuntimePerspectiveGenerator {

    @Inject
    private SyncBeanManager beanManager;

    @Inject
    private ActivityBeansCache activityBeansCache;

    @Inject
    private LayoutGenerator layoutGenerator;

    public PerspectiveEditorActivity generatePerspective(LayoutTemplate layoutTemplate) {
        PerspectiveEditorScreenActivity screen = createNewScreen(layoutTemplate);
        return createNewPerspective(layoutTemplate, screen);
    }

    private PerspectiveEditorScreenActivity createNewScreen(LayoutTemplate perspective) {
        PerspectiveEditorScreenActivity activity = new PerspectiveEditorScreenActivity(perspective,
                                                                                       layoutGenerator);

        final Set<Annotation> qualifiers = new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS));
        final SingletonBeanDefinition<PerspectiveEditorScreenActivity, PerspectiveEditorScreenActivity> beanDef =
                new SingletonBeanDefinition<>(
                                              activity,
                                              PerspectiveEditorScreenActivity.class,
                                              qualifiers,
                                              activity.getIdentifier(),
                                              true,
                                              WorkbenchScreenActivity.class,
                                              Activity.class);

        beanManager.registerBean(beanDef);
        beanManager.registerBeanTypeAlias(beanDef,
                                          Activity.class);
        beanManager.registerBeanTypeAlias(beanDef,
                                          WorkbenchScreenActivity.class);
        String activityID = activity.getIdentifier();
        activityBeansCache.removeActivity(activityID);
        activityBeansCache.addNewScreenActivity(beanManager.lookupBeans(activityID).iterator().next());
        return activity;
    }

    private PerspectiveEditorActivity createNewPerspective(LayoutTemplate perspective,
                                                           PerspectiveEditorScreenActivity screen) {
        final PerspectiveEditorActivity activity = new RuntimePerspectiveEditorActivity(perspective,
                                                                                        screen);

        String perspectiveName = perspective.getName();
        beanManager.registerBean(new SingletonBeanDefinition<>(activity,
                                                               PerspectiveActivity.class,
                                                               new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS)),
                                                               perspectiveName,
                                                               true));
        activityBeansCache.removeActivity(perspectiveName);
        activityBeansCache.addNewPerspectiveActivity(beanManager.lookupBeans(perspectiveName).iterator().next());
        return activity;
    }

}