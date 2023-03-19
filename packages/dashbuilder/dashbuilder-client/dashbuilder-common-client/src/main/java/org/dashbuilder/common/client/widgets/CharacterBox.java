package org.dashbuilder.common.client.widgets;


import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * <p>Box class for character entry widgets (Support boostrap style).</p>
 * <p>This class implementation is same as <code>com.github.gwtbootstrap.client.ui.TextBox</code> but this one inherits from <code>CharacterBoxBase</code>.</p> 
 *
 * @since 0.3.0
 */
public class CharacterBox extends CharacterBoxBase {

    /**
     * Creates a TextBox widget that wraps an existing &lt;input type='text'&gt;
     * element.
     *
     * This element must already be attached to the document. If the element is
     * removed from the document, you must call
     * {@link RootPanel#detachNow(com.google.gwt.user.client.ui.Widget)}.
     *
     * @param element
     *            the element to be wrapped
     */
    public static CharacterBox wrap(Element element) {
        // Assert that the element is attached.
        assert Document.get().getBody().isOrHasChild(element);

        CharacterBox textBox = new CharacterBox(element);

        // Mark it attached and remember it for cleanup.
        textBox.onAttach();
        RootPanel.detachOnWindowClose(textBox);

        return textBox;
    }

    /**
     * Creates an empty text box.
     */
    public CharacterBox() {
        this(Document.get().createTextInputElement(), "gwt-TextBox");
    }

    /**
     * This constructor may be used by subclasses to explicitly use an existing
     * element. This element must be an &lt;input&gt; element whose type is
     * 'text'.
     *
     * @param element
     *            the element to be used
     */
    protected CharacterBox(Element element) {
        super(element);
        assert InputElement.as(element).getType().equalsIgnoreCase("text");
    }

    CharacterBox(Element element,
            String styleName) {
        super(element);
        if (styleName != null) {
            setStyleName(styleName);
        }
    }

    /**
     * Gets the maximum allowable length of the text box.
     *
     * @return the maximum length, in characters
     */
    public int getMaxLength() {
        return getInputElement().getMaxLength();
    }

    /**
     * Gets the number of visible characters in the text box.
     *
     * @return the number of visible characters
     */
    public int getVisibleLength() {
        return getInputElement().getSize();
    }

    /**
     * Sets the maximum allowable length of the text box.
     *
     * @param length
     *            the maximum length, in characters
     */
    public void setMaxLength(int length) {
        getInputElement().setMaxLength(length);
    }

    /**
     * Sets the number of visible characters in the text box.
     *
     * @param length
     *            the number of visible characters
     */
    public void setVisibleLength(int length) {
        getInputElement().setSize(length);
    }

    private InputElement getInputElement() {
        return getElement().cast();
    }

}

