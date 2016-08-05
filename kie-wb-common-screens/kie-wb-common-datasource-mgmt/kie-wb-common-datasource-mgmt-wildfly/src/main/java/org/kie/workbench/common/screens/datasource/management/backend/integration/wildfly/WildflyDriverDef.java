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
 * This class defines the information that us used by the Wildfly/EAP server to define a data base driver.
 * A database driver my be defined by:
 *
 * 1) just deploying a jar file with the packaged driver. e.g. "h2-1.4.190.jar", "postgresql-9.4.1207.jre7.jar".
 *    In this case all driver's parameters will be automatically established by the server.
 *
 * 2) by defining the driver in the <datasources></datasources> section of the server configuration file.
 *  e.g.
 *  <datasources>
 *      <drivers>
 *          <driver name="h2" module="com.h2database.h2">
 *               <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
 *         </driver>
 *      </drivers>
 *  </datasources>
 *  when the driver is defined in this way it should point a server module that contains the .classes.
 *
 */
public class WildflyDriverDef {

    /**
     * "driver-name": Defines the JDBC driver name. This name will later be used by a given data source as a symbolic
     *  link for referencing the driver.
     *  In case the driver is deployed as jar, the name is the name of deployment unit. (typically the file name)
     *  In cases where the driver is defined in the <datasources> section, then an arbitrary name may have been used.
     */
    String driverName;

    /**
     *  "deployment-name": The name of the deployment unit from which the driver was loaded.
     *  If the driver was deployed as a jar, then deployment-name == driver-name.
     *  In cases where the driver is defined in de <datasources> section then deployment-name == undefined.
     */
    String deploymentName;

    /**
     * "driver-module-name"
     * In cases the driver was installed as an EAP module and not just copying it into the deployments directory.
     * (typically defined in the <datasources> section)
     */
    String driverModuleName;

    /**
     * "module-slot"
     * In cases the driver was installed as an EAP module and a module slot is used.
     * (typically defined in the <datasources> section)
     *
     */
    String moduleSlot;

    /**
     * "major-version": The driver's major version number.
     * Typically automatically established by the server both when the driver is deployed as a jar, or defined in
     * the <datasources> section.
     */
    int mayorVersion;

    /**
     * "minor-version": The driver's minor version number.
     * Typically automatically established by the server both when the driver is deployed as a jar, or defined in
     * the <datasources> section.
     */
    int minorVersion;

    /**
     * "driver-class": The fully qualified class name of the java.sql.Driver implementation. (automatically established
     * by the server both when the driver is deployed as a jar or defined in the <datasources> section)
     */
    String driverClass;

    /**
     * "driver-datasource-class-name": The fully qualified class name of the javax.sql.DataSource implementation.
     * (not listed/established when the driver is deployed as a jar. In this case just has undefined value.)
     * And can be manually set when the driver is defined in the <datasources> section.)
     */
    String dataSourceClass;

    /**
     * "driver-xa-datasource-class-name": The fully qualified class name of the javax.sql.XADataSource implementation
     * when an XA Datasource is defined.
     * (not listed when the driver is deployed as a jar. In this case just has undefined value.) And can be manually set
     * when teh driver is defined in the <datasources> section
     */
    String xaDataSourceClass;

    /**
     * "jdbc-compliant": Whether or not the driver is JDBC compliant.
     * This value is automatically established by the server both when it's deployed as a jar or defined in the
     * <datasources> section.
     */
    boolean jdbcCompliant;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName( String driverName ) {
        this.driverName = driverName;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName( String deploymentName ) {
        this.deploymentName = deploymentName;
    }

    public String getDriverModuleName() {
        return driverModuleName;
    }

    public void setDriverModuleName( String driverModuleName ) {
        this.driverModuleName = driverModuleName;
    }

    public String getModuleSlot() {
        return moduleSlot;
    }

    public void setModuleSlot( String moduleSlot ) {
        this.moduleSlot = moduleSlot;
    }

    public int getMayorVersion() {
        return mayorVersion;
    }

    public void setMayorVersion( int mayorVersion ) {
        this.mayorVersion = mayorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion( int minorVersion ) {
        this.minorVersion = minorVersion;
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

    public String getXaDataSourceClass() {
        return xaDataSourceClass;
    }

    public void setXaDataSourceClass( String xaDataSourceClass ) {
        this.xaDataSourceClass = xaDataSourceClass;
    }

    public boolean isJdbcCompliant() {
        return jdbcCompliant;
    }

    public void setJdbcCompliant( boolean jdbcCompliant ) {
        this.jdbcCompliant = jdbcCompliant;
    }

    @Override
    public String toString() {
        return "WildflyDriverDef{" +
                "driverName='" + driverName + '\'' +
                ", deploymentName='" + deploymentName + '\'' +
                ", driverModuleName='" + driverModuleName + '\'' +
                ", moduleSlot='" + moduleSlot + '\'' +
                ", mayorVersion=" + mayorVersion +
                ", minorVersion=" + minorVersion +
                ", driverClass='" + driverClass + '\'' +
                ", dataSourceClass='" + dataSourceClass + '\'' +
                ", xaDataSourceClass='" + xaDataSourceClass + '\'' +
                ", jdbcCompliant=" + jdbcCompliant +
                '}';
    }
}
