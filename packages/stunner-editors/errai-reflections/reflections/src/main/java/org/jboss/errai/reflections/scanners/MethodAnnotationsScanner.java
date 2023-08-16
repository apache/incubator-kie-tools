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

@SuppressWarnings({"unchecked"})
/** scans for method's annotations */
public class MethodAnnotationsScanner extends AbstractScanner {
    public void scan(final Object cls) {
        for (Object method : getMetadataAdapter().getMethods(cls)) {
            for (String methodAnnotation : (List<String>) getMetadataAdapter().getMethodAnnotationNames(method)) {
                if (acceptResult(methodAnnotation)) {
                    getStore().put(methodAnnotation, getMetadataAdapter().getMethodFullKey(cls, method));
                }
            }
        }
    }
}
