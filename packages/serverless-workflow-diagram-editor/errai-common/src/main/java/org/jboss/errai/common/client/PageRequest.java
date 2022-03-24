/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * @author edewit@redhat.com
 */
@Portable
public class PageRequest {
  private final String pageName;
  private final Map<String, Object> state;

  public PageRequest(@MapsTo("pageName") String pageName, @MapsTo("state") Map<String, Object> state) {
    this.pageName = pageName;
    this.state = state;
  }

  public String getPageName() {
    return pageName;
  }

  public Map<String, Object> getState() {
    return state;
  }
}
