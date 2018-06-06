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
import org.drools.core.base.evaluators.Operator;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.model.GuidedEditorContent;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.test.CDITestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

public class GuidedRuleEditorServiceImplCDITest extends CDITestSetup {

    private static final String RULES_ROOT = "rules/src/main/resources/guvnor/feature/rules/";
    private static final String NON_EXISTING_PARENT = "Unable to resolve parent rule, please check that both rules are in the same package";

    private GuidedRuleEditorService guidedRuleService;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        guidedRuleService = getReference(GuidedRuleEditorService.class);
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
    }

    @Test
    public void testValidateRuleThatInherit() throws Exception {
        final String resourcePath = RULES_ROOT + "sendElectionInvitation.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).hasSize(0);
    }

    @Test
    public void testValidateRuleThatInheritNonExistingRule() throws Exception {
        final String resourcePath = RULES_ROOT + "sendElectionInvitationNonExisting.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isNotEmpty();
        messages.forEach(message -> Assertions.assertThat(message.getText()).contains(NON_EXISTING_PARENT));
    }

    @Test
    public void testValidateRuleAlphabeticallyComparesStrings() throws Exception {
        final String resourcePath = RULES_ROOT + "nameOrderingRule.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isEmpty();
    }

    @Test
    public void testAbbreviatedCondition() throws Exception {
        final String resourcePath = RULES_ROOT + "matchPeopleAbbreviatedCondition.rdrl";
        final GuidedEditorContent content = guidedRuleService.loadContent(getPath(resourcePath));
        final RuleModel model = content.getModel();
        Assertions.assertThat(model.lhs.length).isEqualTo(1);
        final FactPattern pattern = (FactPattern) model.lhs[0];
        final SingleFieldConstraint fieldConstraintOne = (SingleFieldConstraint) pattern.getConstraint(0);
        Assertions.assertThat(fieldConstraintOne.getConnectives()).hasSize(2);
        final ConnectiveConstraint fieldConstraintTwo = fieldConstraintOne.getConnectives()[0];
        final ConnectiveConstraint fieldConstraintThree = fieldConstraintOne.getConnectives()[1];

        Assertions.assertThat(fieldConstraintOne.getFieldName()).isEqualTo("age");
        Assertions.assertThat(fieldConstraintOne.getOperator()).isEqualTo(Operator.NOT_EQUAL.getOperatorString());
        Assertions.assertThat(fieldConstraintOne.getValue()).isEqualTo("18");

        Assertions.assertThat(fieldConstraintTwo.getFieldName()).isEqualTo("age");
        Assertions.assertThat(fieldConstraintTwo.getOperator()).isEqualTo("&& " + Operator.LESS.getOperatorString());
        Assertions.assertThat(fieldConstraintTwo.getValue()).isEqualTo("25");

        Assertions.assertThat(fieldConstraintThree.getFieldName()).isEqualTo("age");
        Assertions.assertThat(fieldConstraintThree.getOperator()).isEqualTo("&& " + Operator.GREATER.getOperatorString());
        Assertions.assertThat(fieldConstraintThree.getValue()).isEqualTo("15");

        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isEmpty();
    }

    @Test
    public void testValidateTimeSpecificAttributes() throws Exception {
        final String resourcePath = RULES_ROOT + "timeSpecificAttributes.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isEmpty();
    }

    @Test
    public void testValidateTimeSpecificAttributesCron() throws Exception {
        final String resourcePath = RULES_ROOT + "timeSpecificAttributesCron.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isEmpty();
    }

    @Test
    public void testValidateTimeSpecificAttributesInvalidTimer() throws Exception {
        final String resourcePath = RULES_ROOT + "timeSpecificAttributesInvalidTimer.rdrl";
        final String expectedError = "Incorrect number of arguments for interval timer 'x x x'";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).hasSize(2);
        Assertions.assertThat(messages).allMatch(m -> m.getText().contains(expectedError));
    }

    @Test
    public void testValidateSalienceAttribute() throws Exception {
        final String resourcePath = RULES_ROOT + "salienceAttribute.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isEmpty();
    }

    @Test
    public void testValidateSalienceAttributeInvalidNumber() throws Exception {
        final String resourcePath = RULES_ROOT + "salienceAttributeInvalidNumber.rdrl";
        final String expectedErrorSubstring = "Unable to";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        // There are two messages:
        // Unable to Analyse Expression ...
        // Unable to build expression ...
        // Each present twice due to two defined KIE bases
        Assertions.assertThat(messages).hasSize(4);
        Assertions.assertThat(messages).allMatch(m -> m.getText().contains(expectedErrorSubstring));
    }

    @Test
    public void testValidateDateExpiresAttribute() throws Exception {
        final String resourcePath = RULES_ROOT + "dateExpiresAttribute.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isEmpty();
    }

    @Test
    public void testValidateDateEffectiveAttribute() throws Exception {
        final String resourcePath = RULES_ROOT + "dateEffectiveAttribute.rdrl";
        final List<ValidationMessage> messages = validateResource(resourcePath);
        Assertions.assertThat(messages).isEmpty();
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
