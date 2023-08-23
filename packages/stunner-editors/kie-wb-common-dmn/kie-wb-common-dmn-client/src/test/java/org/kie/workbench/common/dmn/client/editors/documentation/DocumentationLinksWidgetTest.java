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

package org.kie.workbench.common.dmn.client.editors.documentation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinks;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverView;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.mockito.Mock;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget.READ_ONLY_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_Add;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_None;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DocumentationLinksWidgetTest {

    @Mock
    private HTMLDivElement linksContainer;

    @Mock
    private HTMLDivElement noneContainer;

    @Mock
    private HTMLAnchorElement addButton;

    @Mock
    private HTMLElement addLink;

    @Mock
    private HTMLElement noLink;

    @Mock
    private CellEditorControlsView cellEditor;

    @Mock
    private ManagedInstance<DocumentationLinkItem> listItems;

    @Mock
    private NameAndUrlPopoverView.Presenter nameAndUrlPopover;

    @Mock
    private TranslationService translationService;

    @Mock
    private DOMTokenList noneContainerClassList;

    @Mock
    private DOMTokenList addButtonClassList;

    @Mock
    private EventSourceMock<LockRequiredEvent> locker;

    @Mock
    private DOMTokenList linksContainerClassList;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    private DocumentationLinksWidget widget;

    @Before
    public void setup() {

        noneContainer.classList = noneContainerClassList;
        linksContainer.classList = linksContainerClassList;
        addButton.classList = addButtonClassList;

        widget = spy(new DocumentationLinksWidget(listItems,
                                                  translationService,
                                                  linksContainer,
                                                  noneContainer,
                                                  addButton,
                                                  nameAndUrlPopover,
                                                  cellEditor,
                                                  addLink,
                                                  noLink,
                                                  locker,
                                                  readOnlyProvider));
    }

    @Test
    public void testOnClickTypeButton() {

        final int x = 111;
        final int y = 222;
        final ClickEvent clickEvent = mock(ClickEvent.class);
        when(clickEvent.getClientX()).thenReturn(x);
        when(clickEvent.getClientY()).thenReturn(y);

        widget.onClickTypeButton(clickEvent);

        verify(cellEditor).show(nameAndUrlPopover, x, y);
    }

    @Test
    public void testSetDMNModel() {

        final DocumentationLinksHolder holder = mock(DocumentationLinksHolder.class);
        final DocumentationLinks value = mock(DocumentationLinks.class);
        when(holder.getValue()).thenReturn(value);
        final DRGElement model = mock(DRGElement.class);
        when(model.getLinksHolder()).thenReturn(holder);

        widget.setDMNModel(model);

        verify(widget).setValue(value);
        verify(widget).refresh();
    }

    @Test
    public void testRefresh() {

        final DocumentationLinks value = mock(DocumentationLinks.class);
        final DMNExternalLink externalLink = mock(DMNExternalLink.class);
        final List<DMNExternalLink> links = new ArrayList<>();
        links.add(externalLink);
        final DocumentationLinkItem listItem = mock(DocumentationLinkItem.class);
        final HTMLElement element = mock(HTMLElement.class);

        when(listItem.getElement()).thenReturn(element);
        when(listItems.get()).thenReturn(listItem);

        widget.setValue(value);
        when(value.getLinks()).thenReturn(links);

        widget.refresh();

        verify(listItem).init(externalLink);
        verify(linksContainer).appendChild(element);
        verify(widget).refreshContainersVisibility();
    }

    @Test
    public void testRefreshContainersVisibility() {

        final DocumentationLinks value = mock(DocumentationLinks.class);
        final DMNExternalLink externalLink = mock(DMNExternalLink.class);
        final List<DMNExternalLink> links = new ArrayList<>();
        links.add(externalLink);
        when(value.getLinks()).thenReturn(links);

        widget.setValue(value);

        widget.refreshContainersVisibility();

        verify(noneContainerClassList).add(HiddenHelper.HIDDEN_CSS_CLASS);
        verify(linksContainerClassList).remove(HiddenHelper.HIDDEN_CSS_CLASS);

        links.clear();

        widget.refreshContainersVisibility();

        verify(noneContainerClassList).remove(HiddenHelper.HIDDEN_CSS_CLASS);
        verify(linksContainerClassList).add(HiddenHelper.HIDDEN_CSS_CLASS);
    }

    @Test
    public void testOnExternalLinkDeleted() {

        final DocumentationLinks value = mock(DocumentationLinks.class);
        final DMNExternalLink externalLink = mock(DMNExternalLink.class);
        final List<DMNExternalLink> links = new ArrayList<>();
        links.add(externalLink);
        when(value.getLinks()).thenReturn(links);

        widget.setValue(value);

        widget.onExternalLinkDeleted(externalLink);

        assertFalse(links.contains(externalLink));
        verify(widget).refresh();
        verify(locker).fire(any());
    }

    @Test
    public void testOnDMNExternalLinkCreated() {

        final DMNExternalLink createdLink = mock(DMNExternalLink.class);
        final DocumentationLinks value = mock(DocumentationLinks.class);

        widget.setValue(value);

        widget.onDMNExternalLinkCreated(createdLink);

        verify(value).addLink(createdLink);
        verify(locker).fire(any());
        verify(widget).refresh();
    }

    @Test
    public void testInit() {

        final String addText = "add";
        final String noLinkText = "no link text";

        when(translationService.getTranslation(DMNDocumentationI18n_Add)).thenReturn(addText);
        when(translationService.getTranslation(DMNDocumentationI18n_None)).thenReturn(noLinkText);

        widget.init();

        assertEquals(addLink.textContent, addText);
        assertEquals(noLink.textContent, noLinkText);
        verify(widget).setupAddButtonReadOnlyStatus();
    }

    @Test
    public void testSetupAddButtonReadOnlyStatusWhenIsReadOnly() {

        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);

        widget.setupAddButtonReadOnlyStatus();

        verify(addButtonClassList).add(READ_ONLY_CSS_CLASS);
        verify(addButtonClassList, never()).remove(READ_ONLY_CSS_CLASS);
    }

    @Test
    public void testSetupAddButtonReadOnlyStatusWhenIsNotReadOnly() {

        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(false);

        widget.setupAddButtonReadOnlyStatus();

        verify(addButtonClassList, never()).add(READ_ONLY_CSS_CLASS);
        verify(addButtonClassList).remove(READ_ONLY_CSS_CLASS);
    }
}