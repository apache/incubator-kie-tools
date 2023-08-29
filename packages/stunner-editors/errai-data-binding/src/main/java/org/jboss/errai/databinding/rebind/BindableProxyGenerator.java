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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.util.TypeLiteral;

import com.google.gwt.core.ext.TreeLogger;
import org.jboss.errai.codegen.BlockStatement;
import org.jboss.errai.codegen.Cast;
import org.jboss.errai.codegen.DefParameters;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.Variable;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.CaseBlockBuilder;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.ObjectBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.MetaParameter;
import org.jboss.errai.codegen.meta.MetaParameterizedType;
import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaTypeVariable;
import org.jboss.errai.codegen.util.Bool;
import org.jboss.errai.codegen.util.EmptyStatement;
import org.jboss.errai.codegen.util.If;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyAgent;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.databinding.client.NonExistingPropertyException;
import org.jboss.errai.databinding.client.PropertyType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jboss.errai.databinding.client.api.StateSync;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;

import static com.google.gwt.core.ext.TreeLogger.Type.DEBUG;
import static com.google.gwt.core.ext.TreeLogger.Type.WARN;
import static org.jboss.errai.codegen.Parameter.finalOf;
import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.castTo;
import static org.jboss.errai.codegen.util.Stmt.loadLiteral;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;

/**
 * Generates a proxy for a {@link Bindable} type. A bindable proxy subclasses the bindable type and
 * overrides all non-final methods to trigger UI updates and fire property change events when
 * required.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class BindableProxyGenerator {
  private final MetaClass bindable;
  private final String agentField;
  private final String targetField;
  private final TreeLogger logger;
  private final Set<MetaMethod> proxiedAccessorMethods;

  public BindableProxyGenerator(final MetaClass bindable, final TreeLogger logger) {
    this.bindable = bindable;
    this.agentField = inferSafeFieldName("agent");
    this.targetField = inferSafeFieldName("target");
    this.logger = logger;
    this.proxiedAccessorMethods = new HashSet<>();
  }

  public ClassStructureBuilder<?> generate() {
    final String safeProxyClassName = bindable.getFullyQualifiedName().replace('.', '_') + "Proxy";
    final ClassStructureBuilder<?> classBuilder = ClassBuilder.define(safeProxyClassName, bindable)
        .packageScope()
        .implementsInterface(BindableProxy.class)
        .body();

    classBuilder
        .privateField(agentField, parameterizedAs(BindableProxyAgent.class, typeParametersOf(bindable)))
        .finish()
        .privateField(targetField, bindable)
        .finish()
        .publicConstructor()
        .callThis(Stmt.newObject(bindable))
        .finish()
        .publicConstructor(Parameter.of(bindable, "targetVal"))
        .append(Stmt.loadVariable(agentField).assignValue(
            Stmt.newObject(parameterizedAs(BindableProxyAgent.class, typeParametersOf(bindable)),
                Variable.get("this"), Variable.get("targetVal"))))
        .append(Stmt.loadVariable(targetField).assignValue(Variable.get("targetVal")))
        .append(generatePropertiesMap())
        .append(agent().invoke("copyValues"))
        .appendAll(registerDeclarativeHandlers(bindable))
        .finish()
        .publicMethod(BindableProxyAgent.class, "getBindableProxyAgent")
        .append(agent().returnValue())
        .finish()
        .publicMethod(void.class, "updateWidgets")
        .append(agent().invoke("updateWidgetsAndFireEvents"))
        .finish()
        .publicMethod(bindable, "unwrap")
        .append(target().returnValue())
        .finish()
        .publicMethod(bindable, "deepUnwrap")
        .append(generateDeepUnwrapMethodBody("deepUnwrap"))
        .finish()
        .publicMethod(boolean.class, "equals", Parameter.of(Object.class, "obj"))
        .append(
            If.instanceOf(Variable.get("obj"), classBuilder.getClassDefinition())
                .append(Stmt.loadVariable("obj").assignValue(
                    Stmt.castTo(classBuilder.getClassDefinition(), Variable.get("obj")).invoke("unwrap")))
                .finish())
        .append(target().invoke("equals", Variable.get("obj")).returnValue())
        .finish()
        .publicMethod(int.class, "hashCode")
        .append(target().invoke("hashCode").returnValue())
        .finish()
        .publicMethod(String.class, "toString")
        .append(target().invoke("toString").returnValue())
        .finish();

    generateCommonSetter(classBuilder);
    generateAccessorMethods(classBuilder);
    generateNonAccessorMethods(classBuilder);

    return classBuilder;
  }

  private Collection<Statement> registerDeclarativeHandlers(final MetaClass bindable) {
    final List<MetaMethod> handlerMethods = bindable.getMethodsAnnotatedWith(org.jboss.errai.ui.shared.api.annotations.PropertyChangeHandler.class);
    if ( handlerMethods.isEmpty() ) return Collections.emptyList();

    final List<Statement> retVal = new ArrayList<>();
    for (final MetaMethod method : handlerMethods) {
      if (method.getParameters().length == 1
              && method.getParameters()[0].getType().getFullyQualifiedName().equals(PropertyChangeEvent.class.getName())) {
        final String property = method.getAnnotation(org.jboss.errai.ui.shared.api.annotations.PropertyChangeHandler.class).value();
        if (!property.isEmpty()) validateProperty(bindable, property);
        final Object handler = createHandlerForMethod(method);
        final ContextualStatementBuilder subStmt = (property.isEmpty() ?
                loadVariable("agent").invoke("addPropertyChangeHandler", handler):
                loadVariable("agent").invoke("addPropertyChangeHandler", property, handler));

        retVal.add(subStmt);
      }
      else {
        throw new RuntimeException(
                String.format("The @ChangeHandler method [%s] must have exactly one argument of type %s.",
                        method.getName(), PropertyChangeEvent.class.getSimpleName()));
      }
    }

    return retVal;
  }

  private void validateProperty(final MetaClass bindable, final String property) {
    if (!bindable.getBeanDescriptor().getProperties().contains(property)) {
      throw new RuntimeException(String.format("Invalid property name [%s] in @Bindable type [%s].", property,
              bindable.getFullyQualifiedName()));
    }
  }

  private Object createHandlerForMethod(final MetaMethod method) {
    return ObjectBuilder
            .newInstanceOf(PropertyChangeHandler.class)
            .extend()
            .publicOverridesMethod("onPropertyChange", finalOf(PropertyChangeEvent.class, "event"))
            .append(castTo(method.getDeclaringClass(), loadVariable("agent").loadField("target")).invoke(method, loadVariable("event")))
            .finish()
            .finish();
  }

  /**
   * Generates accessor methods for all Java bean properties plus the corresponding code for the
   * method implementations of {@link HasProperties}.
   */
  private void generateAccessorMethods(final ClassStructureBuilder<?> classBuilder) {
    final BlockBuilder<?> getMethod = classBuilder.publicMethod(Object.class, "get",
        Parameter.of(String.class, "property"));

    final BlockBuilder<?> setMethod = classBuilder.publicMethod(void.class, "set",
            Parameter.of(String.class, "property"),
            Parameter.of(Object.class, "value"));
    
    CaseBlockBuilder getSwitchBlock = Stmt.switch_(loadVariable("property"));
    CaseBlockBuilder setSwitchBlock = Stmt.switch_(loadVariable("property"));
    
    for (final String property : bindable.getBeanDescriptor().getProperties()) {
      generateGetter(classBuilder, property, getSwitchBlock);
      generateSetter(classBuilder, property, setSwitchBlock);
    }
    getSwitchBlock.case_("this").append(target().returnValue()).finish();
    setSwitchBlock.case_("this")
        .append(Stmt.loadClassMember(targetField).assignValue(Stmt.castTo(bindable, Stmt.loadVariable("value"))))
        .append(agent().loadField("target").assignValue(Stmt.loadClassMember(targetField)))
        .append(Stmt.break_())
        .finish();

    final Statement nonExistingPropertyException = Stmt.throw_(NonExistingPropertyException.class,
            Stmt.loadLiteral(bindable.getName()), Variable.get("property"));
    getSwitchBlock.default_().append(nonExistingPropertyException).finish();
    setSwitchBlock.default_().append(nonExistingPropertyException).finish();
    getMethod.append(getSwitchBlock).finish();
    setMethod.append(setSwitchBlock).finish();

    classBuilder.publicMethod(Map.class, "getBeanProperties")
      .append(Stmt.declareFinalVariable("props", Map.class, ObjectBuilder.newInstanceOf(HashMap.class)
              .withParameters(agent().loadField("propertyTypes"))))
      .append(Stmt.loadVariable("props").invoke("remove", "this"))
      .append(Stmt.invokeStatic(Collections.class, "unmodifiableMap", Stmt.loadVariable("props")).returnValue())
    .finish();
  }
  
  private void generateCommonSetter(final ClassStructureBuilder<?> classBuilder) {
      classBuilder.privateMethod(void.class, "changeAndFire", 
              Parameter.of(String.class, "property"),
              Parameter.of(Object.class, "value"))
      .append(Stmt.declareFinalVariable("oldValue", Object.class, 
              Stmt.loadVariable("this").invoke("get", loadVariable("property"))))
      .append(Stmt.loadVariable("this").invoke("set", loadVariable("property"), loadVariable("value"))) 
      .append(agent().invoke("updateWidgetsAndFireEvent", false, loadVariable("property"), 
              Variable.get("oldValue"), loadVariable("value")))
      .finish();
  }

  /**
   * Generates a getter method for the provided property plus the corresponding code for the
   * implementation of {@link HasProperties#get(String)}.
   */
  private void generateGetter(final ClassStructureBuilder<?> classBuilder, final String property,
                              final CaseBlockBuilder switchBlock) {

    final MetaMethod getterMethod = bindable.getBeanDescriptor().getReadMethodForProperty(property);
    if (getterMethod != null && !getterMethod.isFinal() && getterMethod.isPublic()) {
      BlockBuilder<CaseBlockBuilder> caseBlock = switchBlock.case_(property);
      caseBlock.append(Stmt.loadVariable("this").invoke(getterMethod.getName()).returnValue()).finish();
      classBuilder.publicMethod(getterMethod.getReturnType(), getterMethod.getName())
          .append(target().invoke(getterMethod.getName()).returnValue())
          .finish();

      proxiedAccessorMethods.add(getterMethod);
    }
  }

  /**
   * Generates a setter method for the provided property plus the corresponding code for the
   * implementation of {@link HasProperties#set(String, Object)}.
   */
  private void generateSetter(final ClassStructureBuilder<?> classBuilder, final String property,
                              final CaseBlockBuilder switchBlock) {
    final MetaMethod getterMethod = bindable.getBeanDescriptor().getReadMethodForProperty(property);
    final MetaMethod setterMethod = bindable.getBeanDescriptor().getWriteMethodForProperty(property);
    if (getterMethod != null && !getterMethod.isFinal() && getterMethod.isPublic() &&
        setterMethod != null && !setterMethod.isFinal() && setterMethod.isPublic()) {
      BlockBuilder<CaseBlockBuilder> caseBlock = switchBlock.case_(property);
      caseBlock
          .append(target().invoke(setterMethod.getName(),
              Cast.to(setterMethod.getParameters()[0].getType().asBoxed(), Variable.get("value"))))
          .append(Stmt.break_())
          .finish();

      final MetaClass paramType = setterMethod.getParameters()[0].getType();

      // If the setter method we are proxying returns a value, capture that value into a local variable
      Statement returnValueOfSetter = null;
      final String returnValName = ensureSafeLocalVariableName("returnValueOfSetter", setterMethod);

      Statement wrappedListProperty = EmptyStatement.INSTANCE;
      if (paramType.isAssignableTo(List.class)) {
        wrappedListProperty = Stmt.loadVariable(property).assignValue(
            Cast.to(paramType, agent().invoke("ensureBoundListIsProxied", property, Stmt.loadVariable(property))));
      }

      Statement callSetterOnTarget =
          target().invoke(setterMethod.getName(), Cast.to(paramType, Stmt.loadVariable(property)));
      if (!setterMethod.getReturnType().equals(MetaClassFactory.get(void.class))) {
        callSetterOnTarget =
            Stmt.declareFinalVariable(returnValName, setterMethod.getReturnType(), callSetterOnTarget);
        returnValueOfSetter = Stmt.nestedCall(Refs.get(returnValName)).returnValue();
      } else {
        returnValueOfSetter = EmptyStatement.INSTANCE;
      }

      Statement updateNestedProxy = null;
      if (DataBindingUtil.isBindableType(paramType)) {
        updateNestedProxy =
            Stmt.if_(Bool.expr(agent("binders").invoke("containsKey", property)))
                .append(Stmt.loadVariable(property).assignValue(Cast.to(paramType,
                    agent("binders").invoke("get", property).invoke("setModel", Variable.get(property),
                        Stmt.loadStatic(StateSync.class, "FROM_MODEL"),
                        Stmt.loadLiteral(true)))))
                .finish();
      } else {
        updateNestedProxy = EmptyStatement.INSTANCE;
      }

      final String oldValName = ensureSafeLocalVariableName("oldValue", setterMethod);
      final boolean propertyIsList = bindable.getBeanDescriptor().getPropertyType(property)
                                         .getFullyQualifiedName().equals(List.class.getName());
      
      if (propertyIsList || returnValueOfSetter != EmptyStatement.INSTANCE) {
        classBuilder.publicMethod(setterMethod.getReturnType(), setterMethod.getName(), 
                                  Parameter.of(paramType, property))
            .append(updateNestedProxy)
            .append(Stmt.declareVariable(oldValName, paramType, target().invoke(getterMethod.getName())))
            .append(wrappedListProperty)
            .append(callSetterOnTarget)
            .append(agent().invoke("updateWidgetsAndFireEvent", propertyIsList, property, 
                                   Variable.get(oldValName), Variable.get(property)))
            .append(returnValueOfSetter)
            .finish();
      } else {
        classBuilder.publicMethod(setterMethod.getReturnType(), setterMethod.getName(),
                                  Parameter.of(paramType, property))
            .append(updateNestedProxy)
            .append(wrappedListProperty)
            .append(loadVariable("this").invoke("changeAndFire", property, Variable.get(property)))
            .finish();  
      }
      proxiedAccessorMethods.add(setterMethod);
    }
  }

  /**
   * Generates proxy methods overriding public non-final methods that are not also property accessor
   * methods. The purpose of this is to allow the proxies to react on model changes that happen
   * outside setters of the bean. These methods will cause a comparison of all bound properties and
   * trigger the appropriate UI updates and property change events.
   */
  private void generateNonAccessorMethods(final ClassStructureBuilder<?> classBuilder) {
    for (final MetaMethod method : bindable.getMethods()) {
      final String methodName = method.getName();
      
      if(isBindableProxyGeneratedMethod(method)) {
    	  logger.log(WARN, "Skipping non accessor method '" + methodName + "' for proxing." +
    			  "Same method signiture used by generated BindableProxy implementations.");
    	  return;
      }
      
      if (!proxiedAccessorMethods.contains(method)
          && !methodName.equals("hashCode") && !methodName.equals("equals") && !methodName.equals("toString")
          && method.isPublic() && !method.isFinal() && !method.isStatic()) {

        final Parameter[] parms = DefParameters.from(method).getParameters().toArray(new Parameter[0]);
        final List<Statement> parmVars = new ArrayList<>();
        for (int i = 0; i < parms.length; i++) {
          parmVars.add(Stmt.loadVariable(parms[i].getName()));
          final MetaClass type = getTypeOrFirstUpperBound(method.getGenericParameterTypes()[i], method);
          if (type == null) return;
          parms[i] = Parameter.of(type, parms[i].getName());
        }

        Statement callOnTarget = null;
        Statement returnValue = null;
        final String returnValName = ensureSafeLocalVariableName("returnValue", method);

        final MetaClass returnType = getTypeOrFirstUpperBound(method.getGenericReturnType(), method);
        if (returnType == null)
          return;

        if (!returnType.equals(MetaClassFactory.get(void.class))) {
          callOnTarget = Stmt.declareFinalVariable(returnValName,
              returnType, target().invoke(method, parmVars.toArray()));
          returnValue = Stmt.nestedCall(Refs.get(returnValName)).returnValue();
        }
        else {
          callOnTarget = target().invoke(method, parmVars.toArray());
          returnValue = EmptyStatement.INSTANCE;
        }

        classBuilder
            .publicMethod(returnType, methodName, parms)
              .append(callOnTarget)
              .append(agent().invoke("updateWidgetsAndFireEvents"))
              .append(returnValue)
            .finish();
      }
    }
  }
  
  /**
   * Returns true if the method signature is the same as the methods generated by BindableProxyGenerator
   */
  private boolean isBindableProxyGeneratedMethod(MetaMethod method) {
    if(method.getName().equals("get") && method.getParameters().length == 1 && method.getParameters()[0].getType().isAssignableFrom(String.class)) {
    	return true;
    }
    
    if(method.getName().equals("set") && method.getParameters().length == 2 && method.getParameters()[0].getType().isAssignableFrom(String.class)) {
    	return true;
    }
    
    return false;  
  }
  

  /**
   * Generates code to collect all existing properties and their types.
   */
  private Statement generatePropertiesMap() {
    final BlockStatement block = new BlockStatement();
    block.addStatement(Stmt.declareFinalVariable("p", new TypeLiteral<Map<String, PropertyType>>() {},
                                                 agent("propertyTypes")));
    
    for (final String property : bindable.getBeanDescriptor().getProperties()) {
      final MetaMethod readMethod = bindable.getBeanDescriptor().getReadMethodForProperty(property);
      if (isProxiable(readMethod)) {
        final MetaClass propertyType = readMethod.getReturnType();
        block.addStatement(loadVariable("p").invoke(
            "put",
            property,
            Stmt.newObject(PropertyType.class, loadLiteral(propertyType.asBoxed()),
                DataBindingUtil.isBindableType(propertyType),
                propertyType.isAssignableTo(List.class))
            )
        );
      }
    }
    block.addStatement(loadVariable("p").invoke(
        "put",
        "this",
        Stmt.newObject(PropertyType.class, loadLiteral(bindable.asBoxed()),
            true,
            bindable.isAssignableTo(List.class))
        )
    );
    return (block.isEmpty()) ? EmptyStatement.INSTANCE : block;
  }

  /**
   * Generates method body for recursively unwrapping a {@link BindableProxy}.
   */
  private Statement generateDeepUnwrapMethodBody(final String methodName) {
    final String cloneVar = "clone";
    final String targetVar = "t";
    final BlockStatement block = new BlockStatement();
    block.addStatement(Stmt.declareFinalVariable(cloneVar, bindable, Stmt.newObject(bindable)));
    block.addStatement(Stmt.declareFinalVariable(targetVar, bindable, Stmt.loadVariable("this").invoke("unwrap")));

    for (final String property : bindable.getBeanDescriptor().getProperties()) {
      final MetaMethod readMethod = bindable.getBeanDescriptor().getReadMethodForProperty(property);
      final MetaMethod writeMethod = bindable.getBeanDescriptor().getWriteMethodForProperty(property);
      if (readMethod != null && writeMethod != null && readMethod.isPublic() && writeMethod.isPublic()) {
        final MetaClass type = readMethod.getReturnType();
        if (!DataBindingUtil.isBindableType(type)) {
          // If we find a collection we copy its elements and unwrap them if necessary
          // TODO support map types
          if (type.isAssignableTo(Collection.class)) {
            final String colVarName = property + "Clone";
            final String elemVarName = property + "Elem";

            final BlockBuilder<ElseBlockBuilder> colBlock = If.isNotNull(Stmt.nestedCall(
                loadVariable(targetVar).invoke(readMethod)));

            if ((type.isInterface() || type.isAbstract()) &&
                    (type.isAssignableTo(List.class) || type.isAssignableTo(Set.class))) {
              final MetaClass clazz = (type.isAssignableTo(Set.class))
                      ? MetaClassFactory.get(HashSet.class) : MetaClassFactory.get(ArrayList.class);
                colBlock.append(Stmt.declareFinalVariable(colVarName, type.getErased(), Stmt.newObject(clazz)));
            }
            else {
              if (!type.isInterface() && !type.isAbstract()) {
                colBlock.append(Stmt.declareFinalVariable(colVarName, type.getErased(), Stmt.newObject(type.getErased())));
              }
              else {
                logger.log(TreeLogger.WARN, "Bean validation on collection " + property + " in class " + bindable +
                        " won't work. Change to either List or Set or use a concrete type instead.");
                continue;
              }
            }
            // Check if the collection element is proxied and unwrap if necessary
            colBlock.append(
              Stmt.nestedCall(loadVariable(targetVar).invoke(readMethod)).foreach(elemVarName, Object.class)
               .append (
                 If.instanceOf(Refs.get(elemVarName), BindableProxy.class)
                  .append (Stmt.loadVariable(colVarName)
                    .invoke("add", Stmt.castTo(BindableProxy.class, Stmt.loadVariable(elemVarName)).invoke(methodName))
                  )
               .finish()
               .else_()
                 .append(Stmt.loadVariable(colVarName).invoke("add", Refs.get(elemVarName)))
               .finish()
             )
             .finish());

            colBlock.append(Stmt.loadVariable(cloneVar).invoke(writeMethod, Refs.get(colVarName)));
            block.addStatement(colBlock.finish());
          }
          else {
            block.addStatement(Stmt.loadVariable(cloneVar).invoke(writeMethod, 
                loadVariable(targetVar).invoke(readMethod)));
          }
        }
        // Found a bindable property: Generate code to unwrap for the case the instance is proxied
        else {
          final Statement field = loadVariable(targetVar).invoke(readMethod);
          block.addStatement (
            If.instanceOf(field, BindableProxy.class)
              .append(Stmt.loadVariable(cloneVar).invoke(writeMethod,
                        Cast.to (
                            readMethod.getReturnType(),
                            Stmt.castTo(BindableProxy.class, Stmt.loadVariable("this").invoke(readMethod)).invoke(methodName)
                        )
                    )
                )
              .finish()
              .elseif_(Stmt.invokeStatic(BindableProxyFactory.class, "isBindableType", 
                  loadVariable(targetVar).invoke(readMethod)))
              .append(Stmt.loadVariable(cloneVar).invoke(writeMethod,
                      Cast.to (
                          readMethod.getReturnType(),
                          Stmt.castTo(BindableProxy.class, Stmt.invokeStatic(BindableProxyFactory.class,
                                  "getBindableProxy", loadVariable(targetVar).invoke(readMethod))).invoke(methodName)
                      )
                  )
              )
              .finish()
              .else_()
                .append(Stmt.loadVariable(cloneVar).invoke(writeMethod, loadVariable(targetVar).invoke(readMethod)))
              .finish()
          );
        }
      }
    }

    block.addStatement(Stmt.loadVariable(cloneVar).returnValue());

    return block;
  }

  private String inferSafeFieldName(String fieldName) {
    while (bindable.getInheritedField(fieldName) != null) {
      fieldName = "_" + fieldName;
    }
    return fieldName;
  }

  private String ensureSafeLocalVariableName(String name, final MetaMethod method) {
    final MetaParameter[] params = method.getParameters();
    if (params != null) {
      for (final MetaParameter param : params) {
        if (name.equals(param.getName())) {
          name = "_" + name;
          break;
        }
      }
    }
    return name;
  }

  private ContextualStatementBuilder agent(final String field) {
    return agent().loadField(field);
  }

  private ContextualStatementBuilder agent() {
    return Stmt.loadClassMember(agentField);
  }

  private ContextualStatementBuilder target() {
    return Stmt.loadClassMember(targetField);
  }

  private MetaClass getTypeOrFirstUpperBound(MetaType clazz, final MetaMethod method) {
    if (clazz instanceof MetaTypeVariable) {
      final MetaType[] bounds = ((MetaTypeVariable) clazz).getBounds();
      if (bounds.length == 1 && bounds[0] instanceof MetaClass) {
        clazz = ((MetaTypeVariable) clazz).getBounds()[0];
      }
      else {
        // TODO add full support for generics in errai codegen
        logger.log(TreeLogger.WARN, "Ignoring method: " + method + " in class " + bindable + ". Methods using " +
            "multiple type parameters or type parameters with multiple bounds are currently not supported in " +
            "@Bindable types! Invoking this method on a bound model will have unpredictable results.");
        return null;
      }
    }
    else if (clazz instanceof MetaParameterizedType) {
      clazz = ((MetaParameterizedType) clazz).getRawType();
    }
    if (clazz instanceof MetaClass) {
      return (MetaClass) clazz;
    }

    logger.log(TreeLogger.WARN, "Ignoring method: " + method + " in class " + bindable + ". Method cannot be proxied!");
    return null;
  }

  protected boolean isProxiable(MetaClassMember member) {
    if(member != null) {
      if (!member.isFinal() && member.isPublic()) {
        return true;
      } else {
        logger.log(DEBUG, "Member " + member.getName() + " in " + bindable.getName() +
            " is " + (member.isFinal() ? "final" : "not public") + ", cannot be proxied.");
      }
    }
    return false;
  }
}
