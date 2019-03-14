/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.shared.whitelist;

import java.util.Collection;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;

@Remote
public interface PackageNameWhiteListService
        extends SupportsRead<WhiteList>,
                SupportsUpdate<WhiteList> {

    WhiteList filterPackageNames(final Module module,
                                 final Collection<String> packageNames);

    void createModuleWhiteList(final Path packageNamesWhiteListPath, String initialContent);
}
