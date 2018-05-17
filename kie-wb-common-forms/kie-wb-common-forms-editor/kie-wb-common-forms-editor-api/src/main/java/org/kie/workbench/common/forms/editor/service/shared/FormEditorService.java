/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.editor.service.shared;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.model.FormModel;
import org.uberfire.backend.vfs.Path;

@Remote
public interface FormEditorService {

    Path createForm(Path path,
                    String formName,
                    FormModel formModel);

    FormModelerContent loadContent(Path path);

    void delete(final Path path,
                final String comment);

    Path save(Path path,
              FormModelerContent content,
              Metadata metadata,
              String comment);

    FormModelerContent rename(Path path,
                              String newFileName,
                              String commitMessage,
                              boolean saveBeforeRenaming,
                              FormModelerContent content,
                              Metadata metadata);

    void copy(Path path,
              String newFileName,
              String commitMessage,
              boolean saveBeforeCopying,
              FormModelerContent content,
              Metadata metadata);
}
