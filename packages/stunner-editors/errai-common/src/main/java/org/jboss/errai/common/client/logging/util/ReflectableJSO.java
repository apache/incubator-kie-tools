package org.jboss.errai.common.client.logging.util;

import com.google.gwt.core.client.ScriptInjector;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * A utility class for accessing arbitrary properties of native Javascript types.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@JsType(isNative = true, namespace = "org.jboss.errai")
public class ReflectableJSO {

  private ReflectableJSO(final Object wrapped) {}

  @JsOverlay
  public static final ReflectableJSO create(final Object wrapped) {
    ScriptInjector
            .fromString(
                    "org = {"
                    + "'jboss' : {"
                      + "'errai' : {"
                        + "'ReflectableJSO' : function(wrapped) {"
                          + "this.get = function(name) {"
                            + "return wrapped[name];"
                          + "};"
                        + "this.set = function(name, value) {"
                          + "wrapped[name] = value;"
                        + "};"
                        + "this.properties = function() {"
                          + "var retVal = [];"
                          + "for (key in wrapped) {"
                            + "retVal.push(key);"
                          + "}"
                          + "return retVal;"
                        + "};"
                        + "this.unwrap = function() {"
                          + "return wrapped;"
                        + "}"
                      + "}"
                    + "}"
                  + "}"
                + "};")
            .setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
    return new ReflectableJSO(wrapped);
  }

  @JsOverlay
  public final boolean hasProperty(final String name) {
    return get(name) != null;
  }

  public final native Object get(final String name);

  public final native void set(final String name, final Object value);

  public final native String[] properties();

  public final native Object unwrap();
}