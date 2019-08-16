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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.poi.ss.usermodel.Workbook;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.shared.XLSConversionResult;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

public class DecisionTableGuidedToDecisionTableXLSConverter {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private GuidedDecisionTableEditorService guidedDecisionTableEditorService;

    @Inject
    private DecisionTableXLSService decisionTableXLSService;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private SessionInfo sessionInfo;

    public XLSConversionResult convert(final Path originPath) throws IOException {
        final PackageDataModelOracle dmo = dataModelService.getDataModel(originPath);
        final GuidedDecisionTable52 dtable = guidedDecisionTableEditorService.load(originPath);
        final XLSBuilder.BuildResult buildResult = new XLSBuilder(dtable, dmo).build();

        if (buildResult.getConversionResult().isConverted()) {

            final Workbook workbook = buildResult.getWorkbook();

            final ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);

            final ByteArrayInputStream inStream = new ByteArrayInputStream(fileOut.toByteArray());

            decisionTableXLSService.create(getDestinationFilePath(originPath),
                                           inStream,
                                           sessionInfo.getId(),
                                           "Converted from " + originPath.getFileName());

            fileOut.close();
            workbook.close();
            inStream.close();
        }

        return buildResult.getConversionResult();
    }

    private Path getDestinationFilePath(final Path originPath) {
        Path result = Paths.convert(Paths.convert(originPath).getParent().resolve(originPath.getFileName() + " export.xls"));
        int index = 1;

        while (ioService.exists(Paths.convert(result))) {
            result = Paths.convert(Paths.convert(originPath).getParent().resolve(String.format("%s export (%d).xls", originPath.getFileName(), index)));
            index++;
        }

        return result;
    }
}
