package org.drools.guvnor.client.editors.enumeditor;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.drools.guvnor.client.resources.ShowcaseImages;

public class DeleteButtonCell extends ButtonCell {

    @Override
    public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
        ShowcaseImages images = GWT.create(ShowcaseImages.class);
        ImageResource imageResource = images.deleteItemSmall();
        sb.appendHtmlConstant("<input type=\"image\" src=\"" + imageResource.getURL() + "\"  tabindex=\"-1\">");

        sb.appendHtmlConstant("</input>");
    }
}
