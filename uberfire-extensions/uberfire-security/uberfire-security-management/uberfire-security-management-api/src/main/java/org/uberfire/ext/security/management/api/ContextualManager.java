/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.api;

/**
 * <p>You should implement this interface if your managed will be handled within the given EE context (eg: JavaEE CDI context).</p>.
 * 
 * @since 0.8.0
 */
public interface ContextualManager {

    /**
     * <p>Use this method to initialize your manager within the given context (usually JavaEE CDI).</p>
     * @param userSystemManager The users system manager instance for the given context.
     */
    void initialize(UserSystemManager userSystemManager) throws Exception;

    /**
     * <p>Use this method to destroy your manager within the given context (usually JavaEE CDI).</p>
     */
    void destroy() throws Exception;
}
