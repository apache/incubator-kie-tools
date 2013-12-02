/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.template.service;

import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.model.GuidedTemplateEditorContent;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.shared.file.SupportsCopy;
import org.guvnor.common.services.shared.file.SupportsCreate;
import org.guvnor.common.services.shared.file.SupportsDelete;
import org.guvnor.common.services.shared.file.SupportsRead;
import org.guvnor.common.services.shared.file.SupportsRename;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.shared.source.ViewSourceService;
import org.uberfire.backend.vfs.Path;

@Remote
public interface GuidedRuleTemplateEditorService
        extends
        BuildValidationHelper,
        ViewSourceService<TemplateModel>,
        ValidationService<TemplateModel>,
        SupportsCreate<TemplateModel>,
        SupportsRead<TemplateModel>,
        SupportsUpdate<TemplateModel>,
        SupportsDelete,
        SupportsCopy,
        SupportsRename {

    GuidedTemplateEditorContent loadContent( final Path path );

}
