/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.exception;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.config.rebind.EnvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Handles backend exceptions.</p>
 * 
 * @since 0.3.0 
 */
@ApplicationScoped
public class ExceptionManager {

    private static Logger log = LoggerFactory.getLogger(ExceptionManager.class);
    
    /**
     * <p>Return a <code>@Portable RuntimeException</code> that can be captured by client side widgets.</p>
     *  
     * @param e The exception that caused the error.
     * @return The portable exception to send to the client side.
     */
    public RuntimeException handleException(final Exception e) {
        log.error(e.getMessage(), e);
        if (e instanceof RuntimeException && EnvUtil.isPortableType(e.getClass()) ) {
            return (RuntimeException) e;
        }
        return new GenericPortableException( e.getMessage(), e );
    }
}
