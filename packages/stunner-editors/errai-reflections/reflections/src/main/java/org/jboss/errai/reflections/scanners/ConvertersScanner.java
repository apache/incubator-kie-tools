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


package org.jboss.errai.reflections.scanners;

import java.util.List;

/** scans for methods that take one class as an argument and returns another class */ 
@SuppressWarnings({"unchecked"})
public class ConvertersScanner extends AbstractScanner {
    public void scan(final Object cls) {
        final List<Object> methods = getMetadataAdapter().getMethods(cls);
        for (final Object method : methods) {
            final List<String> parameterNames = getMetadataAdapter().getParameterNames(method);

            if (parameterNames.size() == 1) {
                final String from = parameterNames.get(0);
                final String to = getMetadataAdapter().getReturnTypeName(method);

                if (!to.equals("void") && (acceptResult(from) || acceptResult(to))) {
                    final String methodKey = getMetadataAdapter().getMethodFullKey(cls, method);
                    getStore().put(getConverterKey(from, to), methodKey);
                }
            }
        }
    }

    public static String getConverterKey(final String from, final String to) {
        return from + " to " + to;
    }

    public static String getConverterKey(final Class<?> from, final Class<?> to) {
        return getConverterKey(from.getName(), to.getName());
    }
}
