/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.CDITestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class GuidedDecisionTableEditorServiceImplCDITest extends CDITestSetup {

    private GuidedDecisionTableEditorService testedService;
    private GuidedDecisionTableEditorService testedServiceWithDifferentTimeZone;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        testedService = getReference(GuidedDecisionTableEditorService.class);
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
    }

    @Test
    public void testFromAccumulate() throws Exception {
        final Path path = getPath("rhpam-issues/src/main/resources/com/myspace/rhpam_issues/applicants-rhpam-1288.gdst");
        final List<ValidationMessage> validationMessages = testedService.validate(path, testedService.load(path));
        Assertions.assertThat(validationMessages).isEmpty();
    }

    @Test
    public void testFunctionFromDrl() throws Exception {
        final Path path = getPath("rhba370/src/main/resources/com/sample/dtissuesampleproject/UseFunctionFromDrl.gdst");
        final List<ValidationMessage> validationMessages = testedService.validate(path, testedService.load(path));
        Assertions.assertThat(validationMessages).isEmpty();
    }

    @Test
    public void testTimeInTimeZones() throws Exception {
        final String timeZoneKey = "user.timezone";
        final String timeZoneToPreserve = System.getProperty(timeZoneKey);

        try {
            final Path path = getPath("rhpam-issues/src/main/resources/com/myspace/rhpam_issues/time-rhdm-693.gdst");

            final GuidedDecisionTable52 modelInFirstTimeZone = testedService.load(path);
            final Date dateInFirstTimeZone = modelInFirstTimeZone.getData().get(0).get(2).getDateValue();

            System.setProperty(timeZoneKey, "Europe/Moscow");

            testedServiceWithDifferentTimeZone = getReference(GuidedDecisionTableEditorService.class);
            final GuidedDecisionTable52 modelInSecondTimeZone = testedServiceWithDifferentTimeZone.load(path);
            final Date dateInSecondTimeZone = modelInSecondTimeZone.getData().get(0).get(2).getDateValue();

            Assertions.assertThat(dateInFirstTimeZone).isEqualTo(dateInSecondTimeZone);
        } finally {
            System.setProperty(timeZoneKey, timeZoneToPreserve);
        }
    }

    private Path getPath(String resource) throws URISyntaxException {
        final URL resourceURL = getClass().getResource(resource);
        final org.uberfire.java.nio.file.Path resourceNioPath = fileSystemProvider.getPath(resourceURL.toURI());
        return Paths.convert(resourceNioPath);
    }
}