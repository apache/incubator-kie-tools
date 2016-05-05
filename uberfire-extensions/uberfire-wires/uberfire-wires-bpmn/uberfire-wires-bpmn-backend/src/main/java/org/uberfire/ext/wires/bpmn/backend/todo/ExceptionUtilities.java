/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.bpmn.backend.todo;

import org.jboss.errai.config.rebind.EnvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When Bpmn moves to KIE-WB this class can be removed.
 */
public class ExceptionUtilities {

    private static final Logger logger = LoggerFactory.getLogger( ExceptionUtilities.class );

    /**
     * Helper to return a @Portable RuntimeException.
     * @param e
     * @return
     */
    public static RuntimeException handleException( final Exception e ) {
        logger.debug( "Exception thrown: " + e.getMessage(), e );
        if ( EnvUtil.isPortableType( e.getClass() ) ) {
            if ( e instanceof RuntimeException ) {
                return (RuntimeException) e;
            } else {
                return new GenericPortableException( e.getMessage(), e );
            }
        }
        return new GenericPortableException( e.getMessage(), e );
    }

}
