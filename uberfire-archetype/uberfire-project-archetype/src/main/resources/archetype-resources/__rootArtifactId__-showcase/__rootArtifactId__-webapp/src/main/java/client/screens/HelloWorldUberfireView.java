#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.screens;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class HelloWorldUberfireView implements HelloWorldUberfirePresenter.View {

    @DataField
    @Inject
    Div container;

    private HelloWorldUberfirePresenter presenter;

    public void init( HelloWorldUberfirePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return container;
    }
}