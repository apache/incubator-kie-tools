package org.dashbuilder.client.widgets.dataset.editor.csv;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Row;

import javax.enterprise.context.Dependent;

/**
 * <p>The CSV Data Set attributes editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class CSVDataSetDefAttributesEditorView extends Composite implements CSVDataSetDefAttributesEditor.View {

    interface Binder extends UiBinder<Widget, CSVDataSetDefAttributesEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    CSVDataSetDefAttributesEditor presenter;

    @UiField
    Row filePathRow;
    
    @UiField(provided = true)
    IsWidget filePathView;

    @UiField
    Row fileURLRow;

    @UiField(provided = true)
    ValueBoxEditor.View fileURLView;
    
    @UiField
    Button useFilePathButton;

    @UiField
    Button useFileURLButton;
    
    @UiField(provided = true)
    ValueBoxEditor.View sepCharView;

    @UiField(provided = true)
    ValueBoxEditor.View quoteCharView;

    @UiField(provided = true)
    ValueBoxEditor.View escCharView;

    @UiField(provided = true)
    ValueBoxEditor.View datePatternView;

    @UiField(provided = true)
    ValueBoxEditor.View numberPatternView;

    @Override
    public void init(final CSVDataSetDefAttributesEditor presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void initWidgets(final ValueBoxEditor.View fileURLView, final IsWidget filePathView,
                            final ValueBoxEditor.View sepCharView, final ValueBoxEditor.View quoteCharView,
                            final ValueBoxEditor.View escCharView, final ValueBoxEditor.View datePatternView,
                            final ValueBoxEditor.View numberPatternView) {
        this.fileURLView = fileURLView;
        this.filePathView = filePathView;
        this.sepCharView = sepCharView;
        this.quoteCharView = quoteCharView;
        this.escCharView = escCharView;
        this.datePatternView = datePatternView;
        this.numberPatternView = numberPatternView;
        initWidget(Binder.BINDER.createAndBindUi(this));
        useFilePathButton.addClickHandler(useFilePathButtonHandler);
        useFileURLButton.addClickHandler(useFileURLButtonHandler);
    }

    @Override
    public void showFilePathInput() {
        fileURLRow.setVisible(false);
        filePathRow.setVisible(true);
    }

    @Override
    public void showFileURLInput() {
        fileURLRow.setVisible(true);
        filePathRow.setVisible(false);
    }

    final ClickHandler useFilePathButtonHandler = new ClickHandler() {
        @Override
        public void onClick(final ClickEvent event) {
            presenter.onUseFilePathButtonClick();
        }
    };
    final ClickHandler useFileURLButtonHandler = new ClickHandler() {
        @Override
        public void onClick(final ClickEvent event) {
            presenter.onUseFileURLButtonClick();
        }
    };
}
