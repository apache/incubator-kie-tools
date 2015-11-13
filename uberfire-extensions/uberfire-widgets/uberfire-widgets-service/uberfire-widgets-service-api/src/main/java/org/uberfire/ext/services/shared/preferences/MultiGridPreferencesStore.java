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

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.HashMap;

@Portable
public class MultiGridPreferencesStore extends UserPreference {

  private String multiGridId;
  private HashMap<String,HashMap> gridsSettings =  new HashMap<String, HashMap>(  );
  private ArrayList<String> gridsIds = new ArrayList<String>(  );
  private String selectedGrid = "NONE";
  private int refreshInterval = 10;

  public MultiGridPreferencesStore() {
  }

  public MultiGridPreferencesStore( String multiGridId ) {
      this.multiGridId = multiGridId;
    super.type= UserPreferencesType.MULTIGRIDPREFERENCES;
    super.preferenceKey = multiGridId;
  }

  public String getMultiGridId() {
    return multiGridId;
  }

  public void setMultiGridId( String multiGridId ) {
    this.multiGridId = multiGridId;
  }

  public HashMap getGridSettings(String key){
    if(gridsSettings!=null) return gridsSettings.get( key );
    return null;
  }

  public String getGridSettingParam(String key, String paramId){
    HashMap<String,String> params = getGridSettings( key );
    if(params!=null) return params.get( paramId );
    return null;
  }

  public void setGridSettings(String key, HashMap params){
    gridsSettings.put( key,params );
  }



  public ArrayList<String>  getGridsId() {
    return gridsIds ;
  }

  public void addGridId(String gridKey) {
    getGridsId().add( gridKey );
  }

  public void addNewTab(String gridKey,HashMap gridsSettings){
    addGridId( gridKey );
    setGridSettings( gridKey,gridsSettings );
  }

  public void removeTab(String gridKey) {
    gridsSettings.remove( gridKey ) ;

    for(int i=0; i< getGridsId().size();i++){
      if(gridsIds.get( i ).equals( gridKey )){
        gridsIds.remove( i );
      }
    }
    if(isSelectedGrid( gridKey )) selectedGrid="";
  }


  public void setSelectedGrid( String gridKey ) {
    selectedGrid = gridKey;
  }

  public boolean isSelectedGrid( String gridKey ) {
    if(selectedGrid!=null){
      return selectedGrid.equals( gridKey );
    }
    return false;
  }
  public String getSelectedGrid(){
    return selectedGrid;
  }

  public int getRefreshInterval() {
    return refreshInterval;
  }

  public void setRefreshInterval( int refreshInterval ) {
    this.refreshInterval = refreshInterval;
  }
}
