/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.workaround;

import java.net.URL;
import java.util.Objects;

import org.jboss.errai.reflections.vfs.SystemDir;
import org.jboss.errai.reflections.vfs.Vfs;

/**
 * This prevents errai's code generation complaining that it does not have a {@link Vfs.UrlType} for
 * file:/x/y/z/target/test-classes. See {@link Vfs.DefaultUrlTypes} directory that expects file:/x/y/z/target/classes
 * to exist. This is not the case if there are no src/main/java classes in the module.
 */
public class TestDirectoryUrlType implements Vfs.UrlType {

    @Override
    public boolean matches(final URL url) {
        return Objects.equals(url.getProtocol(), "file");
    }

    @Override
    public Vfs.Dir createDir(final URL url) {
        return new SystemDir(url);
    }
}
