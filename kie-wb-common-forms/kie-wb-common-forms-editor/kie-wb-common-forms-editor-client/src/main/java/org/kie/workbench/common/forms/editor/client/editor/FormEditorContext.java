/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.editor;

import javax.inject.Singleton;

import org.jboss.errai.ioc.client.container.IOC;

@Singleton
public class FormEditorContext {

    private static FormEditorContext instance;

    static {
        instance = IOC.getBeanManager().lookupBean(FormEditorContext.class).getInstance();
    }

    public static FormEditorContext get() {
        return instance;
    }

    private FormEditorHelper activeEditorHelper;

    public void setActiveEditorHelper(FormEditorHelper activeEditorHelper) {
        this.activeEditorHelper = activeEditorHelper;
    }

    public FormEditorHelper getActiveEditorHelper() {
        return activeEditorHelper;
    }
}
