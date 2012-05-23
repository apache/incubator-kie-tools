/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.mvp;

import java.util.HashMap;
import java.util.Map;


public class PlaceRequest {

  private final String nameToken;

  private final Map<String, String> parameters = new HashMap<String, String>();


  public PlaceRequest(String nameToken) {
    this.nameToken = nameToken;
  }

  public String getNameToken() {
    return nameToken;
  }

  public String getParameter(String key, String defaultValue) {
    String value = null;

    if (parameters != null) {
      value = parameters.get(key);
    }

    if (value == null) {
      value = defaultValue;
    }
    return value;
  }

  public PlaceRequest parameter(String name, String value) {
      this.parameters.put(name, value);
      return this;
  }
  
  @Override
  public boolean equals(Object o) {
      if ( this == o ) return true;
      if ( o == null || getClass() != o.getClass() ) return false;

      PlaceRequest placeRequest = (PlaceRequest) o;

      if ( nameToken != null ? !nameToken.equals(placeRequest.getNameToken()) : placeRequest.getNameToken() != null ) return false;

      return true;
  }

  @Override
  public int hashCode() {
      return nameToken != null ? nameToken.hashCode() : 0;
  }

}
