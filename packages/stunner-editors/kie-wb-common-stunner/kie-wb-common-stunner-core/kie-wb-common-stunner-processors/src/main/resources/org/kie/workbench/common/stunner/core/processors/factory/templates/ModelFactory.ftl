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

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import java.util.LinkedHashSet;
import java.util.Set;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentClassName}<Object> {

    private static final Set<Class<?>> SUPPORTED_DEF_CLASSES = new LinkedHashSet<Class<?>>() {{

        <#list builders as builder>
            add( ${builder.className}.class );
        </#list>

    }};

    public ${className}() {
    }

    @Override
    public Set<Class<?>> getAcceptedClasses() {
        return SUPPORTED_DEF_CLASSES;
    }

    @Override
    public Object build( final Class<?> clazz ) {

        <#list builders as builder>

            if ( ${builder.className}.class.equals( clazz ) ) {

                return ${builder.methodName};

            }

        </#list>

        throw new RuntimeException( "This factory [" + this.getClass().getName() + "] " +
            "should provide a definition for [" + clazz + "]" );
    }

}
