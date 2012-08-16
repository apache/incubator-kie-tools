/*
 * Copyright 2012 JBoss Inc
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

package ${packageName};

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.annotations.Identifier;
import org.uberfire.client.mvp.AbstractPerspectiveActivity;
import org.uberfire.client.workbench.perspectives.PerspectiveDefinition;

<#if isDefault>
import org.uberfire.client.annotations.DefaultPerspective;

</#if>
@ApplicationScoped
<#if isDefault>
@DefaultPerspective
</#if>
@Generated("org.uberfire.annotations.processors.PerspectiveProcessor")
@Identifier("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractPerspectiveActivity {

    @Inject
    private ${realClassName} realClass;

    @Override
    public String getIdentifier() {
        return "${identifier}";
    }

    @Override
    public PerspectiveDefinition getPerspective() {
        return realClass.${methodName}(); 
    }
    
}

