package ${package}.client.navbar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import ${package}.client.resources.AppResource;
import org.uberfire.client.workbench.widgets.menu.PespectiveContextMenusPresenter;

/**
 * A stand-alone (i.e. devoid of Workbench dependencies) View
 */
public class ComplementNavAreaView
        extends Composite
        implements RequiresResize,
                   ComplementNavAreaPresenter.View {

    interface ViewBinder
            extends
            UiBinder<Panel, ComplementNavAreaView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField(provided = true)
    public Image logo;

    @UiField
    public FlowPanel contextMenuArea;

    @Inject
    private PespectiveContextMenusPresenter contextMenu;

    @PostConstruct
    public void init() {
        logo = new Image( AppResource.INSTANCE.images().ufUserLogo() );
        initWidget( uiBinder.createAndBindUi( this ) );
        contextMenuArea.add( contextMenu.getView() );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
//        panel.setPixelSize( width, height );
    }

}