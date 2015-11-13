/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.ace;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * A GWT widget for the Ajax.org Code Editor (ACE).
 * @see <a href="http://ace.ajax.org/">Ajax.org Code Editor</a>
 */
public class AceEditor extends Composite implements RequiresResize,
                                                    HasText,
                                                    TakesValue<String> {

    // Used to generate unique element ids for Ace widgets.
    private static int nextId = 0;

    private final String elementId;

    private JavaScriptObject editor;

    private JsArray<AceAnnotation> annotations = JavaScriptObject.createArray().cast();

    private Element divElement;

    /**
     * Preferred constructor.
     */
    public AceEditor() {
        elementId = "_aceGWT" + nextId;
        nextId++;
        FlowPanel div = new FlowPanel();
        div.getElement().setId( elementId );
        initWidget( div );
        divElement = div.getElement();
        getElement().setAttribute( "data-uf-lock-on-click", "false" );
    }

    /**
     * Do not use this constructor: just use the default constructor.
     */
    @Deprecated
    public AceEditor( boolean unused ) {
        this();
    }

    /**
     * Call this method to start the editor.
     * Make sure that the widget has been attached to the DOM tree
     * before calling this method.
     */
    public native void startEditor() /*-{
        var editor = $wnd.ace.edit(this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::divElement);
        editor.getSession().setUseWorker(false);
        this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor = editor;

        // Store a reference to the (Java) AceEditor object in the
        // JavaScript editor object.
        editor._aceGWTAceEditor = this;

        // I have been noticing sporadic failures of the editor
        // to display properly and receive key/mouse events.
        // Try to force the editor to resize and display itself fully.  See:
        //    https://groups.google.com/group/ace-discuss/browse_thread/thread/237262b521dcea33
        editor.resize();
        this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::redisplay();
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
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.renderer.onResize(true);
        editor.renderer.updateFull();
        editor.resize();
        editor.focus();
    }-*/;

    /**
     * Cleans up the entire editor.
     */
    public native void destroy() /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
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
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
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
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        var modeName = "ace/mode/" + shortModeName;
        var TheMode = $wnd.require(modeName).Mode;
        editor.getSession().setMode(new TheMode());
    }-*/;

    /**
     * Register a handler for change events generated by the editor.
     * @param callback the change event handler
     */
    public native void addOnChangeHandler( AceEditorCallback callback ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.getSession().on("change", function (e) {
            callback.@org.uberfire.ext.widgets.common.client.ace.AceEditorCallback::invokeAceCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
        });
    }-*/;

    /**
     * Register a handler for cursor position change events generated by the editor.
     * @param callback the cursor position change event handler
     */
    public native void addOnCursorPositionChangeHandler( AceEditorCallback callback ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.getSession().selection.on("changeCursor", function (e) {
            callback.@org.uberfire.ext.widgets.common.client.ace.AceEditorCallback::invokeAceCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(e);
        });
    }-*/;

    /**
     * Set font size.
     */
    public native void setFontSize( String fontSize ) /*-{
        var elementId = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::elementId;
        var elt = $doc.getElementById(elementId);
        elt.style.fontSize = fontSize;
    }-*/;

    /**
     * Get the complete text in the editor as a String.
     * @return the text in the editor
     */
    public native String getText() /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        return editor.getSession().getValue();
    }-*/;

    /**
     * Set the complete text in the editor from a String.
     * @param text the text to set in the editor
     */
    public native void setText( String text ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.getSession().setValue(text);
    }-*/;

    /**
     * Get the line of text at the given row number.
     * @param row the row number
     * @return the line of text at that row number
     */
    public native String getLine( int row ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        return editor.getSession().getDocument().getLine(row);
    }-*/;

    /**
     * Insert given text at the cursor.
     * @param text text to insert at the cursor
     */
    public native void insertAtCursor( String text ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.insert(text);
    }-*/;

    /**
     * Get the current cursor position.
     * @return the current cursor position
     */
    public native AceEditorCursorPosition getCursorPosition() /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        var pos = editor.getCursorPosition();
        return this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::getCursorPositionImpl(DD)(pos.row, pos.column);
    }-*/;

    private AceEditorCursorPosition getCursorPositionImpl( final double row,
                                                           final double column ) {
        return new AceEditorCursorPosition( (int) row, (int) column );
    }

    /**
     * Gets the given document position as a zero-based index.
     * @param position the position to obtain the absolute index of (base zero)
     * @return An index to the current location in the document
     */
    public int getIndexFromPosition( AceEditorCursorPosition position ) {
        return getIndexFromPositionImpl( position.toJsObject() );
    }

    private native int getIndexFromPositionImpl( JavaScriptObject jsPosition ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        return editor.getSession().getDocument().positionToIndex(jsPosition);
    }-*/;

    /**
     * Gets a document position from a supplied zero-based index.
     * @param index (base zero)
     * @return A position object showing the row and column of the supplied index in the document
     */
    public native AceEditorCursorPosition getPositionFromIndex( int index ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        var jsPosition = editor.getSession().getDocument().indexToPosition(index);
        return @org.uberfire.ext.widgets.common.client.ace.AceEditorCursorPosition::create(II)(
                jsPosition.row,
                jsPosition.column
        );
    }-*/;

    /**
     * Set whether or not soft tabs should be used.
     * @param useSoftTabs true if soft tabs should be used, false otherwise
     */
    public native void setUseSoftTabs( boolean useSoftTabs ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.getSession().setUseSoftTabs(useSoftTabs);
    }-*/;

    /**
     * Set tab size.  (Default is 4.)
     * @param tabSize the tab size to set
     */
    public native void setTabSize( int tabSize ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.getSession().setTabSize(tabSize);
    }-*/;

    /**
     * Go to given line.
     * @param line the line to go to
     */
    public native void gotoLine( int line ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.gotoLine(line);
    }-*/;

    /**
     * Go to given line.
     * @param line the line to go to
     */
    public native void scrollToLine( int line ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.resize(true);

        editor.scrollToLine(line, true, true, function () {});
    }-*/;


    /**
     * Set whether or not the horizontal scrollbar is always visible.
     * @param hScrollBarAlwaysVisible true if the horizontal scrollbar is always
     * visible, false if it is hidden when not needed
     */
    public native void setHScrollBarAlwaysVisible( boolean hScrollBarAlwaysVisible ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.renderer.setHScrollBarAlwaysVisible(hScrollBarAlwaysVisible);
    }-*/;

    /**
     * Set whether or not the gutter is shown.
     * @param showGutter true if the gutter should be shown, false if it should be hidden
     */
    public native void setShowGutter( boolean showGutter ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.renderer.setShowGutter(showGutter);
    }-*/;

    /**
     * Set or unset read-only mode.
     * @param readOnly true if editor should be set to readonly, false if the
     * editor should be set to read-write
     */
    public native void setReadOnly( boolean readOnly ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.setReadOnly(readOnly);
    }-*/;

    /**
     * Set or unset highlighting of currently selected word.
     * @param highlightSelectedWord true to highlight currently selected word, false otherwise
     */
    public native void setHighlightSelectedWord( boolean highlightSelectedWord ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.setHighlightSelectedWord(highlightSelectedWord);
    }-*/;

    /**
     * Set or unset the visibility of the print margin.
     * @param showPrintMargin true if the print margin should be shown, false otherwise
     */
    public native void setShowPrintMargin( boolean showPrintMargin ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
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
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        var annotations = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::annotations;
        editor.getSession().setAnnotations(annotations);
    }-*/;

    /**
     * Clear any annotations from the editor and reset the local <code>annotations</code> JsArray<AceAnnotation>
     */
    public native void clearAnnotations() /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.getSession().clearAnnotations();
        this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::resetAnnotations()();
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
     * Remove commands, that may not be required, from the editor
     * @param command to be removed, one of
     * "gotoline", "findnext", "findprevious", "find", "replace", "replaceall"
     */
    public native void removeCommandByName( String command ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.commands.removeCommand(command);
    }-*/;

    /**
     * Set whether to use wrap mode or not
     * @param useWrapMode true if word wrap should be used, false otherwise
     */
    public native void setUseWrapMode( boolean useWrapMode ) /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.getSession().setUseWrapMode(useWrapMode);
    }-*/;

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.ResizeComposite#onResize()
     */
    @Override
    public void onResize() {
        redisplay();
    }

    @Override
    public void setValue( String value ) {
        this.setText( value );
    }

    @Override
    public String getValue() {
        return this.getText();
    }

    /**
     * Set whether or not autocomplete is enabled.
     * @param b true if autocomplete should be enabled, false if not
     */
    public native void setAutocompleteEnabled( boolean b ) /*-{
        // See: https://github.com/ajaxorg/ace/wiki/How-to-enable-Autocomplete-in-the-Ace-editor
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        if (b) {
            $wnd.ace.require("ace/ext/language_tools");
            editor.setOptions({ enableBasicAutocompletion: true });
        } else {
            editor.setOptions({ enableBasicAutocompletion: false });
        }
    }-*/;

    /**
     * Removes all existing completers from the langtools<br/><br/>
     * This can be used to disable all completers including local completers, which can be very useful
     * when completers are used on very large files (as the local completer tokenizes every word to put in the selected list).<br/><br/>
     * <strong>NOTE:</strong> This method may be removed, and replaced with another solution. It works at point of check-in, but treat this as unstable for now.
     */
    public native static void removeAllExistingCompleters() /*-{
        var langTools = $wnd.ace.require("ace/ext/language_tools");
        langTools.removeCompleters();
    }-*/;

    /**
     * Add an {@link AceCompletionProvider} to provide
     * custom code completions.
     * <p/>
     * <strong>Warning</strong>: this is an experimental feature of AceGWT.
     * It is possible that the API will change in an incompatible way
     * in future releases.
     * @param provider the {@link AceCompletionProvider}
     */
    public native static void addCompletionProvider( AceCompletionProvider provider ) /*-{
        var langTools = $wnd.ace.require("ace/ext/language_tools");
        var completer = {
            getCompletions: function (editor, session, pos, prefix, callback) {
                var callbackWrapper =
                        @org.uberfire.ext.widgets.common.client.ace.AceEditor::wrapCompletionCallback(Lcom/google/gwt/core/client/JavaScriptObject;)(callback);
                var aceEditor = editor._aceGWTAceEditor;
                provider.@org.uberfire.ext.widgets.common.client.ace.AceCompletionProvider::getProposals(Lorg/uberfire/ext/widgets/common/client/ace/AceEditor;Lorg/uberfire/ext/widgets/common/client/ace/AceEditorCursorPosition;Ljava/lang/String;Lorg/uberfire/ext/widgets/common/client/ace/AceCompletionCallback;)(
                        aceEditor,
                        @org.uberfire.ext.widgets.common.client.ace.AceEditorCursorPosition::create(II)(pos.row, pos.column),
                        prefix,
                        callbackWrapper
                );
            }
        };
        langTools.addCompleter(completer);
    }-*/;

    private static AceCompletionCallback wrapCompletionCallback( JavaScriptObject jsCallback ) {
        return new AceCompletionCallbackImpl( jsCallback );
    }

    public native void setFocus()  /*-{
        var editor = this.@org.uberfire.ext.widgets.common.client.ace.AceEditor::editor;
        editor.focus();
    }-*/;
}
