package org.uberfire.client.screen.preview;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.screen.source.EditorTextContentChanged;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "HTMLPreview")
public class HTMLPreview
        extends Composite implements RequiresResize {

    interface HTMLPreviewBinder
            extends
            UiBinder<Widget, HTMLPreview> {

    }

    private static HTMLPreviewBinder uiBinder = GWT.create( HTMLPreviewBinder.class );

    @UiField
    protected HTML htmlContent;

    public HTMLPreview() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        final String content = placeRequest.getParameter( "content", null );
        htmlContent.setHTML( content );
    }

    public void onEditorChange( @Observes EditorTextContentChanged event ) {
        htmlContent.setHTML( event.getText() );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "HTML Preview";
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }

}
