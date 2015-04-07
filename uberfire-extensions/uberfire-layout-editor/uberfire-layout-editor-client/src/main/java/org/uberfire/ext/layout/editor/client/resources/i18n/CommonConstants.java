package org.uberfire.ext.layout.editor.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface CommonConstants
        extends
        Messages {

    public static final CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String DragAndDrop();

    String InvalidGridConfiguration();

    String InvalidTagName();

    String InvalidParameterName();

    String InvalidActivityID();

    String InvalidMenuLabel();

    String DuplicateParameterName();

    String GridSystem();

    String Components();

    String InvalidLayoutName();
}