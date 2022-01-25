/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.reflections.scanners;

import java.util.List;

/**
 *
 */
@SuppressWarnings({"unchecked"})
public class MethodParametersAnnotationsScanner extends AbstractScanner {
    public void scan(final Object cls) {
        String className = getMetadataAdapter().getClassName(cls);

        List<Object> methods = getMetadataAdapter().getMethods(cls);
        for (Object method : methods) {
            List<String> parameters = getMetadataAdapter().getParameterNames(method);
            for (int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
                List<String> parameterAnnotations = getMetadataAdapter().getParameterAnnotationNames(method, parameterIndex);
                for (String parameterAnnotation : parameterAnnotations) {
                    if (acceptResult(parameterAnnotation)) {
                        getStore().put(parameterAnnotation, String.format("%s.%s:%s %s", className, method, parameters.get(parameterIndex), parameterAnnotation));
                    }
                }
            }
        }
    }
}
