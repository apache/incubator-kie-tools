package org.dashbuilder.common.client.editor.list;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.mvp.Command;

/**
 * <p>The ImageListEditor default view. It places images in an horizontal way.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class HorizImageListEditorView<T> extends Composite implements ImageListEditorView<T> {

    interface Binder extends UiBinder<Widget, HorizImageListEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    interface HorizImageListEditorViewStyle extends CssResource {
        String errorPanel();
        String errorPanelWithError();
        String image();
    }


    @UiField
    HorizImageListEditorViewStyle style;

    @UiField
    @Editor.Ignore
    HTMLPanel errorPanel;

    @UiField
    @Editor.Ignore
    FlowPanel helpPanel;
    
    @UiField
    @Editor.Ignore
    HorizontalPanel mainPanel;

    @UiField
    @Editor.Ignore
    Tooltip errorTooltip;

    ImageListEditor<T> presenter;

    @Override
    public void init(final ImageListEditor<T> presenter) {
        this.presenter = presenter;
    }
    
    @UiConstructor
    public HorizImageListEditorView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public ImageListEditorView<T> add(final SafeUri uri, final String width, final String height,
                                       final SafeHtml heading, final SafeHtml text, 
                                       final boolean selected, final Command clickCommand) {
        final VerticalPanel panel = new VerticalPanel();
        panel.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
        panel.setHeight("100%");

        final Image image = new Image(uri);
        image.setWidth(width);
        image.setHeight(height);
        image.addStyleName(style.image());
        final double alpha = selected ? 1 : 0.2;
        image.getElement().setAttribute("style", "filter: alpha(opacity=5);opacity: " + alpha);

        final Tooltip tooltip = new Tooltip();
        tooltip.setTitle( text.asString() );
        tooltip.setWidget(image);
        tooltip.setContainer("body");
        tooltip.setPlacement(Placement.BOTTOM);
        tooltip.setIsAnimated(false);
        tooltip.setShowDelayMs(100);

        final HTML label = new HTML(heading.asString());
        final HorizontalPanel labelPanel = new HorizontalPanel();
        labelPanel.setWidth("100%");
        labelPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
        labelPanel.add(label);

        panel.add(tooltip);
        panel.add(labelPanel);        
        mainPanel.add(panel);

        image.addClickHandler(e -> {
            tooltip.hide();
            tooltip.destroy();
            clickCommand.execute();
        });

        return this;
    }

    @Override
    public ImageListEditorView<T> setHelpContent(final String title, final String content, final Placement placement) {
        final Tooltip tooltip = new Tooltip(mainPanel);
        tooltip.setContainer("body");
        tooltip.setShowDelayMs(1000);
        tooltip.setPlacement(placement);
        tooltip.setTitle(content);
        helpPanel.add(tooltip);
        return this;
    }

    @Override
    public ImageListEditorView<T> showError(SafeHtml message) {
        errorTooltip.setTitle(message.asString());
        errorPanel.removeStyleName(style.errorPanel());
        errorPanel.addStyleName(style.errorPanelWithError());
        return null;
    }

    @Override
    public ImageListEditorView<T> clearError() {
        errorTooltip.setTitle("");
        errorPanel.removeStyleName(style.errorPanelWithError());
        errorPanel.addStyleName(style.errorPanel());
        return this;
    }

    @Override
    public ImageListEditorView<T> clear() {
        clearError();
        mainPanel.clear();
        return this;
    }
    
}
