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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.DropDown;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.mvp.Command;

/**
 * <p>The ImageListEditor view that uses a drop down as selector.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DropDownImageListEditorView<T> extends Composite implements DropDownImageListEditor.View<T> {

    interface Binder extends UiBinder<Widget, DropDownImageListEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    interface DropDownImageListEditorViewStyle extends CssResource {
        String errorPanel();
        String errorPanelWithError();
        String image();
    }

    @UiField
    DropDownImageListEditorViewStyle style;

    @UiField
    @Editor.Ignore
    HTMLPanel errorPanel;

    @UiField
    @Editor.Ignore
    FlowPanel helpPanel;
    
    @UiField
    @Editor.Ignore
    DropDown dropDown;

    @UiField
    @Editor.Ignore
    Anchor dropDownAnchor;

    @UiField
    @Editor.Ignore
    DropDownMenu dropDownMenu;

    @UiField
    @Editor.Ignore
    Tooltip errorTooltip;

    @Editor.Ignore
    Image currentTypeImage;

    @Editor.Ignore
    InlineLabel caret;

    Tooltip currentTypeImageTooltip;
    ImageListEditor<T> presenter;

    @Override
    public void init(final ImageListEditor<T> presenter) {
        this.presenter = presenter;
    }
    
    @UiConstructor
    public DropDownImageListEditorView() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        currentTypeImage = new Image();
        caret = new InlineLabel();
        caret.addStyleName( "caret" );
        caret.setVisible( true);

        dropDownAnchor.add( currentTypeImage );
        dropDownAnchor.add( caret );
        dropDownAnchor.setEnabled( true );

        currentTypeImageTooltip = new Tooltip(dropDown);
        currentTypeImageTooltip.setContainer("body");
        currentTypeImageTooltip.setShowDelayMs(100);
        currentTypeImage.addClickHandler(e -> currentTypeImageTooltip.hide());
        caret.addClickHandler(e -> currentTypeImageTooltip.hide());
        helpPanel.add(currentTypeImageTooltip);
    }

    @Override
    public ImageListEditorView<T> add(final SafeUri uri, final String width, final String height,
                                       final SafeHtml heading, final SafeHtml text,
                                       final boolean selected, final Command clickCommand) {

        if (selected) {
            currentTypeImage.setUrl( uri );
            currentTypeImage.setSize( width, height );
        }
        else {
            final Tooltip tooltip = new Tooltip();
            tooltip.setTitle(text.asString());
            tooltip.setContainer("body");
            tooltip.setPlacement(Placement.RIGHT);
            tooltip.setShowDelayMs(100);

            final Image image = new Image(uri);
            image.setWidth(width);
            image.setHeight(height);
            image.addStyleName(style.image());
            image.addClickHandler(e -> {
                tooltip.hide();
                clickCommand.execute();
            });

            tooltip.setWidget(image);
            dropDownMenu.add(image);
        }

        return this;
    }

    @Override
    public ImageListEditorView<T> setHelpContent(String title, String content, Placement placement) {
        currentTypeImageTooltip.setPlacement(placement);
        currentTypeImageTooltip.setTitle(content);
        currentTypeImageTooltip.hide();
        return this;
    }

    @Override
    public ImageListEditorView<T> showError(SafeHtml message) {
        errorTooltip.setTitle(message.asString());
        errorPanel.removeStyleName(style.errorPanel());
        errorPanel.addStyleName(style.errorPanelWithError());
        return this;
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
        dropDownMenu.clear();
        return this;
    }

    @Override
    public void setDropDown(boolean isDropDown) {
        dropDownAnchor.setEnabled( isDropDown );
        caret.setVisible( isDropDown );
    }
}
