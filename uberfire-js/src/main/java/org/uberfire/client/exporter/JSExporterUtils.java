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

package org.uberfire.client.exporter;

import java.util.Iterator;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.Activity;

public class JSExporterUtils {

    public static <T extends Activity> T findActivityIfExists(final SyncBeanManager beanManager,
                                                              String id,
                                                              Class<T> activityType) {
        Iterator<SyncBeanDef> existentActivities = beanManager.lookupBeans(id).iterator();
        while (existentActivities.hasNext()) {
            Object existentActivity = existentActivities.next().getInstance();
            if (activityType.equals(existentActivity.getClass())) {
                return (T) existentActivity;
            }
        }

        return null;
    }
}
