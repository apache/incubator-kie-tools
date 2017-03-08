#set($symbol_pound='#')
        #set($symbol_dollar='$')
        #set($symbol_escape='\' )
        package ${package}.client.screens;

        import org.uberfire.client.annotations.WorkbenchPartTitle;
        import org.uberfire.client.annotations.WorkbenchPartView;
        import org.uberfire.client.annotations.WorkbenchScreen;
        import org.uberfire.client.mvp.UberElement;
        import javax.enterprise.context.Dependent;
        import javax.inject.Inject;

@Dependent
@WorkbenchScreen(identifier = "HelloWorldScreen")
public class HelloWorldUberfirePresenter {

    @Inject
    private View view;

    @WorkbenchPartTitle
    public String getTitle() {
        return "HelloWorldScreen";
    }

    @WorkbenchPartView
    public UberElement<HelloWorldUberfirePresenter> getView() {
        return view;
    }

    public interface View extends UberElement<HelloWorldUberfirePresenter> {

    }
}