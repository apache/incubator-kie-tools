package org.kie.uberfire.perspective.editor.client.api;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;

public interface ExternalPerspectiveEditorComponent {

    void setup(final String placeName,
               final Map<String, String> parameters);

    String getPlaceName();

    Map<String,String> getParametersMap();

    IsWidget getConfig();

    IsWidget getPreview(final Map<String, String> parameters);
}
