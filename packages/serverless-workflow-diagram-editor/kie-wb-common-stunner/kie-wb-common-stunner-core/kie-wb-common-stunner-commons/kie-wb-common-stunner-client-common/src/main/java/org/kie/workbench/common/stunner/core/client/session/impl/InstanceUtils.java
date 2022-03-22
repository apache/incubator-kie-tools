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
 */

package org.kie.workbench.common.stunner.core.client.session.impl;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;

public class InstanceUtils {

    @SuppressWarnings("unchecked")
    public static <T> T lookup(final ManagedInstance instance,
                               final Class<T> type,
                               final Annotation... qualifier) {
        final ManagedInstance<T> i = instance.select(type, qualifier);
        return i.isUnsatisfied() ?
                (T) instance.select(type, DefinitionManager.DEFAULT_QUALIFIER).get() :
                i.get();
    }

    public static <T> T lookup(final ManagedInstance<T> instance,
                               final Annotation qualifier) {
        final ManagedInstance<T> i = instance.select(qualifier);
        return i.isUnsatisfied() ?
                instance.select(DefinitionManager.DEFAULT_QUALIFIER).get() :
                i.get();
    }

    public static <T> void destroyAll(final List<ManagedInstance<T>> controlInstances,
                                      final List<T> controls,
                                      final Consumer<T> destroyer) {
        int i = 0;
        for (T control : controls) {
            destroyer.accept(control);
            controlInstances.get(i).destroy(control);
            controlInstances.get(i).destroyAll();
            i++;
        }
        controlInstances.clear();
        controls.clear();
    }

    public static <T> void destroy(final ManagedInstance<T> controlInstance,
                                   final T control,
                                   final Consumer<T> destroyer) {
        destroyer.accept(control);
        controlInstance.destroy(control);
    }
}
