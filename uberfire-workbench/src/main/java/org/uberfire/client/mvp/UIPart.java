package org.uberfire.client.mvp;

import com.google.gwt.user.client.ui.IsWidget;

public class UIPart {

    private final String title;
    private final IsWidget titleDecoration;
    private final IsWidget widget;

    public UIPart( final String title,
                   final IsWidget titleDecoration,
                   final IsWidget widget ) {
        this.title = title;
        this.titleDecoration = titleDecoration;
        this.widget = widget;
    }

    public String getTitle() {
        return this.title;
    }

    public IsWidget getTitleDecoration() {
        return this.titleDecoration;
    }

    public IsWidget getWidget() {
        return this.widget;
    }
}
