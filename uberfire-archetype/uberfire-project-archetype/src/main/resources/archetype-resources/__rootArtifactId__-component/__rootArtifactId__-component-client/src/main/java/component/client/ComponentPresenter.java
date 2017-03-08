#set($symbol_pound='#')
        #set($symbol_dollar='$')
        #set($symbol_escape='\' )
        package ${package}.component.client;

        import javax.annotation.PostConstruct;
        import javax.enterprise.context.Dependent;
        import javax.inject.Inject;

        import com.google.gwt.user.client.ui.IsWidget;
        import org.jboss.errai.common.client.api.Caller;
        import org.jboss.errai.common.client.api.RemoteCallback;
        import org.uberfire.client.mvp.UberElement;
        import ${package}.component.model.MyModel;
        import ${package}.component.service.MyService;
        import org.uberfire.client.annotations.WorkbenchPartTitle;
        import org.uberfire.client.annotations.WorkbenchPartView;
        import org.uberfire.client.annotations.WorkbenchScreen;

@WorkbenchScreen(identifier = "ComponentPresenter")
@Dependent
public class ComponentPresenter {

    @Inject
    private Caller<MyService> myService;
    @Inject
    private View view;

    @PostConstruct
    private void init() {
        myService.call(new RemoteCallback<MyModel>() {
            @Override
            public void callback(MyModel response) {
                view.setValue(response.getValue());
            }
        }).execute("hi");
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Remote Greetings";
    }

    @WorkbenchPartView
    public UberElement<ComponentPresenter> getView() {
        return view;
    }

    public interface View extends UberElement<ComponentPresenter> {

        void setValue(String value);
    }
}
