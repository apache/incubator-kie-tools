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
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.markdown.Markdown;
import org.uberfire.client.screen.source.EditorTextContentChanged;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "MDPreview")
public class MDPreview
        extends Composite implements RequiresResize {

    interface MDPreviewBinder
            extends
            UiBinder<Widget, MDPreview> {

    }

    private static MDPreviewBinder uiBinder = GWT.create( MDPreviewBinder.class );

    @UiField
    protected Markdown markdown;

    public MDPreview() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @OnStart
    public void onStart( final PlaceRequest placeRequest ) {
        final String content = placeRequest.getParameter( "content", null );
        markdown.setContent( content );
    }

    public void onEditorChange( @Observes EditorTextContentChanged event ) {
        markdown.setContent( event.getText() );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Markdown Preview";
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }

}
