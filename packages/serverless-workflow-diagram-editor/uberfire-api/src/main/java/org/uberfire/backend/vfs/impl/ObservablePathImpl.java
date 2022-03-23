/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.vfs.impl;

import javax.enterprise.context.Dependent;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.IsVersioned;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;

@Portable
@Dependent
public class ObservablePathImpl implements ObservablePath,
                                           IsVersioned {

    private Path path;
    private transient Path original;

    public ObservablePathImpl() {
    }

    @Override
    public ObservablePath wrap(final Path path) {
        if (path instanceof ObservablePathImpl) {
            this.original = ((ObservablePathImpl) path).path;
        } else {
            this.original = path;
        }
        this.path = this.original;
        return this;
    }

    // Lazy-population of "original" for ObservablePathImpl de-serialized from a serialized PerspectiveDefinition that circumvent the "wrap" feature.
    // Renamed resources hold a reference to the old "original" Path which is needed to maintain an immutable hashCode used as part of the compound
    // Key for Activity and Place Management). However re-hydration stores the PartDefinition in a HashSet using the incorrect hashCode. By not
    // storing the "original" in the serialized form we can guarantee hashCodes in de-serialized PerspectiveDefinitions remain immutable.
    // See https://bugzilla.redhat.com/show_bug.cgi?id=1200472 for the re-producer.
    public Path getOriginal() {
        if (this.original == null) {
            wrap(this.path);
        }
        return this.original;
    }

    @Override
    public String getFileName() {
        return path.getFileName();
    }

    @Override
    public String toURI() {
        return path.toURI();
    }

    @Override
    public boolean hasVersionSupport() {
        return path instanceof IsVersioned && ((IsVersioned) path).hasVersionSupport();
    }

    @Override
    public int compareTo(final Path o) {
        return path.compareTo(o);
    }

    @Override
    public void dispose() {
        if (IOC.getBeanManager() != null) {
            IOC.getBeanManager().destroyBean(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Path)) {
            return false;
        }

        if (o instanceof ObservablePathImpl) {
            return this.getOriginal().equals(((ObservablePathImpl) o).getOriginal());
        }

        return this.getOriginal().equals(o);
    }

    @Override
    public int hashCode() {
        return this.getOriginal().toURI().hashCode();
    }

    @Override
    public String toString() {
        return toURI();
    }
}
