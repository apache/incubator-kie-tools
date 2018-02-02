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

package org.kie.workbench.common.screens.datasource.management.model;

import org.guvnor.common.services.project.model.Module;

public abstract class DefEditorContent<C extends Def> {

    protected C def;

    protected Module module;

    public DefEditorContent() {
    }

    public C getDef() {
        return def;
    }

    public void setDef(final C def) {
        this.def = def;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(final Module module) {
        this.module = module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DefEditorContent<?> that = (DefEditorContent<?>) o;

        return def != null ? def.equals(that.def) : that.def == null;
    }

    @Override
    public int hashCode() {
        return def != null ? def.hashCode() : 0;
    }
}
