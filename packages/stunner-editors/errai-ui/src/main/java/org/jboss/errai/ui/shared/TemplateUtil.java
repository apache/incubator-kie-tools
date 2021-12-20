/*
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.ui.client.local.spi.TemplateProvider;
import org.jboss.errai.ui.client.local.spi.TemplateRenderingCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField.ConflictStrategy;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.jboss.errai.ui.shared.wrapper.ElementWrapper;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import jsinterop.annotations.JsType;

/**
 * Errai UI Runtime Utility for handling {@link Template} composition.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public final class TemplateUtil {
  private static final Logger logger = Logger.getLogger(TemplateUtil.class.getName());

  private static final Map<Object, List<Runnable>> cleanupTasks = new IdentityHashMap<>();
  private static TranslationService translationService = null;
  public static TranslationService getTranslationService() {
    if (translationService == null) {
      translationService = GWT.create(TranslationService.class);
    }
    return translationService;
  }

  private TemplateUtil() {
  }

  public static void compositeComponentReplace(final String componentType, final String templateFile, final Supplier<Widget> field,
          final Map<String, Element> dataFieldElements, final Map<String, DataFieldMeta> dataFieldMetas, final String fieldName) {
    try {
      compositeComponentReplace(componentType, templateFile, field.get(), dataFieldElements, dataFieldMetas, fieldName);
    } catch (final Throwable t) {
      throw new RuntimeException("There was an error initializing the @DataField " + fieldName + " in the @Templated "
              + componentType + ": " + t.getMessage(), t);
    }
  }

  /**
   * Replace the {@link Element} with the data-field of the given
   * {@link String} with the root {@link Element} of the given {@link UIObject}
   */
  public static void compositeComponentReplace(final String componentType, final String templateFile, final Widget field,
          final Map<String, Element> dataFieldElements, final Map<String, DataFieldMeta> dataFieldMetas, final String fieldName) {
    if (field == null) {
      throw new IllegalStateException("Widget to be composited into [" + componentType + "] field [" + fieldName
              + "] was null. Did you forget to @Inject or initialize this @DataField?");
    }
    final Element element = dataFieldElements.get(fieldName);
    final DataFieldMeta meta = dataFieldMetas.get(fieldName);
    if (element == null) {
      throw new IllegalStateException("Template [" + templateFile
              + "] did not contain data-field, id or class attribute for field [" + componentType + "." + fieldName + "]");
    }
    logger.finer("Compositing @Replace [data-field=" + fieldName + "] element [" + element + "] with Component "
            + field.getClass().getName() + " [" + field.getElement() + "]");

    if (!element.getTagName().equals(field.getElement().getTagName())) {
      logger.warning("Replacing Element type [" + element.getTagName() + "] in " + templateFile + "  with type ["
              + field.getElement().getTagName() + "] for " + fieldName + " in " + componentType);
    }

    final Element parentElement = element.getParentElement();
    try {
      if (field instanceof HasText && (!(field instanceof ElementWrapperWidget) || field.getElement().getChildCount() == 0)) {
        Node firstNode = element.getFirstChild();
        while (firstNode != null) {
          if (firstNode != element.getFirstChildElement())
            field.getElement().appendChild(element.getFirstChild());
          else {
            field.getElement().appendChild(element.getFirstChildElement());
          }
          firstNode = element.getFirstChild();
        }
      }
      parentElement.replaceChild(field.getElement(), element);

      final boolean hasI18nKey = !field.getElement().getAttribute("data-i18n-key").equals("");
      final boolean hasI18nPrefix = !field.getElement().getAttribute("data-i18n-prefix").equals("");

      /*
       * Preserve template Element attributes.
       */
      final JsArray<Node> templateAttributes = getAttributes(element);
      for (int i = 0; i < templateAttributes.length(); i++) {
        final Node node = templateAttributes.get(i);
        final String name = node.getNodeName();
        final String value = node.getNodeValue();
        /*
         * If this new component is templated, do not overwrite i18n related attributes.
         */
        if ((name.equals("data-i18n-key") || name.equals("data-role") && value.equals("dummy"))
                && (hasI18nKey || hasI18nPrefix))
          continue;

        mergeAttribute(meta, field.getElement().cast(), element.cast(), name, value);

        final Element previous = dataFieldElements.put(fieldName, field.getElement());
        final Element root = dataFieldElements.get("this");
        if (root != null && root == previous) {
          dataFieldElements.put("this", field.getElement());
        }
      }
    } catch (final Exception e) {
      throw new IllegalStateException("Could not replace Element with [data-field=" + fieldName + "]" +
            " - Did you already @Insert or @Replace a parent Element?" +
            " Is an element referenced by more than one @DataField?", e);
    }
  }

  private static void mergeAttribute(final DataFieldMeta meta, final HTMLElement beanElement, final HTMLElement templateElement, final String name, final String value) {
    final ConflictStrategy strategy = meta.getStrategy(name);
    // Merge all class names regardless of strategy
    if (name.equals("class")) {
      DOMUtil.tokenStream(templateElement.getClassList())
        .filter(token -> !beanElement.getClassList().contains(token))
        .forEach(token -> beanElement.getClassList().add(token));
    }
    // Merge individual properties in style only using the strategy when both elements have a value.
    else if (name.equals("style")) {
      Stream<String> propertyNameStream = DOMUtil.cssPropertyNameStream(templateElement.getStyle());
      if (ConflictStrategy.USE_BEAN.equals(strategy)) {
        propertyNameStream = propertyNameStream
                .filter(propertyName -> {
                  final String beanPropertyValue = beanElement.getStyle().getPropertyValue(propertyName);
                  return beanPropertyValue == null || beanPropertyValue.isEmpty();
                 });
      }

      propertyNameStream
        .forEach(propertyName -> beanElement.getStyle().setProperty(propertyName, templateElement.getStyle().getPropertyValue(propertyName), ""));
    }
    // Use strategy to decide which value is used.
    else {
      final String beanValue = beanElement.getAttribute(name);
      if (ConflictStrategy.USE_TEMPLATE.equals(strategy) || beanValue == null || beanValue.isEmpty()) {
        beanElement.setAttribute(name, value);
      }
    }
  }

  public static Element asElement(final Object element) {
    try {
      return nativeCast(element);
    } catch (final Throwable t) {
      throw new RuntimeException("Error casting @DataField of type " + element.getClass().getName() + " to " + Element.class.getName(), t);
    }
  }

  public static HTMLElement asErraiElement(final Object element) {
    try {
      return nativeCast(element);
    } catch (final Throwable t) {
      throw new RuntimeException("Error casting @DataField of type " + element.getClass().getName() + " to org.jboss.errai.common.client.dom.HTMLElement", t);
    }
  }

  /**
   * Only works for native {@link JsType JsTypes} and {@link JavaScriptObject JavaScriptObjects}.
   */
  public static native <T> T nativeCast(Object element) /*-{
    return element;
  }-*/;

  public static com.google.gwt.user.client.Element asDeprecatedElement(final Object element) {
    try {
      return nativeCast(element);
    } catch (final Throwable t) {
      throw new RuntimeException("Error casting @DataField of type " + element.getClass().getName() + " to "
              + com.google.gwt.user.client.Element.class.getName(), t);
    }
  }

  public static void initTemplated(final Object templated, final Element wrapped, final Collection<Widget> dataFields) {
    // All template fragments are contained in a single element, during initialization.
    wrapped.removeFromParent();
    final TemplateWidget widget = new TemplateWidget(wrapped, dataFields);
    TemplateWidgetMapper.put(templated, widget);
    StyleBindingsRegistry.get().updateStyles(templated);
    widget.onAttach();
    RootPanel.detachOnWindowClose(widget);
    TemplateInitializedEvent.fire(widget);
  }

  public static void cleanupTemplated(final Object templated) {
    final TemplateWidget templateWidget = TemplateWidgetMapper.get(templated);
    TemplateWidgetMapper.remove(templated);
    runCleanupTasks(templated);
    if (RootPanel.isInDetachList(templateWidget)) {
      RootPanel.detachNow(templateWidget);
    }
  }

  public static void initWidget(final Composite component, final Element wrapped, final Collection<Widget> dataFields) {
    // All template fragments are contained in a single element, during initialization.
    wrapped.removeFromParent();
    if (!(component instanceof ListWidget)) {
      initWidgetNative(component, new TemplateWidget(wrapped, dataFields));
    }
    if (!component.isAttached()) {
      onAttachNative(component);
      RootPanel.detachOnWindowClose(component);
    }
    StyleBindingsRegistry.get().updateStyles(component);
    TemplateInitializedEvent.fire(component);
  }

  public static void cleanupWidget(final Composite component) {
    runCleanupTasks(component);
    if (RootPanel.isInDetachList(component)) {
      RootPanel.detachNow(component);
    }
  }

  public static void runCleanupTasks(final Object component) {
    Optional
      .ofNullable(cleanupTasks.remove(component))
      .ifPresent(tasks -> tasks.forEach(Runnable::run));
  }

  private static native void onAttachNative(Widget w) /*-{
    w.@com.google.gwt.user.client.ui.Widget::onAttach()();
  }-*/;

  private static native void initWidgetNative(Composite component, Widget wrapped) /*-{
    component.@com.google.gwt.user.client.ui.Composite::initWidget(Lcom/google/gwt/user/client/ui/Widget;)(wrapped);
  }-*/;

  private static Map<String, Element> templateRoots = new HashMap<>();

  public static Element getRootTemplateParentElement(final String templateContents, final String templateFileName, final String rootField) {
    final String key = templateFileName + "#" + rootField;
    if (templateRoots.containsKey(key)) {
      return cloneIntoNewParent(templateRoots.get(key));
    }

    Element parserDiv = DOM.createDiv();
    parserDiv.setInnerHTML(templateContents);
    if (rootField != null && !rootField.trim().isEmpty()) {
      logger.finer("Locating root element: " + rootField);
      final VisitContext<TaggedElement> context = Visit.depthFirst(parserDiv, new Visitor<TaggedElement>() {
        @Override
        public boolean visit(final VisitContextMutable<TaggedElement> context, final Element element) {
          for (final AttributeType attrType : AttributeType.values()) {
            final String attrName = attrType.getAttributeName();
            final TaggedElement existingCandidate = context.getResult();
            if (element.hasAttribute(attrName) && element.getAttribute(attrName).equals(rootField)
                && (existingCandidate == null || existingCandidate.getAttributeType().ordinal() < attrType.ordinal())) {
              context.setResult(new TaggedElement(attrType, element));
            }
          }
          return true;
        }
      });

      if (context.getResult() != null) {
        parserDiv = DOM.createDiv();
        parserDiv.appendChild(context.getResult().getElement());
      }
      else {
        throw new IllegalStateException("Could not locate Element in template with data-field, id or class = [" + rootField + "]\n"
                + parserDiv.getInnerHTML());
      }
    }

    logger.finest(parserDiv.getInnerHTML().trim());

    final Element templateRoot = firstNonMetaElement(parserDiv);
    if (templateRoot == null) {
      throw new IllegalStateException("Could not find template root for this template: " + templateContents);
    }
    else {
      templateRoots.put(key, templateRoot);
      return cloneIntoNewParent(templateRoot);
    }
  }

  public static Element getRootTemplateElement(final Element rootParent) {
    return firstNonMetaElement(rootParent);
  }

  /*
   * This ignores meta tags from ERRAI-779.
   */
  private static Element firstNonMetaElement(final Element parserDiv) {
    Element displayable = parserDiv.getFirstChildElement();
    while (displayable != null && displayable.getTagName().equalsIgnoreCase("meta")) {
      displayable = displayable.getNextSiblingElement();
    }

    return displayable;
  }

  /**
   * Indicates the type of attribute a data field was discovered from.
   */
  private enum AttributeType {
    CLASS("class"),
    ID("id"),
    DATA_FIELD("data-field");

    private final String attributeName;

    AttributeType(final String attributeName) {
      this.attributeName = attributeName;
    }

    public String getAttributeName() {
      return attributeName;
    }
  }

  private static class TaggedElement {
    private final AttributeType attributeType;
    private final Element element;

    public TaggedElement(final AttributeType attributeType, final Element element) {
      this.attributeType = attributeType;
      this.element = element;
    }

    public AttributeType getAttributeType() {
      return attributeType;
    }

    public Element getElement() {
      return element;
    }
  }

  /**
   * Called to perform i18n translation on the given template. Add i18n-prefix attribute to root of
   * template to allow translation after bean creation.
   *
   * @param templateRoot
   */
  public static void translateTemplate(final String templateFile, final Element templateRoot) {
    if (!getTranslationService().isEnabled())
      return;

    logger.finer("Translating template: " + templateFile);
    final String i18nKeyPrefix = getI18nPrefix(templateFile);

    // Add i18n prefix attribute for post-creation translation
    templateRoot.setAttribute("data-i18n-prefix", i18nKeyPrefix);

    DomVisit.visit(new ElementWrapper(templateRoot), new TemplateTranslationVisitor(i18nKeyPrefix));
  }

  /**
   * Generate an i18n key prefix from the given template filename.
   *
   * @param templateFile
   */
  public static String getI18nPrefix(final String templateFile) {
    final int idx1 = templateFile.lastIndexOf('/');
    final int idx2 = templateFile.lastIndexOf('.');
    return templateFile.substring(idx1 + 1, idx2 + 1);
  }

  public static Map<String, Element> getDataFieldElements(final Element templateRoot) {

    final Map<String, Element> dataFields = new LinkedHashMap<>();
    final Map<String, TaggedElement> childTemplateElements = new LinkedHashMap<>();

    logger.finer("Searching template for fields.");
    // TODO do this as browser split deferred binding using
    // Document.querySelectorAll() -
    // https://developer.mozilla.org/En/DOM/Element.querySelectorAll
    Visit.depthFirst(templateRoot, new Visitor<Object>() {
      @Override
      public boolean visit(final VisitContextMutable<Object> context, final Element element) {
        for (final AttributeType attrType : AttributeType.values()) {
          final String attrName = attrType.getAttributeName();
          final String attrVal = element.getAttribute(attrName);
          if (attrVal != null && !attrVal.isEmpty()) {
            final String[] attributeValues = (attrType == AttributeType.CLASS) ? attrVal.split(" +") : new String[]{attrVal};
            for (final String dataFieldName : attributeValues) {
              final TaggedElement existingCandidate = childTemplateElements.get(dataFieldName);
              if (existingCandidate == null || existingCandidate.getAttributeType().ordinal() < attrType.ordinal()) {
                childTemplateElements.put(dataFieldName, new TaggedElement(attrType, element));
                dataFields.put(dataFieldName, element);
              }
            }
          }
        }
        return true;
      }
    });

    dataFields.put("this", templateRoot);

    return dataFields;
  }

  public static void setupNativeEventListener(final Object component, final ElementWrapperWidget wrapper, final EventListener listener,
          final int eventsToSink) {

    if (wrapper == null) {
      throw new RuntimeException("A native event source was specified in " + component.getClass().getName()
              + " but the corresponding data-field does not exist!");
    }
    wrapper.setEventListener(eventsToSink, listener);
  }

  /**
   * Use this for elements that are not wrapped by any widgets (including the ElementWrapperWidget).
   */
  public static void setupNativeEventListener(final Object component, final Element element, final EventListener listener,
          final int eventsToSink) {

    if (element == null) {
      throw new RuntimeException("A native event source was specified in " + component.getClass().getName()
              + " but the corresponding data-field does not exist!");
    }
    DOM.setEventListener(element, listener);
    DOM.sinkEvents(element, eventsToSink);
  }

  public static void setupBrowserEventListener(final Object component, final HTMLElement element,
          final org.jboss.errai.common.client.dom.EventListener<?> listener, final String browserEventType) {
    if (element == null) {
      throw new RuntimeException("A browser event source was specified in " + component.getClass().getName()
              + " but the corresponding data-field does not exist!");
    }
    element.addEventListener(browserEventType, listener, false);
    cleanupTasks
      .computeIfAbsent(component, key -> new ArrayList<>())
      .add(() -> element.removeEventListener(browserEventType, listener, false));
  }

  public static void setupBrowserEventListener(final Object component, final Object element,
          final org.jboss.errai.common.client.dom.EventListener<?> listener, final String browserEventType) {
    setupBrowserEventListener(component, TemplateUtil.asErraiElement(element), listener, browserEventType);
  }

  public static void setupBrowserEventListener(final Object component, final Widget widget,
          final org.jboss.errai.common.client.dom.EventListener<?> listener, final String browserEventType) {
    setupBrowserEventListener(component, widget.getElement(), listener, browserEventType);
  }

  public static <T extends EventHandler> Widget setupPlainElementEventHandler(final Composite component, final Element element,
          final T handler, final com.google.gwt.event.dom.client.DomEvent.Type<T> type) {
    final ElementWrapperWidget widget = ElementWrapperWidget.getWidget(element);
    widget.addDomHandler(handler, type);
    // TODO add to Composite as child.
    return widget;
  }

  public static <T extends EventHandler> void setupWrappedElementEventHandler(final Widget widget,
          final T handler, final com.google.gwt.event.dom.client.DomEvent.Type<T> type) {
    widget.addDomHandler(handler, type);
  }

  /**
   * Join strings inserting separator between them.
   */
  private static String join(final String[] strings, final String separator) {
    final StringBuffer result = new StringBuffer();

    for (final String s : strings) {
      if (result.length() != 0) {
        result.append(separator);
      }
      result.append(s);
    }

    return result.toString();
  }

  private static native JsArray<Node> getAttributes(Element elem) /*-{
    return elem.attributes;
  }-*/;

  private static Element cloneIntoNewParent(final Element element) {
    final Element parent = DOM.createDiv();
    final Element clone = DOM.clone(element, true);
    parent.appendChild(clone);
    return parent;
  }

  private final static class TemplateRequest {
    final Class<?> templateProvider;
    final String location;
    final TemplateRenderingCallback renderingCallback;

    TemplateRequest(final Class<?> templateProvider, final String location, final TemplateRenderingCallback renderingCallback) {
      this.templateProvider = templateProvider;
      this.location = location;
      this.renderingCallback = renderingCallback;
    }
  }

  private static Queue<TemplateRequest> requests = new LinkedList<>();

  /**
   * Called by the generated IOC bootstrapper if a provider is specified on a
   * templated composite (see {@link Templated#provider()}). This method will
   * make sure that templates will be provided and rendered in invocation order
   * even if a given provider is asynchronous.
   *
   * @param templateProvider
   *          the template provider to use for supplying the template, must not
   *          be null.
   * @param location
   *          the location of the template, must not be null.
   * @param renderingCallback
   *          the callback to invoke when the template is available, must not be
   *          null.
   */
  public static void provideTemplate(final Class<?> templateProvider, final String location,
          final TemplateRenderingCallback renderingCallback) {

    final TemplateRequest request = new TemplateRequest(templateProvider, location, renderingCallback);
    requests.add(request);
    if (requests.size() == 1) {
      provideNextTemplate();
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static void provideNextTemplate() {
    if (requests.isEmpty())
      return;

    try {
      final TemplateRequest request = requests.peek();
      IOC.getAsyncBeanManager().lookupBean(request.templateProvider).getInstance(new CreationalCallback() {
        @Override
        public void callback(final Object bean) {
          final TemplateProvider provider = ((TemplateProvider) bean);
          try {
            provider.provideTemplate(request.location, new TemplateRenderingCallback() {
              @Override
              public void renderTemplate(final String template) {
                request.renderingCallback.renderTemplate(template);
                requests.remove();
                provideNextTemplate();
              }
            });
          }
          catch (final RuntimeException t) {
            requests.remove();
            throw t;
          }
        }
      });
    }
    catch (final IOCResolutionException ioce) {
      requests.remove();
      throw ioce;
    }
  }
}
