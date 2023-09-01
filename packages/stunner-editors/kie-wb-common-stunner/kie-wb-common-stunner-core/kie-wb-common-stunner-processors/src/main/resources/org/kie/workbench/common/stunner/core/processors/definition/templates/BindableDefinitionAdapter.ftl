/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package ${packageName};

import java.util.Arrays;
import java.util.HashMap;
import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.adapter.HasInheritance;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapterWrapper;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapterImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.DefinitionAdapterBindings;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends DefinitionAdapterWrapper<Object, BindableDefinitionAdapter<Object>> implements HasInheritance {

    protected ${className}() {
    }

    @Inject
    public ${className}(StunnerTranslationService translationService, BindableAdapterFunctions functions) {
        super(BindableDefinitionAdapterImpl.create(translationService, functions, new HashMap<>(${bindingsSize})));
    }

    @PostConstruct
    public void init() {
        <#list bindings as binding>
            adapter.addBindings(${binding.className}.class, ${binding.methodName});
        </#list>
    }

    @Override
    public String getBaseType(Class<?> type) {
        return adapter.getBaseType(type);
    }

    @Override
    public String[] getTypes(String baseType) {
        return adapter.getTypes(baseType);
    }

}