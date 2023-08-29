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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.databinding.client.PropertyChangeUnsubscribeHandle;
import org.jboss.errai.databinding.rebind.DataBindingUtil;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.RefHolder;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jboss.errai.ui.shared.api.annotations.style.StyleBinding;
import org.jboss.errai.ui.shared.api.style.BindingRegistrationHandle;
import org.jboss.errai.ui.shared.api.style.StyleBindingChangeHandler;
import org.jboss.errai.ui.shared.api.style.StyleBindingExecutor;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;

import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.declareFinalVariable;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.codegen.util.Stmt.newObject;

/**
 * @author Mike Brock
 */
@CodeDecorator
@SuppressWarnings("deprecation")
public class StyleBindingCodeDecorator extends IOCDecoratorExtension<StyleBinding> {
  private static final String DATA_BINDING_CONFIG_ATTR = "StyleBinding:DataBinderConfigured";
  private static final String STYLE_BINDING_HOUSEKEEPING_ATTR = "StyleBinding:HousekeepingReg";

  public StyleBindingCodeDecorator(Class<StyleBinding> decoratesWith) {
    super(decoratesWith);
  }

  private static void bindHandlingMethod(final Decorable decorable,
          final FactoryController controller, final MetaParameter parameter) {
    final Statement elementAccessor;
    if (MetaClassFactory.get(Element.class).isAssignableFrom(parameter.getType())) {
      elementAccessor = Refs.get("element");
    }
    else if (MetaClassFactory.get(Style.class).isAssignableFrom(parameter.getType())) {
      elementAccessor = Stmt.loadVariable("element").invoke("getStyle");
    }
    else {
      throw new RuntimeException("illegal target type for style binding method: " + parameter.getType() +
          "; expected Element or Style");
    }

    final String registrationHandleVar = "handleFor" + decorable.getName();
    final List<Statement> bindExecStmts = new ArrayList<Statement>();
    final boolean enclosingTypeIsDependent = decorable.isEnclosingTypeDependent();
    if (!enclosingTypeIsDependent) {
      bindExecStmts.add(declareFinalVariable("instance", decorable.getEnclosingInjectable().getInjectedType(),
              castTo(decorable.getEnclosingInjectable().getInjectedType(),
                      invokeStatic(Factory.class, "maybeUnwrapProxy", controller.contextGetInstanceStmt()))));
    }
    String hanldeVarName = registrationHandleVar;
    bindExecStmts.add(controller.setReferenceStmt(registrationHandleVar, loadVariable(hanldeVarName)));
    bindExecStmts.add(decorable.getAccessStatement(elementAccessor));

    final ObjectBuilder bindExec = newObject(StyleBindingExecutor.class)
        .extend()
        .publicOverridesMethod("invokeBinding", Parameter.of(Element.class, "element"))
        .appendAll(bindExecStmts)
        .finish()
        .finish();

    final List<Statement> initStmts = new ArrayList<Statement>();
    final List<Statement> destructionStmts = new ArrayList<Statement>();
    initStmts.add(declareFinalVariable(hanldeVarName, parameterizedAs(RefHolder.class, typeParametersOf(BindingRegistrationHandle.class)), newObject(RefHolder.class)));
    initStmts.add(loadVariable(hanldeVarName).invoke("set", invokeStatic(StyleBindingsRegistry.class, "get")
            .invoke("addStyleBinding", decorable.getAnnotation().annotationType(), bindExec)));

    destructionStmts.add(Stmt
            .castTo(BindingRegistrationHandle.class,
                    controller.getReferenceStmt(registrationHandleVar, RefHolder.class).invoke("get"))
            .invoke("cleanup"));
    if (enclosingTypeIsDependent) {
      addCleanup(decorable, controller, destructionStmts);
      controller.addInitializationStatements(initStmts);
    } else {
      controller.addFactoryInitializationStatements(initStmts);
    }
    controller.addDestructionStatements(destructionStmts);
  }

  private static void addCleanup(final Decorable decorable, final FactoryController controller, final List<Statement> destructionStmts) {
    final DataBindingUtil.DataBinderRef dataBinder = DataBindingUtil.lookupDataBinderRef(decorable, controller);

    if (!controller.hasAttribute(STYLE_BINDING_HOUSEKEEPING_ATTR)) {
      destructionStmts.add(
              Stmt.invokeStatic(StyleBindingsRegistry.class, "get").invoke("cleanAllForBean", Refs.get("instance")));
      if (dataBinder != null) {
        destructionStmts.add(controller.getReferenceStmt("styleBindingChangeHandlerUnsub", PropertyChangeUnsubscribeHandle.class).invoke("unsubscribe"));
      }
      controller.setAttribute(STYLE_BINDING_HOUSEKEEPING_ATTR, Boolean.TRUE);
    }
  }

  @Override
  public void generateDecorator(Decorable decorable, FactoryController controller) {
    final Statement valueAccessor;

    switch (decorable.decorableType()) {
    case METHOD:
      final MetaMethod method = decorable.getAsMethod();
      final MetaParameter[] parameters = method.getParameters();
      if (!method.getReturnType().isVoid() && parameters.length == 0) {
        valueAccessor = decorable.getAccessStatement();
      }
      else if (method.getReturnType().isVoid() && parameters.length == 1) {
        // this method returns void and accepts exactly one parm. assume it's a handler method.
        bindHandlingMethod(decorable, controller, parameters[0]);
        return;
      }
      else {
        throw new RuntimeException("problem with style binding. method is not a valid binding " + method);
      }
      break;

    case FIELD:
      valueAccessor = decorable.getAccessStatement();
      break;

    case TYPE:
      // for api annotations being on a type is allowed.
      if (decorable.getAnnotation().annotationType().getPackage().getName().startsWith("org.jboss.errai")) {
        return;
      }
    default:
      throw new RuntimeException("problem with style binding. element target type is invalid: " + decorable.decorableType());
    }


    final DataBindingUtil.DataBinderRef dataBinder = DataBindingUtil.lookupDataBinderRef(decorable, controller);

    final List<Statement> initStmts = new ArrayList<Statement>();
    final List<Statement> destructionStmts = new ArrayList<Statement>();

    if (dataBinder != null) {
      if (!controller.hasAttribute(DATA_BINDING_CONFIG_ATTR)) {
        final String handlerVarName = "bindingChangeHandler";
        controller.setAttribute(DATA_BINDING_CONFIG_ATTR, Boolean.TRUE);

        initStmts.add(controller.setReferenceStmt(handlerVarName, newObject(StyleBindingChangeHandler.class)));
        // ERRAI-817 deferred initialization
        initStmts.add(controller.setReferenceStmt("styleBindingChangeHandlerUnsub", nestedCall(dataBinder.getValueAccessor()).invoke("addPropertyChangeHandler",
                controller.getReferenceStmt(handlerVarName, StyleBindingChangeHandler.class))));
      }
    }
    // ERRAI-821 deferred initialization
    if (decorable.getType().isAssignableTo(Widget.class)) {
      initStmts.add(invokeStatic(StyleBindingsRegistry.class, "get")
              .invoke("addElementBinding", Refs.get("instance"),
                      decorable.getAnnotation(),
                      nestedCall(valueAccessor).invoke("getElement")));
    }
    else if (decorable.getType().isAnnotationPresent(Templated.class)) {
      initStmts.add(invokeStatic(StyleBindingsRegistry.class, "get")
              .invoke("addElementBinding", Refs.get("instance"),
                      decorable.getAnnotation(),
                      Stmt.invokeStatic(TemplateWidgetMapper.class, "get", valueAccessor).invoke("getElement")));
    }
    else if (decorable.getType().isAssignableTo(com.google.gwt.dom.client.Element.class)
            || RebindUtil.isNativeJsType(decorable.getType()) || RebindUtil.isElementalIface(decorable.getType())) {
      initStmts.add(invokeStatic(StyleBindingsRegistry.class, "get")
              .invoke("addElementBinding", Refs.get("instance"),
                      decorable.getAnnotation(),
                      invokeStatic(TemplateUtil.class, "asDeprecatedElement", valueAccessor)));
    } else {
      throw new RuntimeException("Unrecognized type, " + decorable.getType().getFullyQualifiedName()
              + ", with style binding " + decorable.getAnnotation().annotationType().getName());
    }

    addCleanup(decorable, controller, destructionStmts);

    controller.addInitializationStatements(initStmts);
    controller.addDestructionStatements(destructionStmts);
  }
}
