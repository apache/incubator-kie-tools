/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.common.services.project.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * An item representing a Package within a Module
 */
@Portable
public class Package {

    private Path moduleRootPath;
    private Path packageMainSrcPath;
    private Path packageTestSrcPath;
    private Path packageMainResourcesPath;
    private Path packageTestResourcesPath;
    private String packageName;
    private String caption;
    private String relativeCaption;

    public Package() {
        //For Errai-marshalling
    }

    public Package(final Path moduleRootPath,
                   final Path packageMainSrcPath,
                   final Path packageTestSrcPath,
                   final Path packageMainResourcesPath,
                   final Path packageTestResourcesPath,
                   final String packageName,
                   final String caption,
                   final String relativeCaption) {
        this.moduleRootPath = checkNotNull("moduleRootPath",
                                           moduleRootPath);
        this.packageMainSrcPath = packageMainSrcPath;
        this.packageTestSrcPath = packageTestSrcPath;
        this.packageMainResourcesPath = packageMainResourcesPath;
        this.packageTestResourcesPath = packageTestResourcesPath;
        this.packageName = checkNotNull("packageName",
                                        packageName);
        this.caption = checkNotNull("caption",
                                    caption);
        this.relativeCaption = checkNotNull("relativeCaption",
                                            relativeCaption);
    }

    public Path getModuleRootPath() {
        return this.moduleRootPath;
    }

    public Path getPackageMainSrcPath() {
        return packageMainSrcPath;
    }

    public Path getPackageTestSrcPath() {
        return packageTestSrcPath;
    }

    public Path getPackageMainResourcesPath() {
        return packageMainResourcesPath;
    }

    public Path getPackageTestResourcesPath() {
        return packageTestResourcesPath;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getRelativeCaption() {
        return relativeCaption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Package)) {
            return false;
        }

        Package aPackage = (Package) o;

        if (!caption.equals(aPackage.caption)) {
            return false;
        }
        if (!packageName.equals(aPackage.packageName)) {
            return false;
        }
        if (!moduleRootPath.equals(aPackage.moduleRootPath)) {
            return false;
        }
        if (packageMainSrcPath != null ? !packageMainSrcPath.equals(aPackage.packageMainSrcPath) : aPackage.packageMainSrcPath != null) {
            return false;
        }
        if (packageTestSrcPath != null ? !packageTestSrcPath.equals(aPackage.packageTestSrcPath) : aPackage.packageTestSrcPath != null) {
            return false;
        }
        if (packageMainResourcesPath != null ? !packageMainResourcesPath.equals(aPackage.packageMainResourcesPath) : aPackage.packageMainResourcesPath != null) {
            return false;
        }
        if (packageTestResourcesPath != null ? !packageTestResourcesPath.equals(aPackage.packageTestResourcesPath) : aPackage.packageTestResourcesPath != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = moduleRootPath.hashCode();
        result = ~~result;
        result = 31 * result + (packageMainSrcPath != null ? packageMainSrcPath.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (packageTestSrcPath != null ? packageTestSrcPath.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (packageMainResourcesPath != null ? packageMainResourcesPath.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (packageTestResourcesPath != null ? packageTestResourcesPath.hashCode() : 0);
        result = ~~result;
        result = 31 * result + packageName.hashCode();
        result = ~~result;
        result = 31 * result + caption.hashCode();
        result = ~~result;
        return result;
    }
}
