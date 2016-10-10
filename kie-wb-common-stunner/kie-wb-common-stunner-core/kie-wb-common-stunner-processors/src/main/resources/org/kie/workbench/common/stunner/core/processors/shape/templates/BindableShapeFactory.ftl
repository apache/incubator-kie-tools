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

import ${parentClassName};

<#list definitionClasses as dc>
import ${dc};
</#list>

<#list shapeDefClasses as pc>
import ${pc};
</#list>

<#list shapeDefFactoryEntities as pc>
import ${pc.className};
</#list>

import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated( "${generatedByClassName}" )
@ApplicationScoped
public class ${className} extends ${parentClassName} {

    <#list shapeDefFactoryEntities as pc>
        ${pc.className} ${pc.id};
    </#list>

    protected ${className}() {
    }

    @Inject
    public ${className}(
            <#list shapeDefFactoryEntities as pc>
                final ${pc.className} ${pc.id}
            </#list>) {

        <#list shapeDefFactoryEntities as pc>
            this.${pc.id} = ${pc.id};
        </#list>

    }
    
    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {

        <#list addProxySentences as ps>
            ${ps}
        </#list>
    
    }

    <#list shapeDefFactoryEntities as pc>
        @Override
        protected ShapeFactory getFactory() {
            return this.${pc.id};
        }
    </#list>

}
