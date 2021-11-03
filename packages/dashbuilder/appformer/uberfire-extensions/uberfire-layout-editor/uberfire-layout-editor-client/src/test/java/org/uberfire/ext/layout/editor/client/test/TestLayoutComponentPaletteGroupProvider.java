package org.uberfire.ext.layout.editor.client.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteGroupProvider;

public abstract class TestLayoutComponentPaletteGroupProvider implements LayoutComponentPaletteGroupProvider {

    private String name;
    private boolean defaultExpanded;

    private Map<String, LayoutDragComponent> components = new HashMap<>();

    public TestLayoutComponentPaletteGroupProvider(String name) {
        this(name, false);
    }

    public TestLayoutComponentPaletteGroupProvider(String name, boolean defaultExpanded) {
        this.name = name;
        this.defaultExpanded = defaultExpanded;

        getTestComponents().stream().forEach(component -> components.put(component.getIdentifier(), component));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LayoutDragComponentGroup getComponentGroup() {

        LayoutDragComponentGroup group = new LayoutDragComponentGroup(name, defaultExpanded);
        group.getComponents().putAll(components);

        return group;
    }

    protected abstract Collection<TestLayoutDragComponent> getTestComponents();
}
