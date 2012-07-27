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

import java.util.Date;

/**
 * A single row of a paged data
 */
public class AdminArchivedPageRow extends AbstractAssetPageRow {

    private String packageName;
    private String lastContributor;
    private Date   lastModified;

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getLastContributor() {
        return lastContributor;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setLastContributor(String lastContributor) {
        this.lastContributor = lastContributor;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
