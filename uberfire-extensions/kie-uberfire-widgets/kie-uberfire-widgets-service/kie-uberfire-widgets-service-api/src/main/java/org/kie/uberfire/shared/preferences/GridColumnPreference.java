/*
 * Copyright 2014 JBoss by Red Hat.
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
package org.kie.uberfire.shared.preferences;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GridColumnPreference implements Comparable {

  private String name;
  private Integer position;
  private String width;

  public GridColumnPreference() {
  }

  public GridColumnPreference(String name, Integer position, String width) {
    this.name = name;
    this.position = position;
    this.width = width;
  }

  public String getName() {
    return name;
  }

  public Integer getPosition() {
    return position;
  }

  public String getWidth() {
    return width;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  @Override
  public int compareTo(Object o) {
    if (!(o instanceof GridColumnPreference)) {
      return 0;
    }
    if (position < ((GridColumnPreference) o).getPosition()) {
      return -1;
    } else if (position > ((GridColumnPreference) o).getPosition()) {
      return 1;
    } else {
      return 0;
    }

  }

}
