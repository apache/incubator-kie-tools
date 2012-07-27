/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.shared;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.InboxPageRequest;
import org.drools.guvnor.client.rpc.InboxPageRow;
import org.drools.guvnor.client.rpc.LogPageRow;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.NewAssetConfiguration;
import org.drools.guvnor.client.rpc.NewAssetWithContentConfiguration;
import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.PermissionsPageRow;
import org.drools.guvnor.client.rpc.PushResponse;
import org.drools.guvnor.client.rpc.QueryMetadataPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.StatePageRequest;
import org.drools.guvnor.client.rpc.StatePageRow;
import org.drools.guvnor.client.rpc.TableConfig;
import org.drools.guvnor.client.rpc.TableDataResult;
import org.drools.guvnor.shared.api.PortableObject;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.util.LogEntry;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * This is what the remote service will implement, as a servlet. (in
 * hosted/debug mode, you could also use an implementation that was in-process).
 */
@Remote
public interface RepositoryService {

    /**
     * Return a a 2d array/grid of results for rules.
     * 
     * @param The
     *            name of the state.
     * @deprecated in favour of {@link loadRuleListForState(StatePageRequest)}
     */
    public TableDataResult loadRuleListForState(String state,
                                                int skip,
                                                int numRows,
                                                String tableConfig) throws SerializationException;

/*    *//**
     * Return a list of Assets by status
     * 
     * @param request
     *            Request specific details
     *//*
    public PageResponse<StatePageRow> loadRuleListForState(StatePageRequest request) throws SerializationException;*/

    /**
     * This will return a TableConfig of header names.
     * 
     * @param listName
     *            The name of the list that we are going to render.
     * @deprecated in favour of {@link AbstractPagedTable}
     */
    public TableConfig loadTableConfig(String listName);

    /**
     * Creates a brand new rule with the initial category. Return the UUID of
     * the item created. This will not check in the rule, but just leave it as
     * saved in the repo.
     */
    public String createNewRule(String ruleName,
                                String description,
                                String initialCategory,
                                String initialPackage,
                                String format) throws SerializationException;


    /**
     * Creates a brand new rule with the initial category. Return the UUID of
     * the item created. This will not check in the rule, but just leave it as
     * saved in the repo.
     */
    public String createNewRule(NewAssetConfiguration configuration) throws SerializationException;

    /**
     * Creates a brand new rule with the initial category. Return the UUID of
     * the item created. This will not check in the rule, but just leave it as
     * saved in the repo.
     */
    public String createNewRule(NewAssetWithContentConfiguration<? extends PortableObject> configuration) throws SerializationException;

    /**
     * Check whether an asset exists in a package
     * 
     * @param assetName
     * @param packageName
     * @return True if the asset already exists in the package
     * @throws SerializationException
     */
    public boolean doesAssetExistInModule(String assetName,
                                          String packageName) throws SerializationException;

    /**
     * Creates a new rule which is imported from global area. Return the UUID of
     * the item created. This will not check in the rule, but just leave it as
     * saved in the repo.
     */
    public String createNewImportedRule(String sharedAssetName,
                                        String initialPackage) throws SerializationException;

    /**
     * Delete un checked in Asset
     */
    public void deleteUncheckedRule(String ruleName);

/*    *//**
     * Clear the rules repositoty, Use at your own risk.
     *//*
    public void clearRulesRepository();*/

/*    *//**
     * This returns a list of workspaces
     *//*
    public String[] listWorkspaces();

    *//**
     * This creates a workspace
     *//*
    public void createWorkspace(String workspace);

    *//**
     * This removes a workspace
     *//*
    public void removeWorkspace(String workspace);

    *//**
     * This removes a workspace
     *//*
    public void updateWorkspace(String workspace,
                                String[] selectedModules,
                                String[] unselectedModules);*/

/*    *//**
     * Returns a list of valid states.
     *//*
    public String[] listStates() throws SerializationException;

    *//**
     * Create a state (status).
     * 
     * @return the UUID of the created StateItem.
     *//*
    public String createState(String name) throws SerializationException;

    *//**
     * Renames a state.
     * 
     * @param oldName
     *            states old name.
     * @param newName
     *            states new name.
     * @throws SerializationException
     *//*
    public void renameState(String oldName,
                            String newName) throws SerializationException;

    *//**
     * Removes a state.
     * 
     * @param name
     *            state name that will be removed.
     * @throws SerializationException
     *//*
    public void removeState(String name) throws SerializationException;*/

    /**
     * return custom selector names
     */
    public String[] getCustomSelectors() throws SerializationException;

    /**
     * This will list the last N log entryies logged by the server. For
     * debugging purposes in the GUI.
     * 
     * @deprecated in favour of {@link showLogEntries()}
     */
    public LogEntry[] showLog();

    /**
     * This will list log entries logged by the server. For debugging purposes
     * in the GUI. This is an equivalent function to {@link showLog()} which has
     * been deprecated in favour of DTO centric operations.
     */
    public PageResponse<LogPageRow> showLog(PageRequest request);

    /**
     * clean up the log entry.
     */

    public void cleanLog();

    /**
     * @param valuePairs
     *            key=value pairs to be interpolated into the expression.
     * @param expression
     *            The expression, which will then be eval'ed to generate a
     *            String[]
     */
    public String[] loadDropDownExpression(String[] valuePairs,
                                           String expression);

/*    *//**
     * Runs a full text search using JCR.
     * 
     * @param request
     * @return
     * @throws SerializationException
     *//*
    public PageResponse<QueryPageRow> queryFullText(QueryPageRequest request) throws SerializationException;

    *//**
     * Run a meta data search. All dates are in format as configured for the
     * system. Pass in null and they will not be included in the search (that
     * applies to any field).
     * 
     * @param qr
     * @param createdAfter
     * @param createdBefore
     * @param modifiedAfter
     * @param modifiedBefore
     * @param seekArchived
     * @param skip
     * @param numRows
     * @return
     * @throws SerializationException
     * @deprecated in favour of {@link queryMetaData(QueryPageRequest)}
     *//*
    public TableDataResult queryMetaData(final MetaDataQuery[] qr,
                                         Date createdAfter,
                                         Date createdBefore,
                                         Date modifiedAfter,
                                         Date modifiedBefore,
                                         boolean seekArchived,
                                         int skip,
                                         int numRows) throws SerializationException;

    *//**
     * Run a meta data search. All dates are in format as configured for the
     * system. Pass in null and they will not be included in the search (that
     * applies to any field).
     * 
     * @param request
     * @return
     * @throws SerializationException
     *//*
    public PageResponse<QueryPageRow> queryMetaData(QueryMetadataPageRequest request) throws SerializationException;*/

/*    *//**
     * @return A map of username : list of permission types for display reasons.
     * @deprecated in favour of {@link listUserPermissions(PageRequest)}
     *//*
    public Map<String, List<String>> listUserPermissions() throws DetailedSerializationException;

    *//**
     * @return A map of username : list of permission types for display reasons.
     *//*
    public PageResponse<PermissionsPageRow> listUserPermissions(PageRequest request) throws DetailedSerializationException;

    *//**
     * Loads the user permissions.
     * 
     * @param userName
     * @return A map of permission type to the targets it applies to.
     *//*
    public Map<String, List<String>> retrieveUserPermissions(String userName);

    *//**
     * Update the user permissions - takes the userName, and a map from
     * permission type to the list of targets it applies to.
     *//*
    public void updateUserPermissions(String userName,
                                      Map<String, List<String>> perms);

    *//**
     * List the available permission types.
     * 
     * @deprecated in favour of {@link listAvailablePermissionRoleTypes()}
     * @return
     *//*
    public String[] listAvailablePermissionTypes();

    *//**
     * List the available permission types.
     * 
     * @return
     *//*
    public List<String> listAvailablePermissionRoleTypes();*/
    
    public boolean isDoNotInstallSample();

    public void setDoNotInstallSample();
    
/*    *//**
     * Removes user security data.
     *//*
    public void deleteUser(String userName);

    *//**
     * create new user.
     *//*
    public void createUser(String userName);

    *//**
     * Subscribe for a "callback" for a given request.
     *//*
    public List<PushResponse> subscribe();

    *//**
     * Load the data for a given inbox for the currently logged in user.
     * 
     * @deprecated in favour of {@link loadInbox(InboxPageRequest)}
     *//*
    public TableDataResult loadInbox(String inboxName) throws DetailedSerializationException;*/

    /**
     * Load and process the repository configuration templates.
     */
    public String processTemplate(String name,
                                  Map<String, Object> data);

/*    *//**
     * Load the data for a given inbox for the currently logged in user.
     *//*
    public PageResponse<InboxPageRow> loadInbox(InboxPageRequest request) throws DetailedSerializationException;*/

    /**
     * Returns the Spring context elements specified by
     * SpringContextElementsManager
     * 
     * @return a Map containing the key,value pairs of data.
     * @throws DetailedSerializationException
     */
    public Map<String, String> loadSpringContextElementData() throws DetailedSerializationException;

}
