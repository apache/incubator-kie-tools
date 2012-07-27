/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.SerializationException;


@RemoteServiceRelativePath("categoryService")
public interface CategoryService
        extends RemoteService {


    /**
     * @param categoryPath
     *            A "/" delimited path to a category.
     * @param callback
     */
    public String[] loadChildCategories(String categoryPath);

    /**
     * Return a a 2d array/grid of results for rules.
     * 
     * @param A
     *            "/" delimited path to a category.
     *            
     * @deprecated in favour of {@link loadRuleListForCategories(CategoryPageRequest)}
     */
    public TableDataResult loadRuleListForCategories(String categoryPath,
                                                     int skip,
                                                     int numRows,
                                                     String tableConfig) throws SerializationException;

    /**
     * Return a list of Assets by category.
     * 
     * @param request
     *            Request specific details
     */
    public PageResponse<CategoryPageRow> loadRuleListForCategories(CategoryPageRequest request) throws SerializationException;
    
    /**
     * This will create a new category at the specified path.
     */
    public Boolean createCategory(String path,
                                  String name,
                                  String description);
    
    /**
     * This will remove a category. A category must have no current assets
     * linked to it, or else it will not be able to be removed.
     * 
     * @param categoryPath
     *            The full path to the category. Any sub categories will also be
     *            removed.
     * @throws SerializationException
     *             For when it all goes horribly wrong.
     */
    public void removeCategory(String categoryPath) throws SerializationException;

    /**
     * Rename a category - taking in the full path, and just the new name.
     */
    public void renameCategory(String fullPathAndName,
                               String newName);


}
