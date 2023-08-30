/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.api.definition.model.common;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.dmn.api.definition.model.HasDomainObject;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;

/**
 * Utility class to find {@link DomainObject}.
 */
public class DomainObjectSearcherHelper {

    private DomainObjectSearcherHelper() {
        // Private constructor as recommended by SonarCloud
    }

    /**
     * Search in a list of {@link HasDomainObject} for the {@link DomainObject} with the given ID.
     *
     * @param list The list.
     * @param uuid The given UUID.
     * @param <D>  A class that implements {@link HasDomainObject}
     * @return The found {@link DomainObject} or null if none was found.
     */
    public static <D extends HasDomainObject> Optional<DomainObject> find(final List<D> list,
                                                                          final String uuid) {
        for (final D hasDomainObject : list) {
            final Optional<DomainObject> result = hasDomainObject.findDomainObject(uuid);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    /**
     * Get a {@link DomainObject} with the given UUID from a {@link List} of {@link DomainObject}.
     *
     * @param list The list.
     * @param uuid The given UUID.
     * @param <D>  A class that implements {@link DomainObject}
     * @return The found {@link DomainObject} or empty if none was found.
     */
    public static <D extends DomainObject> Optional<DomainObject> getDomainObject(final List<D> list,
                                                                        final String uuid) {
        for (final D domainObject : list) {
            if (Objects.equals(domainObject.getDomainObjectUUID(), uuid)) {
                return Optional.of(domainObject);
            }
        }

        return Optional.empty();
    }

    /**
     * Verifies if the given {@link DomainObject} has the given UUID, doing null check.
     *
     * @param object           The {@link DomainObject}.
     * @param domainObjectUUID The UUID.
     * @return True if it matches false otherwise.
     */
    public static boolean matches(final DomainObject object,
                                  final String domainObjectUUID) {
        if (!Objects.isNull(object)) {
            return Objects.equals(object.getDomainObjectUUID(), domainObjectUUID);
        }
        return false;
    }
}
