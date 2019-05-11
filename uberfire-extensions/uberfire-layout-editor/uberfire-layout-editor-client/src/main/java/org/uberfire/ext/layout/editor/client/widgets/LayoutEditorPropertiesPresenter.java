/*
 * Copyright 2018 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.layout.editor.client.widgets;

import com.google.gwt.user.client.ui.IsWidget;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.api.*;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutEditorElementSelectEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.uberfire.ext.layout.editor.client.api.LayoutEditorElementType.*;

@ApplicationScoped
public class LayoutEditorPropertiesPresenter {

    public static final String PART_ROOT = "Root";

    public interface View extends UberElement<LayoutEditorPropertiesPresenter> {

        void dispose();

        void showSelector(IsWidget selectorView);

        void showElement(IsWidget elementView);

        String getDisplayPosition(String parentPosition, String elementName, String elementIndex);

        String getDisplayName(String elementName, String elementIndex);

        String getLayoutElementTypePage();

        String getLayoutElementTypeRow();

        String getLayoutElementTypeColumn();

        String getLayoutElementTypeComponent();

        void setClearPropertiesEnabled(boolean enabled);

        void noParts();

        void showParts(List<String> parts);
    }

    private View view;
    private ManagedInstance<LayoutElementPropertiesPresenter> layoutElementPropertiesPresenterInstance;
    private LiveSearchDropDown<String> elementSelector;
    private LayoutEditor layoutEditor;
    private LayoutElementPropertiesPresenter propertiesPresenter;
    private Map<String,LayoutElementPropertiesPresenter> presenterMap = new HashMap<>();

    SingleLiveSearchSelectionHandler<String> selectionHandler = new SingleLiveSearchSelectionHandler<>();
    LiveSearchService<String> searchService = new LiveSearchService<String>() {
        @Override
        public void search(String pattern, int maxResults, LiveSearchCallback<String> callback) {
            LiveSearchResults result = new LiveSearchResults(maxResults);
            layoutEditor.getLayoutElements().stream()
                    .filter(LayoutEditorPropertiesPresenter::isSupported)
                    .forEach(element -> {
                        String elementId = element.getId();
                        String elementPosition = getDisplayPosition(element);
                        if (elementPosition.toLowerCase().contains(pattern.toLowerCase())) {
                            result.add(elementId, elementPosition);
                        }
                    });
            callback.afterSearch(result);
        }

        @Override
        public void searchEntry(String key, LiveSearchCallback<String> callback) {
            LiveSearchResults result = new LiveSearchResults(1);
            layoutEditor.getLayoutElements().stream()
                    .filter(element -> LayoutEditorPropertiesPresenter.isSupported(element) && element.getId().equals(key))
                    .findAny()
                    .ifPresent(element -> {
                        String elementId = element.getId();
                        String elementPosition = getDisplayPosition(element);
                        result.add(elementId, elementPosition);
                    });

            callback.afterSearch(result);
        }
    };

    static boolean isSupported(LayoutEditorElement element) {
        if (element == null) {
            return false;
        }
        switch (element.geElementType()) {
            case ROW:
                LayoutEditorElement parent = element.getParentElement();
                return parent == null || !COLUMN_WITH_COMPONENTS.equals(parent.geElementType());
            case COLUMN_WITH_COMPONENTS:
                return false;
            default:
                return true;
        }
    }

    public LayoutEditorPropertiesPresenter() {
    }

    @Inject
    public LayoutEditorPropertiesPresenter(final View view,
            ManagedInstance<LayoutElementPropertiesPresenter> layoutElementPropertiesPresenterInstance,
            LiveSearchDropDown elementSelector) {
        this.view = view;
        this.layoutElementPropertiesPresenterInstance = layoutElementPropertiesPresenterInstance;
        this.elementSelector = elementSelector;
        view.init(this);
    }

    @PostConstruct
    private void init() {
        this.elementSelector.setSearchEnabled(false);
        this.elementSelector.setClearSelectionEnabled(false);
        this.elementSelector.setWidth(275);
        this.elementSelector.setOnChange(this::onElementSelected);
        this.elementSelector.init(searchService, selectionHandler);
    }

    public UberElement<LayoutEditorPropertiesPresenter> getView() {
        return view;
    }

    public LayoutEditor getLayoutEditor() {
        return layoutEditor;
    }

    public LiveSearchService<String> getSearchService() {
        return searchService;
    }
    
    public void setSelectionHandler(SingleLiveSearchSelectionHandler<String> selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    public void edit(LayoutEditor layoutEditor) {
        if (layoutEditor != null) {
            this.dispose();
            this.layoutEditor = layoutEditor;
            view.showSelector(elementSelector);

            // Edit the first element available
            LayoutEditorElement firstElement = layoutEditor.getLayoutElements().get(0);
            edit(firstElement);
        }
    }

    public void edit(LayoutEditorElement layoutElement) {
        if (layoutEditor != null) {
            String elementId = layoutElement.getId();
            elementSelector.setSelectedItem(elementId);
            fillElementParts(layoutElement);
            updateCurrentPropertiesPresenter(elementId, layoutElement);
            view.showElement(propertiesPresenter.getView());
            updateClearElementStatus();
        }
    }
    
    public void edit(LayoutEditorElementPart layoutElementPart) {
        if (layoutEditor != null) {
            String elementPartId = layoutElementPart.getParent().getId() + "." + layoutElementPart.getId();
            updateCurrentPropertiesPresenter(elementPartId, layoutElementPart);
            view.showElement(propertiesPresenter.getView());
            updateClearElementStatus();
        }
    }

    public void dispose() {
        this.propertiesPresenter = null;
        elementSelector.clear();
        presenterMap.clear();
        view.dispose();
    }

    public String getDisplayPosition(LayoutEditorElement element) {
        LayoutEditorElement parent = element.getParentElement();
        String name = getElementName(element);
        if (parent == null) {
            return name;
        }

        int position = parent.getChildElements().indexOf(element) + 1;
        LayoutEditorElement granpa = parent.getParentElement();
        if (granpa == null) {
            return view.getDisplayName(name, Integer.toString(position));
        }

        boolean nestedRow = ROW.equals(parent.geElementType()) && COLUMN_WITH_COMPONENTS.equals(granpa.geElementType());
        String parentDisplay = nestedRow ? getDisplayPosition(granpa) : getDisplayPosition(parent);
        String elementDisplay = view.getDisplayPosition(parentDisplay, name, Integer.toString(position));
        String componentName = view.getLayoutElementTypeComponent();

        if (COLUMN.equals(element.geElementType())) {
            if (nestedRow) {
                return view.getDisplayPosition(parentDisplay, componentName, Integer.toString(position));
            } else {
                return view.getDisplayPosition(elementDisplay, componentName, "1");
            }
        } else {
            return elementDisplay;
        }
    }

    public String getElementName(LayoutEditorElement element) {
        switch (element.geElementType()) {
            case CONTAINER:
                return view.getLayoutElementTypePage();
            case ROW:
                return view.getLayoutElementTypeRow();
            case COLUMN:
                return view.getLayoutElementTypeColumn();
            case COLUMN_WITH_COMPONENTS:
                return view.getLayoutElementTypeColumn();
            default:
                return "Unknown element type: " + element.geElementType();
        }
    }

    public void clearElementProperties() {
        if (propertiesPresenter != null) {
            propertiesPresenter.reset();
        }
    }

    public void reset() {
        this.dispose();
        this.edit(layoutEditor);
    }

    private void updateClearElementStatus() {
        if (propertiesPresenter != null) {
            boolean hasValues = propertiesPresenter.hasValues();
            view.setClearPropertiesEnabled(hasValues);
        }
    }

    // View actions

    void onElementSelected() {
        LayoutEditorElement selectedElement = getSelectedElement();
        this.edit(selectedElement);
    }
    
    // LayoutElementPropertiesPresenter events

    void onLayoutPropertyChangedEvent(@Observes LayoutElementPropertyChangedEvent event) {
        updateClearElementStatus();
    }

    void onClearAllPropertiesEvent(@Observes LayoutElementClearAllPropertiesEvent event) {
        updateClearElementStatus();
    }

    // Layout editor events

    void onLayoutElementSelected(@Observes LayoutEditorElementSelectEvent event) {
        LayoutEditorElement element = event.getElement();
        this.edit(element);
    }

    public void onPartSelected(String partId) {
        LayoutEditorElement selectedElement = getSelectedElement();
        if (PART_ROOT.equals(partId)) {
            this.edit(selectedElement);
        } else {
            selectedElement.getLayoutEditorElementParts().stream()
                                                         .filter(p -> p.getId().equals(partId))
                                                         .findFirst().ifPresent(this::edit);
        }
    }

    void onComponentDropped(@Observes ComponentDropEvent event) {
        this.reset();
    }

    void onComponentRemoved(@Observes ComponentRemovedEvent event) {
        this.reset();
    }

    void onRowsSwap(@Observes RowDnDEvent rowDndEvent) {
        this.reset();
    }
    
    protected void fillElementParts(LayoutEditorElement element) {
        List<LayoutEditorElementPart> currentLayoutElementParts = element.getLayoutEditorElementParts();
        if (currentLayoutElementParts.isEmpty()) {
            view.noParts();
        } else {
            List<String> parts = new ArrayList<>();
            parts.add(PART_ROOT);
            currentLayoutElementParts.stream().map(LayoutEditorElementPart::getId)
                                             .forEach(parts::add);
            view.showParts(parts);
        }
    }
    
    private void updateCurrentPropertiesPresenter(String elementId, LayoutElementWithProperties layoutElement) {
        LayoutElementPropertiesPresenter presenter = presenterMap.get(elementId);
        if (presenter == null) {
            propertiesPresenter = layoutElementPropertiesPresenterInstance.get();
            propertiesPresenter.edit(layoutElement);
            presenterMap.put(elementId, propertiesPresenter);
        } else if (presenter != propertiesPresenter) {
            propertiesPresenter.getLayoutElement().setSelected(false);
            propertiesPresenter = presenter;
        }
    }
    
    private LayoutEditorElement getSelectedElement() {
        String elementId = selectionHandler.getSelectedKey();
        for (LayoutEditorElement element : layoutEditor.getLayoutElements()) {
            if (element.getId().equals(elementId)) {
                return element;
            }
        }
        return null;
    }
}