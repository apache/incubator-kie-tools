package ${package}.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.gwtbootstrap3.client.ui.Label;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

@Dependent
public class SimpleView extends Composite implements SimplePresenter.View {

    private FlowPanel container = new FlowPanel();

    private Label label = new Label( "Empty" );

    @PostConstruct
    public void setup() {
        initWidget( container );
        container.add( label );
    }

    @Override
    public void setValue( String value ) {
        label.setText( value );
    }
}