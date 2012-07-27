/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is passed back to the client to give the UI some context information on
 * what to display and not display.
 */
public class UserSecurityContext
    implements
    IsSerializable {

    private String  userName;

    private boolean isAdministrator;

    public UserSecurityContext() {
    }

    public UserSecurityContext(final String userName,
                               final boolean isAdministrator) {
        this.userName = userName;
        this.isAdministrator = isAdministrator;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isAdministrator() {
        return this.isAdministrator;
    }

}
