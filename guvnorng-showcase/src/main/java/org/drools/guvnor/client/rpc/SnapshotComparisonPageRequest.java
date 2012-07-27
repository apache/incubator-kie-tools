/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.rpc;

/**
 * A request for Snapshot comparison differences.
 */
public class SnapshotComparisonPageRequest extends PageRequest {

    private String packageName;
    private String firstSnapshotName;
    private String secondSnapshotName;

    // For GWT serialisation
    public SnapshotComparisonPageRequest() {
    }

    public SnapshotComparisonPageRequest(String packageName,
                                         String firstSnapshotName,
                                         String secondSnapshotName,
                                         int startRowIndex,
                                         Integer pageSize) {
        super( startRowIndex,
               pageSize );
        this.packageName = packageName;
        this.firstSnapshotName = firstSnapshotName;
        this.secondSnapshotName = secondSnapshotName;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getFirstSnapshotName() {
        return firstSnapshotName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSecondSnapshotName() {
        return secondSnapshotName;
    }

    public void setFirstSnapshotName(String firstSnapshotName) {
        this.firstSnapshotName = firstSnapshotName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setSecondSnapshotName(String secondSnapshotName) {
        this.secondSnapshotName = secondSnapshotName;
    }

}
