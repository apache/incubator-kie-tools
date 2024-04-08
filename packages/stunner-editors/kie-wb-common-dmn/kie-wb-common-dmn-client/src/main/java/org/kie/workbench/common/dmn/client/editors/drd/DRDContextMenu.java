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

package org.kie.workbench.common.dmn.client.editors.drd;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLBodyElement;
import elemental2.dom.HTMLElement;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramTuple;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.contextmenu.ContextMenu;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@ApplicationScoped
public class DRDContextMenu {

    static final String DRDACTIONS_CONTEXT_MENU_TITLE = "DRDActions.ContextMenu.Title";
    static final String DRDACTIONS_CONTEXT_MENU_ACTIONS_CREATE = "DRDActions.ContextMenu.Actions.Create";
    static final String DRDACTIONS_CONTEXT_MENU_ACTIONS_ADD_TO = "DRDActions.ContextMenu.Actions.AddTo";
    static final String DRDACTIONS_CONTEXT_MENU_ACTIONS_REMOVE = "DRDActions.ContextMenu.Actions.Remove";
    static final String HEADER_MENU_ICON_CLASS = "fa fa-share-alt";

    private final ClientTranslationService translationService;
    private final ContextMenu contextMenu;
    private final DRDContextMenuService drdContextMenuService;
    private final DMNDiagramsSession dmnDiagramsSession;

    @Inject
    public DRDContextMenu(final ContextMenu contextMenu,
                          final ClientTranslationService translationService,
                          final DRDContextMenuService drdContextMenuService,
                          final DMNDiagramsSession dmnDiagramsSession) {
        this.contextMenu = contextMenu;
        this.translationService = translationService;
        this.drdContextMenuService = drdContextMenuService;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    public String getTitle() {
        return translationService.getValue(DRDACTIONS_CONTEXT_MENU_TITLE);
    }

    public void appendContextMenuToTheDOM(final double x, final double y) {
        final HTMLElement contextMenuElement = contextMenu.getElement();
        contextMenuElement.style.position = "absolute";
        contextMenuElement.style.left = x + "px";
        contextMenuElement.style.top = y + "px";
        getDocumentBody().appendChild(contextMenuElement);
    }

    /* Indirection required by unit tests */
    protected HTMLBodyElement getDocumentBody() {
        return DomGlobal.document.body;
    }

    public void show(final Collection<Node<? extends Definition<?>, Edge>> selectedNodes) {
        contextMenu.show(self -> setDRDContextMenuHandler(self, selectedNodes));
    }

    protected void setDRDContextMenuHandler(final ContextMenu contextMenu, final Collection<Node<? extends Definition<?>, Edge>> selectedNodes) {
        contextMenu.hide();
        contextMenu.setHeaderMenu(translationService.getValue(DRDACTIONS_CONTEXT_MENU_TITLE).toUpperCase(), HEADER_MENU_ICON_CLASS);
        contextMenu.addTextMenuItem(translationService.getValue(DRDACTIONS_CONTEXT_MENU_ACTIONS_CREATE),
                                    true,
                                    () -> drdContextMenuService.addToNewDRD(selectedNodes));

        getDiagrams()
                .stream()
                .filter(excludeGlobalGraphPredicate())
                .filter(excludeCurrentDRDPredicate())
                .forEach(dmnDiagram ->
                                 contextMenu.addTextMenuItem(translationService.getValue(DRDACTIONS_CONTEXT_MENU_ACTIONS_ADD_TO) + " " + getDiagramName(dmnDiagram),
                                                             true,
                                                             () -> drdContextMenuService.addToExistingDRD(dmnDiagram, selectedNodes))
                );

        if (!dmnDiagramsSession.isGlobalGraphSelected()) {
            contextMenu.addTextMenuItem(translationService.getValue(DRDACTIONS_CONTEXT_MENU_ACTIONS_REMOVE),
                                        true,
                                        () -> drdContextMenuService.removeFromCurrentDRD(selectedNodes));
        }
    }

    private Predicate<DMNDiagramTuple> excludeCurrentDRDPredicate() {
        return dmnDiagramTuple -> {
            final Id dmnDiagramId = dmnDiagramTuple.getDMNDiagram().getId();
            return dmnDiagramsSession
                    .getCurrentDMNDiagramElement()
                    .map(dmnDiagramElement -> !dmnDiagramId.equals(dmnDiagramElement.getId()))
                    .orElse(false);
        };
    }

    private Predicate<DMNDiagramTuple> excludeGlobalGraphPredicate() {
        return dmnDiagramTuple -> {
            final Id dmnDiagramId = dmnDiagramTuple.getDMNDiagram().getId();
            return !dmnDiagramId.equals(dmnDiagramsSession.getDRGDiagramElement().getId());
        };
    }

    private String getDiagramName(final DMNDiagramTuple dmnDiagram) {
        return dmnDiagram.getDMNDiagram().getName().getValue();
    }

    private List<DMNDiagramTuple> getDiagrams() {
        return drdContextMenuService.getDiagrams();
    }

    public HTMLElement getElement() {
        return contextMenu.getElement();
    }
}
