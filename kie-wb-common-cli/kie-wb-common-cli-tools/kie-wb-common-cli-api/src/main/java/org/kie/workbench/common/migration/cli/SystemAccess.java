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

package org.kie.workbench.common.migration.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public interface SystemAccess {
    /**
     * @return This method never returns normally. Either the JVM exits, or an exception is thrown. This return value,
     *         is just a convenience.
     * @throws HaltingException Thrown for implementations that do not acutally exit the JVM so that execution is interrupted.
     */
    <T> T exit(int status) throws HaltingException;
    Console console();
    PrintStream err();
    PrintStream out();
    void setProperty(String name, String value);
    Path move(Path source, Path target, CopyOption... options) throws IOException;
    Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException;
    Path createTemporaryDirectory(String prefix, FileAttribute<?>... attrs) throws IOException;
    Path currentWorkingDirectory();
    void recursiveDelete(Path tmpNiogit) throws IOException;
    void copyDirectory(Path source, Path target) throws IOException;

    interface Console {
        void format(String fmt, Object... args);
        String readLine(String promptFmt, Object... args);
    }

    class HaltingException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final int status;

        public HaltingException(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
