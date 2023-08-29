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


package org.jboss.errai.databinding.rebind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Widget;
import jsinterop.annotations.JsType;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.util.If;
import org.jboss.errai.codegen.util.PrivateAccessUtil;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.common.client.ui.HasValue;
import org.jboss.errai.databinding.client.BoundUtil;
import org.jboss.errai.databinding.client.api.Convert;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.client.container.InitializationCallback;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable.DecorableType;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.ui.shared.api.annotations.Bound;

import static org.jboss.errai.codegen.util.Stmt.declareVariable;
import static org.jboss.errai.codegen.util.Stmt.invokeStatic;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;
import static org.jboss.errai.codegen.util.Stmt.nestedCall;
import static org.jboss.errai.codegen.util.Stmt.throw_;

/**
 * Generates an {@link InitializationCallback} that contains automatic binding logic.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
// Must run after TemplatedCodeDecorator
@CodeDecorator(order=2)
public class BoundDecorator extends IOCDecoratorExtension<Bound> {

  final Set<MetaClass> processedTypes = Collections.newSetFromMap(new ConcurrentHashMap<MetaClass, Boolean>());

  public BoundDecorator(Class<Bound> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public void generateDecorator(final Decorable decorable, final FactoryController controller) {
    final MetaClass targetClass = decorable.getEnclosingInjectable().getInjectedType();
    final List<Statement> statements = new ArrayList<Statement>();
    final boolean hasRunForType =  processedTypes.contains(targetClass);

    final DataBindingUtil.DataBinderRef binderLookup = DataBindingUtil.lookupDataBinderRef(decorable, controller);
    if (binderLookup != null) {
      // Generate a reference to the bean's @AutoBound data binder
      if (!hasRunForType) {
        statements.add(declareVariable("binder", DataBinder.class, binderLookup.getValueAccessor()));
        statements.add(If.isNull(Refs.get("binder")).append(
                throw_(RuntimeException.class, "@AutoBound data binder for class "
                    + targetClass
                    + " has not been initialized. Either initialize or add @Inject!")).finish());
        statements.add(controller.setReferenceStmt(DataBindingUtil.BINDER_VAR_NAME, loadVariable("binder")));
      }

      // Check if the bound property exists in data model type
      final Bound bound = (Bound) decorable.getAnnotation();
      final boolean propertyIsEmpty = bound.property().equals("");
      String property = propertyIsEmpty ? decorable.getName() : bound.property();
      if (!DataBindingValidator.isValidPropertyChain(binderLookup.getDataModelType(), property)) {
        if (propertyIsEmpty && binderLookup.getDataModelType().equals(getValueType(decorable.getType()))) {
          property = "this";
        }
        else {
          throw new GenerationException("Invalid binding of field " + decorable.getName()
            + " in class " + targetClass + "! Property " + property
            + " not resolvable from class " + binderLookup.getDataModelType()
            + "! Hint: Is " + binderLookup.getDataModelType() + " marked as @Bindable? When binding to a "
            + "property chain, all properties but the last in a chain must be of a @Bindable type!");
        }
      }

      Statement component = decorable.getAccessStatement();
      controller.ensureMemberExposed(decorable.get());

      // Ensure the @Bound field or method provides a widget or DOM element
      MetaClass componentType = decorable.getType();
      if (componentType.isAssignableTo(Widget.class)) {
        // Ensure @Bound widget field is initialized
        if (!decorable.get().isAnnotationPresent(Inject.class) && decorable.decorableType().equals(DecorableType.FIELD) && componentType.isDefaultInstantiable()) {
          Statement widgetInit = Stmt.loadVariable("this").invoke(
              PrivateAccessUtil.getPrivateFieldAccessorName(decorable.getAsField()),
              Refs.get("instance"),
              ObjectBuilder.newInstanceOf(componentType));

          statements.add(If.isNull(component).append(widgetInit).finish());
        }
      }
      else if (componentType.isAnnotationPresent(JsType.class)) {
        if (componentType.isAssignableTo(HasValue.class)) {
          final MetaClass valueType = componentType.getMethod("getValue", new Class[0]).getReturnType();
          component = Stmt.invokeStatic(ElementWrapperWidget.class, "getWidget",
                  Stmt.invokeStatic(BoundUtil.class, "asElement", component), Stmt.loadLiteral(valueType));
        }
        else {
          component = Stmt.invokeStatic(ElementWrapperWidget.class, "getWidget", Stmt.invokeStatic(BoundUtil.class, "asElement", component));
        }
      }
      else if (!(componentType.isAssignableTo(TakesValue.class)
              || componentType.isAssignableTo(BindableListChangeHandler.class)
              || componentType.isAssignableTo(Element.class)
              || componentType.isAnnotationPresent(JsType.class)
              || componentType.isAssignableTo(org.jboss.errai.common.client.api.elemental2.IsElement.class)
              || componentType.isAssignableTo(IsElement.class))) {
        throw new GenerationException("@Bound field or method " + decorable.getName()
            + " in class " + targetClass
            + " must be assignable to Widget, TakesValue, or a DOM element type but provides: "
            + componentType.getFullyQualifiedName());
      }

      // Generate the binding
      Statement conv = coverterStatement(bound, decorable.getType(),
              DataBindingValidator.getPropertyType(binderLookup.getDataModelType(), property));
      Statement onKeyUp = Stmt.load(bound.onKeyUp());
      statements.add(Stmt.loadVariable("binder").invoke("bind", component, property, conv, loadLiteral(null), onKeyUp));
    }
    else {
      throw new GenerationException("No @Model or @AutoBound data binder found for @Bound field or method "
          + decorable.getName() + " in class " + targetClass);
    }

    processedTypes.add(targetClass);

    controller.setAttribute(DataBindingUtil.BINDER_MODEL_TYPE_VALUE, binderLookup.getDataModelType());
    controller.addInitializationStatements(statements);
    if (!hasRunForType) {
      controller.addDestructionStatements(Collections.<Statement> singletonList(
              nestedCall(controller.getReferenceStmt(DataBindingUtil.BINDER_VAR_NAME, DataBinder.class)).invoke("unbind")));
    }
  }

  private MetaClass getValueType(final MetaClass type) {
    if (type.isAssignableTo(TakesValue.class)) {
      return type.getMethod("getValue", new Class[0]).getReturnType();
    }
    else {
      return null;
    }
  }

  private Statement coverterStatement(final Bound bound, final MetaClass boundType, final MetaClass propertyType) {
    if (bound.converter().equals(Bound.NO_CONVERTER.class)) {
      final Optional<MetaClass> valueType;
      if (boundType.isAssignableTo(TakesValue.class)) {
        valueType = Optional.ofNullable(boundType.getMethod("getValue", new Class[0]).getReturnType());
      }
      else if (boundType.isAssignableTo(BindableListChangeHandler.class)) {
        valueType = Optional.ofNullable(MetaClassFactory.get(List.class));
      }
      else {
        valueType = Optional.empty();
      }

      return valueType
              .map(type -> invokeStatic(Convert.class, "getConverter", loadLiteral(propertyType), loadLiteral(type)))
              .orElse(loadLiteral(null));
    }
    else {
      return Stmt.newObject(bound.converter());
    }
  }

}
