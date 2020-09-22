/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.shared.project;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class PackageItem {

    public static final String DEFAULT_PACKAGE_NAME = "<default>";

    private String packageName;
    private String caption;

    public PackageItem() {
        //For Errai-marshalling
    }

    public PackageItem(final String packageName,
                       final String caption) {
        this.packageName = checkNotNull("packageName",
                                        packageName);
        this.caption = checkNotNull("caption",
                                    caption);
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getCaption() {
        return this.caption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PackageItem)) {
            return false;
        }

        PackageItem aPackage = (PackageItem) o;

        if (!caption.equals(aPackage.caption)) {
            return false;
        }

        return packageName.equals(aPackage.packageName) && caption.equals(aPackage.caption);
    }

    @Override
    public int hashCode() {
        int result = packageName.hashCode();
        result = ~~result;
        result = 31 * result + caption.hashCode();
        result = ~~result;
        return result;
    }
}
