package org.uberfire.client.screens.splash;

import java.util.Arrays;

import com.github.gwtbootstrap.client.ui.base.TextNode;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.annotations.SplashBodyHeight;
import org.uberfire.client.annotations.SplashFilter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchSplashScreen;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;

@WorkbenchSplashScreen(identifier = "fileExplorer.splash")
public class FileExplorerSplashScreen {

    @WorkbenchPartTitle
    public String getTitle() {
        return "Cool Splash Screen!";
    }

    @WorkbenchPartView
    public Widget getView() {
        return new FlowPanel() {{
            add( new TextNode( "OI MUNDO!" ) );
        }};
    }

    @SplashFilter
    public SplashScreenFilter getFilter() {
        return new SplashScreenFilterImpl( "fileExplorer.splash", true, Arrays.asList( "FileExplorerPerspective", "FileExplorer" ) );
    }

    @SplashBodyHeight
    public Integer getBodySize() {
        return 40;
    }

}
