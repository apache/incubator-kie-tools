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

import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.QNameMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_2.TDefinitions;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace.KIE;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDIExtensionsRegister.COMPONENTS_WIDTHS_EXTENSION_ALIAS;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDIExtensionsRegister.COMPONENT_WIDTHS_ALIAS;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDIExtensionsRegister.COMPONENT_WIDTH_ALIAS;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.dd.DMNDIExtensionsRegister.EXTERNAL_LINK_ALIAS;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDIExtensionsRegisterTest {

    @Mock
    private XStream xStream;

    @Mock
    private QNameMap qmap;

    @Captor
    private ArgumentCaptor<Converter> converterCaptor;

    @Captor
    private ArgumentCaptor<QName> qNameCaptor;

    private DMNDIExtensionsRegister register;

    @Before
    public void setup() {
        this.register = new DMNDIExtensionsRegister();
    }

    @Test
    public void testRegisterExtensionConverters() {
        register.registerExtensionConverters(xStream);

        verify(xStream).processAnnotations(eq(ComponentsWidthsExtension.class));
        verify(xStream).processAnnotations(eq(ComponentWidths.class));
        verify(xStream).processAnnotations(eq(ExternalLink.class));
        verify(xStream).alias(eq(COMPONENT_WIDTH_ALIAS), eq(Double.class));
        verify(xStream, times(2)).registerConverter(converterCaptor.capture());
        verify(xStream).alias(eq(EXTERNAL_LINK_ALIAS), eq(ExternalLink.class));

        final List<Converter> values = converterCaptor.getAllValues();
        assertThat(values).hasAtLeastOneElementOfType(ComponentWidthsConverter.class);
        assertThat(values).hasAtLeastOneElementOfType(ExternalLinksConverter.class);
    }

    @Test
    public void testBeforeMarshal() {
        final DMNModelInstrumentedBase base = mock(TDefinitions.class);
        when(base.getPrefixForNamespaceURI(anyString())).thenReturn(Optional.empty());

        register.beforeMarshal(base, qmap);

        verify(qmap).registerMapping(qNameCaptor.capture(),
                                     eq(COMPONENTS_WIDTHS_EXTENSION_ALIAS));
        final QName qName1 = qNameCaptor.getValue();
        assertThat(qName1.getNamespaceURI()).isEqualTo(KIE.getUri());
        assertThat(qName1.getLocalPart()).isEqualTo(COMPONENTS_WIDTHS_EXTENSION_ALIAS);
        assertThat(qName1.getPrefix()).isEqualTo(KIE.getPrefix());

        verify(qmap).registerMapping(qNameCaptor.capture(),
                                     eq(COMPONENT_WIDTHS_ALIAS));
        final QName qName2 = qNameCaptor.getValue();
        assertThat(qName2.getNamespaceURI()).isEqualTo(KIE.getUri());
        assertThat(qName2.getLocalPart()).isEqualTo(COMPONENT_WIDTHS_ALIAS);
        assertThat(qName2.getPrefix()).isEqualTo(KIE.getPrefix());

        verify(qmap).registerMapping(qNameCaptor.capture(),
                                     eq(COMPONENT_WIDTH_ALIAS));
        final QName qName3 = qNameCaptor.getValue();
        assertThat(qName3.getNamespaceURI()).isEqualTo(KIE.getUri());
        assertThat(qName3.getLocalPart()).isEqualTo(COMPONENT_WIDTH_ALIAS);
        assertThat(qName3.getPrefix()).isEqualTo(KIE.getPrefix());

        verify(qmap).registerMapping(qNameCaptor.capture(),
                                     eq(EXTERNAL_LINK_ALIAS));
        final QName qName4 = qNameCaptor.getValue();
        assertThat(qName4.getNamespaceURI()).isEqualTo(KIE.getUri());
        assertThat(qName4.getLocalPart()).isEqualTo(EXTERNAL_LINK_ALIAS);
        assertThat(qName4.getPrefix()).isEqualTo(KIE.getPrefix());
    }
}
