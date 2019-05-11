/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.client.api.LayoutEditor;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementPart;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElementType;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesPresenter;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LayoutEditorPropertiesPresenterTest {

    private class LayoutElementPartTest implements LayoutEditorElementPart {

        private String id;
        private LayoutEditorElement parent;

        
        public LayoutElementPartTest(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Map<String, String> getProperties() {
            return null;
        }

        @Override
        public void clearProperties() {
        }

        @Override
        public void setProperty(String property, String value) {
        }

        @Override
        public void removeProperty(String property) {
            
        }

        @Override
        public List<PropertyEditorCategory> getPropertyCategories() {
            return null;
        }

        @Override
        public void setSelected(boolean status) {
        }

        @Override
        public LayoutEditorElement getParent() {
            return this.parent;
        }
        
        public void setParent(LayoutEditorElement parent) {
            this.parent = parent;
        }
    }
    
    private class LayoutElementTest implements LayoutEditorElement {

        private String id;
        private LayoutEditorElementType type;
        private LayoutEditorElement parent;
        private List<LayoutElementTest> children;
        private List<LayoutElementPartTest> parts;

        public LayoutElementTest(String id, LayoutEditorElementType type, List<LayoutElementTest> children) {
            this(id, type, children, new ArrayList<>());
        }
        
        public LayoutElementTest(String id, 
                                 LayoutEditorElementType type, 
                                 List<LayoutElementTest> children, 
                                 List<LayoutElementPartTest> parts) {
            this.id = id;
            this.type = type;
            this.children = children;
            this.parts = parts;
            this.children.forEach(child -> child.parent = this);
            this.parts.forEach(p -> p.setParent(this));
        }

        @Override public LayoutEditorElementType geElementType() {
            return type;
        }

        @Override public String getId() {
            return id;
        }

        @Override public void setSelectable(boolean selectable) {

        }

        @Override public boolean isSelected() {
            return false;
        }

        @Override public void setSelected(boolean status) {

        }

        @Override public Map<String, String> getProperties() {
            return null;
        }

        @Override public void clearProperties() {

        }

        @Override public void setProperty(String property, String value) {

        }

        @Override public void removeProperty(String property) {

        }

        @Override public List<PropertyEditorCategory> getPropertyCategories() {
            return null;
        }

        @Override public LayoutEditorElement getParentElement() {
            return parent;
        }

        @Override public List<? extends LayoutEditorElement> getChildElements() {
            return children;
        }
        
        @Override public List<LayoutEditorElementPart> getLayoutEditorElementParts() {
            return parts.stream().map(p -> (LayoutEditorElementPart) p).collect(Collectors.toList());
        }
    }

    private LayoutEditorPropertiesPresenter.View view = spy(new LayoutEditorPropertiesPresenter.View() {

        @Override public void dispose() {
        }

        @Override public void showSelector(IsWidget selectorView) {
        }

        @Override public void showElement(IsWidget elementView) {
        }

        @Override public String getDisplayPosition(String parentPosition, String elementName, String elementIndex) {
            return parentPosition + " > " + elementName + " " + elementIndex;
        }

        @Override public String getDisplayName(String elementName, String elementIndex) {
            return elementName + " " + elementIndex;
        }

        @Override public String getLayoutElementTypePage() {
            return "page";
        }

        @Override public String getLayoutElementTypeRow() {
            return "row";
        }

        @Override public String getLayoutElementTypeColumn() {
            return "column";
        }

        @Override public String getLayoutElementTypeComponent() {
            return "component";
        }

        @Override public void setClearPropertiesEnabled(boolean enabled) {

        }

        @Override public void init(LayoutEditorPropertiesPresenter presenter) {

        }

        @Override public HTMLElement getElement() {
            return null;
        }

        @Override
        public void noParts() {
        }

        @Override
        public void showParts(List<String> parts) {
        }
    });

    @Mock
    private ManagedInstance<LayoutElementPropertiesPresenter> layoutElementPropertiesPresenterInstance;

    @Mock
    private LayoutEditor layoutEditor;

    @Mock
    private LiveSearchDropDown<String> elementSelector;

    @Mock
    private LayoutElementPropertiesPresenter elementPropertiesPresenter;
    
    LayoutElementPartTest PART_1 = new LayoutElementPartTest("P1");
    LayoutElementPartTest PART_2 = new LayoutElementPartTest("P2");
    List<LayoutElementPartTest> columnParts = Arrays.asList(PART_1, PART_2);

    LayoutElementTest column1 = new LayoutElementTest("1", LayoutEditorElementType.COLUMN, new ArrayList<>());
    LayoutElementTest column2 = new LayoutElementTest("2", LayoutEditorElementType.COLUMN, new ArrayList<>());
    LayoutElementTest column4 = new LayoutElementTest("1", LayoutEditorElementType.COLUMN, new ArrayList<>());
    LayoutElementTest column5 = new LayoutElementTest("2", LayoutEditorElementType.COLUMN, new ArrayList<>());
    LayoutElementTest columnWithParts = new LayoutElementTest("CPARTS", LayoutEditorElementType.COLUMN, new ArrayList<>(), columnParts);
    LayoutElementTest rowcol3 = new LayoutElementTest("1", LayoutEditorElementType.ROW, Arrays.asList(column4, column5, columnWithParts));
    LayoutElementTest column3 = new LayoutElementTest("2", LayoutEditorElementType.COLUMN_WITH_COMPONENTS, Arrays.asList(rowcol3));
    LayoutElementTest row1 = new LayoutElementTest("1", LayoutEditorElementType.ROW, Arrays.asList(column1, column2));
    LayoutElementTest row2 = new LayoutElementTest("2", LayoutEditorElementType.ROW, Arrays.asList(column3));
    LayoutElementTest container = new LayoutElementTest("container", LayoutEditorElementType.CONTAINER, Arrays.asList(row1, row2));
    private LayoutEditorPropertiesPresenter presenter;

    @Before
    public void initialize() {
        when(layoutElementPropertiesPresenterInstance.get()).thenReturn(elementPropertiesPresenter);
        when(layoutEditor.getLayoutElements()).thenReturn(Arrays.asList(container, 
                                                                        row1, column1, column2, 
                                                                        row2, column3, rowcol3, column4, column5, columnWithParts));

        presenter = new LayoutEditorPropertiesPresenter(view, layoutElementPropertiesPresenterInstance, elementSelector);
    }

    @Test
    public void testInit() {
        this.elementSelector.setSearchEnabled(false);
        this.elementSelector.setClearSelectionEnabled(false);
        this.elementSelector.setWidth(275);
        this.elementSelector.init(eq(presenter.getSearchService()), any());
    }

    @Test
    public void testEditLayout() {
        presenter.edit(layoutEditor);

        verify(view).dispose();
        verify(view).showSelector(elementSelector);
        verify(view, never()).setClearPropertiesEnabled(true);
        verify(elementSelector).clear();
        verify(elementSelector).setSelectedItem("container");
        verify(elementPropertiesPresenter).edit(container);
    }

    @Test
    public void testSelectorEntries() {
        presenter.edit(layoutEditor);
        presenter.getSearchService().search("", -1, results -> {
            assertEquals(results.size(), 8);
            assertEquals(results.get(0).getKey(), "container");
            assertEquals(results.get(0).getValue(), "page");
            assertEquals(results.get(1).getKey(), "1");
            assertEquals(results.get(1).getValue(), "row 1");
            assertEquals(results.get(2).getKey(), "1");
            assertEquals(results.get(2).getValue(), "row 1 > column 1 > component 1");
            assertEquals(results.get(3).getKey(), "2");
            assertEquals(results.get(3).getValue(), "row 1 > column 2 > component 1");
            assertEquals(results.get(4).getKey(), "2");
            assertEquals(results.get(4).getValue(), "row 2");
            assertEquals(results.get(5).getKey(), "1");
            assertEquals(results.get(5).getValue(), "row 2 > column 1 > component 1");
            assertEquals(results.get(6).getKey(), "2");
            assertEquals(results.get(6).getValue(), "row 2 > column 1 > component 2");
            assertEquals(results.get(7).getKey(), "CPARTS");
            assertEquals(results.get(7).getValue(), "row 2 > column 1 > component 3");
        });
    }
    
    @Test
    public void columnWithPartsTest() {
        presenter.edit(layoutEditor);
        presenter.edit(columnWithParts);
        verify(view).showParts(Arrays.asList(LayoutEditorPropertiesPresenter.PART_ROOT, 
                                             PART_1.id, 
                                             PART_2.id));
    }
    
    @Test
    public void columnWithoutPartsTest() {
        presenter.edit(layoutEditor);
        verify(view).noParts();
    }
    
    @Test
    public void partEditTest() {
        mockSelectedElement(columnWithParts.id);
        presenter.edit(layoutEditor);
        presenter.edit(columnWithParts);
        presenter.onPartSelected(PART_1.id);
        verify(elementPropertiesPresenter).edit(PART_1);
    }

    @Test
    public void rootPartTest() {
        mockSelectedElement(columnWithParts.id);
        presenter.edit(layoutEditor);
        presenter.edit(columnWithParts);
        presenter.onPartSelected(LayoutEditorPropertiesPresenter.PART_ROOT);
        verify(elementPropertiesPresenter).edit(columnWithParts);
    }
    
    private void mockSelectedElement(String id) {
        SingleLiveSearchSelectionHandler<String> selectionHandler = mock(SingleLiveSearchSelectionHandler.class);
        when(selectionHandler.getSelectedKey()).thenReturn(id);
        presenter.setSelectionHandler(selectionHandler);
    }
}
