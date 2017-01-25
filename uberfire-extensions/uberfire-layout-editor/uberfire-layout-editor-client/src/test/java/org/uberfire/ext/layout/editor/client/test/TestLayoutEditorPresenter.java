package org.uberfire.ext.layout.editor.client.test;

import java.util.Map;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;

public class TestLayoutEditorPresenter extends LayoutEditorPresenter {

    public TestLayoutEditorPresenter(View view,
                                     Container container,
                                     ManagedInstance<LayoutDragComponentGroupPresenter> layoutDragComponentGroupInstance) {
        super(view,
              container,
              layoutDragComponentGroupInstance);
    }

    public Map<String, LayoutDragComponentGroupPresenter> getLayoutDragComponentGroups() {
        return layoutDragComponentGroups;
    }
}
