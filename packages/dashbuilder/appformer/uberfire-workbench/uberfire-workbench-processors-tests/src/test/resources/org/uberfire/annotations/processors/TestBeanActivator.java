package org.uberfire.annotations.processors;

import org.jboss.errai.ioc.client.container.BeanActivator;

public class TestBeanActivator implements BeanActivator {

    @Override
    public boolean isActivated() {
        return false;
    }
}
