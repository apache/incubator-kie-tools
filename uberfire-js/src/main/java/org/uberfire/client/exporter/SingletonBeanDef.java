package org.uberfire.client.exporter;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

import javax.inject.Singleton;

import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

public class SingletonBeanDef<T, B extends T>
    implements
    SyncBeanDef<T> {

    private final B instance;
    private final Class<T> type;
    private final Set<Annotation> qualifiers;
    private final String name;
    private final boolean concrete;
    private final boolean activated;

    public SingletonBeanDef( final B instance,
                             final Class<T> type,
                             final Set<Annotation> qualifiers,
                             final String name,
                             final boolean concrete,
                             final boolean activated ) {
        this.instance = instance;
        this.type = type;
        this.qualifiers = qualifiers;
        this.name = name;
        this.concrete = concrete;
        this.activated = activated;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Class< ? > getBeanClass() {
        return instance.getClass();
    }

    @Override
    public Class< ? extends Annotation> getScope() {
        return Singleton.class;
    }

    @Override
    public T getInstance() {
        return instance;
    }

    @Override
    public T newInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        if (qualifiers == null) {
            return Collections.emptySet();
        } else {
            return qualifiers;
        }
    }

    @Override
    public boolean matches( final Set<Annotation> annotations ) {
        return QualifierUtil.matches( annotations, getQualifiers() );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isConcrete() {
        return concrete;
    }

    @Override
    public boolean isActivated() {
        return activated;
    }

}
