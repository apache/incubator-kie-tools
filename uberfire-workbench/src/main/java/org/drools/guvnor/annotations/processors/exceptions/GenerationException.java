/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.annotations.processors.exceptions;

/**
 * An Exception relating to generation of classes
 */
public class GenerationException extends Exception {

    private static final long serialVersionUID = 1L;

    public GenerationException() {
        super();
    }

    public GenerationException(String msg) {
        super( msg );
    }

    public GenerationException(Throwable t) {
        super( t );
    }

    public GenerationException(String message,
                               Throwable cause) {
        super( message,
               cause );
    }

}
