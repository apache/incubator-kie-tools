/*
 * Copyright 2014 JBoss Inc
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
package org.kie.workbench.common.screens.messageconsole.client.console;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import org.uberfire.commons.validation.PortablePreconditions;

import static com.google.gwt.dom.client.BrowserEvents.*;

public class HyperLinkCell extends AbstractCell<HyperLinkCell.HyperLink> {

    interface HyperLinkTemplate extends SafeHtmlTemplates {

        @Template("<a href=\"#\">{0}</a>")
        SafeHtml hyperLink( final SafeHtml label );
    }

    interface TextTemplate extends SafeHtmlTemplates {

        @Template("<span>{0}</span>")
        SafeHtml text( final String label );
    }

    /**
     * Inner class to contain the Hyper Link details
     */
    public static class HyperLink {

        private String label;
        private boolean isLink;

        public static HyperLink newLink( final String label ) {
            return new HyperLink( label,
                                  true );
        }

        public static HyperLink newText( final String label ) {
            return new HyperLink( label,
                                  false );
        }

        private HyperLink( final String label,
                           final boolean isLink ) {
            this.label = PortablePreconditions.checkNotNull( "label",
                                                             label );
            this.isLink = PortablePreconditions.checkNotNull( "isLink",
                                                              isLink );
        }

        public String getLabel() {
            return label;
        }

        public boolean isLink() {
            return isLink;
        }

    }

    private static HyperLinkTemplate hyperLinkTemplate = GWT.create( HyperLinkTemplate.class );
    private static TextTemplate textTemplate = GWT.create( TextTemplate.class );

    /**
     * Construct a new HyperLinkCell that will use a given
     * {@link SafeHtmlRenderer}.
     */
    public HyperLinkCell() {
        super( CLICK,
               KEYDOWN );
    }

    @Override
    public void onBrowserEvent( final Context context,
                                final Element parent,
                                final HyperLinkCell.HyperLink value,
                                final NativeEvent event,
                                final ValueUpdater<HyperLinkCell.HyperLink> valueUpdater ) {
        super.onBrowserEvent( context,
                              parent,
                              value,
                              event,
                              valueUpdater );
        if ( CLICK.equals( event.getType() ) ) {
            onEnterKeyDown( context,
                            parent,
                            value,
                            event,
                            valueUpdater );
        }
    }

    @Override
    protected void onEnterKeyDown( final Context context,
                                   final Element parent,
                                   final HyperLinkCell.HyperLink value,
                                   final NativeEvent event,
                                   final ValueUpdater<HyperLinkCell.HyperLink> valueUpdater ) {
        final Element element = event.getEventTarget().cast();
        if ( !parent.getFirstChildElement().equals( element ) ) {
            return;
        }
        event.preventDefault();
        event.stopPropagation();
        if ( valueUpdater != null ) {
            valueUpdater.update( value );
        }
    }

    @Override
    public void render( final Context context,
                        final HyperLink value,
                        final SafeHtmlBuilder sb ) {
        if ( value != null ) {
            if ( value.isLink() ) {
                sb.append( hyperLinkTemplate.hyperLink( SafeHtmlUtils.fromString( value.getLabel() ) ) );
            } else {
                sb.append( textTemplate.text( value.getLabel() ) );
            }
        }
    }

}
