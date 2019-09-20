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
import org.uberfire.commons.clusterapi.Clustered;

@Portable
@Clustered
public class ChangeRequestStatusUpdatedEvent {

    private final String repositoryId;
    private final Long changeRequestId;
    private final ChangeRequestStatus oldStatus;
    private final ChangeRequestStatus newStatus;
    private final String userId;

    public ChangeRequestStatusUpdatedEvent(@MapsTo("repositoryId") final String repositoryId,
                                           @MapsTo("changeRequestId") final Long changeRequestId,
                                           @MapsTo("oldStatus") final ChangeRequestStatus oldStatus,
                                           @MapsTo("newStatus") final ChangeRequestStatus newStatus,
                                           @MapsTo("userId") final String userId) {
        this.repositoryId = repositoryId;
        this.changeRequestId = changeRequestId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.userId = userId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public Long getChangeRequestId() {
        return changeRequestId;
    }

    public ChangeRequestStatus getOldStatus() {
        return oldStatus;
    }

    public ChangeRequestStatus getNewStatus() {
        return newStatus;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChangeRequestStatusUpdatedEvent that = (ChangeRequestStatusUpdatedEvent) o;
        return repositoryId.equals(that.repositoryId) &&
                changeRequestId.equals(that.changeRequestId) &&
                oldStatus == that.oldStatus &&
                newStatus == that.newStatus &&
                userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repositoryId,
                            changeRequestId,
                            oldStatus,
                            newStatus,
                            userId);
    }
}
