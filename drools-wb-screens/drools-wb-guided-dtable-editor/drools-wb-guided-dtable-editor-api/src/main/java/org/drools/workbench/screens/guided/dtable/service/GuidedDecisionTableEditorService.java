/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.service;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResult;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.source.ViewSourceService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsCreate;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;

@Remote
public interface GuidedDecisionTableEditorService
        extends
        ViewSourceService<GuidedDecisionTable52>,
        ValidationService<GuidedDecisionTable52>,
        SupportsCreate<GuidedDecisionTable52>,
        SupportsRead<GuidedDecisionTable52>,
        SupportsSaveAndRename<GuidedDecisionTable52, Metadata>,
        SupportsDelete,
        SupportsCopy {

    String DTABLE_VERIFICATION_DISABLED = "org.kie.verification.disable-dtable-realtime-verification";

    GuidedDecisionTableEditorContent loadContent(final Path path);

    PackageDataModelOracleBaselinePayload loadDataModel(final Path path);

    Path saveAndUpdateGraphEntries(final Path resource,
                                   final GuidedDecisionTable52 model,
                                   final Metadata metadata,
                                   final String comment);

    XLSConversionResult convert(final Path path);

}
