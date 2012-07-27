/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.SerializationException;


/**
 * This is for more detailed reports to send back to the client.
 * Typically there is a short message and longer message. The longer one is used by support. The shorter one displayed by default.
 */
public class DetailedSerializationException extends SerializationException {
    private static final long serialVersionUID = 510l;

    private String longDescription;

    private List<BuilderResultLine> errs;
    public DetailedSerializationException() {}

    public DetailedSerializationException(String shortDescription, String longDescription) {
        super(shortDescription);
        this.longDescription = longDescription;
    }
    public DetailedSerializationException(String shortDescription, List<BuilderResultLine> errs) {
        super(shortDescription);
        this.errs = errs;
    }

    public String getLongDescription() {
        return this.longDescription;
    }
    public List<BuilderResultLine> getErrs(){
        return errs;
    }

}
