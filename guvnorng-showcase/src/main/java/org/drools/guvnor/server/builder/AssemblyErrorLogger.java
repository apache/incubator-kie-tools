/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.builder;

import org.drools.repository.AssetItem;
import org.drools.repository.ModuleItem;

import java.util.ArrayList;
import java.util.List;

public class AssemblyErrorLogger {

    private final List<ContentAssemblyError> errors = new ArrayList<ContentAssemblyError>();

    public AssemblyErrorLogger() {
    }

    public void logError(ContentAssemblyError err) {
        errors.add(err);
    }

    public void addError(ModuleItem moduleItem, String errorReport) {
        errors.add(new ContentAssemblyError(
                errorReport, moduleItem.getFormat(), moduleItem.getName(), moduleItem.getUUID(), true, false));
    }

    public void addError(AssetItem assetItem, String errorReport) {
        errors.add(new ContentAssemblyError(
                errorReport, assetItem.getFormat(), assetItem.getName(), assetItem.getUUID(), false, true));
    }

    public void addError(String message, String format, String name, String uuid, boolean isModuleItem, boolean isAssetItem) {
        errors.add(new ContentAssemblyError(
                message,
                format,
                name,
                uuid,
                isModuleItem,
                isAssetItem));
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public List<ContentAssemblyError> getErrors() {
        return errors;
    }
}
