package org.guvnor.messageconsole.client.console.widget;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.messageconsole.client.console.resources.MessageConsoleResources;
import org.gwtbootstrap3.client.ui.html.Span;
import org.uberfire.client.views.pfly.icon.PatternFlyIconType;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

public class MessageTableWidget<T> extends Composite implements HasData<T> {

    SimpleTable<T> dataGrid;

    public enum Mode {
        SIMPLE,
        PAGED
    }

    public interface ColumnExtractor<V> {

        V getValue(Object row);
    }

    public MessageTableWidget() {
        dataGrid = new SimpleTable<T>();
        initWidget(dataGrid);
    }

    public MessageTableWidget(final Mode mode) {
        if (mode == Mode.SIMPLE) {
            dataGrid = new SimpleTable<T>();
        } else if (mode == Mode.PAGED) {
            dataGrid = new PagedTable<T>();
            dataGrid.setHeight("165px");
            dataGrid.setAlwaysShowScrollBars(false);
        }
        initWidget(dataGrid);
    }

    public MessageTableWidget(final SimpleTable dataGrid) {
        this.dataGrid = dataGrid;
        initWidget(dataGrid);
    }

    public void addLevelColumn(final int px,
                               final ColumnExtractor<Level> extractor) {
        final Column<T, ?> column = new Column<T, Level>(new IconCell()) {

            @Override
            public Level getValue(final T row) {
                return extractor.getValue(row);
            }

            @Override
            public void render(Cell.Context context,
                               T row,
                               SafeHtmlBuilder sb) {
                String title = getLevelTitle(extractor.getValue(row));
                sb.append(createDivStart(title,
                                         "",
                                         "text-center"));
                super.render(context,
                             row,
                             sb);
                sb.append(createDivEnd());
            }
        };
        dataGrid.addColumn(column,
                           MessageConsoleResources.CONSTANTS.Level());
        dataGrid.setColumnWidth(column,
                                px,
                                Style.Unit.PX);
    }

    public void addTextColumn(final int pct,
                              final ColumnExtractor<String> extractor) {
        Column<T, ?> column = new Column<T, String>(new TextCell()) {
            @Override
            public String getValue(T row) {
                return extractor.getValue(row);
            }

            @Override
            public void render(Cell.Context context,
                               T row,
                               SafeHtmlBuilder sb) {
                String title = extractor.getValue(row);
                sb.append(createDivStart(title));
                super.render(context,
                             row,
                             sb);
                sb.append(createDivEnd());
            }
        };
        dataGrid.addColumn(column,
                           MessageConsoleResources.CONSTANTS.Text());
        dataGrid.setColumnWidth(column,
                                pct,
                                Style.Unit.PCT);
    }

    private String getLevelTitle(Level level) {
        switch (level) {
            case ERROR:
                return MessageConsoleResources.CONSTANTS.ErrorLevelTitle();
            case WARNING:
                return MessageConsoleResources.CONSTANTS.WarningLevelTitle();
            case INFO:
            default:
                return MessageConsoleResources.CONSTANTS.InfoLevelTitle();
        }
    }

    public SafeHtml createDivStart(String title) {
        return createDivStart(title,
                              "");
    }

    public SafeHtml createDivStart(String title,
                                   String defaultValue) {
        return createDivStart(title,
                              defaultValue,
                              null);
    }

    public SafeHtml createDivStart(String title,
                                   String defaultValue,
                                   String cssClasses) {
        if (title == null || "".equals(title)) {
            title = defaultValue;
        }

        final String css = cssClasses == null ? "" : "class=\"" + cssClasses + "\"";
        return SafeHtmlUtils.fromTrustedString("<div title=\"" + SafeHtmlUtils.htmlEscape(title.trim()) + "\" " + css + " >");
    }

    public SafeHtml createDivEnd() {
        return SafeHtmlUtils.fromTrustedString("</div>");
    }

    private class IconCell extends AbstractCell<Level> {

        @Override
        public void render(Context context,
                           Level level,
                           SafeHtmlBuilder sb) {
            final Span icon = GWT.create(Span.class);
            icon.addStyleName("glyphicon");
            icon.addStyleName(getIconClass(level));
            sb.appendHtmlConstant(icon.getElement().getString());
        }

        private String getIconClass(final Level level) {
            switch (level) {
                case ERROR:
                    return PatternFlyIconType.ERROR_CIRCLE_O.getCssName();
                case WARNING:
                    return PatternFlyIconType.WARNING_TRIANGLE_O.getCssName();
                case INFO:
                default:
                    return PatternFlyIconType.INFO.getCssName();
            }
        }
    }

    public void setDataProvider(final AbstractDataProvider<T> dataProvider) {
        if (dataGrid instanceof PagedTable) {
            ((PagedTable<T>) dataGrid).setDataProvider(dataProvider);
        }
    }

    public final AbstractDataProvider<T> getDataProvider() {
        if (dataGrid instanceof PagedTable) {
            return ((PagedTable<T>) dataGrid).getDataProvider();
        }
        return null;
    }

    public void setToolBarVisible(final boolean visible) {
        dataGrid.setToolBarVisible(visible);
    }

    public ColumnSortList getColumnSortList() {
        return dataGrid.getColumnSortList();
    }

    public HasWidgets getToolbar() {
        return dataGrid.getToolbar();
    }

    public HasWidgets getRightToolbar() {
        return dataGrid.getRightToolbar();
    }

    public HasWidgets getRightActionsToolbar() {
        return dataGrid.getRightActionsToolbar();
    }

    public HasWidgets getLeftToolbar() {
        return dataGrid.getLeftToolbar();
    }

    public HasWidgets getCenterToolbar() {
        return dataGrid.getCenterToolbar();
    }

    public void setRowStyles(final RowStyles<T> styles) {
        dataGrid.setRowStyles(styles);
    }

    public void setColumnPickerButtonVisible(final boolean show) {
        dataGrid.setColumnPickerButtonVisible(show);
    }

    public void addColumn(final Column<T, ?> lineColumn,
                          final String line) {
        dataGrid.addColumn(lineColumn,
                           line);
    }

    public void setColumnWidth(final Column<T, ?> lineColumn,
                               final int i,
                               final Style.Unit pct) {
        dataGrid.setColumnWidth(lineColumn,
                                i,
                                pct);
    }

    @Override
    public void setHeight(final String height) {
        dataGrid.setHeight(height);
    }

    @Override
    public void setPixelSize(final int width,
                             final int height) {
        dataGrid.setPixelSize(width,
                              height);
    }

    @Override
    public void setSize(final String width,
                        final String height) {
        dataGrid.setSize(width,
                         height);
    }

    @Override
    public void setWidth(final String width) {
        dataGrid.setWidth(width);
    }

    @Override
    public SelectionModel<? super T> getSelectionModel() {
        return dataGrid.getSelectionModel();
    }

    @Override
    public T getVisibleItem(final int indexOnPage) {
        return dataGrid.getVisibleItem(indexOnPage);
    }

    @Override
    public int getVisibleItemCount() {
        return dataGrid.getVisibleItemCount();
    }

    @Override
    public Iterable<T> getVisibleItems() {
        return dataGrid.getVisibleItems();
    }

    @Override
    public void setRowData(final int start,
                           final List<? extends T> values) {
        dataGrid.setRowData(start,
                            values);
    }

    public void setRowData(final List<? extends T> values) {
        dataGrid.setRowData(values);
    }

    public void redraw() {
        dataGrid.redraw();
    }

    @Override
    public void setSelectionModel(final SelectionModel<? super T> selectionModel) {
        dataGrid.setSelectionModel(selectionModel);
    }

    @Override
    public void setVisibleRangeAndClearData(final Range range,
                                            final boolean forceRangeChangeEvent) {
        dataGrid.setVisibleRangeAndClearData(range,
                                             forceRangeChangeEvent);
    }

    @Override
    public HandlerRegistration addCellPreviewHandler(final CellPreviewEvent.Handler<T> handler) {
        return dataGrid.addCellPreviewHandler(handler);
    }

    @Override
    public HandlerRegistration addRangeChangeHandler(final RangeChangeEvent.Handler handler) {
        return dataGrid.addRangeChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addRowCountChangeHandler(final RowCountChangeEvent.Handler handler) {
        return dataGrid.addRowCountChangeHandler(handler);
    }

    @Override
    public int getRowCount() {
        return dataGrid.getRowCount();
    }

    @Override
    public Range getVisibleRange() {
        return dataGrid.getVisibleRange();
    }

    @Override
    public boolean isRowCountExact() {
        return dataGrid.isRowCountExact();
    }

    @Override
    public void setRowCount(final int count) {
        dataGrid.setRowCount(count);
    }

    @Override
    public void setRowCount(final int count,
                            final boolean isExact) {
        dataGrid.setRowCount(count,
                             isExact);
    }

    @Override
    public void setVisibleRange(final int start,
                                final int length) {
        dataGrid.setVisibleRange(length,
                                 length);
    }

    @Override
    public void setVisibleRange(final Range range) {
        dataGrid.setVisibleRange(range);
    }

    public void setToolbarVisible(final boolean visible) {
        this.dataGrid.setToolBarVisible(visible);
    }
}

