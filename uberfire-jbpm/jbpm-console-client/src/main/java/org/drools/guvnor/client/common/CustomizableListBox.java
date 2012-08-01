package org.drools.guvnor.client.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

//TODO: This should be a popup with nice panels -Rikkola-
public class CustomizableListBox<T> implements IsWidget {

    private List<T> list = new ArrayList<T>();

    private ListBox listBox = new ListBox();
    private final ItemFormatter<T> formatter;

    private boolean hasFirstLine = false;

    public CustomizableListBox(ItemFormatter<T> formatter) {
        this.formatter = formatter;
    }

    public void addItem(T item) {
        list.add(item);
        listBox.addItem(formatter.format(item));
    }

    public Widget asWidget() {
        return listBox;
    }

    public void clear() {
        list.clear();
        listBox.clear();
    }

    public int getSelectedIndex() {
        if (hasFirstLine) {
            return listBox.getSelectedIndex() - 1;
        } else {
            return listBox.getSelectedIndex();
        }
    }

    public T getItem(int selectedIndex) {
        return list.get(selectedIndex);
    }

    public void setFirstLine(String line) {
        listBox.addItem(line);
    }

    public int getItemCount() {
        return list.size();
    }

    public void setSelectedIndex(int index) {
        listBox.setSelectedIndex(index);
    }

    public void setFocus(boolean b) {
        listBox.setFocus(b);
    }

    public interface ItemFormatter<T> {

        public String format(T t);
    }

    public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
        return listBox.addChangeHandler(changeHandler);
    }
}