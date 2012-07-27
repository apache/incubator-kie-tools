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
 * A Page of Snapshot comparison results for display in a PagedTable
 */
public class SnapshotComparisonPageResponse extends PageResponse<SnapshotComparisonPageRow> {

    private String leftSnapshotName;
    private String rightSnapshotName;

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getLeftSnapshotName() {
        return leftSnapshotName;
    }

    public String getRightSnapshotName() {
        return rightSnapshotName;
    }

    public void setLeftSnapshotName(String leftSnapshotName) {
        this.leftSnapshotName = leftSnapshotName;
    }

    public void setRightSnapshotName(String rightSnapshotName) {
        this.rightSnapshotName = rightSnapshotName;
    }

}
