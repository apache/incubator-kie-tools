/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.m2repo.client.upload;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.FileUpload;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class UploadFormViewImplTest {

    @Mock
    private FileUpload uploader;

    @Mock
    private UploadFormViewImpl testedView;

    @Before
    public void setUp() throws Exception {
        testedView.uploader = uploader;
    }

    @Test
    public void testHide() {
        doCallRealMethod().when(testedView).hide();

        testedView.hide();

        verify(uploader).clear();
    }

    @Test
    public void testRemoveFromParent() {
        doCallRealMethod().when(testedView).removeFromParent();

        testedView.removeFromParent();

        verify(uploader).clear();
    }
}
