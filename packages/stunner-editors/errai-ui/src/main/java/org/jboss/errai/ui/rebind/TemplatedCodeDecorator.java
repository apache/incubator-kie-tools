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


package org.jboss.errai.ui.rebind;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.util.TypeLiteral;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jboss.errai.codegen.Cast;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.AnonymousClassStructureBuilder;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.ClassDefinitionBuilderInterfaces;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilderAbstractMethodOption;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.meta.impl.java.JavaReflectionClass;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.annotations.BrowserEvent;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.common.client.ui.HasValue;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.client.container.DestructionCallback;
import org.jboss.errai.ioc.rebind.ioc.bootstrapper.InjectUtil;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.ui.client.local.spi.TemplateRenderingCallback;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.DataField.ConflictStrategy;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;
import org.lesscss.LessSource;
import org.lesscss.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.jboss.errai.codegen.builder.impl.ObjectBuilder.newInstanceOf;
import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.declareVariable;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.codegen.util.Stmt.newObject;
import static org.jboss.errai.ioc.util.GeneratedNamesUtil.qualifiedClassNameToShortenedIdentifier;

/**
 * Generates the code required for {@link Templated} classes.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Christian Sadilek <csadilek@redhat.com>
 */

//This decorator has to run after the decorator for @DataField
@CodeDecorator(order=1)
public class TemplatedCodeDecorator extends IOCDecoratorExtension<Templated> {
  private static final String CONSTRUCTED_TEMPLATE_SET_KEY = "constructedTemplate";

  private static final Logger logger = LoggerFactory.getLogger(TemplatedCodeDecorator.class);

  public TemplatedCodeDecorator(final Class<Templated> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public void generateDecorator(final Decorable decorable, final FactoryController controller) {
    final MetaClass declaringClass = decorable.getDecorableDeclaringType();

    final Templated anno = (Templated) decorable.getAnnotation();
    final Class<?> templateProvider = anno.provider();
    final boolean customProvider = templateProvider != Templated.DEFAULT_PROVIDER.class;
    final Optional<String> styleSheetPath = getTemplateStyleSheetPath(declaringClass);
    final boolean explicitStyleSheetPresent = styleSheetPath.filter(path -> Thread.currentThread().getContextClassLoader().getResource(path) != null).isPresent();

    if (declaringClass.isAssignableTo(Composite.class)) {
      logger.warn("The @Templated class, {}, extends Composite. This will not be supported in future versions.", declaringClass.getFullyQualifiedName());
    }
    if (styleSheetPath.isPresent() && !explicitStyleSheetPresent) {
      throw new GenerationException("@Templated class [" + declaringClass.getFullyQualifiedName()
              + "] declared a stylesheet [" + styleSheetPath + "] that could not be found.");
    }

    final List<Statement> initStmts = new ArrayList<>();

    generateTemplatedInitialization(decorable, controller, initStmts, customProvider);

    if (customProvider) {
      final Statement init =
        Stmt.invokeStatic(TemplateUtil.class, "provideTemplate",
          templateProvider,
          getTemplateUrl(declaringClass),
          Stmt.newObject(TemplateRenderingCallback.class)
            .extend()
            .publicOverridesMethod("renderTemplate", Parameter.of(String.class, "template", true))
            .appendAll(initStmts)
            .finish()
            .finish());

      controller.addInitializationStatements(Collections.singletonList(init));
    }
    else {
      controller.addInitializationStatements(initStmts);
    }

    controller.addDestructionStatements(generateTemplateDestruction(decorable));
    controller.addInitializationStatementsToEnd(Collections.<Statement>singletonList(invokeStatic(StyleBindingsRegistry.class, "get")
        .invoke("updateStyles", Refs.get("instance"))));
  }

  /**
   * Generates a {@link DestructionCallback} for the {@link Templated} component.
   *
   * @return statement representing the template destruction logic.
   */
  private List<Statement> generateTemplateDestruction(final Decorable decorable) {
    final List<Statement> destructionStatements = new ArrayList<>();
    final Map<String, Statement> dataFields = DataFieldCodeDecorator.aggregateDataFieldMap(decorable, decorable.getDecorableDeclaringType());
    final Map<String, MetaClass> dataFieldTypes =
      DataFieldCodeDecorator.aggregateDataFieldTypeMap(decorable, decorable.getDecorableDeclaringType());

    for (final String fieldName : dataFields.keySet()) {
      final Statement field = dataFields.get(fieldName);
      final MetaClass fieldType = dataFieldTypes.get(fieldName);

      if (fieldType.isAssignableTo(Element.class)) {
        destructionStatements.add(Stmt.invokeStatic(ElementWrapperWidget.class, "removeWidget", field));
      }
    }

    if (decorable.getDecorableDeclaringType().isAssignableTo(Composite.class)) {
      destructionStatements.add(Stmt.invokeStatic(TemplateUtil.class, "cleanupWidget", decorable.getAccessStatement()));
    } else {
      destructionStatements.add(Stmt.invokeStatic(TemplateUtil.class, "cleanupTemplated", decorable.getAccessStatement()));
    }

    return destructionStatements;
  }

  /**
   * Generate the actual construction logic for our {@link Templated} component
   * @param styleSheet
   */
  @SuppressWarnings("serial")
  private void generateTemplatedInitialization(final Decorable decorable,
                                               final FactoryController controller,
                                               final List<Statement> initStmts,
                                               final boolean customProvider) {

    final Map<MetaClass, BuildMetaClass> constructed = getConstructedTemplateTypes(decorable);
    final MetaClass declaringClass = decorable.getDecorableDeclaringType();

    if (!constructed.containsKey(declaringClass)) {
      final String templateVarName = "templateFor" + decorable.getDecorableDeclaringType().getName();
      final Optional<String> resolvedStylesheetPath = getResolvedStyleSheetPath(getTemplateStyleSheetPath(declaringClass), declaringClass);
      final boolean lessStylesheet = resolvedStylesheetPath.filter(path -> path.endsWith(".less")).isPresent();

      /*
       * Generate this component's ClientBundle resource if necessary
       */
      final boolean generateCssBundle = resolvedStylesheetPath.isPresent() && !lessStylesheet;
      if (!customProvider || generateCssBundle) {
        generateTemplateResourceInterface(decorable, declaringClass, customProvider, resolvedStylesheetPath.filter(path -> path.endsWith(".css")));

      /*
       * Instantiate the ClientBundle Template resource
       */
        initStmts.add(declareVariable(constructed.get(declaringClass)).named(templateVarName)
                .initializeWith(invokeStatic(GWT.class, "create", constructed.get(declaringClass))));

        if (generateCssBundle) {
          controller.addFactoryInitializationStatements(singletonList(castTo(constructed.get(declaringClass),
                  invokeStatic(GWT.class, "create", constructed.get(declaringClass))).invoke("getStyle")
                          .invoke("ensureInjected")));
        }
      }

      /*
       * Compile LESS stylesheet to CSS and generate StyleInjector code
       */
      if (resolvedStylesheetPath.isPresent() && lessStylesheet) {
        try {
          final Resource lessResource = new ClassPathResource(resolvedStylesheetPath.get(), Thread.currentThread().getContextClassLoader());
          final LessSource source = new LessSource(lessResource);
          final LessCompiler compiler = new LessCompiler();
          final String compiledCss = compiler.compile(source);

          controller.addFactoryInitializationStatements(singletonList(invokeStatic(StyleInjector.class, "inject", loadLiteral(compiledCss))));
        } catch (IOException | LessException e) {
          throw new RuntimeException("Error while attempting to compile the LESS stylesheet [" + resolvedStylesheetPath.get() + "].", e);
        }
      }

      /*
       * Get root Template Element
       */
      final String parentOfRootTemplateElementVarName = "parentElementForTemplateOf" + decorable.getDecorableDeclaringType().getName();
      initStmts.add(Stmt
          .declareVariable(Element.class)
          .named(parentOfRootTemplateElementVarName)
          .initializeWith(
              Stmt.invokeStatic(TemplateUtil.class, "getRootTemplateParentElement",
                  (customProvider) ? Variable.get("template") :
                    Stmt.loadVariable(templateVarName).invoke("getContents").invoke("getText"),
                  getTemplateFileName(declaringClass),
                  getTemplateFragmentName(declaringClass))));

      final Statement rootTemplateElement = Stmt.invokeStatic(TemplateUtil.class, "getRootTemplateElement",
              Stmt.loadVariable(parentOfRootTemplateElementVarName));

      /*
       * If i18n is enabled for this module, translate the root template element here
       */
      if (!customProvider) {
        translateTemplate(decorable, initStmts, rootTemplateElement);
      }

      /*
       * Get a reference to the actual Composite component being created
       */
      final Statement component = Refs.get("instance");

      /*
       * Get all of the data-field Elements from the Template
       */
      final String dataFieldElementsVarName = "dataFieldElements";
      initStmts.add(Stmt.declareVariable(dataFieldElementsVarName,
          new TypeLiteral<Map<String, Element>>() {},
          Stmt.invokeStatic(TemplateUtil.class, "getDataFieldElements",
                  rootTemplateElement))
      );

      final String dataFieldMetasVarName = "dataFieldMetas";
      initStmts.addAll(generateDataFieldMetas(dataFieldMetasVarName, decorable));

      /*
       * Attach Widget field children Elements to the Template DOM
       */
      final String fieldsMapVarName = "templateFieldsMap";

      /*
       * The Map<String, Widget> to store actual component field references.
       */
      initStmts.add(declareVariable(fieldsMapVarName, new TypeLiteral<Map<String, Widget>>() {},
          newObject(new TypeLiteral<LinkedHashMap<String, Widget>>() {}))
      );
      final Statement fieldsMap = Stmt.loadVariable(fieldsMapVarName);

      generateComponentCompositions(decorable, initStmts, component, rootTemplateElement,
          loadVariable(dataFieldElementsVarName), fieldsMap, loadVariable(dataFieldMetasVarName));

      generateEventHandlerMethodClasses(decorable, controller, initStmts, dataFieldElementsVarName, fieldsMap);
    }
  }

  private Optional<String> getResolvedStyleSheetPath(final Optional<String> declaredStylesheetPath,
          final MetaClass declaringClass) {
    if (declaredStylesheetPath.isPresent()) {
      return declaredStylesheetPath;
    }
    else {
      final String simpleName = declaringClass.getName();
      final String unsuffixedPath = declaringClass.getPackageName().replace('.', '/') + "/" + simpleName;
      final boolean cssSheetExists = (Thread.currentThread().getContextClassLoader().getResource(unsuffixedPath + ".css") != null);
      if (cssSheetExists) {
        return Optional.of(unsuffixedPath + ".css");
      }
      final boolean lessSheetExists = (Thread.currentThread().getContextClassLoader().getResource(unsuffixedPath + ".less") != null);
      if (lessSheetExists) {
        return Optional.of(unsuffixedPath + ".less");
      }

      return Optional.empty();
    }
  }

  @SuppressWarnings("serial")
  private List<Statement> generateDataFieldMetas(final String dataFieldMetasVarName, final Decorable decorable) {
    final Map<String, DataField> annoMap = DataFieldCodeDecorator.aggregateDataFieldAnnotationMap(decorable, decorable.getType());
    final List<Statement> stmts = new ArrayList<>(annoMap.size()+1);
    stmts.add(declareFinalVariable(dataFieldMetasVarName, new TypeLiteral<Map<String, DataFieldMeta>>() {
    }, newObject(parameterizedAs(HashMap.class, typeParametersOf(String.class, DataFieldMeta.class)), annoMap.size())));
    annoMap
      .entrySet()
      .stream()
      .map(entry -> {
        final String fieldName = entry.getKey();
        final DataField dataField = entry.getValue();
        Statement dataFieldMetaInstance;
        if (dataField.attributeRules().length == 0 && dataField.defaultStrategy().equals(ConflictStrategy.USE_TEMPLATE)) {
          dataFieldMetaInstance = newObject(DataFieldMeta.class);
        }
        else {
          dataFieldMetaInstance = newObject(DataFieldMeta.class,
                  loadLiteral(dataField.attributeRules()), loadLiteral(dataField.defaultStrategy()));
        }

        return loadVariable(dataFieldMetasVarName).invoke("put", fieldName, dataFieldMetaInstance);
       })
      .collect(Collectors.toCollection(() -> stmts));

    return stmts;
  }

  private void generateEventHandlerMethodClasses(final Decorable decorable, final FactoryController controller,
          final List<Statement> initStmts, final String dataFieldElementsVarName, final Statement fieldsMap) {

    final Statement instance = Refs.get("instance");
    final Map<String, MetaClass> dataFieldTypes = DataFieldCodeDecorator.aggregateDataFieldTypeMap(decorable, decorable.getDecorableDeclaringType());
    dataFieldTypes.put("this", decorable.getDecorableDeclaringType());

    final MetaClass declaringClass = decorable.getDecorableDeclaringType();

    /* Ensure that no @DataFields are handled more than once when used in combination with @SyncNative */
    final Set<String> processedNativeHandlers = new HashSet<>();
    final Set<String> processedEventHandlers = new HashSet<>();

    for (final MetaMethod method : declaringClass.getMethodsAnnotatedWith(EventHandler.class)) {

      final String[] targetDataFieldNames = method.getAnnotation(EventHandler.class).value();

      validateNonEmptyEventHandlerTargets(declaringClass, method, targetDataFieldNames);
      final MetaClass eventType = assertEventType(declaringClass, method);

      if (eventType.isAssignableTo(Event.class)) {
        processGwtDomEvent(controller, initStmts, dataFieldElementsVarName, fieldsMap, instance, dataFieldTypes, declaringClass,
                processedNativeHandlers, processedEventHandlers, method, targetDataFieldNames, eventType);
      }
      else if (eventType.isAssignableTo(com.google.web.bindery.event.shared.Event.class)) {
        processGwtWidgetEvent(controller, initStmts, fieldsMap, dataFieldTypes, declaringClass, processedNativeHandlers,
                processedEventHandlers, method, targetDataFieldNames, eventType);
      }
      else {
        processJsInteropDomEvent(initStmts, dataFieldElementsVarName, fieldsMap, instance, dataFieldTypes, declaringClass, method,
                targetDataFieldNames, eventType, controller);
      }
    }
  }

  private void processJsInteropDomEvent(final List<Statement> initStmts, final String dataFieldElementsVarName,
          final Statement fieldsMap, final Statement instance, final Map<String, MetaClass> dataFieldTypes,
          final MetaClass declaringClass, final MetaMethod method, final String[] targetDataFieldNames,
          final MetaClass eventType, final FactoryController controller) {
    final String[] browserEventTypes = Optional
      .ofNullable(method.getParameters()[0].getAnnotation(ForEvent.class))
      .map(anno -> anno.value())
      .filter(value -> value.length > 0)
      .orElseGet(() -> Optional
                        .ofNullable(eventType.getAnnotation(BrowserEvent.class))
                        .map(anno -> anno.value())
                        .orElseThrow(() ->
                          new GenerationException(
            String.format(
                    "The event argument of %s.%s must specify event types "
                    + "either with @%s on the call site or @%s on the event type.",
                    declaringClass.getName(),
                    method.getName(),
                    ForEvent.class.getSimpleName(),
                    BrowserEvent.class.getSimpleName()))));

    for (final String dataFieldName : targetDataFieldNames) {
      final ObjectBuilder listener = ObjectBuilder
        .newInstanceOf(org.jboss.errai.common.client.dom.EventListener.class)
        .extend()
        .publicOverridesMethod("call", Parameter.of(org.jboss.errai.common.client.dom.Event.class, "event"))
          .append(InjectUtil
                  .invokePublicOrPrivateMethod(controller,
                                               method,
                                               invokeStatic(Js.class, "cast", loadVariable("event"))))
          .finish()
        .finish();
      final ContextualStatementBuilder elementStmt;
      if (dataFieldTypes.containsKey(dataFieldName) && !"this".equals(dataFieldName)) {
        final MetaClass fieldType = dataFieldTypes.get(dataFieldName);
        if (fieldType.isAssignableTo(Widget.class)) {
          elementStmt = castTo(Widget.class, nestedCall(fieldsMap).invoke("get", dataFieldName));
        }
        else {
          elementStmt = nestedCall(fieldsMap).invoke("get", dataFieldName);
        }
      }
      else {
        elementStmt = loadVariable(dataFieldElementsVarName).invoke("get", dataFieldName);
      }
      final String listenerVarName = "listenerFor" + eventType.getName() + "Calling" + capitalize(method.getName());
      initStmts.add(declareFinalVariable(listenerVarName, org.jboss.errai.common.client.dom.EventListener.class, listener));
      for (final String browserEventType : browserEventTypes) {
        initStmts.add(invokeStatic(TemplateUtil.class, "setupBrowserEventListener", instance, elementStmt,
                loadVariable(listenerVarName), loadLiteral(browserEventType)));
      }
    }
  }

  private void processGwtWidgetEvent(final FactoryController controller, final List<Statement> initStmts, final Statement fieldsMap,
          final Map<String, MetaClass> dataFieldTypes, final MetaClass declaringClass,
          final Set<String> processedNativeHandlers, final Set<String> processedEventHandlers, final MetaMethod method,
          final String[] targetDataFieldNames, final MetaClass eventType) {
    /*
     * We have a GWT Widget type
     */
    final MetaClass handlerType = getGwtHandlerType(declaringClass, eventType);

    final BlockBuilder<AnonymousClassStructureBuilder> listenerBuiler = ObjectBuilder.newInstanceOf(handlerType)
        .extend()
        .publicOverridesMethod(handlerType.getMethods()[0].getName(), Parameter.of(eventType, "event"));


    listenerBuiler.append(InjectUtil.invokePublicOrPrivateMethod(controller, method, Stmt.loadVariable("event")));

    final ObjectBuilder listenerInstance = listenerBuiler.finish().finish();

    final MetaClass hasHandlerType = MetaClassFactory.get("com.google.gwt.event.dom.client.Has"
        + handlerType.getName()
        + "s");

    for (final String name : targetDataFieldNames) {
      final MetaClass dataFieldType = dataFieldTypes.get(name);

      if (dataFieldType == null) {
        throw new GenerationException("@EventHandler method [" + method.getName() + "] in class ["
            + declaringClass.getFullyQualifiedName()
            + "] handles a GWT event type but the specified @DataField [" + name + "] was not found.");
      }

      if (processedNativeHandlers.contains(name)) {
        throw new GenerationException(
            "Cannot specify more than one @EventHandler method when @SinkNative is used for data-field ["
                + name + "] in class [" + declaringClass.getFullyQualifiedName()
                + "].");
      }

      processedEventHandlers.add(name);

      // Where will the event come from? It could be a @DataField member, or it could be the templated widget itself!
      final Statement eventSource;
      if ("this".equals(name)) {
        eventSource = Stmt.loadVariable("instance");
      }
      else {
        eventSource = Stmt.nestedCall(fieldsMap).invoke("get", name);
      }

      if (dataFieldType.isAssignableTo(Element.class)) {
        initStmts.add(Stmt.invokeStatic(TemplateUtil.class, "setupWrappedElementEventHandler",
            eventSource, listenerInstance,
            Stmt.invokeStatic(eventType, "getType")));
      }
      else if (dataFieldType.isAssignableTo(hasHandlerType)) {
        final Statement widget = Cast.to(hasHandlerType, eventSource);
        initStmts.add(Stmt.nestedCall(widget).invoke("add" + handlerType.getName(),
            Cast.to(handlerType, listenerInstance)));
      }
      else if (dataFieldType.isAssignableTo(Widget.class)) {
        final Statement widget = Cast.to(Widget.class, eventSource);
        initStmts.add(Stmt.nestedCall(widget).invoke("addDomHandler",
            listenerInstance, Stmt.invokeStatic(eventType, "getType")));
      } else if (RebindUtil.isNativeJsType(dataFieldType) || RebindUtil.isElementalIface(dataFieldType)) {
        initStmts.add(Stmt.invokeStatic(TemplateUtil.class, "setupWrappedElementEventHandler",
            eventSource, listenerInstance,
            Stmt.invokeStatic(eventType, "getType")));
      } else if (dataFieldType.isAnnotationPresent(Templated.class)) {
        final ContextualStatementBuilder widget = Stmt.invokeStatic(TemplateWidgetMapper.class, "get", eventSource);
        initStmts.add(widget.invoke("addDomHandler",
            listenerInstance, Stmt.invokeStatic(eventType, "getType")));
      } else {
        throw new GenerationException("@DataField [" + name + "] of type [" + dataFieldType.getName()
            + "] in class [" + declaringClass.getFullyQualifiedName()
            + "] must implement the interface [" + hasHandlerType.getName()
            + "] specified by @EventHandler method " + method.getName() + "(" + eventType.getName()
            + ")], be a DOM element (wrapped as either a JavaScriptObject or a native @JsType), "
            + "or be a @Templated bean.");
      }
    }
  }

  private MetaClass getGwtHandlerType(final MetaClass declaringClass, final MetaClass eventType) {
    try {
      return getHandlerForEvent(eventType);
    }
    catch (final GenerationException e) {
      /*
       *  see ERRAI-373 for details on this crazy inference (without this message, the cause of the
       *  problem is nearly impossible to diagnose)
       */
      if (declaringClass.getClass() == JavaReflectionClass.class) {
        throw new GenerationException(
            "The type " + declaringClass.getFullyQualifiedName() + " looks like a client-side" +
                " @Templated class, but it is not known to GWT. This probably means that " +
                declaringClass.getName() + " or one of its supertypes contains non-translatable code." +
                " Run the GWT compiler with logLevel=DEBUG to pinpoint the problem.", e);
      }
      throw e;
    }
  }

  private void processGwtDomEvent(final FactoryController controller, final List<Statement> initStmts,
          final String dataFieldElementsVarName, final Statement fieldsMap, final Statement instance,
          final Map<String, MetaClass> dataFieldTypes, final MetaClass declaringClass,
          final Set<String> processedNativeHandlers, final Set<String> processedEventHandlers, final MetaMethod method,
          final String[] targetDataFieldNames, final MetaClass eventType) {
    /*
     * Generate native DOM event handlers.
     */
    final MetaClass handlerType = MetaClassFactory.get(EventListener.class);
    final BlockBuilder<AnonymousClassStructureBuilder> listenerBuilder = ObjectBuilder.newInstanceOf(handlerType)
        .extend()
        .publicOverridesMethod(handlerType.getMethods()[0].getName(), Parameter.of(eventType, "event"));
    listenerBuilder.append(InjectUtil.invokePublicOrPrivateMethod(controller, method, Stmt.loadVariable("event")));

    final ObjectBuilder listenerInstance = listenerBuilder.finish().finish();

    int eventsToSink =
        Event.FOCUSEVENTS | Event.GESTUREEVENTS | Event.KEYEVENTS | Event.MOUSEEVENTS | Event.TOUCHEVENTS;
    if (method.isAnnotationPresent(SinkNative.class)) {
      eventsToSink = method.getAnnotation(SinkNative.class).value();
    }

    for (final String name : targetDataFieldNames) {

      if (processedNativeHandlers.contains(name) || processedEventHandlers.contains(name)) {
        throw new GenerationException(
            "Cannot specify more than one @EventHandler method when @SyncNative is used for data-field ["
                + name + "] in class ["
                + declaringClass.getFullyQualifiedName()
                + "].");
      }
      else {
        processedNativeHandlers.add(name);
      }
      final ContextualStatementBuilder elementStmt;
      if (dataFieldTypes.containsKey(name)) {
        final MetaClass dataFieldType = dataFieldTypes.get(name);
        final boolean gwtUserElement = dataFieldType.isAssignableTo(Element.class);
        final boolean nativeJsType = RebindUtil.isNativeJsType(dataFieldType);
        if (gwtUserElement || nativeJsType) {
          if (dataFieldType.isAssignableTo(HasValue.class)) {
            final MetaClass valueType = dataFieldType.getMethod("getValue", new Class[0]).getReturnType();
            elementStmt = Stmt.castTo(ElementWrapperWidget.class, Stmt.nestedCall(fieldsMap).invoke("get", name, loadLiteral(valueType)));
          }
          else {
            elementStmt = Stmt.castTo(ElementWrapperWidget.class, Stmt.nestedCall(fieldsMap).invoke("get", name));
          }
        } else {
          /*
           * We have a GWT or other Widget type.
           */
          throw new GenerationException("@DataField [" + name + "] of type [" + dataFieldType.getName()
              + "] in class [" + declaringClass.getFullyQualifiedName() + "] is not assignable to ["
              + Element.class.getName() + "] specified by @EventHandler method " + method.getName()
              + "("
              + eventType.getName() + ")]\n");
        }
      } else {
        elementStmt = Stmt.loadVariable(dataFieldElementsVarName).invoke("get", name);
      }

      initStmts.add(Stmt.invokeStatic(TemplateUtil.class, "setupNativeEventListener", instance,
          elementStmt, listenerInstance,
          eventsToSink));
    }
  }

  private void validateNonEmptyEventHandlerTargets(final MetaClass declaringClass, final MetaMethod method, final String[] targetDataFieldNames) {
    if (targetDataFieldNames.length == 0) {
      throw new GenerationException("@EventHandler annotation on method ["
          + declaringClass.getFullyQualifiedName()
          + "." + method.getName() + "] must specify at least one data-field target.");
    }
  }

  private MetaClass assertEventType(final MetaClass declaringClass, final MetaMethod method) {
    final MetaClass eventType = (method.getParameters().length == 1) ? method.getParameters()[0].getType() : null;
    if (eventType != null) {
      if (eventType.isAssignableTo(Event.class) || eventType.isAssignableTo(DomEvent.class)) {
        return eventType;
      }
      else if (isAnnotatedBrowserEvent(eventType)) {
        final BrowserEvent eventTypeAnno = eventType.getAnnotation(BrowserEvent.class);
        final boolean eventTypeMatchesAll = eventTypeAnno.value().length == 0;
        final Optional<ForEvent> oParamAnno = Optional.ofNullable(method.getParameters()[0].getAnnotation(ForEvent.class)).filter(anno -> anno.value().length > 0);
        final boolean parameterDeclaresEvent = oParamAnno.isPresent();

        if (eventTypeMatchesAll && parameterDeclaresEvent
                || !eventTypeMatchesAll && (!parameterDeclaresEvent || Arrays.asList(eventTypeAnno.value()).containsAll(Arrays.asList(oParamAnno.get().value())))) {
          return eventType;
        }
        else {
          String message = String.format("@EventHandler parameter [%s] of method [%s] in class [%s] must declare an event type with @%s",
                  method.getParameters()[0].getName(), method.getName(), declaringClass.getFullyQualifiedName(), ForEvent.class.getSimpleName());
          if (!eventTypeMatchesAll) {
            message += " and must be a subset of the following event types: " + Arrays.toString(eventTypeAnno.value());
          }

          throw new GenerationException(message);
        }
      }
      else if (isElemental2Event(eventType)) {
        return eventType;
      }
    }
    throw new GenerationException(String.format(
            "@EventHandler method [%s] in class [%s] must have exactly one parameter of a type "
            + "annotated with @%s and @JsType(isNative=true) or extending either [%s] or [%s].",
            method.getName(), declaringClass.getFullyQualifiedName(), BrowserEvent.class.getSimpleName(),
            DomEvent.class.getName(), NativeEvent.class.getName()));
  }

  private boolean isElemental2Event(final MetaClass eventType) {
    return eventType.isAssignableTo(elemental2.dom.Event.class) && isNativeJsType(eventType);
  }

  private boolean isAnnotatedBrowserEvent(final MetaClass eventType) {
    return eventType.isAnnotationPresent(BrowserEvent.class) && isNativeJsType(eventType);
  }

  private boolean isNativeJsType(final MetaClass eventType) {
    return Optional
            .ofNullable(eventType.getAnnotation(JsType.class)).filter(anno -> anno.isNative()).isPresent();
  }

  private MetaClass getHandlerForEvent(final MetaClass eventType) {

    /*
     * All handlers event must have an overrided method getAssociatedType(). We
     * take advantage of this information to get the associated handler. Ex:
     * com.google.gwt.event.dom.client.ClickEvent --->
     * com.google.gwt.event.dom.client.ClickHandler
     *
     * com.google.gwt.event.dom.client.BlurEvent --->
     * com.google.gwt.event.dom.client.BlurHandler
     */

    if (eventType == null) {
      return null;
    }

    MetaMethod method = eventType.getBestMatchingMethod("getAssociatedType", Type.class);

    if (method == null) {
      for (final MetaMethod m : eventType.getMethods()) {
        if ("getAssociatedType".equals(m.getName())) {
          method = m;
          break;
        }
      }
    }

    if (method == null) {
      throw new GenerationException("Method 'getAssociatedType()' could not be found in the event ["
          + eventType.getName() + "]");
    }

    final MetaType returnType = method.getGenericReturnType();
    if (returnType == null) {
      throw new GenerationException("The method 'getAssociatedType()' in the event [" + eventType.getName()
          + "] returns void.");
    }

    logger.debug("eventType: " + eventType.getClass() + " -- " + eventType);
    logger.debug("method: " + method.getClass() + " -- " + method);
    logger.debug("genericReturnType: " + returnType.getClass() + " -- " + returnType);

    if (!(returnType instanceof MetaParameterizedType)) {
      throw new GenerationException("The method 'getAssociatedType()' in the event [" + eventType.getName()
          + "] does not return Type<? extends EventHandler>..");
    }

    final MetaParameterizedType parameterizedType = (MetaParameterizedType) returnType;
    logger.debug("parameterizedType: " + parameterizedType.getClass() + " -- " + parameterizedType);

    final MetaType[] argTypes = parameterizedType.getTypeParameters();
    if ((argTypes.length != 1) && argTypes[0] instanceof MetaClass
        && !((MetaClass) argTypes[0]).isAssignableTo(EventHandler.class)) {
      throw new GenerationException("The method 'getAssociatedType()' in the event [" + eventType.getName()
          + "] does not return Type<? extends EventHandler>..");
    }

    return (MetaClass) argTypes[0];
  }

  /**
   * Translates the template using the module's i18n message bundle (only if
   * i18n is enabled for the module).
   * @param decorable
   * @param initStmts
   * @param rootTemplateElement
   */
  private void translateTemplate(final Decorable decorable, final List<Statement> initStmts,
          final Statement rootTemplateElement) {
    initStmts.add(
            Stmt.invokeStatic(
                    TemplateUtil.class,
                    "translateTemplate",
                    getTemplateFileName(decorable.getDecorableDeclaringType()),
                    rootTemplateElement
                    ));
  }

  private void generateComponentCompositions(final Decorable decorable,
                                             final List<Statement> initStmts,
                                             final Statement component,
                                             final Statement rootTemplateElement,
                                             final Statement dataFieldElements,
                                             final Statement fieldsMap,
                                             final Statement fieldsMetaMap) {

    final boolean composite = decorable.getType().isAssignableTo(Composite.class);

    /*
     * Merge each field's Widget Element into the DOM in place of the
     * corresponding data-field
     */
    final Map<String, Statement> dataFields = DataFieldCodeDecorator.aggregateDataFieldMap(decorable, decorable.getType());
    for (final Entry<String, Statement> field : dataFields.entrySet()) {
      initStmts.add(invokeStatic(TemplateUtil.class, "compositeComponentReplace", decorable.getDecorableDeclaringType()
          .getFullyQualifiedName(), getTemplateFileName(decorable.getDecorableDeclaringType()), supplierOf(Cast.to(Widget.class, field.getValue())),
          dataFieldElements, fieldsMetaMap, field.getKey()));
    }

    /*
     * Add each field to the Collection of children of the new Composite
     * Template
     */
    for (final Entry<String, Statement> field : dataFields.entrySet()) {
      initStmts.add(Stmt.nestedCall(fieldsMap).invoke("put", field.getKey(), field.getValue()));
    }

    final String initMethodName;
    if (composite) {
      /*
       * Attach the Template to the Component, and set up the GWT Widget hierarchy
       * to preserve Handlers and DOM events.
       */
      initMethodName = "initWidget";
    } else {
      initMethodName = "initTemplated";
    }
    initStmts.add(Stmt.invokeStatic(TemplateUtil.class, initMethodName, component, rootTemplateElement,
            Stmt.nestedCall(fieldsMap).invoke("values")));

  }

  private static Statement supplierOf(final Statement value) {
    return newInstanceOf(parameterizedAs(Supplier.class, typeParametersOf(value.getType())))
            .extend()
            .publicOverridesMethod("get")
            .append(Stmt.nestedCall(value).returnValue())
            .finish()
            .finish();
  }

  /**
   * Possibly create an inner interface {@link ClientBundle} for the template's HTML or CSS resources.
   * @param customProvider
   * @param generateCssBundle
   */
  private void generateTemplateResourceInterface(final Decorable decorable, final MetaClass type, final boolean customProvider, final Optional<String> cssPath) {
    final ClassDefinitionBuilderInterfaces<ClassStructureBuilderAbstractMethodOption> ifaceDef = ClassBuilder
            .define(getTemplateTypeName(type)).publicScope().interfaceDefinition();
    if (!customProvider) {
      ifaceDef.implementsInterface(Template.class);
    }
    if (cssPath.isPresent()) {
      ifaceDef.implementsInterface(TemplateStyleSheet.class);
    }

    final ClassStructureBuilder<ClassStructureBuilderAbstractMethodOption> componentTemplateResource = ifaceDef
            .implementsInterface(ClientBundle.class).body();

    if (!customProvider) {
      componentTemplateResource.publicMethod(TextResource.class, "getContents").annotatedWith(new Source() {

        @Override
        public Class<? extends Annotation> annotationType() {
          return Source.class;
        }

        @Override
        public String[] value() {
          return new String[] { getTemplateFileName(type) };
        }

      }).finish();
    }

    cssPath.ifPresent(path -> addCssResourceMethod(componentTemplateResource, path));

    decorable.getFactoryMetaClass().addInnerClass(new InnerClass(componentTemplateResource.getClassDefinition()));

    getConstructedTemplateTypes(decorable).put(type, componentTemplateResource.getClassDefinition());
  }

  private void addCssResourceMethod(
          final ClassStructureBuilder<ClassStructureBuilderAbstractMethodOption> componentTemplateResource,
          final String templateStyleSheetPath) {
    componentTemplateResource.publicMethod(CssResource.class, "getStyle").annotatedWith(new Source() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Source.class;
      }

      @Override
      public String[] value() {
        return new String[] { templateStyleSheetPath };
      }

    }, new CssResource.NotStrict() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return CssResource.NotStrict.class;
      }

    }).finish();
  }

  public static Optional<String> getTemplateStyleSheetPath(final MetaClass type) {
    final Templated anno = type.getAnnotation(Templated.class);

    if (anno.stylesheet().isEmpty()) {
      return Optional.empty();
    }
    else {
      final String rawPath = anno.stylesheet();
      final boolean absolute = rawPath.startsWith("/");

      if (absolute) {
        return Optional.of(rawPath.substring(1));
      } else {
        return Optional.of(type.getPackageName().replace('.', '/') + "/" + rawPath);
      }
    }
  }

  /**
   * Get a map of all previously constructed {@link Template} object types
   */
  @SuppressWarnings("unchecked")
  private Map<MetaClass, BuildMetaClass> getConstructedTemplateTypes(final Decorable decorable) {
    Map<MetaClass, BuildMetaClass> result = (Map<MetaClass, BuildMetaClass>) decorable.getInjectionContext().getAttribute(
        CONSTRUCTED_TEMPLATE_SET_KEY);

    if (result == null) {
      result = new LinkedHashMap<>();
      decorable.getInjectionContext().setAttribute(CONSTRUCTED_TEMPLATE_SET_KEY, result);
    }

    return result;
  }

  /*
   * Non-generation utility methods.
   */

  /**
   * Get the name of the {@link Template} class of the given {@link MetaClass} type
   */
  private String getTemplateTypeName(final MetaClass type) {
    return qualifiedClassNameToShortenedIdentifier(type) + "TemplateResource";
  }

  /**
   * Get the name of the {@link Template} HTML file of the given {@link MetaClass} component type
   */
  public static String getTemplateFileName(final MetaClass type) {
    String resource = type.getFullyQualifiedName().replace('.', '/') + ".html";

    if (type.isAnnotationPresent(Templated.class)) {
      final String source = canonicalizeTemplateSourceSyntax(type, type.getAnnotation(Templated.class).value());
      final Matcher matcher = Pattern.compile("^([^#]+)#?.*$").matcher(source);
      if (matcher.matches()) {
        resource = (matcher.group(1) == null ? resource : matcher.group(1));
        if (resource.matches("\\S+\\.html")) {
          if (resource.startsWith("/")) {
            resource = resource.substring(1);
          }
          else {
            resource = type.getPackageName().replace('.', '/') + "/" + resource;
          }
        }
      }
    }

    return resource;
  }

  /**
   * Get the URL of the server-side {@link Template} HTML file of the given {@link MetaClass} component type
   */
  public static String getTemplateUrl(final MetaClass type) {
    String resource = type.getFullyQualifiedName().replace('.', '/') + ".html";

    if (type.isAnnotationPresent(Templated.class)) {
      final String source = canonicalizeTemplateSourceSyntax(type, type.getAnnotation(Templated.class).value());
      final Matcher matcher = Pattern.compile("^([^#]+)#?.*$").matcher(source);
      if (matcher.matches()) {
        resource = (matcher.group(1) == null ? resource : matcher.group(1));
      }
    }

    return resource;
  }

  /**
   * Get the name of the {@link Template} HTML fragment (Element subtree) to be used as the template root of the given
   * {@link MetaClass} component type
   */
  public static String getTemplateFragmentName(final MetaClass type) {
    String fragment = "";

    if (type.isAnnotationPresent(Templated.class)) {
      final String source = canonicalizeTemplateSourceSyntax(type, type.getAnnotation(Templated.class).value());
      final Matcher matcher = Pattern.compile("^.*#([^#]+)$").matcher(source);
      if (matcher.matches()) {
        fragment = (matcher.group(1) == null ? fragment : matcher.group(1));
      }
    }

    return fragment;
  }

  /**
   * Throw an exception if the template source syntax is invalid
   */
  private static String canonicalizeTemplateSourceSyntax(final MetaClass component, final String source) {
    final String result = Strings.nullToEmpty(source).trim();

    if (result.matches(".*#.*#.*")) {
      throw new IllegalArgumentException("Invalid syntax: @" + Templated.class.getSimpleName() + "(" + source
          + ") on component " + component.getFullyQualifiedName()
          + ". Multiple '#' found, where only one fragment is permitted.");
    }

    return result;
  }

}
