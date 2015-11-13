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
package org.uberfire.ext.services.shared.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GridPreferencesStore extends UserPreference{

  private GridGlobalPreferences globalPreferences;
  private List<GridColumnPreference> columnPreferences = new ArrayList<GridColumnPreference>();
  private int pageSizePreferences;
  private String selectedFilterKey;
  private HashMap<String,HashMap> customFilters =new HashMap<String, HashMap>(  );

  public GridPreferencesStore() {
  }

  public GridPreferencesStore(GridGlobalPreferences globalPreferences) {
    this.globalPreferences = globalPreferences;
    if(globalPreferences!=null) {
      this.pageSizePreferences= globalPreferences.getPageSize();
    }
    super.type=UserPreferencesType.GRIDPREFERENCES;
    super.preferenceKey = globalPreferences.getKey();
  }

  public GridGlobalPreferences getGlobalPreferences() {
    return globalPreferences;
  }

  public List<GridColumnPreference> getColumnPreferences() {
    return columnPreferences;
  }

  public void addGridColumnPreference(GridColumnPreference preference) {
    columnPreferences.add(preference);
  }

  public void resetGridColumnPreferences(){
    columnPreferences.clear();
  }
  public void resetPageSizePreferences(){
    if(globalPreferences!=null) {
      this.pageSizePreferences= globalPreferences.getPageSize();
    }
  }

  public int getPageSizePreferences() {
    return pageSizePreferences;
  }

  public void setPageSizePreferences( int pageSizePreferences ) {
    this.pageSizePreferences = pageSizePreferences;
  }

  public String getSelectedFilterKey() {
    return selectedFilterKey;
  }

  public void setSelectedFilterKey( String selectedFilterKey ) {
    if(!"addFilter".equals( selectedFilterKey )) {
      this.selectedFilterKey = selectedFilterKey;
    }
  }

  public void addCustomFilter(String filterName, HashMap filterParams){
    customFilters.put(filterName,filterParams);
  }

  public HashMap getCustomFilters(){
    return customFilters;
  }

  public void removeCustomFilter(String filterName){
    customFilters.remove( filterName);
  }

  public void resetGridPreferences(){
    resetPageSizePreferences();
    resetGridColumnPreferences();
    selectedFilterKey="";
    customFilters.clear();
  }
}
