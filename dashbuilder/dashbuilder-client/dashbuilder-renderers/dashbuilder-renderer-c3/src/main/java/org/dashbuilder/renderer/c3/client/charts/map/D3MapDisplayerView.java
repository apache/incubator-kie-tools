/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.c3.client.charts.map;

import javax.inject.Inject;

import org.dashbuilder.renderer.c3.client.C3AbstractDisplayerView;
import org.dashbuilder.renderer.c3.client.charts.map.widgets.D3Map;

import jsinterop.base.Js;

public class D3MapDisplayerView extends C3AbstractDisplayerView<D3MapDisplayer> 
                                                      implements D3MapDisplayer.View {
    
    D3Map map;
    
    @Inject     
    public D3MapDisplayerView(D3Map map) {
        this.map = map;
    }

    @Override
    public void init(D3MapDisplayer presenter) {
        super.init(presenter);
    }

    @Override
    public String getColumnsTitle() {
        return "Columns";
    }

    @Override
    public String getGroupsTitle() {
        return "Groups";
    }

    @Override
    public void createMap(D3MapConf conf) {
        displayerPanel.getElement().removeAllChildren();
        map.generateMap(width, height, conf);
        displayerPanel.getElement().appendChild(Js.cast( map.getElement()));
    }

}