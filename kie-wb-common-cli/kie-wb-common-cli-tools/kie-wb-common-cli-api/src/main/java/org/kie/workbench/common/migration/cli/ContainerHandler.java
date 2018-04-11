/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.migration.cli;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jboss.weld.environment.se.WeldContainer;

public class ContainerHandler {

    private final WeldContainer container;
    private final Throwable initError;

    public ContainerHandler(Supplier<WeldContainer> provider) {
        WeldContainer container = null;
        Throwable initError = null;
        try {
            container = provider.get();
        } catch (Throwable t) {
            container = null;
            initError = t;
        }

        this.container = container;
        this.initError = initError;
    }

    public <T> void run(Class<T> beanType, Consumer<T> action, Consumer<Throwable> errorHandler) {
        if (container != null) {
            try {
                final T instance = container.instance().select(beanType).get();
                action.accept(instance);
            } catch (Throwable t) {
                errorHandler.accept(t);
            }
        } else {
            errorHandler.accept(initError);
        }
    }

    public void close() {
        if (container != null && container.isRunning()) {
            try {
                container.close();
            } catch (Throwable ignore) {
            }
        }
    }

}
