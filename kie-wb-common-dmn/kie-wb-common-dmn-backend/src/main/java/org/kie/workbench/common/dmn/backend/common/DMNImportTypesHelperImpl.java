/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.backend.common;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.marshalling.DMNImportTypesHelper;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DMNImportTypesHelperImpl implements DMNImportTypesHelper {

    @Override
    public boolean isDMN(final Path path) {
        return matchesExtension(path, DMNImportTypes.DMN);
    }

    @Override
    public boolean isPMML(final Path path) {
        return matchesExtension(path, DMNImportTypes.PMML);
    }

    @Override
    public boolean isJava(final Path path) {
        final String fileName = path.getFileName();
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        return fileName.endsWith(".java");
    }

    private boolean matchesExtension(final Path path,
                                     final DMNImportTypes importTypes) {
        final String fileName = path.getFileName();
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        return fileName.endsWith("." + importTypes.getFileExtension());
    }
}
