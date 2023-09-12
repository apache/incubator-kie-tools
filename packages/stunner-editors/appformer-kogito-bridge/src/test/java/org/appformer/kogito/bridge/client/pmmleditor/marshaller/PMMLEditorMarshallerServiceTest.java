/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.appformer.kogito.bridge.client.pmmleditor.marshaller;

import org.appformer.kogito.bridge.client.pmmleditor.marshaller.model.PMMLDocumentData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PMMLEditorMarshallerServiceTest {

    @Mock
    private PMMLEditorMarshallerApiInteropWrapper wrapperMock;

    private PMMLEditorMarshallerService pmmlEditorMarshallerServiceSpy;

    @Before
    public void setup() {
        pmmlEditorMarshallerServiceSpy = spy(new PMMLEditorMarshallerService() {
            @Override
            PMMLEditorMarshallerApiInteropWrapper getWrapper() {
                return wrapperMock;
            }
        });
    }

    @Test
    public void getPMMLDocument() {
        final String xmlContent = "<PMML>content</PMML>";
        when(wrapperMock.getPMMLDocumentData(xmlContent)).thenReturn(new PMMLDocumentData());
        pmmlEditorMarshallerServiceSpy.getPMMLDocumentData(xmlContent);
        verify(wrapperMock, times(1)).getPMMLDocumentData(xmlContent);
    }
}
