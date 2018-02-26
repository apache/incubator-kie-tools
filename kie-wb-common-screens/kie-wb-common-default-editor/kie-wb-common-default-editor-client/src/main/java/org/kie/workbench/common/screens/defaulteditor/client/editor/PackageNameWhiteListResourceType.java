/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.defaulteditor.service.PackageNameWhiteListResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.core.client.resources.CoreImages;
import org.uberfire.workbench.category.Others;

@ApplicationScoped
public class PackageNameWhiteListResourceType extends PackageNameWhiteListResourceTypeDefinition implements ClientResourceType {

    private static final Image IMAGE = new Image(CoreImages.INSTANCE.file());

    public PackageNameWhiteListResourceType() {
    }

    @Inject
    public PackageNameWhiteListResourceType(final Others category) {
        super(category);
    }

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }
}
