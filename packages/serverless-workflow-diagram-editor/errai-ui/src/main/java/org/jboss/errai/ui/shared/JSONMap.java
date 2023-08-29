/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.ui.shared;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A JSON map of key-value data.
 * @author eric.wittmann@redhat.com
 */
public final class JSONMap extends JavaScriptObject {

  public static native JSONMap create(String jsonData) /*-{
    return eval('['+jsonData+']')[0];
  }-*/;

  /**
   * Constructor.
   */
  protected JSONMap() {
  }

  /**
   * Get a single value from the map.
   * @param key
   */
  public final native String get(String key) /*-{
    return String(this[key]);
  }-*/;

  /**
   * @return all the keys in the map
   */
  public final Set<String> keys() {
    HashSet<String> s = new HashSet<String>();
    addKeys(s);
    return s;
  }

  /**
   * Adds all the keys in the json map to the given set.
   * @param s
   */
  private native void addKeys(HashSet<String> set) /*-{
    for (var key in this) {
      set.@java.util.HashSet::add(Ljava/lang/Object;)(key);
    }
  }-*/;

}
