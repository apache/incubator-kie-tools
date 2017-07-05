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
public class DriverDef
        extends Def {

    private String groupId = null;

    private String artifactId = null;

    private String version = null;

    private String driverClass = null;

    public DriverDef() {
    }

    public DriverDef(@MapsTo("uuid") final String uuid,
                     @MapsTo("name") final String name,
                     @MapsTo("groupId") final String groupId,
                     @MapsTo("artifactId") final String artifactId,
                     @MapsTo("version") final String version,
                     @MapsTo("driverClass") final String driverClass) {
        super(uuid,
              name);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.driverClass = driverClass;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

        DriverDef driverDef = (DriverDef) o;

        if (groupId != null ? !groupId.equals(driverDef.groupId) : driverDef.groupId != null) {
            return false;
        }
        if (artifactId != null ? !artifactId.equals(driverDef.artifactId) : driverDef.artifactId != null) {
            return false;
        }
        if (version != null ? !version.equals(driverDef.version) : driverDef.version != null) {
            return false;
        }
        return driverClass != null ? driverClass.equals(driverDef.driverClass) : driverDef.driverClass == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (driverClass != null ? driverClass.hashCode() : 0);
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "DriverDef{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", driverClass='" + driverClass + '\'' +
                '}';
    }
}
