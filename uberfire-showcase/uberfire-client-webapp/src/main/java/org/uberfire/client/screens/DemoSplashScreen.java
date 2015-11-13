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

package org.uberfire.client.screens;

import java.util.Arrays;

import org.uberfire.client.annotations.SplashBodyHeight;
import org.uberfire.client.annotations.SplashFilter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchSplashScreen;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;

import com.github.gwtbootstrap.client.ui.base.TextNode;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

@WorkbenchSplashScreen(identifier = "demo.splash")
public class DemoSplashScreen {

    @WorkbenchPartTitle
    public String getTitle() {
        return "Cool Splash Screen!";
    }

    @WorkbenchPartView
    public Widget getView() {
        return new FlowPanel() {{
            add( new TextNode( "Cool!!" ) );
        }};
    }

    @SplashFilter
    public SplashScreenFilter getFilter() {
        return new SplashScreenFilterImpl( "demo.splash", true, Arrays.asList( "HomePerspective" ) );
    }

    @SplashBodyHeight
    public Integer getBodySize() {
        return 40;
    }

}
