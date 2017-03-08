#set($symbol_pound='#')
        #set($symbol_dollar='$')
        #set($symbol_escape='\' )
        package ${package}.client.navbar;

        import org.jboss.errai.common.client.dom.DOMUtil;
        import org.jboss.errai.common.client.dom.Div;
        import org.jboss.errai.ioc.client.api.AfterInitialization;
        import org.jboss.errai.ui.shared.api.annotations.DataField;
        import org.jboss.errai.ui.shared.api.annotations.Templated;
        import org.uberfire.client.workbench.Header;
        import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;

        import javax.enterprise.context.ApplicationScoped;
        import javax.inject.Inject;

        import static java.lang.Integer.MAX_VALUE;

@ApplicationScoped
@Templated
public class AppNavBar implements Header {

    @Inject
    @DataField
    Div header;

    @Inject
    private WorkbenchMenuBarPresenter menuBarPresenter;

    @AfterInitialization
    public void setup() {
        DOMUtil.appendWidgetToElement(header,
                                      menuBarPresenter.getView().asWidget());
    }

    @Override
    public String getId() {
        return "AppNavBar";
    }

    @Override
    public int getOrder() {
        return MAX_VALUE;
    }
}

