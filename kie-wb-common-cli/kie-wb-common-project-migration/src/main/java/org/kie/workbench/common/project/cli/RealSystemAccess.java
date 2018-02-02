/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.project.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RealSystemAccess implements SystemAccess {

    @Override
    public void exit(int status) {
        System.exit(status);
    }

    @Override
    public Console console() {
        return new Console() {
            final java.io.Console delegate = System.console();
            @Override
            public void format(String fmt, Object... args) {
                delegate.format(fmt, args);
            }

            @Override
            public String readLine(String promptFmt, Object... args) {
                return delegate.readLine(promptFmt, args);
            }
        };
    }

    @Override
    public PrintStream err() {
        return System.err;
    }

    @Override
    public PrintStream out() {
        return System.out;
    }

    @Override
    public void setProperty(String name, String value) {
        System.setProperty(name, value);
    }

    @Override
    public Path move(Path source, Path target, CopyOption... options) throws IOException {
        return Files.move(source, target, options);
    }

    @Override
    public Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        return Files.createDirectory(dir, attrs);
    }

}
