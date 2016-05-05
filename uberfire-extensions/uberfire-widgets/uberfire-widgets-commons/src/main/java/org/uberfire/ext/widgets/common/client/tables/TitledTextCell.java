/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import org.uberfire.ext.widgets.common.client.resources.CommonCss;
import org.uberfire.ext.widgets.common.client.resources.CommonResources;

/**
 * An extension to the normal TextCell that renders upto two rows of text; one
 * being the title and the other being narrative.
 */
public class TitledTextCell extends AbstractSafeHtmlCell<TitledTextCell.TitledText> {

    protected static final CommonCss css = CommonResources.INSTANCE.CSS();

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
     * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
     */
    public TitledTextCell( final SafeHtmlRenderer<TitledText> renderer ) {
        super( renderer );
    }

    @Override
    public void render( Context context,
                        SafeHtml value,
                        SafeHtmlBuilder sb ) {
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

        public TitledText( String title,
                           String description ) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int compareTo( TitledText o ) {
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

        public SafeHtml render( TitledText object ) {
            boolean bHasDescription = object.description != null && !"".equals( object.description );

            if ( bHasDescription ) {
                String html = "<div class='" + css.titleTextCellContainer() + "'>";
                html = html + "<div>" + object.title + "</div>";
                html = html + "<div class='" + css.titleTextCellDescription() + "'>" + object.description + "</div>";
                html = html + "</div>";
                return SafeHtmlUtils.fromTrustedString( html );
            } else {
                String html = "<div class='" + css.titleTextCellContainer() + "'>";
                html = html + "<div>" + object.title + "</div>";
                html = html + "</div>";
                return SafeHtmlUtils.fromTrustedString( html );
            }
        }

        public void render( TitledText object,
                            SafeHtmlBuilder builder ) {
            builder.append( render( object ) );
        }

    }

}
