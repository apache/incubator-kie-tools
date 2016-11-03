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

package org.kie.workbench.common.widgets.client.ruleselector;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({ AnchorListItem.class })
public class RuleSelectorDropdownTest {

    @GwtMock
    Button dropdownButton;

    @Test
    public void checkEnable() {
        final RuleSelectorDropdown ruleSelectorDropdown = new RuleSelectorDropdown();
        ruleSelectorDropdown.setEnabled( true );

        verify( dropdownButton,
                times( 1 ) ).setEnabled( true );
    }

    @Test
    public void checkDisable() {
        final RuleSelectorDropdown ruleSelectorDropdown = new RuleSelectorDropdown();
        ruleSelectorDropdown.setEnabled( false );

        verify( dropdownButton,
                times( 1 ) ).setEnabled( false );
    }

}
