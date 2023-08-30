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


package org.jboss.errai.ui.test.stylebinding.client.res;

import javax.inject.Singleton;

import com.google.gwt.dom.client.Style;

/**
 * @author Mike Brock
 */
@Singleton
public class StyleControl {
  private boolean admin = false;

  @AdminBinding
  private void adminStyleUpdate(Style style) {
    if (!admin) {
      style.setVisibility(Style.Visibility.HIDDEN);
    }
    else {
      style.setVisibility(Style.Visibility.VISIBLE);
    }
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }
}
