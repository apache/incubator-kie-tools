package ${package}.client.navbar;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.Header;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
public class ComplementNavAreaPresenter implements Header {

    public interface View extends IsWidget {

    }

    @Inject
    private View view;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getId() {
        return "ComplementArea";
    }

}