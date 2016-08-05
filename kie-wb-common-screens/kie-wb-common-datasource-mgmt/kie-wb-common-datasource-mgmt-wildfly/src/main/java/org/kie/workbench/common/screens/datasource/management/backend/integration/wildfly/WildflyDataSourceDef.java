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

package org.kie.workbench.common.screens.datasource.management.backend.integration.wildfly;

/**
 * This class defines the information used by the Wildfly/EAP server to define a data source.
 */
public class WildflyDataSourceDef {

    /*
     * "name": (required) The data source name must be unique.
     */
    String name;

    /**
     * "jndi-name":  (required) Specifies the JNDI name for the datasource.
     */
    String jndi;

    /**
     * "connection-url": (required) The JDBC driver connection URL.
     */
    String connectionURL;

    /**
     * "driver-class" The fully qualified name of the JDBC driver class. (seems to be mandatory en EAP 6.4.6)
     */
    String driverClass;

    /**
     * "datasource-class": The fully qualified name of the JDBC datasource class.
     */
    String dataSourceClass;

    /**
     * "driver-name" :Defines the JDBC driver the datasource should use.
     * It is a symbolic name matching the the name of installed driver. In case the driver is deployed as jar, the
     * name is the name of deployment unit. (see WildflyDriverDef).
     */
    String driverName;

    /**
     * "user-name": Specify the user name used when creating a new connection.
     */
    String user;

    /**
     * "password": Specifies the password used when creating a new connection.
     */
    String password;

    /**
     * "jta": Enable JTA integration
     */
    boolean useJTA;

    /**
     * "use-ccm": Enable the use of a cached connection manager
     */
    boolean useCCM;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getJndi() {
        return jndi;
    }

    public void setJndi( String jndi ) {
        this.jndi = jndi;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL( String connectionURL ) {
        this.connectionURL = connectionURL;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass( String driverClass ) {
        this.driverClass = driverClass;
    }

    public String getDataSourceClass() {
        return dataSourceClass;
    }

    public void setDataSourceClass( String dataSourceClass ) {
        this.dataSourceClass = dataSourceClass;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName( String driverName ) {
        this.driverName = driverName;
    }

    public String getUser() {
        return user;
    }

    public void setUser( String user ) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public boolean isUseJTA() {
        return useJTA;
    }

    public void setUseJTA( boolean useJTA ) {
        this.useJTA = useJTA;
    }

    public boolean isUseCCM() {
        return useCCM;
    }

    public void setUseCCM( boolean useCCM ) {
        this.useCCM = useCCM;
    }

    @Override
    public String toString() {
        return "WildflyDataSourceDef{" +
                "name='" + name + '\'' +
                ", jndi='" + jndi + '\'' +
                ", connectionURL='" + connectionURL + '\'' +
                ", driverClass='" + driverClass + '\'' +
                ", dataSourceClass='" + dataSourceClass + '\'' +
                ", driverName='" + driverName + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", useJTA=" + useJTA +
                ", useCCM=" + useCCM +
                '}';
    }
}
