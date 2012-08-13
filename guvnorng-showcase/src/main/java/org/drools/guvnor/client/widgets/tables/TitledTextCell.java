package org.drools.guvnor.client.widgets.tables;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.resources.TitledTextCellCss;
import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;
/**
 * An extension to the normal TextCell that renders upto two rows of text; one
 * being the title and the other being narrative.
 */
public class TitledTextCell extends AbstractSafeHtmlCell<TitledText> {

    protected static final TitledTextCellCss css = GuvnorResources.INSTANCE.titledTextCellCss();

    /**
     * Constructs a TitledTextCell that uses a
     * {@link TitledTextSafeHtmlRenderer} to render its text.
     */
    public TitledTextCell() {
        super( TitledTextSafeHtmlRenderer.getInstance() );
    }

    /**
     * Constructs a TextCell that uses the provided {@link SafeHtmlRenderer} to
     * render its text.
     * 
     * @param renderer
     *            a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
     */
    public TitledTextCell(SafeHtmlRenderer<TitledText> renderer) {
        super( renderer );
    }

    @Override
    public void render(Context context,
                       SafeHtml value,
                       SafeHtmlBuilder sb) {
        if ( value != null ) {
            sb.append( value );
        }
    }

    /**
     * Container for the Cell value; consisting of title and description
     */
    public static class TitledText
        implements
        Comparable<TitledText> {
        private String title;
        private String description;

        public TitledText(String title,
                          String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int compareTo(TitledText o) {
            return title.compareTo( o.title );
        }

    }

    /**
     * A renderer for TitledText values
     */
    public static class TitledTextSafeHtmlRenderer
        implements
        SafeHtmlRenderer<TitledText> {

        // Singleton
        private static TitledTextSafeHtmlRenderer instance;

        public static TitledTextSafeHtmlRenderer getInstance() {
            if ( instance == null ) {
                instance = new TitledTextSafeHtmlRenderer();
            }
            return instance;
        }

        private TitledTextSafeHtmlRenderer() {
        }

        public SafeHtml render(TitledText object) {
            boolean bHasDescription = object.description != null && !"".equals( object.description );

            if ( bHasDescription ) {
                String html = "<div class='" + css.container() + "'>";
                html = html + "<div>" + object.title + "</div>";
                html = html + "<div class='" + css.description() + "'>" + object.description + "</div>";
                html = html + "</div>";
                return SafeHtmlUtils.fromTrustedString( html );
            } else {
                String html = "<div class='" + css.container() + "'>";
                html = html + "<div>" + object.title + "</div>";
                html = html + "</div>";
                return SafeHtmlUtils.fromTrustedString( html );
            }
        }

        public void render(TitledText object,
                           SafeHtmlBuilder builder) {
            builder.append( render( object ) );
        }

    }

}
