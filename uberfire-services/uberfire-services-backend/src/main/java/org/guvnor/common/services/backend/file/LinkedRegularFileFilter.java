/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.backend.file;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

/**
 * A Filter that only accepts "Regular files"
 */
public class LinkedRegularFileFilter implements LinkedFilter {

    private LinkedFilter next = null;

    public LinkedRegularFileFilter() {
    }

    /**
     * Constructor that automatically chains the next filter
     *
     * @param filter
     */
    public LinkedRegularFileFilter(final LinkedFilter filter) {
        setNextFilter(PortablePreconditions.checkNotNull("filter",
                                                         filter));
    }

    @Override
    public boolean accept(final Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }
        if (next != null) {
            return next.accept(path);
        }
        return true;
    }

    @Override
    public void setNextFilter(final LinkedFilter filter) {
        this.next = filter;
    }
}
