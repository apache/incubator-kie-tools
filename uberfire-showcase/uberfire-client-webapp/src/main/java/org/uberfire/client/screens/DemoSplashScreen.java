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
