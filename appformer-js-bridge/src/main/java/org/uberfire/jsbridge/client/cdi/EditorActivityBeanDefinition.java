/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.jsbridge.client.cdi;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;

import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.jsbridge.client.editor.JsWorkbenchEditorActivity;

import static java.util.Arrays.asList;
import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

public class EditorActivityBeanDefinition<T, B extends T> implements SyncBeanDef<JsWorkbenchEditorActivity> {

    private final Supplier<JsWorkbenchEditorActivity> factory;
    private final Set<Class<?>> assignableTypes = new HashSet<>();

    public EditorActivityBeanDefinition(final Supplier<JsWorkbenchEditorActivity> factory) {
        this.factory = factory;
        assignableTypes.add(JsWorkbenchEditorActivity.class);
        assignableTypes.add(WorkbenchEditorActivity.class);
        assignableTypes.add(Activity.class);
    }

    @Override
    public Class<JsWorkbenchEditorActivity> getType() {
        return JsWorkbenchEditorActivity.class;
    }

    @Override
    public Class<?> getBeanClass() {
        return JsWorkbenchEditorActivity.class;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public JsWorkbenchEditorActivity getInstance() {
        return factory.get();
    }

    @Override
    public JsWorkbenchEditorActivity newInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return new HashSet<>(asList(DEFAULT_QUALIFIERS));
    }

    @Override
    public boolean matches(final Set<Annotation> annotations) {
        return QualifierUtil.matches(annotations, getQualifiers());
    }

    @Override
    public String getName() {
        return "JsWorkbenchEditorActivityBean";
    }

    @Override
    public boolean isActivated() {
        return true;
    }

    @Override
    public boolean isAssignableTo(final Class<?> type) {
        return assignableTypes.contains(type);
    }
}
