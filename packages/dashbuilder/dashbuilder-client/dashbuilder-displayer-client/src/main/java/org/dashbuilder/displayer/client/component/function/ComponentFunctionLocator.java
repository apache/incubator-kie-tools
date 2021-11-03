/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.component.function;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import org.dashbuilder.displayer.external.ExternalComponentFunction;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

/**
 * Looks for components functions
 *
 */
@ApplicationScoped
public class ComponentFunctionLocator {

    @Inject
    SyncBeanManager beanManager;

    List<ExternalComponentFunction> functions;

    @PostConstruct
    void loadFunctions() {
        functions = beanManager.lookupBeans(ExternalComponentFunction.class)
                               .stream()
                               .map(SyncBeanDef::getInstance)
                               .collect(Collectors.toList());
        DomGlobal.console.log("Registered " + functions.size() + " functions");
    }

    public Optional<ExternalComponentFunction> findFunctionByName(String name) {
        return functions.stream().filter(f -> name.equals(f.getName())).findAny();
    }

    public void registerFunction(ExternalComponentFunction function) {
        functions.add(function);
    }
    
    public JsArray<String> listFunctions() {
        JsArray<String> array = new JsArray<>();
        functions.stream().map(ExternalComponentFunction::getName).forEach(array::push);
        return array;
    }

}