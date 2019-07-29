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

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ExternalLinksConverter.NAME;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ExternalLinksConverter.URL;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalLinksConverterTest {

    @Mock
    private XStream xStream;

    private ExternalLinksConverter converter;

    private final String TEST_URL = "http://www.kiegroup.org/foo.pdf";
    private final String TEST_NAME = "Something";

    @Before
    public void setup() {
        converter = spy(new ExternalLinksConverter(xStream));
        doNothing().when(converter).superAssignAttributes(any(), any());
        doNothing().when(converter).superWriteAttributes(any(), any());
    }

    @Test
    public void testAssignAttributes() {

        final ExternalLink externalLink = mock(ExternalLink.class);
        final HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);

        when(reader.getAttribute(NAME)).thenReturn(TEST_NAME);
        when(reader.getAttribute(URL)).thenReturn(TEST_URL);

        converter.assignAttributes(reader, externalLink);

        verify(externalLink).setName(TEST_NAME);
        verify(externalLink).setUrl(TEST_URL);
    }

    @Test
    public void testWriteAttributes() {

        final HierarchicalStreamWriter writer = mock(HierarchicalStreamWriter.class);
        final ExternalLink externalLink = new ExternalLink();
        externalLink.setUrl(TEST_URL);
        externalLink.setName(TEST_NAME);

        converter.writeAttributes(writer, externalLink);

        verify(writer).addAttribute(URL, TEST_URL);
        verify(writer).addAttribute(NAME, TEST_NAME);
    }
}