/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DataSourceDef
        extends Def {

    private String connectionURL = null;

    private String driverUuid = null;

    private String user = null;

    private String password = null;

    public DataSourceDef() {
    }

    public DataSourceDef(@MapsTo("uuid") final String uuid,
                         @MapsTo("name") final String name,
                         @MapsTo("connectionURL") final String connectionURL,
                         @MapsTo("driverUuid") final String driverUuid,
                         @MapsTo("user") final String user,
                         @MapsTo("password") final String password) {
        super(uuid,
              name);
        this.connectionURL = connectionURL;
        this.driverUuid = driverUuid;
        this.user = user;
        this.password = password;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public String getDriverUuid() {
        return driverUuid;
    }

    public void setDriverUuid(String driverUuid) {
        this.driverUuid = driverUuid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DataSourceDef{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", connectionURL='" + connectionURL + '\'' +
                ", driverUuid='" + driverUuid + '\'' +
                ", user='******'" +
                ", password='******'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        DataSourceDef that = (DataSourceDef) o;

        if (connectionURL != null ? !connectionURL.equals(that.connectionURL) : that.connectionURL != null) {
            return false;
        }
        if (driverUuid != null ? !driverUuid.equals(that.driverUuid) : that.driverUuid != null) {
            return false;
        }
        if (user != null ? !user.equals(that.user) : that.user != null) {
            return false;
        }
        return password != null ? password.equals(that.password) : that.password == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (connectionURL != null ? connectionURL.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (driverUuid != null ? driverUuid.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = ~~result;
        return result;
    }
}