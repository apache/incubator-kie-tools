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
 * A Category request.
 */
public class CategoryPageRequest extends PageRequest {

    private String categoryPath;

    // For GWT serialisation
    public CategoryPageRequest() {
    }

    public CategoryPageRequest(String categoryPath,
                               int startRowIndex,
                               Integer pageSize) {
        super( startRowIndex,
               pageSize );
        this.categoryPath = categoryPath;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

}
