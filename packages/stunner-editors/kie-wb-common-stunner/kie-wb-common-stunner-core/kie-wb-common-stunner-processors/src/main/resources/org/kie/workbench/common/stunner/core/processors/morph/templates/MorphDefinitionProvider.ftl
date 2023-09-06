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


import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} implements ${parentClassName} {

    private static final List<MorphDefinition> MORPH_DEFINITIONS =
        new ArrayList<MorphDefinition>( ${morphDefinitionClassNameSize} ) {{

                <#list morphDefinitionClassNames as morphDefinitionClassName>
                    add( new ${morphDefinitionClassName}() );
                </#list>

                }};

    @Override
    public Collection<MorphDefinition> getMorphDefinitions() {
        return MORPH_DEFINITIONS;
    }

}
