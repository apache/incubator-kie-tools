package org.dashbuilder.common.client.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.text.shared.Parser;
import org.gwtbootstrap3.client.ui.base.ValueBoxBase;

/**
 * <p>Abstract base class for most character entry widgets. and Support boostrap style.</p>
 * <p>This class implementation is same as <code>com.github.gwtbootstrap.client.ui.base.TextBoxBase</code> but using Character types instead of String.</p> 
 *
 * @since 0.3.0
 */
public class CharacterBoxBase extends ValueBoxBase<Character> {

    private static final PassthroughRenderer RENDERER_INSTANCE = new PassthroughRenderer();
    private static final PassthroughParser PARSER_INSTANCE = new PassthroughParser();

    /**
     * Creates a text box that wraps the given browser element handle. This is
     * only used by subclasses.
     *
     * @param elem
     *            the browser element to wrap
     */
    protected CharacterBoxBase(Element elem) {
        super(elem, RENDERER_INSTANCE, PARSER_INSTANCE);
    }

    /**
     * Overridden to return "" from an empty text box.
     */
    @Override
    public Character getValue() {
        Character raw = super.getValue();
        return raw == null
                ? ' '
                : raw;
    }

    private static class PassthroughRenderer extends AbstractRenderer<Character> {

        public PassthroughRenderer() {
        }

        public String render(Character object) {
            return object.toString();
        }
    }


    private static class PassthroughParser implements Parser<Character> {

        public PassthroughParser() {
            
        }

        public Character parse(CharSequence object) {
            if (object.length() > 0) return object.charAt(0);
            return null;
        }
    }

}