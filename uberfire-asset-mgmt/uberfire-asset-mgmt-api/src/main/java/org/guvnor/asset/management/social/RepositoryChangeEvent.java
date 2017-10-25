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

package org.guvnor.asset.management.social;

public class RepositoryChangeEvent extends AssetManagementEvent {

    public enum ChangeType {
        VERSION_CHANGED
    }

    ;

    private ChangeType changeType;

    public RepositoryChangeEvent(String processName,
                                 String repositoryAlias,
                                 String rootURI,
                                 String user,
                                 Long timestamp,
                                 ChangeType changeType) {
        super(processName,
              repositoryAlias,
              rootURI,
              user,
              timestamp);
        this.changeType = changeType;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }
}
