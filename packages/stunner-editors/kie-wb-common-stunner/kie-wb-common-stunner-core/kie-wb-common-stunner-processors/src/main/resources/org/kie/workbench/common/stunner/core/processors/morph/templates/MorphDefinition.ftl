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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.jboss.errai.common.client.api.annotations.Portable;

@Generated("${generatedByClassName}")
@Portable
public class ${className} extends ${parentClassName} {

    private static final  Map<Class<?>, Collection<Class<?>>> DOMAIN_MORPHS =
        new HashMap<Class<?>, Collection<Class<?>>>( 1 ) {{
            put( ${morphBaseClassName}.class,
                new ArrayList<Class<?>>( ${targetClassNamesSize} ) {{
                    <#list targetClassNames as targetClassName>
                        add( ${targetClassName}.class );
                    </#list>
                }} );
        }};

    public ${className}() {
    }

    @Override
    protected Class<?> getDefaultType() {
        return ${defaultTypeClassName}.class;
    }

    @Override
    protected Map<Class<?>, Collection<Class<?>>> getDomainMorphs() {
        return DOMAIN_MORPHS;
    }

}
