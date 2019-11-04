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

package org.kie.workbench.common.dmn.backend.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.marshalling.DMNImportTypesHelper;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNImportTypesHelperImplTest {

    @Mock
    private Path dmnPath;

    @Mock
    private Path pmmlPath;

    @Mock
    private Path javaPath;

    private DMNImportTypesHelper helper;

    @Before
    public void setup() {
        this.helper = new DMNImportTypesHelperImpl();

        when(dmnPath.getFileName()).thenReturn("file.dmn");
        when(pmmlPath.getFileName()).thenReturn("file.pmml");
        when(javaPath.getFileName()).thenReturn("file.java");
    }

    @Test
    public void testIsDMN() {
        assertThat(helper.isDMN(dmnPath)).isTrue();
        assertThat(helper.isDMN(pmmlPath)).isFalse();
        assertThat(helper.isDMN(javaPath)).isFalse();
    }

    @Test
    public void testIsPMML() {
        assertThat(helper.isPMML(dmnPath)).isFalse();
        assertThat(helper.isPMML(pmmlPath)).isTrue();
        assertThat(helper.isPMML(javaPath)).isFalse();
    }

    @Test
    public void testIsJava() {
        assertThat(helper.isJava(javaPath)).isTrue();
        assertThat(helper.isPMML(javaPath)).isFalse();
        assertThat(helper.isDMN(javaPath)).isFalse();
    }
}
