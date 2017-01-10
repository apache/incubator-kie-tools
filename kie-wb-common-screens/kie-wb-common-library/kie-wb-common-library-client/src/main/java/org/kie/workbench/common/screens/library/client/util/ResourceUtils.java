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

package org.kie.workbench.common.screens.library.client.util;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

@Dependent
public class ResourceUtils {

    private Classifier classifier;

    @Inject
    public ResourceUtils( final Classifier classifier ) {
        this.classifier = classifier;
    }

    public String getBaseFileName( final Path path ) {
        final ClientResourceType resourceType = classifier.findResourceType( path );
        final String baseName = Utils.getBaseFileName( path.getFileName(), resourceType.getSuffix() );

        return baseName;
    }
}
