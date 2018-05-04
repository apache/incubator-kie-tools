/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.builder.core;

import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.builder.ResourceChangeObservableFile;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;

/**
 * Changes to DRL files containing type-definitions invalidates the DMO cache
 */
@ApplicationScoped
public class ObservableDRLFile implements ResourceChangeObservableFile {

    static final String EXTENSION = "drl";

    //Naive match for type declarations
    private static final String REGEX = "^.*declare\\s.+\\send.*$";
    private static final Pattern PATTERN = Pattern.compile(REGEX,
                                                           Pattern.DOTALL);

    private IOService ioService;

    public ObservableDRLFile() {
        //CDI proxy
    }

    @Inject
    public ObservableDRLFile(final @Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public boolean accept(final Path path) {
        try {
            final String fileName = path.getFileName();
            if (!fileName.endsWith("." + EXTENSION)) {
                return false;
            }
            final String drl = ioService.readAllString(convert(path));
            return PATTERN.matcher(drl.toLowerCase()).matches();
        } catch (NoSuchFileException e) {
            return false;
        }
    }

    org.uberfire.java.nio.file.Path convert(final Path path) {
        return Paths.convert(path);
    }
}
