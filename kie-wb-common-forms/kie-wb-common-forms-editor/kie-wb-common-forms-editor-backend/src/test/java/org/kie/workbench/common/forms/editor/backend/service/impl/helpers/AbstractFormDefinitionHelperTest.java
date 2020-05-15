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

package org.kie.workbench.common.forms.editor.backend.service.impl.helpers;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractFormDefinitionHelperTest<HELPER extends AbstractFormDefinitionHelper> {

    private static final String FORM_PATH = "/org/kie/workbench/common/forms/editor/backend/indexing/";

    private static final String ORIGINAL_FORM_NAME = "PersonShort";
    private static final String ORIGNAL_FORM_ASSET_NAME = ORIGINAL_FORM_NAME + "." + FormResourceTypeDefinition.EXTENSION;
    private static final String ORIGINAL_FORM_PATH = FORM_PATH + ORIGNAL_FORM_ASSET_NAME;

    private static final String DESTINATION_FORM_NAME = "PersonShort2";
    private static final String DESTINATION_FORM_ASSET_NAME = DESTINATION_FORM_NAME + "." + FormResourceTypeDefinition.EXTENSION;
    private static final String DESTINATION_FORM_PATH = "file:///src/main" + FORM_PATH + DESTINATION_FORM_ASSET_NAME;

    @Mock
    protected IOService ioService;

    protected FormDefinitionSerializer serializer;

    @Mock
    protected CommentedOptionFactory commentedOptionFactory;

    @Mock
    protected Path originalPath;

    @Mock
    protected Path destinationPath;

    protected SimpleFileSystemProvider simpleFileSystemProvider;

    private String originalFormName;

    protected FormDefinition formDefinition;

    protected HELPER helper;

    @Before
    public void init() throws IOException {
        simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        serializer = spy(new FormDefinitionSerializerImpl(new FieldSerializer(), new FormModelSerializer(), new TestMetaDataEntryManager()));

        when(originalPath.getFileName()).thenReturn(ORIGNAL_FORM_ASSET_NAME);
        when(destinationPath.getFileName()).thenReturn(DESTINATION_FORM_ASSET_NAME);
        when(destinationPath.toURI()).thenReturn(DESTINATION_FORM_PATH);

        when(ioService.readAllString(any())).thenReturn(IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream(ORIGINAL_FORM_PATH))));

        doAnswer((Answer<FormDefinition>) invocationOnMock -> {
            FormDefinition formDefinition = (FormDefinition) invocationOnMock.callRealMethod();
            beforeProcess(formDefinition);
            return formDefinition;
        }).when(serializer).deserialize(anyString());

        helper = getHelper(ioService, serializer, commentedOptionFactory);
    }

    protected void beforeProcess(FormDefinition formDefinition) {
        this.formDefinition = formDefinition;
    }

    @Test
    public void testSupports() {

        Assertions.assertThat(helper.supports(originalPath))
                .isNotNull()
                .isTrue();

        Path wrongPath = mock(Path.class);
        when(wrongPath.getFileName()).thenReturn("wrongh.path");

        Assertions.assertThat(helper.supports(wrongPath))
                .isNotNull()
                .isFalse();
    }

    @Test
    public void testPostProcess() {
        helper.postProcess(originalPath, destinationPath);

        verify(ioService).readAllString(any());

        verify(serializer).deserialize(anyString());

        verify(serializer).serialize(eq(formDefinition));

        verifyForm(formDefinition);

        verify(commentedOptionFactory).makeCommentedOption(anyString());

        ioService.write(any(), anyString(), any());
    }

    protected void verifyForm(FormDefinition formDefinition) {
        Assertions.assertThat(formDefinition)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", DESTINATION_FORM_NAME);
    }

    protected abstract HELPER getHelper(IOService ioService, FormDefinitionSerializer serializer, CommentedOptionFactory commentedOptionFactory);
}
