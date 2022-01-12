/*
* Copyright 2016 Red Hat, Inc. and/or its affiliates.
*  
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*  
*    http://www.apache.org/licenses/LICENSE-2.0
*  
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ${packageName};

import java.util.HashMap;
import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapterWrapper;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertyAdapterImpl;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends PropertyAdapterWrapper<Object, Object, BindablePropertyAdapter<Object, Object>> {

    @Inject
    public ${className}(StunnerTranslationService translationService,
                            BindableAdapterFunctions functions) {
        super(BindablePropertyAdapterImpl.create(translationService, functions, new HashMap<>(${valuePropNamesSize})));
    }

    @PostConstruct
    public void init() {
        <#list valuePropNames as valuePropName>
            adapter.addBinding(${valuePropName.className}.class, "${valuePropName.methodName}");
        </#list>
    }

}
