package org.uberfire.ext.metadata.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;

public class MetaObjectImpl implements MetaObject {

    private final MetaType metaType;
    private final ConcurrentHashMap<String, MetaProperty> properties;

    public MetaObjectImpl(MetaType metaType,
                          Set<MetaProperty> properties) {
        this.metaType = metaType;

        if (properties == null) {
            this.properties = new ConcurrentHashMap<>();
        } else {
            this.properties = toHashMap(properties);
        }
    }

    private ConcurrentHashMap<String, MetaProperty> toHashMap(Set<MetaProperty> properties) {
        ConcurrentHashMap<String, MetaProperty> map = new ConcurrentHashMap<>();
        if (properties != null) {
            properties.forEach(metaProperty -> map.put(metaProperty.getName(),
                                                       metaProperty));
        }
        return map;
    }

    @Override
    public MetaType getType() {
        return this.metaType;
    }

    @Override
    public Collection<MetaProperty> getProperties() {
        return this.properties.values();
    }

    @Override
    public Optional<MetaProperty> getProperty(String name) {
        return Optional.ofNullable(this.properties.get(name));
    }

    @Override
    public void addProperty(MetaProperty metaProperty) {
        this.properties.put(metaProperty.getName(),
                            metaProperty);
    }
}
