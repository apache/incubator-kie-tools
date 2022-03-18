/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Manages bindings and acts in behalf of a {@link BindableProxy} to keep the target model and bound widgets in sync.
 * <p>
 * An agent will:
 * <ul>
 * <li>Carry out an initial state sync between the bound widgets and the target model, if specified (see
 * {@link DataBinder#setModel(Object, StateSync)})</li>
 *
 * <li>Update the bound widget when a setter method is invoked on the model (see
 * {@link #updateWidgetsAndFireEvent(boolean, String, Object, Object)}). Works for components that either implement
 * {@link TakesValue} or {@link HasText}). Also works for native wrappers of input elements. Components displaying lists
 * can implement {@link BindableListChangeHandler} to receive incremental updates of the model state.</li>
 *
 * <li>Update the bound components when a non-accessor method is invoked on the model (by comparing all bound properties
 * to detect changes). See {@link #updateWidgetsAndFireEvents()}. Works for components that either implement
 * {@link TakesValue} or {@link HasText})</li>
 *
 * <li>Update the target model in response to value change events (only works for bound components that implement
 * {@link HasValue})</li>
 * <ul>
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 *
 * @param <T>
 *          The type of the target model being proxied.
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class BindableProxyAgent<T> implements HasPropertyChangeHandlers {
  private static final Logger logger = LoggerFactory.getLogger(BindableProxyAgent.class);

  final Multimap<String, Binding> bindings = LinkedHashMultimap.create();
  final Map<String, PropertyType> propertyTypes = new HashMap<>();
  final Map<String, DataBinder> binders = new HashMap<>();
  final Map<String, Object> knownValues = new HashMap<>();
  final Collection<HandlerRegistration> modelChangeHandlers = new ArrayList<>();

  PropertyChangeHandlerSupport propertyChangeHandlerSupport = new PropertyChangeHandlerSupport();

  final BindableProxy<T> proxy;
  T target;

  BindableProxyAgent(final BindableProxy<T> proxy, final T target) {
    this.proxy = proxy;
    this.target = target;
  }

  /**
   * Copies the values of all properties to be able to compare them in case they
   * change outside a setter method.
   */
  void copyValues() {
    for (final String property : propertyTypes.keySet()) {
      if (!"this".equals(property)) {
        knownValues.put(property, proxy.get(property));
      }
    }
  }

  /**
   * Binds the provided component to the specified property (or property chain) of the model instance associated with
   * this proxy (see {@link DataBinder#setModel(Object, StateSync)}).
   *
   * @param component
   *          the UI component to bind to, must not be null.
   * @param property
   *          the property of the model to bind the widget to, must not be null.
   * @param converter
   *          the converter to use for this binding, null if default conversion should be used. See
   *          {@link Convert#getConverter(Class, Class)} and {@link Convert#identityConverter(Class)} for possible
   *          arguments.
   * @return binding the created binding.
   */
  public Binding bind(final Object component, final String property, final Converter converter) {
    return bind(component, property, converter, false, StateSync.FROM_MODEL);
  }

  /**
   * Binds the provided component to the specified property (or property chain) of the model instance associated with
   * this proxy (see {@link DataBinder#setModel(Object, StateSync)}).
   *
   * @param component
   *          the UI component to bind to, must not be null.
   * @param property
   *          the property of the model to bind the widget to, must not be null.
   * @param providedConverter
   *          the converter to use for this binding, null if default conversion should be used. See
   *          {@link Convert#getConverter(Class, Class)} and {@link Convert#identityConverter(Class)} for possible
   *          arguments.
   * @param bindOnKeyUp
   *          a flag indicating that the property should be updated when the widget fires a
   *          {@link com.google.gwt.event.dom.client.KeyUpEvent} along with the default
   *          {@link com.google.gwt.event.logical.shared.ValueChangeEvent}.
   * @param initialState
   *          specifies whether to use the model or component state when initially synchronizing this binding. If null,
   *          the values will not be synchronized until a change is made to the model or component.
   * @return binding the created binding.
   */
  public Binding bind(final Object component, final String property, final Converter providedConverter,
          final boolean bindOnKeyUp, final StateSync initialState) {
    logger.debug("Binding property {} to component {}", property, component);

    final Converter converter = findConverter(property, getPropertyType(property).getType(), component, providedConverter);
    logger.debug("Using converter: {} <--> {}", converter.getModelType().getSimpleName(), converter.getComponentType().getSimpleName());

    final String lastSubProperty = property.substring(property.lastIndexOf('.')+1);

    final Optional<Supplier<Object>> uiGetter = maybeCreateUIGetter(component);
    final Function<BindableProxyAgent<?>, Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>>> registrar =
          agent -> addHandlers(component, bindOnKeyUp, uiGetter, modelUpdater(component, converter, lastSubProperty, agent));

    return bindHelper(component, property, converter, registrar, uiGetter, initialState);
  }

  private Optional<Supplier<Object>> maybeCreateUIGetter(final Object component) {
    if (component instanceof TakesValue) {
      return createTakesValueGetter((TakesValue) component);
    }
    else if (component instanceof HasText) {
      return createHasTextGetter((HasText) component);
    }
    else if (component instanceof IsElement) {
      return maybeCreateElementValueGetter(BoundUtil.asElement(((IsElement) component).getElement()));
    }
    else if (component instanceof org.jboss.errai.common.client.api.elemental2.IsElement) {
      return maybeCreateElementValueGetter(BoundUtil.asElement(((org.jboss.errai.common.client.api.elemental2.IsElement) component).getElement()));
    }
    else if (isElement(component)) {
      return maybeCreateElementValueGetter(BoundUtil.asElement(component));
    }
    else {
      return Optional.empty();
    }
  }

  private Consumer<Object> modelUpdater(final Object component, final Converter converter, final String lastSubProperty,
          final BindableProxyAgent<?> agent) {
    return uiValue -> {
      final Object oldValue = agent.proxy.get(lastSubProperty);
      final Object newValue = converter.toModelValue(uiValue);
      agent.trySettingModelProperty(lastSubProperty, converter, uiValue, newValue);
      agent.updateWidgetsAndFireEvent(false, lastSubProperty, oldValue, newValue, component);
    };
  }

  private Optional<Supplier<Object>> createTakesValueGetter(final TakesValue component) {
    return Optional.ofNullable(() -> component.getValue());
  }

  private Optional<Supplier<Object>> createHasTextGetter(final HasText component) {
    return Optional.ofNullable(() -> component.getText());
  }

  private Optional<Supplier<Object>> maybeCreateElementValueGetter(final Element element) {
    final Optional<Supplier<Object>> uiGetter;
    final Supplier<ElementWrapperWidget> toWidget = () -> ElementWrapperWidget.getWidget(element);
    final ElementWrapperWidget wrapper = toWidget.get();
    if (wrapper instanceof HasValue) {
      uiGetter = Optional.ofNullable(() -> ((HasValue) toWidget.get()).getValue());
    }
    else if (wrapper instanceof HasHTML) {
      uiGetter = Optional.ofNullable(() -> ((HasHTML) toWidget.get()).getText());
    }
    else {
      uiGetter = Optional.empty();
    }
    return uiGetter;
  }

  private Binding bindHelper(final Object component, final String property, final Converter converter,
          final Function<BindableProxyAgent<?>, Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>>> registrar,
          final Optional<Supplier<Object>> uiGetter, final StateSync initialState) {

    validatePropertyExpr(property);

    if (property.contains(".")) {
      return bindNestedProperty(component, property, converter, registrar, uiGetter, initialState);
    }
    else {
      return bindDirectProperty(component, property, converter, registrar.apply(this), uiGetter, initialState);
    }
  }

  private Binding bindNestedProperty(final Object component, final String property, final Converter<?, ?> converter,
          final Function<BindableProxyAgent<?>, Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>>> registrar,
          final Optional<Supplier<Object>> uiGetter, final StateSync initialState) {

      final DataBinder nestedBinder = createNestedBinder(property, initialState);
      final String subProperty = property.substring(property.indexOf('.') + 1);
      final BindableProxyAgent<?> nestedAgent = ((BindableProxy<?>) nestedBinder.getModel()).getBindableProxyAgent();
      nestedBinder.addBinding(subProperty, nestedAgent.bindHelper(component, subProperty, converter, registrar, uiGetter, initialState));
      final Binding binding = new Binding(property, component, converter, null);
      bindings.put(property, binding);

      return binding;
  }

  private void trySettingModelProperty(final String property, final Converter converter, final Object uiValue,
          final Object newValue) {
    try {
      proxy.set(property, newValue);
    } catch (final Throwable t) {
      if (newValue == null && isCausedByNullOrUndefined(t)) {
        /*
         * Don't fail here because likely this is an error from trying to unbox a null value to a primitive.
         * XXX Maybe we should check for boxed types before trying to set the property.
         */
        logger.warn("Encountered a null while trying to set the property [" + property + "] to ["
                + newValue + "].", t);
      }
      else {
        throw new RuntimeException("Error while setting property [" + property + "] to [" + newValue
        + "] converted from [" + uiValue + "] with converter [" + converter.getComponentType().getName() + " -> "
        + converter.getModelType().getName() + "].", t);
      }
    }
  }

  private boolean isCausedByNullOrUndefined(final Throwable t) {
    return t instanceof NullPointerException
            || (t instanceof JavaScriptException && t.getMessage().contains("null"));
  }

  private PropertyType getPropertyType(final String property) {
    return getPropertyType(proxy, property);
  }

  private static PropertyType getPropertyType(final BindableProxy<?> proxy, final String property) {
    final BindableProxyAgent<?> agent = proxy.getBindableProxyAgent();
    if (property.contains(".")) {
      final int firstDot = property.indexOf(".");
      final String topLevelProperty = property.substring(0, firstDot);
      final String childProperty = property.substring(firstDot+1);
      final PropertyType topLevelType = getPropertyType(proxy, topLevelProperty);
      final BindableProxy<?> topLevelProxy;
      if (topLevelType instanceof MapPropertyType) {
        topLevelProxy = (BindableProxy<?>) DataBinder.forMap(((MapPropertyType) topLevelType).getPropertyTypes()).getModel();
      }
      else {
        topLevelProxy = BindableProxyFactory.getBindableProxy(topLevelType.getType().getName());
      }

      return getPropertyType(topLevelProxy, childProperty);
    }
    else if (agent.propertyTypes.containsKey(property)) {
      return agent.propertyTypes.get(property);
    }
    else {
      final String type = proxy.getClass().getSuperclass().getSimpleName();
      throw new NonExistingPropertyException(type, property);
    }
  }

  private Binding bindDirectProperty(final Object component, final String property, final Converter converter,
          final Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> handlerRegistrar,
          final Optional<Supplier<Object>> uiGetter, final StateSync initialState) {
    checkComponentNotAlreadyBound(component, property);
    final Binding binding = createBinding(component, property, converter, handlerRegistrar);
    syncState(component, property, converter, uiGetter, initialState);

    return binding;
  }

  private Binding createBinding(final Object component, final String property, final Converter converter,
          final Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> handlerRegistrar) {
    final Binding binding = new Binding(property, component, converter, handlerRegistrar.get());
    bindings.put(property, binding);

    if (propertyTypes.get(property).isList()) {
      if ("this".equals(property) && proxy instanceof BindableListWrapper) {
        addHandlersForBindableListWrapper("this", (BindableListWrapper) proxy);
      }
      else {
        proxy.set(property, ensureBoundListIsProxied(property));
      }
    }

    return binding;
  }

  private void checkComponentNotAlreadyBound(final Object component, final String property) {
    for (final Binding binding : bindings.values()) {
      if (binding.getComponent().equals(component) && !property.equals(binding.getProperty())) {
        throw new ComponentAlreadyBoundException("Widget already bound to property: " + binding.getProperty());
      }
    }
  }

  private Converter findConverter(final String property, final Class<?> propertyType, final Object component, final Converter userProvidedConverter) {
    final Optional<Class<?>> componentValueType;

    if (component instanceof Widget) {
      final Class<?> presumedType = (userProvidedConverter == null) ? propertyType : userProvidedConverter.getComponentType();
      componentValueType = Optional.ofNullable(Convert.inferWidgetValueType((Widget) component, presumedType));
    }
    else if (isElement(component)) {
      final Element element = BoundUtil.asElement(component);
      if (InputElement.is(element)) {
        componentValueType = Optional.ofNullable(ElementWrapperWidget.getValueClassForInputType(element.getPropertyString("type")));
      }
      else if (TextAreaElement.is(element)) {
        componentValueType = Optional.ofNullable(String.class);
      }
      else {
        componentValueType = Optional.empty();
      }
    }
    else {
      componentValueType = Optional.empty();
    }

    if (userProvidedConverter != null) {
      validateTypes(property, propertyType, userProvidedConverter.getModelType(), "model");
      componentValueType.ifPresent(t -> validateTypes(property, t, userProvidedConverter.getComponentType(), "widget"));

      return userProvidedConverter;
    }
    else {
      final Class<?> effectiveComponentType = componentValueType.orElse(String.class);
      final Converter<?, ?> converter = Convert.getConverter(propertyType, effectiveComponentType);

      if (converter == null) {
        throw new RuntimeException(
                "Cannot convert between " + propertyType.getName() + " and " + effectiveComponentType.getName()
                        + " for property [" + property + "] in " + proxy.unwrap().getClass().getName());
      }

      return converter;
    }
  }

  private void validateTypes(final String property, final Class<?> actualType, final Class<?> converterType, final String modelOrWidget) {
    if (!actualType.equals(converterType) && !oneTypeIsInterface(actualType, converterType)) {
      throw new RuntimeException("Converter " + modelOrWidget + " type, " + converterType.getName()
              + ", does not match the required type, " + actualType.getName());
    }
  }

  private boolean oneTypeIsInterface(final Class<?> propertyType, final Class<?> converterModelType) {
    return propertyType.isInterface() || converterModelType.isInterface();
  }

  /**
   * Makes a supplier for adding the required event handlers to the provided component. A {@link ValueChangeHandler} is
   * added by default, a {@link KeyUpHandler} is added if bindOnKeyUp is true.
   *
   * @param component
   *          the bound UI component, must not be null.
   * @param bindOnKeyUp
   *          a flag indicating that the property should be updated when the widget fires a
   *          {@link com.google.gwt.event.dom.client.KeyUpEvent} , in addition to the default
   *          {@link com.google.gwt.event.logical.shared.ValueChangeEvent}.
   * @param uiGetter
   *          an optional function for extracting the UI component value.
   * @param modelUpdater
   *          A {@link Consumer<?>} that updates the bound property of the model in response to UI changes.
   * @return a supplier that registers handlers and returns a collection of event handler registrations.
   */
  private static Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> addHandlers(
          final Object component, final boolean bindOnKeyUp, final Optional<Supplier<Object>> uiGetter,
          final Consumer<Object> modelUpdater) {
    checkWidgetHasTextOrValue(component);

    Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> registrar = HashMap::new;

    if (component instanceof HasValue) {
      registrar = mergeHasValueChangeHandler(component, modelUpdater, registrar);
    }
    else if (component instanceof IsElement) {
      registrar = mergeNativeChangeEventListener(((IsElement) component).getElement(), uiGetter, modelUpdater, registrar);
    }
    else if (component instanceof org.jboss.errai.common.client.api.elemental2.IsElement) {
      registrar = mergeNativeChangeEventListener(((org.jboss.errai.common.client.api.elemental2.IsElement) component).getElement(), uiGetter, modelUpdater, registrar);
    }
    else if (isElement(component)) {
      registrar = mergeNativeChangeEventListener(component, uiGetter, modelUpdater, registrar);
    }

    if (bindOnKeyUp) {
      if (component instanceof ValueBoxBase) {
        registrar = mergeValueBoxKeyUpHandler(component, modelUpdater, registrar);
      }
      else if (component instanceof ElementWrapperWidget) {
        registrar = mergeNativeKeyUpEventListener(((ElementWrapperWidget) component).getElement(), uiGetter, modelUpdater, registrar);
      }
      else if (component instanceof IsElement) {
        registrar = mergeNativeKeyUpEventListener(((IsElement) component).getElement(), uiGetter, modelUpdater, registrar);
      }
      else if (component instanceof org.jboss.errai.common.client.api.elemental2.IsElement) {
        registrar = mergeNativeKeyUpEventListener(((org.jboss.errai.common.client.api.elemental2.IsElement) component).getElement(), uiGetter, modelUpdater, registrar);
      }
      else if (isElement(component)) {
        registrar = mergeNativeKeyUpEventListener(component, uiGetter, modelUpdater, registrar);
      }
      else {
        throw new RuntimeException("Cannot bind component " + component.toString() + " on key up events, " + component.toString()
                + " is not a ValueBoxBase or an element wrapper");
      }
    }

    return registrar;
  }

  private static boolean isElement(final Object obj) {
    return obj instanceof JavaScriptObject && Node.is((JavaScriptObject) obj) && Element.is((Node) obj);
  }

  private static native JavaScriptObject wrap(Runnable runnable) /*-{
    return function() {
      runnable.@java.lang.Runnable::run()();
    };
  }-*/;

  private static native void addChangeEventListener(Object element, JavaScriptObject listener) /*-{
    element.addEventListener("change", listener);
  }-*/;

  private static native void addKeyUpEventListener(Object element, JavaScriptObject listener) /*-{
    element.addEventListener("keyup", listener);
  }-*/;

  private static native void removeChangeEventListener(Object element, JavaScriptObject listener) /*-{
    element.removeEventListener("change", listener);
  }-*/;

  private static native void removeKeyUpEventListener(Object element, JavaScriptObject listener) /*-{
    element.removeEventListener("keyup", listener);
  }-*/;

  private static Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> mergeValueBoxKeyUpHandler(
          final Object component, final Consumer<Object> modelUpdater,
          Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> registrar) {

    registrar = mergeToLeft(registrar, () -> {
      logger.debug("Adding ValueBox keyup handler to {}", component);
      final HandlerRegistration keyUpHandlerReg = ((ValueBoxBase) component)
              .addKeyUpHandler(event -> modelUpdater.accept(((ValueBoxBase) component).getText()));

      return Collections.singletonMap(KeyUpEvent.class, keyUpHandlerReg);
    });

    return registrar;
  }

  private static Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> mergeHasValueChangeHandler(
          final Object component, final Consumer<Object> modelUpdater,
          Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> registrar) {

    registrar = mergeToLeft(registrar, () -> {
      logger.debug("Adding value change handler to {}", component);
      final HandlerRegistration valueHandlerReg = ((HasValue) component).addValueChangeHandler(event -> {
        final Object value = ((HasValue) component).getValue();
        modelUpdater.accept(value);
      });

      return Collections.singletonMap(ValueChangeEvent.class, valueHandlerReg);
    });
    return registrar;
  }

  private static Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> mergeNativeChangeEventListener(
          final Object component, final Optional<Supplier<Object>> uiGetter, final Consumer<Object> modelUpdater,
          Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> registrar) {

    registrar = mergeToLeft(registrar, () -> {
      logger.debug("Adding native change event listener to {}", component);
      final JavaScriptObject listener = wrap(() -> uiGetter.ifPresent(getter -> modelUpdater.accept(getter.get())));
      addChangeEventListener(component, listener);

      final HandlerRegistration hr = () -> removeChangeEventListener(component, listener);
      return Collections.singletonMap(ValueChangeEvent.class, hr);
    });

    return registrar;
  }

  private static Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> mergeNativeKeyUpEventListener(
          final Object component, final Optional<Supplier<Object>> uiGetter, final Consumer<Object> modelUpdater,
          Supplier<Map<Class<? extends GwtEvent>, HandlerRegistration>> registrar) {

    registrar = mergeToLeft(registrar, () -> {
      final JavaScriptObject listener = wrap(() -> {
        logger.debug("keyup listener invoked for {}", component);
        uiGetter.ifPresent(getter -> {
          final Object value = getter.get();
          logger.debug("keyup listener invoked with UI value {}", value);
          modelUpdater.accept(value);
        });
      });
      logger.debug("Adding native keyup listener to {}...", component);
      addKeyUpEventListener(component, listener);
      logger.debug("Added native keyup listener to {}", component);

      final HandlerRegistration hr = () -> removeKeyUpEventListener(component, listener);
      return Collections.singletonMap(ValueChangeEvent.class, hr);
    });

    return registrar;
  }

  private static void checkWidgetHasTextOrValue(final Object component) {
    if (component instanceof Widget && !(component instanceof HasText || component instanceof TakesValue)) {
      throw new RuntimeException(
              "Widget must implement either " + TakesValue.class.getName() + " or " + HasText.class.getName() + "!");
    }
  }

  private static <K, V> Supplier<Map<K, V>> mergeToLeft(final Supplier<Map<K, V>> f, final Supplier<Map<K, V>> g) {
    return () -> {
      final Map<K, V> retVal = f.get();
      retVal.putAll(g.get());

      return retVal;
    };
  }

  /**
   * Creates a data binder for a nested property to support property chains. The
   * nested data binder is initialized with the current value of the specified
   * property, or with a new instance of the property type if the value is null.
   * The proxy's value for this property is then replaced with the proxy managed
   * by the nested data binder.
   *
   * @param property
   *          the property of the model to bind the widget to, must not be null.
   *          The property must be of a @Bindable type.
   */
  private DataBinder createNestedBinder(final String property, final StateSync initialState) {
    final String bindableProperty = property.substring(0, property.indexOf("."));
    DataBinder<Object> binder = binders.get(bindableProperty);

    if (!propertyTypes.containsKey(bindableProperty)) {
      final String type = proxy.getClass().getSuperclass().getSimpleName();
      throw new NonExistingPropertyException(type, bindableProperty);
    }

    if (!propertyTypes.get(bindableProperty).isBindable()) {
      throw new RuntimeException("The type of property " + bindableProperty + " ("
              + propertyTypes.get(bindableProperty).getType().getName() + ") is not a @Bindable type!");
    }

    if (binder == null) {
      if (proxy.get(bindableProperty) == null) {
        binder = DataBinder.forType(propertyTypes.get(bindableProperty).getType());
      }
      else {
        binder = DataBinder.forModel(proxy.get(bindableProperty));
      }
      binders.put(bindableProperty, binder);
      for (final PropertyChangeHandler<?> handler : propertyChangeHandlerSupport.specificPropertyHandlers.get("**")) {
        binder.addPropertyChangeHandler("**", handler);
      }
    }
    else if (proxy.get(bindableProperty) != null) {
      binder.setModel(proxy.get(bindableProperty), initialState, true);
    }
    proxy.set(bindableProperty, binder.getModel());
    knownValues.put(bindableProperty, binder.getModel());

    if (property.indexOf('.') != property.lastIndexOf('.')) {
      ((BindableProxy<?>) binder.getModel()).getBindableProxyAgent().createNestedBinder(property.substring(property.indexOf('.')+1), initialState);
    }

    return binder;
  }

  private void validatePropertyExpr(final String property) {
    if (property.startsWith(".") || property.endsWith(".")) {
      throw new InvalidPropertyExpressionException(
              "Binding expression (property chain) cannot start or end with '.' : " + property);
    }
    if (property.contains("*.")) {
      throw new InvalidPropertyExpressionException("Wildcards can only appear at the end of property expressions : "
              + property);
    }
  }

  /**
   * Unbinds the property with the given name.
   *
   * @param binding
   *          the name of the model property to unbind, must not be null.
   */
  public void unbind(final Binding binding) {
    final String property = binding.getProperty();
    validatePropertyExpr(property);

    final int dotPos = property.indexOf(".");
    if (dotPos > 0) {
      final String bindableProperty = property.substring(0, dotPos);
      final DataBinder binder = binders.get(bindableProperty);
      if (binder != null) {
        final BindableProxyAgent<T> nestedAgent = ((BindableProxy<T>) binder.getModel()).getBindableProxyAgent();
        final Collection<Binding> nestedBindings = nestedAgent.bindings.get(property.substring(dotPos + 1));
        for (final Binding nestedBinding : nestedBindings.toArray(new Binding[nestedBindings.size()])) {
          if (binding.getComponent() == nestedBinding.getComponent()) {
            nestedAgent.unbind(nestedBinding);
          }
        }
      }
    }
    binding.removeHandlers();
    bindings.remove(property, binding);

    if (bindings.isEmpty()) {
      BindableProxyFactory.removeCachedProxyForModel(target);
    }
  }

  /**
   * Updates all bound widgets if necessary (if a bound property's value has
   * changed). This method is invoked in case a bound property changed outside
   * the property's write method (when using a non accessor method).
   */
  void updateWidgetsAndFireEvents() {
    for (final String property : propertyTypes.keySet()) {
      final Object knownValue = knownValues.get(property);
      final Object actualValue = proxy.get(property);

      if ((knownValue == null && actualValue != null) || (knownValue != null && !knownValue.equals(actualValue))) {

        final DataBinder nestedBinder = binders.get(property);
        if (nestedBinder != null) {
          nestedBinder.setModel(actualValue, StateSync.FROM_MODEL, true);
          proxy.set(property, nestedBinder.getModel());
        }
        updateWidgetsAndFireEvent(true, property, knownValue, actualValue);
      }
    }
  }

  /**
   * Updates all bound widgets and fires the corresponding {@link PropertyChangeEvent}.
   *
   * @param
   *          <P>
   *          The property type of the changed property.
   * @param sync
   *          True if a {@link BindableListChangeHandler} component bound to a list should have it's value set via
   *          {@link TakesValue#setValue(Object)}.
   * @param property
   *          The name of the property that changed. Must not be null.
   * @param oldValue
   *          The old value of the property.
   * @param newValue
   *          The new value of the property.
   */
  <P> void updateWidgetsAndFireEvent(final boolean sync, final String property, final P oldValue, final P newValue) {
    updateWidgetsAndFireEvent(sync, property, oldValue, newValue, null);
  }

  /**
   * Updates all bound widgets and fires the corresponding
   * {@link PropertyChangeEvent}.
   *
   * @param <P>
   *          The property type of the changed property.
   * @param sync
   *          True if a {@link BindableListChangeHandler} component bound to a list should have it's value set via
   *          {@link TakesValue#setValue(Object)}.
   * @param property
   *          The name of the property that changed.
   * @param oldValue
   *          The old value of the property.
   * @param newValue
   *          The new value of the property.
   * @param excluding
   *          A widget reference that does not need to be updated (the origin of
   *          the value change event).
   */
  private <P> void updateWidgetsAndFireEvent(final boolean sync, final String property, final P oldValue, final P newValue,
          final Object excluding) {

    for (final Binding binding : bindings.get(property)) {
      final Object component = binding.getComponent();
      final Converter converter = binding.getConverter();

      if (component == excluding)
        continue;

      if (!sync && binding.propertyIsList() && component instanceof BindableListChangeHandler)
        continue;

      if (component instanceof TakesValue) {
        updateComponentValue(newValue, (TakesValue) component, converter);
      } else if (component instanceof HasText) {
        updateComponentValue(newValue, (HasText) component, converter);
      } else if (component instanceof IsElement
              || component instanceof org.jboss.errai.common.client.api.elemental2.IsElement || isElement(component)) {

        final Element element = BoundUtil.asElement(getUIPart(component));
        final ElementWrapperWidget<?> wrapper = ElementWrapperWidget.getWidget(element);

        if (wrapper instanceof TakesValue) {
          updateComponentValue(newValue, (TakesValue) wrapper, converter);
        } else if (wrapper instanceof HasText) {
          updateComponentValue(newValue, (HasText) wrapper, converter);
        }
      }
    }

    maybeFirePropertyChangeEvent(property, oldValue, newValue);
  }

  private Object getUIPart(final Object component) {
    if (component instanceof IsElement) {
      return ((IsElement) component).getElement();
    } else if (component instanceof org.jboss.errai.common.client.api.elemental2.IsElement) {
      return ((org.jboss.errai.common.client.api.elemental2.IsElement) component).getElement();
    } else {
      return component;
    }
  }

  private <P> void updateComponentValue(final P newValue, final HasText component, final Converter converter) {
    assert String.class.equals(converter.getComponentType());
    final Object widgetValue = converter.toWidgetValue(newValue);
    component.setText((String) widgetValue);
  }

  private <P> void updateComponentValue(final P newValue, final TakesValue component, final Converter converter) {
    final Object widgetValue = converter.toWidgetValue(newValue);
    component.setValue(widgetValue);
  }

  /**
   * Fires a property change event unless the property is {@code "this"}. The {@code "this"} property is only fired for
   * types with no other properties.
   *
   * @param
   *          <P>
   *          The property type of the changed property.
   * @param property
   *          The name of the property that changed. Must not be null.
   * @param oldValue
   *          The old value of the property.
   * @param newValue
   *          The new value of the property.
   * @return true Iff property change handlers were notified for this type.
   */
  private <P> boolean maybeFirePropertyChangeEvent(final String property, final P oldValue, final P newValue) {
    knownValues.put(property, newValue);

    final PropertyChangeEvent<P> event = new PropertyChangeEvent<>(proxy, Assert.notNull(property), oldValue, newValue);

    if (!"this".equals(property) || propertyTypes.size() == 1) {
      propertyChangeHandlerSupport.notifyHandlers(event);
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Synchronizes the state of the provided widgets and model property based on the value of the provided
   * {@link StateSync}.
   *
   * @param component
   *          The widget to synchronize. Must not be null.
   * @param property
   *          The name of the model property that should be synchronized. Must not be null.
   * @param converter
   *          The converter specified for the property binding. Must not be null.
   * @param uiGetter
   *          An optional function for getting the value from the UI component. Must not be null.
   * @param initialState
   *          If {@link StateSync#FROM_MODEL} the UI value will be updated from the model. If {@link StateSync#FROM_UI}
   *          the model value will be updated from the UI. If null no synchronization is performed.
   */
  private void syncState(final Object component, final String property, final Converter converter,
          final Optional<Supplier<Object>> uiGetter, final StateSync initialState) {
    Assert.notNull(component);
    Assert.notNull(property);

    if (initialState != null) {
      final Object modelValue = proxy.get(property);
      final Optional<Object> uiValue = uiGetter.map(f -> f.get());

      final Object value = uiValue.map(v -> initialState.getInitialValue(modelValue, v)).orElse(modelValue);

      if (initialState == StateSync.FROM_MODEL) {
        updateWidgetsAndFireEvent(true, property, knownValues.get(property), value);
      }
      else if (initialState == StateSync.FROM_UI) {
        final Object newValue = converter.toModelValue(value);
        proxy.set(property, newValue);
        maybeFirePropertyChangeEvent(property, knownValues.get(property), newValue);
        updateWidgetsAndFireEvent(true, property, knownValues.get(property), newValue, component);
      }
    }
  }

  /**
   * Ensures that the given list property is wrapped in a
   * {@link BindableListWrapper}, so changes to the list become observable.
   *
   * @param property
   *          the name of the list property
   *
   * @return a new the wrapped (proxied) list or the provided list if already
   *         proxied
   */
  private List ensureBoundListIsProxied(final String property) {
    final List oldList = (List) proxy.get(property);
    final List newList = ensureBoundListIsProxied(property, oldList);
    if (oldList != newList)
      updateWidgetsAndFireEvent(true, property, proxy.get(property), newList);

    return newList;
  }

  /**
   * Ensures that the given list property is wrapped in a
   * {@link BindableListWrapper}, so changes to the list become observable.
   *
   * @param property
   *          the name of the list property
   * @param list
   *          the list that needs to be proxied
   *
   * @return a new the wrapped (proxied) list or the provided list if already
   *         proxied
   */
  List ensureBoundListIsProxied(final String property, final List list) {
    if (!(list instanceof BindableListWrapper) && bindings.containsKey(property) && list != null) {
      final BindableListWrapper newList = new BindableListWrapper(list);
      addHandlersForBindableListWrapper(property, newList);

      return newList;
    }

    return list;
  }

  private void addHandlersForBindableListWrapper(final String property, final BindableListWrapper newList) {
    modelChangeHandlers.add(newList.addChangeHandler(new UnspecificListChangeHandler() {
      @Override
      void onListChanged(final List oldList) {
        updateWidgetsAndFireEvent(false, property, oldList, newList);
      }
    }));

    for (final Binding binding : bindings.get(property)) {
      if (binding.getComponent() instanceof BindableListChangeHandler) {
        modelChangeHandlers.add(newList.addChangeHandler((BindableListChangeHandler) binding.getComponent()));
      }
    }
  }

  /**
   * Returns the {@link PropertyChangeHandlerSupport} object of this agent
   * containing all change handlers that have been registered for the
   * corresponding model proxy.
   *
   * @return propertyChangeHandlerSupport object, never null.
   */
  public PropertyChangeHandlerSupport getPropertyChangeHandlers() {
    return propertyChangeHandlerSupport;
  }

  @Override
  public PropertyChangeUnsubscribeHandle addPropertyChangeHandler(final PropertyChangeHandler handler) {
    propertyChangeHandlerSupport.addPropertyChangeHandler(handler);

    return new OneTimeUnsubscribeHandle() {

      @Override
      public void doUnsubscribe() {
        propertyChangeHandlerSupport.removePropertyChangeHandler(handler);
      }
    };
  }

  @Override
  public <P> PropertyChangeUnsubscribeHandle addPropertyChangeHandler(final String property, final PropertyChangeHandler<P> handler) {
    validatePropertyExpr(property);

    final Collection<PropertyChangeUnsubscribeHandle> unsubHandles = new ArrayList<>();

    final int dotPos = property.indexOf(".");
    if (dotPos > 0) {
      final DataBinder nested = createNestedBinder(property, StateSync.FROM_MODEL);
      unsubHandles.add(nested.addPropertyChangeHandler(property.substring(dotPos + 1), handler));
    }
    else if (property.equals("*")) {
      propertyChangeHandlerSupport.addPropertyChangeHandler(handler);
      unsubHandles.add(new PropertyChangeUnsubscribeHandle() {

        @Override
        public void unsubscribe() {
          propertyChangeHandlerSupport.removePropertyChangeHandler(handler);
        }
      });
    }
    else if (property.equals("**")) {
      for (final DataBinder nested : binders.values()) {
        unsubHandles.add(nested.addPropertyChangeHandler(property, handler));
      }
      propertyChangeHandlerSupport.addPropertyChangeHandler(handler);
      unsubHandles.add(new PropertyChangeUnsubscribeHandle() {

        @Override
        public void unsubscribe() {
          propertyChangeHandlerSupport.removePropertyChangeHandler(handler);
        }
      });
    }

    propertyChangeHandlerSupport.addPropertyChangeHandler(property, handler);
    unsubHandles.add(new PropertyChangeUnsubscribeHandle() {

      @Override
      public void unsubscribe() {
        propertyChangeHandlerSupport.removePropertyChangeHandler(property, handler);
      }
    });

    return new OneTimeUnsubscribeHandle() {

      @Override
      public void doUnsubscribe() {
        for (final PropertyChangeUnsubscribeHandle handle : unsubHandles) {
          handle.unsubscribe();
        }
      }
    };
  }

  /**
   * Merges the provided {@link PropertyChangeHandler}s of the provided agent
   * instance. If a handler instance is already registered on this agent, it
   * will NOT be added again.
   *
   * @param pchs
   *          the instance who's change handlers will be merged, must not be
   *          null.
   */
  public void mergePropertyChangeHandlers(final PropertyChangeHandlerSupport pchs) {
    Assert.notNull(pchs);

    for (final PropertyChangeHandler pch : pchs.handlers) {
      if (!propertyChangeHandlerSupport.handlers.contains(pch)) {
        addPropertyChangeHandler(pch);
      }
    }

    for (final String pchKey : pchs.specificPropertyHandlers.keys()) {
      for (final PropertyChangeHandler pch : pchs.specificPropertyHandlers.get(pchKey)) {
        if (!propertyChangeHandlerSupport.specificPropertyHandlers.containsEntry(pchKey, pch)) {
          addPropertyChangeHandler(pchKey, pch);
        }
      }
    }
  }

  /**
   * Compares property values between this agent and the provided agent recursively and fires
   * {@link PropertyChangeEvent}s for all differences.
   *
   * @param other
   *          the agent to compare against.
   * @param initialState
   *          If {@link StateSync#FROM_MODEL} the state from this agent's model overrides the other. If
   *          {@link StateSync#FROM_UI} the state from the other agent's model overrides this one. If null, default to
   *          {@link StateSync#FROM_MODEL}.
   */
  public void fireChangeEvents(final BindableProxyAgent other, final StateSync initialState) {
    for (final String property : propertyTypes.keySet()) {
      final Object curValue,
                   oldValue,
                   thisValue = knownValues.get(property),
                   otherValue = other.knownValues.get(property);

      final StateSync state = (initialState != null ? initialState : StateSync.FROM_MODEL);
      curValue = state.getInitialValue(thisValue, otherValue);
      oldValue = state.getInitialValue(otherValue, thisValue);

      if ((curValue == null && oldValue != null) || (curValue != null && !curValue.equals(oldValue))) {

        final DataBinder nestedBinder = binders.get(property);
        final DataBinder otherNestedBinder = (DataBinder) other.binders.get(property);
        if (nestedBinder != null && otherNestedBinder != null) {
          final BindableProxyAgent nestedAgent = ((BindableProxy<T>) nestedBinder.getModel()).getBindableProxyAgent();
          final BindableProxyAgent otherNestedAgent = ((BindableProxy<T>) otherNestedBinder.getModel()).getBindableProxyAgent();
          nestedAgent.fireChangeEvents(otherNestedAgent, state);
        }

        maybeFirePropertyChangeEvent(property, oldValue, curValue);
      }
    }
  }

  public void clearModelHandlers() {
    for (final HandlerRegistration reg : modelChangeHandlers) {
      reg.removeHandler();
    }
    modelChangeHandlers.clear();

    for (final DataBinder<?> binder : binders.values()) {
      getAgent(binder).clearModelHandlers();
    }
  }

  private static BindableProxyAgent<?> getAgent(final DataBinder<?> binder) {
    return ((BindableProxy<?>) binder.getModel()).getBindableProxyAgent();
  }
}
