package org.uberfire.ext.metadata.model.impl;

import java.util.HashSet;
import java.util.Set;

import org.uberfire.ext.metadata.model.schema.MetaProperty;

public class MetaPropertyImpl implements MetaProperty {

    private final String name;
    private boolean searchable;
    private boolean sortable;
    private final Set<Class<?>> types;

    public MetaPropertyImpl(String name,
                            boolean searchable,
                            boolean sortable,
                            Set<Class<?>> types) {

        this.name = name;
        this.searchable = searchable;
        this.sortable = sortable;
        if (types == null) {
            this.types = new HashSet<>();
        } else {
            this.types = types;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<Class<?>> getTypes() {
        return types;
    }

    @Override
    public boolean isSearchable() {
        return this.searchable;
    }

    @Override
    public void setAsSearchable() {
        this.searchable = true;
    }

    @Override
    public boolean isSortable() {
        return this.sortable;
    }

    @Override
    public void setAsSortable() {
        this.sortable = true;
    }

    @Override
    public void addType(final Class<?> type) {
        types.add(type);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MetaProperty)) {
            return false;
        }
        return ((MetaProperty) obj).getName().equals(getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
