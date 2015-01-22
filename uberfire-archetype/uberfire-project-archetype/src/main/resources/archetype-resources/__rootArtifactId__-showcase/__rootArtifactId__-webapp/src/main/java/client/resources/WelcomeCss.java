#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.resources;

import com.google.gwt.resources.client.CssResource;

/**
 * TODO: update me
 */
public interface WelcomeCss extends CssResource {

    @ClassName("welcome-top")
    String welcomeTop();

    @ClassName("welcome-box")
    String welcomeBox();

    @ClassName("welcome-body")
    String welcomeBody();

    @ClassName("get-started")
    String getStarted();

    @ClassName("link-list")
    String linkList();

    String git();

    String downloads();
}
