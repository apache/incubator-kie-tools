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

import org.jboss.errai.ui.client.local.spi.TranslationService;

/**
 * <p>
 * An annotation that provides a mechanism for the developer to declare translation strings from
 * within the GWT application code. This extends the Errai i18n support so that translations can be
 * used from Java code (not just from templates).
 * </p>
 * <p>
 * How does it work? I'm glad you asked. The developer must annotate a field which represents the
 * translation key. This key will then map to a value in the translation bundle file. Once the field
 * is annotated appropriately, the developer must directly invoke the {@link TranslationService}'s
 * <code>format</code> method. This method call will perform a lookup in the translation service of
 * the value mapped to the provided key. Note that value substitution is supported via the {N}
 * format. See below for some examples.
 * </p>
 * 
 * <span><b>AppMessages.java</b></span>
 * 
 * <pre>
 * package org.example.ui.client.local;
 * 
 * public class AppMessages {
 *   &#064;TranslationKey(defaultValue = &quot;I guess something happened!&quot;)
 *   public static final String CUSTOM_MESSAGE = &quot;app.custom-message&quot;;
 *   &#064;TranslationKey(defaultValue = &quot;Hey {0}, I just told you something happened!&quot;)
 *   public static final String CUSTOM_MESSAGE_WITH_NAME = &quot;app.custom-message-with-name&quot;;
 * }
 * </pre>
 * 
 * <hr/>
 * 
 * <span><b>CustomComponent.java</b></span>
 * 
 * <pre>
 * package org.example.ui.client.local;
 * 
 * &#064;Dependent
 * &#064;Templated
 * public class CustomComponent extends Composite {
 *   &#064;Inject
 *   private TranslationService translationService;
 * 
 *   &#064;Inject
 *   &#064;DataField
 *   private Button someAction;
 * 
 *   &#064;EventHandler(&quot;someAction&quot;)
 *   private void doLogin(ClickEvent event) {
 *     // do some action that may require a notification sent to the user
 *     String messageToUser = translationService.format(AppMessages.CUSTOM_MESSAGE);
 *     Window.alert(messageToUser);
 *     String username = getCurrentUserName(); // hand wave, hand wave
 *     String messageToUserWithName = translationService.format(AppMessages.CUSTOM_MESSAGE_WITH_NAME, username);
 *     Window.alert(messageToUserWithName);
 *   }
 * }
 * </pre>
 * 
 * @author eric.wittmann@redhat.com
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TranslationKey {

  /**
   * Indicates the default value to use if no translation is available for the current locale.
   */
  String defaultValue();

}
