/*
 * Copyright 2014 JBoss Inc
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
package org.uberfire.user.management.model;

import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class UserInformation {

    private String userName;
    private Set<String> userRoles;

    public UserInformation() {
        //Errai marshalling
    }

    public UserInformation( final String userName,
                            final Set<String> userRoles ) {
        this.userName = PortablePreconditions.checkNotNull( "userName",
                                                            userName );
        this.userRoles = PortablePreconditions.checkNotNull( "userRoles",
                                                             userRoles );
    }

    public String getUserName() {
        return userName;
    }

    public Set<String> getUserRoles() {
        return userRoles;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        UserInformation that = (UserInformation) o;

        if ( !userName.equals( that.userName ) ) {
            return false;
        }
        if ( !userRoles.equals( that.userRoles ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + userRoles.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserInformation{" +
                "userName='" + userName + "\'" +
                ", userRoles=" + userRoles +
                '}';
    }
}
