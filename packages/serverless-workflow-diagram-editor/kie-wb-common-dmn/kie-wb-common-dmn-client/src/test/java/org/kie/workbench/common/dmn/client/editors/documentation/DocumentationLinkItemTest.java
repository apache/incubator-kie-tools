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

package org.kie.workbench.common.dmn.client.editors.documentation;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DocumentationLinkItemTest {

    @Mock
    private HTMLDivElement item;

    @Mock
    private HTMLAnchorElement link;

    @Mock
    private HTMLAnchorElement deleteLink;

    private DocumentationLinkItem documentationLinkItem;

    @Before
    public void setup() {
        documentationLinkItem = new DocumentationLinkItem(item,
                                                          link,
                                                          deleteLink);
    }

    @Test
    public void testInit() {

        final String url = "http://www.kiegroup.org";
        final String description = "My nice description.";

        final DMNExternalLink externalLink = new DMNExternalLink();
        externalLink.setDescription(description);
        externalLink.setUrl(url);

        documentationLinkItem.init(externalLink);

        assertEquals(description, link.textContent);
        assertEquals(url, link.href);
    }

    @Test
    public void testOnDeleteLinkClick() {

        final Consumer<DMNExternalLink> onDelete = mock(Consumer.class);
        documentationLinkItem.setOnDeleted(onDelete);
        final DMNExternalLink externalLink = mock(DMNExternalLink.class);
        documentationLinkItem.init(externalLink);

        documentationLinkItem.onDeleteLinkClick(null);

        verify(onDelete).accept(externalLink);
    }
}