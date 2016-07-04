/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.rule.client.editor.RuleAttributeWidget;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.gwtbootstrap3.client.ui.Heading;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@WithClassesToStub({ GuidedRuleEditorImages508.class, Heading.class, ApplicationPreferences.class })
@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableAttributeSelectorPopupTest {

    @Test
    public void getAttributes() {
        GuidedDecisionTableAttributeSelectorPopup popup = mock( GuidedDecisionTableAttributeSelectorPopup.class );
        when( popup.getAttributes() ).thenCallRealMethod();

        String[] attributes = popup.getAttributes();
        assertEquals( RuleAttributeWidget.getAttributesList().length + 1, attributes.length );
        assertEquals( GuidedDecisionTable52.NEGATE_RULE_ATTR, attributes[ attributes.length - 1 ] );
    }
}