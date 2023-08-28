/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.dashbuilder.displayer.DisplayerType;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

/**
 * Base class for implementing custom renderer libraries.
 */
public abstract class AbstractRendererLibrary implements RendererLibrary {

    List<AbstractDisplayer<?>> displayersToBeDestroyed;

    @Inject
    protected SyncBeanManager beanManager;

    @PostConstruct
    void setup() {
        displayersToBeDestroyed = new ArrayList<>();
    }

    @PreDestroy
    public void cleanUp() {
        displayersToBeDestroyed.forEach(d -> {
            IOC.getBeanManager().destroyBean(d);
        });

    }

    protected <T extends AbstractDisplayer<?>> T buildAndManageInstance(Class<T> clazz) {
        var newInstance = beanManager.lookupBean(clazz).newInstance();
        displayersToBeDestroyed.add(newInstance);
        return newInstance;
    }

    @Override
    public boolean isDefault(DisplayerType type) {
        return false;
    }

    @Override
    public void draw(List<Displayer> displayerList) {
        for (Displayer displayer : displayerList) {
            displayer.draw();
        }
    }

    @Override
    public void redraw(List<Displayer> displayerList) {
        for (Displayer displayer : displayerList) {
            displayer.redraw();
        }
    }
}
