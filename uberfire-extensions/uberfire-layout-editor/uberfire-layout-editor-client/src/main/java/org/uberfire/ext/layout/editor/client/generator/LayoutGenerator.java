package org.uberfire.ext.layout.editor.client.generator;

import com.google.gwt.user.client.ui.Panel;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public interface LayoutGenerator {

    Panel build(LayoutTemplate layoutTemplate);
}
