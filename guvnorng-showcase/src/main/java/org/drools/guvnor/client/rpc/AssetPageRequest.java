/*
 * Copyright 2010 JBoss Inc
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

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A request for AssetItem paged data used from the Knowledge Bases Browser
 * 
 * @see AssetPageResponse
 */
public class AssetPageRequest extends PageRequest
        implements
    IsSerializable {

    // Filter properties: null properties are ignored for filtering
    private String       packageUuid        = null;
    private List<String> formatInList       = null;
    private Boolean      formatIsRegistered = null;

    // For GWT serialisation
    public AssetPageRequest() {
    }

    public AssetPageRequest(String packageUuid,
                            List<String> formatInList,
                            Boolean formatIsRegistered,
                            int startRowIndex,
                            Integer pageSize) {
        super( startRowIndex,
               pageSize );
        this.packageUuid = packageUuid;
        this.formatInList = formatInList;
        this.formatIsRegistered = formatIsRegistered;
    }

    public AssetPageRequest(String packageUuid,
                            List<String> formatInList,
                            Boolean formatIsRegistered) {
        super( 0,
               null );
        this.packageUuid = packageUuid;
        this.formatInList = formatInList;
        this.formatIsRegistered = formatIsRegistered;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public List<String> getFormatInList() {
        return formatInList;
    }

    public Boolean getFormatIsRegistered() {
        return formatIsRegistered;
    }

    public String getPackageUuid() {
        return packageUuid;
    }

    public void setFormatInList(List<String> formatInList) {
        this.formatInList = formatInList;
    }

    public void setFormatIsRegistered(Boolean formatIsRegistered) {
        this.formatIsRegistered = formatIsRegistered;
    }

    public void setPackageUuid(String packageUuid) {
        this.packageUuid = packageUuid;
    }

}
