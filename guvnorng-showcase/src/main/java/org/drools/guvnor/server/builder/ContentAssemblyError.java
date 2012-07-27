/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.builder;

/**
 * This class is used to accumulate error reports for asset.
 * This can then be used to feed back to the user where the problems are.
 */
public class ContentAssemblyError {

    private final String  errorReport;

    private final String  format;
    private final String  name;
    private final String  uuid;
    private final boolean isModuleItem;
    private final boolean isAssetItem;

    public ContentAssemblyError(String errorReport, String format, String name, String uuid, boolean isModuleItem, boolean isAssetItem) {
        this.format = format;
        this.name = name;
        this.uuid = uuid;
        this.isModuleItem = isModuleItem;
        this.isAssetItem = isAssetItem;

        this.errorReport = errorReport;
    }

    public String toString() {
        return this.getErrorReport();
    }

    public String getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public String getUUID() {
        return uuid;
    }

    public boolean isAssetItem() {
        return isAssetItem;
    }

    public boolean isModuleItem() {
        return isModuleItem;
    }

    public String getErrorReport() {
        return errorReport;
    }

}
