package org.uberfire.client.editor;

import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;

@Templated
public class DynamicEditorView extends Composite {

    @Inject
    @DataField("text-area")
    private TextArea textArea;
    
}