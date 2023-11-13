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


package org.kie.workbench.common.stunner.client.widgets.explorer.navigator;

import com.google.gwt.safehtml.shared.SafeUri;
import org.uberfire.client.mvp.UberView;

public interface NavigatorItemView<P extends NavigatorItem> extends UberView<P> {

    NavigatorItemView setUUID(String uuid);

    NavigatorItemView setItemTitle(String title);

    NavigatorItemView setThumbData(String thumbData);

    NavigatorItemView setThumbUri(SafeUri safeUri);

    NavigatorItemView setItemPxSize(int width,
                                    int height);

    NavigatorItemView select();

    NavigatorItemView deselect();
}
