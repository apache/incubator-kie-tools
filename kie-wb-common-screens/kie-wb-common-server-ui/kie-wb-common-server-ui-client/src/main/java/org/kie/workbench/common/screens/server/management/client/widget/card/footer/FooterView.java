package org.kie.workbench.common.screens.server.management.client.widget.card.footer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class FooterView extends Composite
        implements FooterPresenter.View {

    @Inject
    @DataField("copy-url")
    Icon copyUrl;

    @Inject
    @DataField("url")
    Anchor url;

    @Inject
    @DataField("version")
    Span version;

    @Override
    public void setup( final String url,
                       final String version ) {
        this.url.setText( url );
        this.url.setHref( url );
        this.version.setText( version );
        this.copyUrl.getElement().setPropertyString( "data-clipboard-text", url );
        glueCopy( this.copyUrl.getElement() );
    }

    public static native void glueCopy( final Element element ) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;

}
