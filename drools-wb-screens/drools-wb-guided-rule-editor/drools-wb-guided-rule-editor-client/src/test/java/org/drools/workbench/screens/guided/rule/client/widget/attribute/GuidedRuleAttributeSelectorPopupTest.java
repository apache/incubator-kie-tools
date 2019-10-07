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

package org.drools.workbench.screens.guided.rule.client.widget.attribute;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({GuidedRuleEditorImages508.class, Text.class, DateTimeFormat.class})
public class GuidedRuleAttributeSelectorPopupTest {

    private RuleModel model;

    private boolean lockLHS = false;

    private boolean lockRHS = false;

    @Mock
    private Command refreshCommand;

    private GuidedRuleAttributeSelectorPopup popup;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();

        popup = new GuidedRuleAttributeSelectorPopup();
        popup.init(model,
                   lockLHS,
                   lockRHS,
                   refreshCommand);
    }

    @Test
    public void testHandleAttributeAddition_Calendars() {
        popup.handleAttributeAddition(Attribute.CALENDARS.getAttributeName());

        assertThat(model.attributes).containsExactly(new RuleAttribute(Attribute.CALENDARS.getAttributeName(), ""));
        assertThat(model.metadataList).isEmpty();
        verify(refreshCommand).execute();
    }

    @Test
    public void testHandleAttributeAddition_Timer() {
        popup.handleAttributeAddition(Attribute.TIMER.getAttributeName());

        assertThat(model.attributes).containsExactly(new RuleAttribute(Attribute.TIMER.getAttributeName(), ""));
        assertThat(model.metadataList).isEmpty();
        verify(refreshCommand).execute();
    }

    @Test
    public void testHandleAttributeAddition_LockRHS() {
        popup.handleAttributeAddition(RuleAttributeWidget.LOCK_RHS);

        assertThat(model.attributes).isEmpty();
        assertThat(model.metadataList).containsExactly(new RuleMetadata(RuleAttributeWidget.LOCK_RHS, "true"));
        verify(refreshCommand).execute();
    }

    @Test
    public void testHandleAttributeAddition_LockLHS() {
        popup.handleAttributeAddition(RuleAttributeWidget.LOCK_LHS);

        assertThat(model.attributes).isEmpty();
        assertThat(model.metadataList).containsExactly(new RuleMetadata(RuleAttributeWidget.LOCK_LHS, "true"));
        verify(refreshCommand).execute();
    }

    @Test
    public void testHandleMetadataAddition() {
        final String metadata = "a_b_c";
        popup.handleMetadataAddition(metadata);

        assertThat(model.attributes).isEmpty();
        assertThat(model.metadataList).containsExactly(new RuleMetadata(metadata, ""));
    }

    @Test
    public void testIsMetadataUnique() {
        final String metadataOne = "a_b";
        final String metadataTwo = "c_d";
        model.addMetadata(new RuleMetadata(metadataOne, ""));
        model.addMetadata(new RuleMetadata(metadataTwo, ""));

        assertThat(popup.isMetadataUnique("A_b")).isTrue();
        assertThat(popup.isMetadataUnique("a_B")).isTrue();
        assertThat(popup.isMetadataUnique("a_b_c")).isTrue();
        assertThat(popup.isMetadataUnique("c_D")).isTrue();
        assertThat(popup.isMetadataUnique("C_D")).isTrue();
        assertThat(popup.isMetadataUnique("a_c_c")).isTrue();

        assertThat(popup.isMetadataUnique(metadataOne)).isFalse();
        assertThat(popup.isMetadataUnique(metadataTwo)).isFalse();
    }
}
