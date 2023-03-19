package org.jboss.errai.ui.test.binding.client.res;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.annotations.Element;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.ui.HasValue;

@Element("a")
@JsType(isNative = true)
public interface BindableEmailAnchor extends Anchor, HasValue<String> {

  @JsOverlay @Override
  default String getValue() {
    return getTextContent();
  }

  @JsOverlay @Override
  default void setValue(final String value) {
    setTextContent(value);
    setHref("mailto:" + value);
  }
}
