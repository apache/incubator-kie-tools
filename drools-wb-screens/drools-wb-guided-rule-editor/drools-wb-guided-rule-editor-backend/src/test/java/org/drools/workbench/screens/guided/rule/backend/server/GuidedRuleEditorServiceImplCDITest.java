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

package org.drools.workbench.screens.guided.rule.backend.server;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.CDITestSetup;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class GuidedRuleEditorServiceImplCDITest extends CDITestSetup {

    private static final String NON_EXISTING_PARENT = "Unable to resolve parent rule, please check that both rules are in the same package";

    private GuidedRuleEditorService guidedRuleService;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        guidedRuleService = getReference(GuidedRuleEditorService.class);
    }

    @Test
    public void testValidateRuleThatInherit() throws Exception {
        final String resourcePath = "rules/src/main/resources/guvnor/feature/rules/sendElectionInvitation.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).hasSize(0);
    }

    @Test
    public void testValidateRuleThatInheritNonExistingRule() throws Exception {
        final String resourcePath = "rules/src/main/resources/guvnor/feature/rules/sendElectionInvitationNonExisting.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isNotEmpty();
        messages.forEach(message -> Assertions.assertThat(message.getText()).contains(NON_EXISTING_PARENT));
    }

    private List<ValidationMessage> validateResource(final String resource) throws Exception {
        final Path resourcePath = getPath(resource);
        return guidedRuleService.validate(resourcePath, guidedRuleService.load(resourcePath));
    }

    private Path getPath(String resource) throws URISyntaxException {
        final URL resourceURL = getClass().getResource(resource);
        final org.uberfire.java.nio.file.Path resourceNioPath = fileSystemProvider.getPath(resourceURL.toURI());
        return Paths.convert(resourceNioPath);
    }
}
