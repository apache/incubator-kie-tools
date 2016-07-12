#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.screens;

import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.client.local.api.IsElement;
import javax.enterprise.context.Dependent;

@Dependent
@Templated
public class HelloWorldUberfireView implements HelloWorldUberfirePresenter.View, IsElement {

    private HelloWorldUberfirePresenter presenter;

    public void init( HelloWorldUberfirePresenter presenter ) {
        this.presenter = presenter;
    }

}