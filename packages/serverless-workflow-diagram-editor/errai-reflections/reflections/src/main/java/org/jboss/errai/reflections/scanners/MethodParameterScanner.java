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

import org.jboss.errai.reflections.adapters.MetadataAdapter;

/** scans methods/constructors and indexes parameters, return type and parameter annotations */
@SuppressWarnings("unchecked")
public class MethodParameterScanner extends AbstractScanner {

    @Override
    public void scan(Object cls) {
        final MetadataAdapter md = getMetadataAdapter();

        for (Object method : md.getMethods(cls)) {

            String signature = md.getParameterNames(method).toString();
            if (acceptResult(signature)) {
                getStore().put(signature, md.getMethodFullKey(cls, method));
            }

            String returnTypeName = md.getReturnTypeName(method);
            if (acceptResult(returnTypeName)) {
                getStore().put(returnTypeName, md.getMethodFullKey(cls, method));
            }

            List<String> parameterNames = md.getParameterNames(method);
            for (int i = 0; i < parameterNames.size(); i++) {
                for (Object paramAnnotation : md.getParameterAnnotationNames(method, i)) {
                    if (acceptResult((String) paramAnnotation)) {
                        getStore().put((String) paramAnnotation, md.getMethodFullKey(cls, method));
                    }
                }
            }
        }
    }
}
