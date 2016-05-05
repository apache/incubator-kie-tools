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
package org.uberfire.ext.editor.commons.backend.validation;

import java.lang.String;import org.uberfire.backend.vfs.Path;

/**
 * Generic validator for Path based resources
 */
public interface FileNameValidator {

    /**
     * Defines the validator priority in terms of validator resolution. Lower is lower priority.
     * @return the priority
     */
    public int getPriority();

    /**
     * Indicates if the current parameter path matched the current validator
     * @param fileName File name to validate (including extension)
     * @return true if matches, otherwise false
     */
    public boolean accept( final String fileName );

    /**
     * Indicates if the current parameter path matched the current validator
     * @param path Path to validate
     * @return true if matches, otherwise false
     */
    public boolean accept( final Path path );

    /**
     * Validate the value
     * @param value Value to be validated
     */
    public boolean isValid( final String value );

}
