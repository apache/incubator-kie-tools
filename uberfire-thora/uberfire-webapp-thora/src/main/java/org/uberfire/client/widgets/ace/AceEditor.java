// Copyright (c) 2011-2012, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.uberfire.client.widgets.ace;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A GWT widget for the Ajax.org Code Editor (ACE).
 * @see <a href="http://ace.ajax.org/">Ajax.org Code Editor</a>
 */
public class AceEditor extends Composite implements RequiresResize {

    // Used to generate unique element ids for Ace widgets.
    private static int nextId = 0;

    private Node node;

    private JavaScriptObject editor;

    private JsArray<AceAnnotation> annotations = JavaScriptObject.createArray().cast();

    /**
     * Preferred constructor.
     * You should pass <code>true</code> to this constructor,
     * unless you did something special to redefine the <code>.ace_editor</code>
     * CSS class.
     * is set with <code>position: absolute;</code>, which is
     * the default; false if <code>.ace_editor</code> is set to
     * use <code>position: relative;</code>
     */
    public AceEditor( final Element element ) {
//		if (!positionAbsolute) {
//			// Create a single div with width/height 100% with the generated
//			// element id.  The ACE editor will replace this div.
//			// Note that the .ace_editor style must be set with
//			// "position: relative !important;" for this this to work.
//			html = new HTML("<div style=\"width: 100%; height: 100%;\" id=\"" + elementId + "\"></div>");
//            node = html.getElement().getChild( 0 );
//		} else {
//			// Create a div with "position: relative;" that will expand to fill its parent.
//			// Then nest a div with the generated element id inside it.
//			// The ACE editor will replace the inner div.  Because ACE defaults
//			// to absolute positioning, we can set left/right/top/bottom to 0,
//			// causing ACE to completely expand to fill the outer div.
//			html = new HTML(
//					"<div style=\"width: 100%; height: 100%; position: relative;\">" +
//					"<div style=\"top: 0px; bottom: 0px; left: 0px; right: 0px;\" id=\"" + elementId + "\"></div>" +
//					"</div>"
//					);
//            node = html.getElement().getChild( 0 ).getChild( 0 );
//		}
//

        final SimplePanel panel = new SimplePanel();
        panel.getElement().getStyle().setPosition( Style.Position.RELATIVE );

        initWidget( panel );

        startEditor( panel.getElement() );

        element.appendChild( panel.getElement() );
    }

    /**
     * Call this method to start the editor.
     * Make sure that the widget has been attached to the DOM tree
     * before calling this method.
     */
    private native void startEditor( final Element element ) /*-{
        var editor = $wnd.ace.edit(element);
        editor.getSession().setUseWorker(false);
        this.@org.uberfire.client.widgets.ace.AceEditor::editor = editor;

        // I have been noticing sporadic failures of the editor
        // to display properly and receive key/mouse events.
        // Try to force the editor to resize and display itself fully.  See:
        //    https://groups.google.com/group/ace-discuss/browse_thread/thread/237262b521dcea33
        editor.resize();
        this.@org.uberfire.client.widgets.ace.AceEditor::redisplay();
    }-*/;

    /**
     * Call this to force the editor contents to be redisplayed.
     * There seems to be a problem when an AceEditor is embedded in a LayoutPanel:
     * the editor contents don't appear, and it refuses to accept focus
     * and mouse events, until the browser window is resized.
     * Calling this method works around the problem by forcing
     * the underlying editor to redisplay itself fully. (?)
     */
    public native void redisplay() /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.renderer.onResize(true);
        editor.renderer.updateFull();
        editor.resize();
        editor.focus();
    }-*/;

    /**
     * Cleans up the entire editor.
     */
    public native void destroy() /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.destroy();
    }-*/;

    /**
     * Set the theme.
     * @param theme the theme (one of the values in the {@link AceEditorTheme}
     * enumeration)
     */
    public void setTheme( final AceEditorTheme theme ) {
        setThemeByName( theme.getName() );
    }

    /**
     * Set the theme by name.
     * @param themeName the theme name (e.g., "twilight")
     */
    public native void setThemeByName( String themeName ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.setTheme("ace/theme/" + themeName);
    }-*/;

    /**
     * Set the mode.
     * @param mode the mode (one of the values in the
     * {@link AceEditorMode} enumeration)
     */
    public void setMode( final AceEditorMode mode ) {
        setModeByName( mode.getName() );
    }

    /**
     * Set the mode by name.
     * @param shortModeName name of mode (e.g., "eclipse")
     */
    public native void setModeByName( String shortModeName ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        var modeName = "ace/mode/" + shortModeName;
        editor.getSession().setMode(modeName);
    }-*/;

    /**
     * Register a handler for change events generated by the editor.
     * @param callback the change event handler
     */
    public native void addOnChangeHandler( AceEditorCallback callback ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.getSession().on("change", function (e) {
            callback.@org.uberfire.client.widgets.ace.AceEditorCallback::invokeAceCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
        });
    }-*/;

    /**
     * Register a handler for cursor position change events generated by the editor.
     * @param callback the cursor position change event handler
     */
    public native void addOnCursorPositionChangeHandler( AceEditorCallback callback ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.getSession().selection.on("changeCursor", function (e) {
            callback.@org.uberfire.client.widgets.ace.AceEditorCallback::invokeAceCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
        });
    }-*/;

    /**
     * Get the complete text in the editor as a String.
     * @return the text in the editor
     */
    public native String getText() /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        return editor.getSession().getValue();
    }-*/;

    /**
     * Set the complete text in the editor from a String.
     * @param text the text to set in the editor
     */
    public native void setText( String text ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.getSession().setValue(text);
    }-*/;

    /**
     * Insert given text at the cursor.
     * @param text text to insert at the cursor
     */
    public native void insertAtCursor( String text ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.insert(text);
    }-*/;

    /**
     * Get the current cursor position.
     * @return the current cursor position
     */
    public native AceEditorCursorPosition getCursorPosition() /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        var pos = editor.getCursorPosition();
        return this.@org.uberfire.client.widgets.ace.AceEditor::getCursorPositionImpl(DD)(pos.row, pos.column);
    }-*/;

    private AceEditorCursorPosition getCursorPositionImpl( final double row,
                                                           final double column ) {
        return new AceEditorCursorPosition( (int) row, (int) column );
    }

    /**
     * Set whether or not soft tabs should be used.
     * @param useSoftTabs true if soft tabs should be used, false otherwise
     */
    public native void setUseSoftTabs( boolean useSoftTabs ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.getSession().setUseSoftTabs(useSoftTabs);
    }-*/;

    /**
     * Set tab size.  (Default is 4.)
     * @param tabSize the tab size to set
     */
    public native void setTabSize( int tabSize ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.getSession().setTabSize(tabSize);
    }-*/;

    /**
     * Go to given line.
     * @param line the line to go to
     */
    public native void gotoLine( int line ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.gotoLine(line);
    }-*/;

    /**
     * Set whether or not the horizontal scrollbar is always visible.
     * @param hScrollBarAlwaysVisible true if the horizontal scrollbar is always
     * visible, false if it is hidden when not needed
     */
    public native void setHScrollBarAlwaysVisible( boolean hScrollBarAlwaysVisible ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.renderer.setHScrollBarAlwaysVisible(hScrollBarAlwaysVisible);
    }-*/;

    /**
     * Set whether or not the gutter is shown.
     * @param showGutter true if the gutter should be shown, false if it should be hidden
     */
    public native void setShowGutter( boolean showGutter ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.renderer.setShowGutter(showGutter);
    }-*/;

    /**
     * Set or unset read-only mode.
     * @param readOnly true if editor should be set to readonly, false if the
     * editor should be set to read-write
     */
    public native void setReadOnly( boolean readOnly ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.setReadOnly(readOnly);
    }-*/;

    /**
     * Set or unset highlighting of currently selected word.
     * @param highlightSelectedWord true to highlight currently selected word, false otherwise
     */
    public native void setHighlightSelectedWord( boolean highlightSelectedWord ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.setHighlightSelectedWord(highlightSelectedWord);
    }-*/;

    /**
     * Set or unset the visibility of the print margin.
     * @param showPrintMargin true if the print margin should be shown, false otherwise
     */
    public native void setShowPrintMargin( boolean showPrintMargin ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.renderer.setShowPrintMargin(showPrintMargin);
    }-*/;

    /**
     * Add an annotation to a the local <code>annotations</code> JsArray<AceAnnotation>, but does not set it on the editor
     * @param row to which the annotation should be added
     * @param column to which the annotation applies
     * @param text to display as a tooltip with the annotation
     * @param type to be displayed (one of the values in the
     * {@link AceAnnotationType} enumeration)
     */
    public void addAnnotation( final int row,
                               final int column,
                               final String text,
                               final AceAnnotationType type ) {
        annotations.push( AceAnnotation.create( row, column, text, type.getName() ) );
    }

    /**
     * Set any annotations which have been added via <code>addAnnotation</code> on the editor
     */
    public native void setAnnotations() /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        var annotations = this.@org.uberfire.client.widgets.ace.AceEditor::annotations;
        editor.getSession().setAnnotations(annotations);
    }-*/;

    /**
     * Clear any annotations from the editor and reset the local <code>annotations</code> JsArray<AceAnnotation>
     */
    public native void clearAnnotations() /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.getSession().clearAnnotations();
        this.@org.uberfire.client.widgets.ace.AceEditor::resetAnnotations()();
    }-*/;

    /**
     * Reset any annotations in the local <code>annotations</code> JsArray<AceAnnotation>
     */
    private void resetAnnotations() {
        annotations = JavaScriptObject.createArray().cast();
    }

    /**
     * Remove a command from the editor.
     * @param command the command (one of the values in the
     * {@link AceCommand} enumeration)
     */
    public void removeCommand( final AceCommand command ) {
        removeCommandByName( command.getName() );
    }

    /**
     * Remove commands, that may not me required, from the editor
     * @param command to be removed, one of
     * "gotoline", "findnext", "findprevious", "find", "replace", "replaceall"
     */
    public native void removeCommandByName( String command ) /*-{
        var editor = this.@org.uberfire.client.widgets.ace.AceEditor::editor;
        editor.commands.removeCommand(command);
    }-*/;

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.ResizeComposite#onResize()
     */
    @Override
    public void onResize() {
        redisplay();
    }
}
