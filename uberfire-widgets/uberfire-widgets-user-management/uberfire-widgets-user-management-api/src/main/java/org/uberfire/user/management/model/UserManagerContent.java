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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Data Transfer Object for the content of the UI
 */
@Portable
public class UserManagerContent {

    private List<UserInformation> userInformation;
    private UserManagerCapabilities capabilities;

    public UserManagerContent() {
        //Errai marshalling
    }

    /**
     * Constructor
     * @param userInformation Information for all Users. Cannot be null.
     * @param capabilities Capabilities supported by the User Manager. Cannot be null.
     */
    public UserManagerContent( final List<UserInformation> userInformation,
                               final UserManagerCapabilities capabilities ) {
        this.userInformation = PortablePreconditions.checkNotNull( "userInformation",
                                                                   userInformation );
        this.capabilities = PortablePreconditions.checkNotNull( "capabilities",
                                                                capabilities );
    }

    /**
     * @return Information for all Users. Will not be null.
     */
    public List<UserInformation> getUserInformation() {
        return userInformation;
    }

    /**
     * @return The capabilities supported by the User Manager
     */
    public UserManagerCapabilities getCapabilities() {
        return capabilities;
    }
}
