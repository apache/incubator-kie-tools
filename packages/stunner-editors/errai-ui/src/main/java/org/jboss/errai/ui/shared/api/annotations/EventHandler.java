/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.ui.shared.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.annotations.BrowserEvent;
import org.jboss.errai.common.client.dom.FocusEvent;

/**
 * This annotation may only be used in classes that has been annotated with {@link Templated}, or in a super-class of
 * said types.
 * <p>
 * Declares a method in a {@link Templated} component as a handler for standard {@link Event GWT Widget events},
 * {@link DomEvent GWT native events}, and {@link BrowserEvent JS interop wrapped DOM events} fired by {@link DataField}
 * components and <code>data-field</code> elements within the component HTML template. Method handlers must accept a single
 * argument of the type of event to be handled.
 * <p>
 * The following scenarios are supported by this annotation:
 * <ol>
 *
 * <li>{@link BrowserEvent JS interop wrapped events} on any template element or {@link Templated} field
 * <li>GWT events on Widgets (Deprecated)</li>
 * <li>GWT events on {@link Element Elements} (Deprecated)</li>
 * <li>GWT events on native {@link JsType JsTypes} wrapping DOM elements (Deprecated)</li>
 * <li>Native DOM events on DOM elements (Deprecated)</li>
 * <li>Native DOM events on native {@link JsType JsTypes} wrapping DOM elements (Deprecated)</li>
 *
 * </ol>
 * <p>
 * <b>WARNING:</b> Native GWT events cannot be used in conjunction with GWT standard events, since upon addition to the
 * widget tree, GWT will override any {@link Widget#sinkEvents(int)} that may have been configured by Errai UI.
 *
 * <p>
 * <b>JS Interop wrapped events</b>
 * <p>
 * With this approach, the parameter of the {@link EventHandler} method is a type
 * annotated with {@link BrowserEvent} such as
 * {@link org.jboss.errai.common.client.dom.Event Errai's DOM event wrapper}
 * or any of its subtypes. The target element for the registered listener can be any
 * {@code @DataField} in a {@link Templated} bean or any {@code data-field} in the template HTML file.
 * <p>
 * For types like {@link org.jboss.errai.common.client.dom.Event Event} that do not specify allowable
 * event types via {@link BrowserEvent}, an {@link EventHandler} must use {@link ForEvent} to specify
 * the event types being listened to. When using {@link BrowserEvent} types that do specify event types
 * with {@link BrowserEvent}, {@link ForEvent} is optional.
 * <p>
 * Example:
 * <p>
 *
 * <pre>
 * {@code @Templated}
 * public class QuickHandlerComponent {
 *   {@code @Inject}
 *   {@code @DataField}
 *   private Anchor link;
 *
 *   // Listens to only "click" events on link.
 *   {@code @EventHandler("link")}
 *   public void doSomething({@code @ForEvent("click")} {@link org.jboss.errai.common.client.dom.Event Event} e) {
 *     // do something
 *   }
 *
 *   // Listens to all events declared by {@link FocusEvent} in {@link BrowserEvent} ("blur", "focus", "focusin", and "focusout").
 *   {@code @EventHandler("link")}
 *   public void doSomethingElse({@link FocusEvent} e) {
 *     // do something else
 *   }
 * }
 * </pre>
 * <p>
 * <b>GWT events on Widgets</b>
 * <p>
 * Example:
 * <p>
 *
 * <pre>
 * &#064;Templated
 * public class WidgetHandlerComponent extends Composite {
 *   &#064;Inject
 *   &#064;DataField
 *   private Button button;
 *
 *   &#064;EventHandler(&quot;button&quot;)
 *   public void doSomethingC1(ClickEvent e) {
 *     // do something
 *   }
 * }
 * </pre>
 * <p>
 * <b>GWT events on DOM Elements</b>
 * <p>
 * Example:
 * <p>
 *
 * <pre>
 * &#064;Templated
 * public class WidgetHandlerComponent extends Composite {
 *   &#064;Inject
 *   &#064;DataField
 *   private DivElement button;
 *
 *   &#064;EventHandler(&quot;button&quot;)
 *   public void doSomethingC1(ClickEvent e) {
 *     // do something
 *   }
 * }
 * </pre>
 * <p>
 * <b>GWT events on JsTypes</b>
 * <p>
 * Example:
 * <p>
 *
 * <pre>
 * &#064;JsType(isNative = true)
 * public interface ButtonElementWrapper {
 *   // ...
 * }
 * </pre>
 *
 * <pre>
 * &#064;Templated
 * public class WidgetHandlerComponent extends Composite {
 *   &#064;Inject
 *   &#064;DataField
 *   private ButtonElementWrapper button;
 *
 *   &#064;EventHandler(&quot;button&quot;)
 *   public void doSomethingC1(ClickEvent e) {
 *     // do something
 *   }
 * }
 * </pre>
 * <p>
 * <b>Native events on DOM Elements</b>
 * <p>
 * This approach requires use of the {@link SinkNative} annotation, but does not require that target {@link Element}
 * instances be referenced via {@link DataField} in the {@link Templated} {@link Composite} component; they may also
 * target un-referenced <code>data-field</code> elements from the corresponding HTML template.
 * <p>
 * Example:
 * <p>
 *
 * <pre>
 * &#064;Templated
 * public class QuickHandlerComponent extends Composite {
 *   &#064;DataField
 *   private AnchorElement link = DOM.createAnchor().cast();
 *
 *   &#064;EventHandler(&quot;link&quot;)
 *   &#064;SinkNative(Event.ONCLICK | Event.ONMOUSEOVER)
 *   public void doSomething(Event e) {
 *     // do something
 *   }
 *
 *   &#064;EventHandler(&quot;div&quot;)
 *   &#064;SinkNative(Event.ONMOUSEOVER)
 *   public void doSomethingElse(Event e) {
 *     // do something with an element not otherwise referenced in our component
 *   }
 * }
 * </pre>
 * <p>
 * <b>Native events on JsTypes</b>
 * <p>
 * This approach requires use of the {@link SinkNative} annotation, but does not require that target {@link Element}
 * instances be referenced via {@link DataField} in the {@link Templated} component; they may also target un-referenced
 * <code>data-field</code> elements from the corresponding HTML template.
 * <p>
 * Example:
 * <p>
 *
 * <pre>
 * &#064;JsType(isNative = true)
 * public interface AnchorElementWrapper {
 *   // ...
 * }
 * </pre>
 *
 * <pre>
 * &#064;Templated
 * public class QuickHandlerComponent extends Composite {
 *   &#064;Inject
 *   &#064;DataField
 *   private AnchorElementWrapper link;
 *
 *   &#064;EventHandler(&quot;link&quot;)
 *   &#064;SinkNative(Event.ONCLICK | Event.ONMOUSEOVER)
 *   public void doSomething(Event e) {
 *     // do something
 *   }
 *
 *   &#064;EventHandler(&quot;div&quot;)
 *   &#064;SinkNative(Event.ONMOUSEOVER)
 *   public void doSomethingElse(Event e) {
 *     // do something with an element not otherwise referenced in our component
 *   }
 * }
 * </pre>
 *
 * <p>
 * <b>See also:</b> {@link SinkNative}, {@link Templated}, {@link DataField}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Max Barkley <mbarkley@redhat.com>
 *
 */
@Inherited
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler
{
  /**
   * Specifies the element for which the corresponding event type should be be handled. The handled event type is
   * defined by the handler method argument. By default, the event handler will be installed on the templated widget
   * itself. To handle events that occur on a child widget or DOM node in the template, specify a value. This value must
   * match either a {@link DataField} specified within the {@link Templated} {@link Composite} component (in the case of
   * handling GWT event types,) or a <code>data-field</code> attribute in the corresponding HTML template (in the case
   * of handling native DOM events.)
   */
   String[] value() default "this";
}
