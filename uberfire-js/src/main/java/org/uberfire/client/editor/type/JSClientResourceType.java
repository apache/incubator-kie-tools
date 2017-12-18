/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.editor.type;

import javax.enterprise.inject.Alternative;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.workbench.category.Category;

@Alternative
public class JSClientResourceType implements ClientResourceType {

    private JSNativeClientResourceType jsNativeClientResourceType;
    private Category category;

    public JSClientResourceType() {

    }

    public JSClientResourceType(JSNativeClientResourceType jsNativeClientResourceType,
                                Category category) {
        this.jsNativeClientResourceType = jsNativeClientResourceType;
        this.category = category;
    }

    @Override
    public String getShortName() {
        return jsNativeClientResourceType.getShortName();
    }

    @Override
    public String getDescription() {
        return jsNativeClientResourceType.getDescription();
    }

    @Override
    public String getPrefix() {
        return jsNativeClientResourceType.getPrefix();
    }

    @Override
    public String getSuffix() {
        return jsNativeClientResourceType.getSuffix();
    }

    @Override
    public int getPriority() {
        return Integer.valueOf(jsNativeClientResourceType.getPriority());
    }

    @Override
    public String getSimpleWildcardPattern() {
        return jsNativeClientResourceType.getSimpleWildcardPattern();
    }

    @Override
    public boolean accept(Path path) {
        return jsNativeClientResourceType.acceptFileName(path.getFileName());
    }

    @Override
    public Category getCategory() {
        return this.category;
    }

    public String getId() {
        return jsNativeClientResourceType.getId();
    }
}
