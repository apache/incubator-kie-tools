package org.gwtproject.user.client.ui;


/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 8/12/21
 */
public class FakeWidget {

    private final Widget widget;

    public FakeWidget(Widget widget) {
        this.widget = widget;
    }

    public void init() {
        widget.onAttach();
    }
}
