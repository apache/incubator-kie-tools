/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.rpc;

/**
 * A State request.
 */
public class StatePageRequest extends PageRequest {

    private String stateName;

    // For GWT serialisation
    public StatePageRequest() {
    }

    public StatePageRequest(String stateName,
                            int startRowIndex,
                            Integer pageSize) {
        super( startRowIndex,
               pageSize );
        this.stateName = stateName;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

}
