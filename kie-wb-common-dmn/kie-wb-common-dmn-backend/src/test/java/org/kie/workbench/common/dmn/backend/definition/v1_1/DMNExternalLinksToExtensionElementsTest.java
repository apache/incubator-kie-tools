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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DRGElement;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinks;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ExternalLink;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.DMNExternalLinksToExtensionElements.getOrCreateExtensionElements;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements;
import static org.kie.workbench.common.dmn.backend.definition.v1_1.DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNExternalLinksToExtensionElementsTest {

    @Test
    public void testLoadExternalLinksFromExtensionElements() {

        final org.kie.dmn.model.api.DRGElement source = mock(org.kie.dmn.model.api.DRGElement.class);
        final org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement target = mock(org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement.class);

        final DMNElement.ExtensionElements extensionElements = mock(DMNElement.ExtensionElements.class);
        final List<Object> externalLinks = new ArrayList<>();

        final String linkDescription1 = "l1";
        final String url1 = "url1";
        final ExternalLink external1 = createExternalLinkMock(linkDescription1, url1);
        externalLinks.add(external1);

        final String linkDescription2 = "l2";
        final String url2 = "url2";
        final ExternalLink external2 = createExternalLinkMock(linkDescription2, url2);
        externalLinks.add(external2);

        when(extensionElements.getAny()).thenReturn(externalLinks);
        when(source.getExtensionElements()).thenReturn(extensionElements);

        final DocumentationLinksHolder linksHolder = mock(DocumentationLinksHolder.class);
        final DocumentationLinks links = new DocumentationLinks();
        when(linksHolder.getValue()).thenReturn(links);
        when(target.getLinksHolder()).thenReturn(linksHolder);

        loadExternalLinksFromExtensionElements(source, target);

        assertEquals(2, links.getLinks().size());

        compare(links.getLinks().get(0), linkDescription1, url1);
        compare(links.getLinks().get(1), linkDescription2, url2);
    }

    private void compare(final org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink converted,
                         final String description,
                         final String url) {

        assertEquals(url, converted.getUrl());
        assertEquals(description, converted.getDescription());
    }

    private ExternalLink createExternalLinkMock(final String description, final String url) {

        final ExternalLink external = mock(ExternalLink.class);
        when(external.getName()).thenReturn(description);
        when(external.getUrl()).thenReturn(url);
        return external;
    }

    @Test
    public void testLoadExternalLinksIntoExtensionElements() {

        final org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement source = mock(org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement.class);
        final org.kie.dmn.model.api.DRGElement target = mock(org.kie.dmn.model.api.DRGElement.class);

        final DocumentationLinksHolder linksHolder = mock(DocumentationLinksHolder.class);
        when(source.getLinksHolder()).thenReturn(linksHolder);
        final DocumentationLinks documentationLinks = new DocumentationLinks();

        final String url1 = "url1";
        final String description1 = "desc1";
        final org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink link1 = createWBExternalLinkMock(description1, url1);
        documentationLinks.addLink(link1);

        final String url2 = "url2";
        final String description2 = "desc2";
        final org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink link2 = createWBExternalLinkMock(description2, url2);
        documentationLinks.addLink(link2);

        when(linksHolder.getValue()).thenReturn(documentationLinks);
        final DMNElement.ExtensionElements extensionElements = mock(DMNElement.ExtensionElements.class);
        when(target.getExtensionElements()).thenReturn(extensionElements);
        final List<Object> externalLinks = new ArrayList<>();
        when(extensionElements.getAny()).thenReturn(externalLinks);

        loadExternalLinksIntoExtensionElements(source, target);

        assertEquals(2, externalLinks.size());

        compare((ExternalLink) externalLinks.get(0), description1, url1);
        compare((ExternalLink) externalLinks.get(1), description2, url2);
    }

    private void compare(final ExternalLink converted,
                         final String description,
                         final String url) {

        assertEquals(url, converted.getUrl());
        assertEquals(description, converted.getName());
    }

    private static org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink createWBExternalLinkMock(final String description,
                                                                                                          final String url) {
        final org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink external = mock(org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink.class);
        when(external.getDescription()).thenReturn(description);
        when(external.getUrl()).thenReturn(url);

        return external;
    }

    @Test
    public void testGetOrCreateExtensionElements() {

        final DRGElement element = mock(DRGElement.class);

        final DMNElement.ExtensionElements result = getOrCreateExtensionElements(element);

        assertNotNull(result);

        final DMNElement.ExtensionElements existingElements = mock(DMNElement.ExtensionElements.class);
        when(element.getExtensionElements()).thenReturn(existingElements);

        final DMNElement.ExtensionElements actual = getOrCreateExtensionElements(element);

        assertEquals(actual, existingElements);
    }
}