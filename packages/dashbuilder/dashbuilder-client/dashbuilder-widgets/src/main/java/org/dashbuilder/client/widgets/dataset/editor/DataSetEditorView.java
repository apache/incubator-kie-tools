package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import org.dashbuilder.client.widgets.dataset.editor.attributes.*;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Text;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;

/**
 * <p>The Data Set Editor view.</p>
 *
 * @since 0.4.0
 */
@Dependent
public class DataSetEditorView extends Composite implements DataSetEditor.View {

    interface Binder extends UiBinder<Widget, DataSetEditorView> {
        Binder BINDER = GWT.create(Binder.class);
    }
    
    DataSetEditor presenter;

    @UiField
    TabListItem basicAttributesTabItem;

    @UiField
    TabListItem previewTabItem;

    @UiField
    TabListItem advancedAttributesTabItem;
    
    @UiField
    TabPane basicAttributesTabPane;

    @UiField
    TabPane previewTabPane;

    @UiField
    DisclosurePanel filterAndColumnsPanel;
    
    @UiField
    Button filterAndColumnsPanelToggleButton;
    
    @UiField
    TabPane advancedAttributesTabPane;
    
    @UiField(provided = true)
    DataSetDefBasicAttributesEditor.View basicAttributesEditorView;
    
    @UiField(provided = true)
    IsWidget providerAttributesEditorView;

    @UiField(provided = true)
    DataSetDefColumnsFilterEditor.View columnsAndFilterEditorView;

    @UiField
    FlowPanel previewTablePanel;
    
    @UiField(provided = true)
    DataSetDefPreviewTable.View previewTableView;

    @UiField
    Alert previewErrorNotification;
    
    @UiField
    Text notificationLabel;
    
    @UiField(provided = true)
    DataSetDefCacheAttributesEditorView backendCacheAttributesEditorView;

    @UiField(provided = true)
    DataSetDefCacheAttributesEditorView clientCacheAttributesEditorView;
    
    @UiField(provided = true)
    DataSetDefRefreshAttributesEditor.View refreshEditorView;
    
    @Override
    public void init(final DataSetEditor presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initWidgets(final DataSetDefBasicAttributesEditor.View basicAttributesEditorView,
                            final IsWidget providerAttributesEditorView,
                            final DataSetDefColumnsFilterEditor.View columnsAndFilterEditorView,
                            final DataSetDefPreviewTable.View previewTableView,
                            final DataSetDefCacheAttributesEditorView backendCacheAttributesEditorView,
                            final DataSetDefCacheAttributesEditorView clientCacheAttributesEditorView,
                            final DataSetDefRefreshAttributesEditor.View refreshEditorView) {
        this.basicAttributesEditorView = basicAttributesEditorView;
        this.providerAttributesEditorView = providerAttributesEditorView;
        this.columnsAndFilterEditorView = columnsAndFilterEditorView;
        this.previewTableView = previewTableView;
        this.backendCacheAttributesEditorView = backendCacheAttributesEditorView;
        this.clientCacheAttributesEditorView = clientCacheAttributesEditorView;
        this.refreshEditorView = refreshEditorView;
        initWidget(Binder.BINDER.createAndBindUi(this));
        basicAttributesTabItem.setDataTargetWidget(basicAttributesTabPane);
        previewTabItem.setDataTargetWidget(previewTabPane);
        advancedAttributesTabItem.setDataTargetWidget(advancedAttributesTabPane);
        filterAndColumnsPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
            @Override
            public void onOpen(final OpenEvent<DisclosurePanel> event) {
                presenter.onOpenColumnsFilterPanel();
            }
        });
        filterAndColumnsPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            @Override
            public void onClose(final CloseEvent<DisclosurePanel> event) {
                presenter.onCloseColumnsFilterPanel();
            }
        });
    }

    @Override
    public void setConfigurationTabTitle(String title) {
        basicAttributesTabItem.setText(title);
        basicAttributesTabItem.setTitle(title);
    }

    @Override
    public void showConfigurationTab() {
        basicAttributesTabItem.showTab();
    }

    @Override
    public void showPreviewTab() {
        previewTabItem.showTab();
    }

    @Override
    public void addPreviewTabItemClickHandler(final Command command) {
        previewTabItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                command.execute();
            }
        });
    }

    @Override
    public void showAdvancedTab() {
        advancedAttributesTabItem.showTab();
    }

    @Override
    public void addAdvancedTabItemClickHandler(final Command command) {
        advancedAttributesTabItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                command.execute();
            }
        });
    }

    @Override
    public void openColumnsFilterPanel(final String title) {
        filterAndColumnsPanelToggleButton.setTitle(title);
        filterAndColumnsPanelToggleButton.setIcon(IconType.STEP_BACKWARD);
        filterAndColumnsPanelToggleButton.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
    }

    @Override
    public void closeColumnsFilterPanel(final String title) {
        filterAndColumnsPanelToggleButton.setTitle(title);
        filterAndColumnsPanelToggleButton.setIcon(IconType.STEP_FORWARD);
        filterAndColumnsPanelToggleButton.getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
    }

    public void addConfigurationTabItemClickHandler(final Command command) {
        basicAttributesTabItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                command.execute();
            }
        });
    }

    public void showErrorNotification(final SafeHtml text) {
        notificationLabel.setText(text.asString());
        previewErrorNotification.setVisible(true);
        filterAndColumnsPanel.setVisible(false);
        previewTablePanel.setVisible(false);
    }

    public void clearErrorNotification() {
        notificationLabel.setText("");
        previewErrorNotification.setVisible(false);
        filterAndColumnsPanel.setVisible(true);
        previewTablePanel.setVisible(true);
    }
}
