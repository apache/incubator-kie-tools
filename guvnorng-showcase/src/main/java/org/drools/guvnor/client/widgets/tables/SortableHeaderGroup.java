package org.drools.guvnor.client.widgets.tables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.cellview.client.CellTable;

public class SortableHeaderGroup<T extends Comparable> {

    private final CellTable<T> cellTable;
    // TODO change List into Deque after upgrade to java 6
    private List<SortableHeader<T, ?>> sortOrderList = new LinkedList<SortableHeader<T, ?>>();

    public SortableHeaderGroup(CellTable<T> cellTable) {
        this.cellTable = cellTable;
    }

    public void headerClicked(SortableHeader<T, ?> header) {
        updateSortOrder(header);
        cellTable.redrawHeaders();
        updateData();
    }

    private void updateSortOrder(SortableHeader<T, ?> header) {
        int index = sortOrderList.indexOf(header);
        if (index == 0) {
            if (header.getSortDirection() != SortDirection.ASCENDING) {
                header.setSortDirection(SortDirection.ASCENDING);
            } else {
                header.setSortDirection(SortDirection.DESCENDING);
            }
        } else {
            // Remove it if it's already sorted on this header later
            if (index > 0) {
                sortOrderList.remove(index);
            }
            header.setSortDirection(SortDirection.ASCENDING);
            // Bring this header to front // Deque.addFirst(sortableHeader)
            sortOrderList.add(0, header);
            // Update sortIndexes
            int sortIndex = 0;
            for (SortableHeader<T, ?> sortableHeader : sortOrderList) {
                sortableHeader.setSortIndex(sortIndex);
                sortIndex++;
            }
        }
    }

    private void updateData() {
        // TODO If paging is used, this should be a back-end call with a sorting meta data parameter
        List<T> displayedItems = new ArrayList<T>(cellTable.getDisplayedItems());
        Collections.sort(displayedItems, new Comparator<T>() {
            public int compare(T leftRow, T rightRow) {
                for (SortableHeader<T, ?> sortableHeader : sortOrderList) {
                    Comparable leftColumnValue = sortableHeader.getColumn().getValue(leftRow);
                    Comparable rightColumnValue = sortableHeader.getColumn().getValue(rightRow);
                    int comparison = (leftColumnValue == rightColumnValue) ? 0
                            : (leftColumnValue == null) ? -1
                            : (rightColumnValue == null) ? 1
                            : leftColumnValue.compareTo(rightColumnValue);
                    if (comparison != 0) {
                        switch (sortableHeader.getSortDirection()) {
                            case ASCENDING:
                                break;
                            case DESCENDING:
                                comparison = -comparison;
                                break;
                            default:
                                throw new IllegalStateException("Sorting can only be enabled for ASCENDING or" +
                                        " DESCENDING, not sortDirection (" + sortableHeader.getSortDirection() + ") .");
                        }
                        return comparison;
                    }
                }
                return leftRow.compareTo(rightRow);
            }
        });
        cellTable.setRowData(0, displayedItems);
        cellTable.redraw();
    }

}
