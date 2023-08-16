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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.client.local.spi.TemplateProvider;
import org.jboss.errai.ui.shared.ServerTemplateProvider;

/**
 * Since Errai 4.0.0 this annotation may be used on classes that do not extends {@link Composite}.
 * <p>
 * Indicates that the annotated class will participate in the Errai UI templating framework. Instances of the annotated
 * component must be retrieved via {@link Inject} or {@link Instance} references in bean classes.
 * <p>
 * Unless otherwise specified in the {@link #value()} and {@link #provider()} attributes, a corresponding
 * ComponentName.html file must be placed in the same directory on the class-path as the custom ComponentName type.
 * <p>
 * <b>Example:</b>
 * <p>
 *
 * <pre>
 * package org.example;
 *
 * &#064;Templated
 * public class CustomComponent
 * {
 * }
 * </pre>
 *
 * <b>And the corresponding HTML template:</b>
 *
 * <pre>
 * &lt;form&gt;
 *   &lt;legend&gt;Log in to your account&lt;/legend&gt;
 *
 *   &lt;label for="username"&gt;Username&lt;/label&gt;
 *   &lt;input data-field="username" id="username" type="text" placeholder="Username"&gt;
 *
 *   &lt;label for="password"&gt;Password&lt;/label&gt;
 *   &lt;input data-field="password" id="password" type="password" placeholder="Password"&gt;
 *
 *   &lt;button data-field="login" &gt;Log in&lt;/button&gt;
 *   &lt;button data-field="cancel" &gt;Cancel&lt;/button&gt;
 * &lt;/form&gt;
 * </pre>
 * <p>
 *
 * <p>
 * Each element with a <code>id</code>, <code>data-field</code>, or <code>class</code> attribute may be bound to a
 * field, method, or constructor parameter in the annotated class, using the {@link DataField} annotation. Events
 * triggered by elements or widgets in the template may be handled using the {@link EventHandler} annotation to
 * specify handler methods.
 * <p>
 *
 * <pre>
 * package org.example;
 *
 * &#064;Templated
 * public class CustomComponent
 * {
 *    &#064;Inject
 *    &#064;DataField
 *    private TextBox username;
 *
 *    &#064;Inject
 *    &#064;DataField
 *    private TextBox password;
 *
 *    &#064;Inject
 *    &#064;DataField
 *    private Button login;
 *
 *    &#064;Inject
 *    &#064;DataField
 *    private Button cancel;
 *
 *    &#064;EventHandler(&quot;login&quot;)
 *    private void doLogin(ClickEvent event)
 *    {
 *       // log in
 *    }
 * }
 * </pre>
 * <p>
 * <b>Obtaining a widget reference via {@link Inject}:</b>
 *
 * <pre>
 * &#064;ApplicationScoped
 * public class ExampleBean
 * {
 *    &#064;Inject
 *    private CustomComponent comp;
 * }
 * </pre>
 * <p>
 * <b>Obtaining widget references on demand, via {@link Instance}.</b> One may also create multiple instances of a
 * {@link Templated} widget using this approach:
 *
 * <pre>
 * &#064;ApplicationScoped
 * public class ExampleBean
 * {
 *    &#064;Inject
 *    Instance&lt;CustomComponent&gt; instance;
 *
 *    public CustomComponent getNewComponent()
 *    {
 *       return instance.get();
 *    }
 * }
 * </pre>
 *
 * <p>
 * <b>See also:</b> {@link DataField}, {@link Bound}, {@link AutoBound}, {@link EventHandler}, {@link SinkNative}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Templated
{
   /**
    * Specifies the resource path (<code>com/example/foo/CompositeComponent.html</code>) and fragment (
    * <code>#name</code>) of the HTML template with which the annotated widget should be composited.
    * <p>
    * The resource path is the location of the HTML file on the classpath. If omitted, this defaults to the fully
    * qualified class name of the annotated type, plus `.html`. If the fragment is omitted, composition will be
    * performed using the first single element found (and all inner HTML) in the specified template.
    * <p>
    * The fragment corresponds to an element with matching <code>id</code>, <code>data-field</code> or
    * <code>class</code> attribute. If specified, this singleelement (and all inner HTML) will be used as the root
    * of the widget.
    */
   String value() default "";

   /**
   * Specifies the resource path (<code>com/example/foo/CompositeComponent.css</code>) of a CSS stylesheet to be added
   * to the DOM when this component is created.
   * <p>
   * The resource path is the location of the CSS file on the classpath. If omitted, this defaults to the fully
   * qualified class name of the annotated type, plus `.css`, in which case it is not an error if the CSS file is not
   * found. For non-default values, missing stylesheets will cause rebind errors.
   */
   String stylesheet() default "";

   /**
   * Specifies a {@link TemplateProvider} that is used to supply a template at run-time i.e.
   * {@link ServerTemplateProvider}. By default, and if omitted, templates must be present at compile-time at the
   * class-path location specified by {@link #value()}.
   */
   Class<? extends TemplateProvider> provider() default
     org.jboss.errai.ui.shared.api.annotations.Templated.DEFAULT_PROVIDER.class;

   static abstract class DEFAULT_PROVIDER implements TemplateProvider {}
}
