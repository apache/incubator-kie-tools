/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client;

import org.kie.workbench.common.services.shared.validation.Validator;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

public class FileMenuBuilderMock
        implements FileMenuBuilder {

    @Override
    public Menus build() {
        return null;
    }

    @Override
    public FileMenuBuilder addSave(Command command) {
        return this;
    }

    @Override
    public FileMenuBuilder addDelete(Command command) {
        return this;
    }

    @Override
    public FileMenuBuilder addDelete(Path path) {
        return this;
    }

    @Override
    public FileMenuBuilder addRename(Command command) {
        return this;
    }

    @Override
    public FileMenuBuilder addRename(Path path) {
        return this;
    }

    @Override
    public FileMenuBuilder addRename(Path path, Validator validator) {
        return this;
    }

    @Override
    public FileMenuBuilder addCopy(Command command) {
        return this;
    }

    @Override
    public FileMenuBuilder addCopy(Path path) {
        return this;
    }

    @Override
    public FileMenuBuilder addCopy(Path path, Validator validator) {
        return this;
    }

    @Override
    public FileMenuBuilder addValidate(Command command) {
        return this;
    }

    @Override
    public FileMenuBuilder addRestoreVersion(Path path) {
        return this;
    }

    @Override
    public FileMenuBuilder addCommand(String caption, Command command) {
        return this;
    }

    @Override
    public FileMenuBuilder addNewTopLevelMenu(MenuItem menu) {
        return this;
    }
}
