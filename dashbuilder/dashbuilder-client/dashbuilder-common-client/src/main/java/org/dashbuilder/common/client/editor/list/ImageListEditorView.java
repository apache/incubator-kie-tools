package org.dashbuilder.common.client.editor.list;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;

/**
 * <p>An image list editor view contract.</p>
 *
 * @since 0.4.0
 */
public interface ImageListEditorView<T> extends UberView<ImageListEditor<T>> {

    ImageListEditorView<T> add(final SafeUri uri, final String width, final String height,
                final SafeHtml heading, final SafeHtml text,
                final boolean selected, final Command clickCommand);
    ImageListEditorView<T> setHelpContent(final String title, final String content, final Placement placement);
    ImageListEditorView<T> showError(final SafeHtml message);
    ImageListEditorView<T> clearError();
    ImageListEditorView<T> clear();
}