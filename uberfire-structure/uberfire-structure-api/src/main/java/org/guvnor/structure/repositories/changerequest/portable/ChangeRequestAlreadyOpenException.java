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

package org.guvnor.structure.repositories.changerequest.portable;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ChangeRequestAlreadyOpenException extends RuntimeException {

    private final Long changeRequestId;

    public ChangeRequestAlreadyOpenException(@MapsTo("changeRequestId") final Long changeRequestId) {
        super("Change request already open with id #" + changeRequestId);
        this.changeRequestId = changeRequestId;
    }

    public Long getChangeRequestId() {
        return changeRequestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChangeRequestAlreadyOpenException that = (ChangeRequestAlreadyOpenException) o;
        return changeRequestId.equals(that.changeRequestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changeRequestId);
    }
}
