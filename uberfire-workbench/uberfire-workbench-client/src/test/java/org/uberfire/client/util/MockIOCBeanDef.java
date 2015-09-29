package org.uberfire.client.util;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.SyncBeanDef;

public class MockIOCBeanDef<T,B extends T>
    implements
    SyncBeanDef<T> {

    private final B beanInstance;
    private final Class<T> type;
    private final Class<? extends Annotation> scope;
    private final Set<Annotation> qualifiers;
    private final String name;
    private final boolean concrete;
    private final boolean activated;

    public MockIOCBeanDef( final B beanInstance,
                           final Class<T> type,
                           final Class< ? extends Annotation> scope,
                           final Set<Annotation> qualifiers,
                           final String name,
                           final boolean concrete,
                           final boolean activated ) {
        this.beanInstance = beanInstance;
        this.type = type;
        this.scope = scope;
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
        return beanInstance.getClass();
    }

    @Override
    public Class< ? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public T getInstance() {
        return beanInstance;
    }

    @Override
    public T newInstance() {
        return beanInstance;
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
