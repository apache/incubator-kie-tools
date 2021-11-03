/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.exception;

import java.io.Serializable;

/**
 * <p>Default exception when a data set lookup fails.</p>
 * 
 * @since 0.3.0 
 */
public class DataSetLookupException extends RuntimeException implements Serializable {

    private String uuid;

    public DataSetLookupException() {
        
    }
    
    public DataSetLookupException(final String uuid) {
        this.uuid = uuid;
    }

    public DataSetLookupException(final String uuid, final String message ) {
        super( message );
        this.uuid = uuid;
    }

    public DataSetLookupException(final String uuid, final String message, Exception e ) {
        super( message, e );
        this.uuid = uuid;
    }

}
