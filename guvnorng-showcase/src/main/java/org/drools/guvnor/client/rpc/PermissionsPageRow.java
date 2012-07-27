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

import java.util.List;

/**
 * A single row of User Permissions
 */
public class PermissionsPageRow extends AbstractPageRow {

    private String       userName;
    private List<String> userPermissions;

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getUserName() {
        return userName;
    }

    public List<String> getUserPermissions() {
        return userPermissions;
    }

    public boolean isAdministrator() {
        return isAdministrator( this.userPermissions );
    }

    public boolean hasCategoryPermissions() {
        return hasCategoryPermissions( this.userPermissions );
    }

    public boolean hasPackagePermissions() {
        return hasPackagePermissions( this.userPermissions );
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPermissions(List<String> userPermissions) {
        this.userPermissions = userPermissions;
    }

    private boolean hasCategoryPermissions(List<String> permTypes) {
        for ( String s : permTypes ) {
            if ( s.startsWith( "analyst" ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPackagePermissions(List<String> permTypes) {
        for ( String s : permTypes ) {
            if ( s.startsWith( "package" ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdministrator(List<String> permTypes) {
        if ( permTypes.contains( "admin" ) ) {
            return true;
        } else {
            return false;
        }
    }

}
