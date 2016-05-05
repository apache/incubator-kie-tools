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
import java.util.List;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GridGlobalPreferences {
  private static int DEFAULT_PAGE_SIZE=10;

  private String key;
  private List<String> initialColumns = new ArrayList<String>();
  private List<String> bannedColumns = new ArrayList<String>();
  private int pageSize =DEFAULT_PAGE_SIZE;

  public GridGlobalPreferences() {
  }

  public GridGlobalPreferences(String key, List<String> initialColumns, List<String> bannedColumns) {
    this.key = key;
    this.initialColumns = initialColumns;
    this.bannedColumns = bannedColumns;
  }

  public String getKey() {
    return key;
  }

  public List<String> getInitialColumns() {
    return initialColumns;
  }

  public List<String> getBannedColumns() {
    return bannedColumns;
  }

  public void setPageSize( int pageSize ) {
    this.pageSize = pageSize;
  }

  public int getPageSize(){
    return this.pageSize;
  }

}
