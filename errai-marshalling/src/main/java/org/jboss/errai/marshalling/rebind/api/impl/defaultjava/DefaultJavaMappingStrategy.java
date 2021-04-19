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

package org.jboss.errai.marshalling.rebind.api.impl.defaultjava;

import static org.jboss.errai.codegen.meta.MetaClassFactory.parameterizedAs;
import static org.jboss.errai.codegen.meta.MetaClassFactory.typeParametersOf;
import static org.jboss.errai.codegen.util.Stmt.loadVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.codegen.Cast;
import org.jboss.errai.codegen.InnerClass;
import org.jboss.errai.codegen.Parameter;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.StringExpression;
import org.jboss.errai.codegen.StringOperator;
import org.jboss.errai.codegen.TernaryStatement;
import org.jboss.errai.codegen.builder.BlockBuilder;
import org.jboss.errai.codegen.builder.CaseBlockBuilder;
import org.jboss.errai.codegen.builder.ClassDefinitionStaticOption;
import org.jboss.errai.codegen.builder.ClassStructureBuilder;
import org.jboss.errai.codegen.builder.ContextualStatementBuilder;
import org.jboss.errai.codegen.builder.ElseBlockBuilder;
import org.jboss.errai.codegen.builder.impl.ClassBuilder;
import org.jboss.errai.codegen.builder.impl.StatementBuilder;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.meta.MetaClassFactory;
import org.jboss.errai.codegen.meta.MetaClassMember;
import org.jboss.errai.codegen.meta.MetaConstructor;
import org.jboss.errai.codegen.meta.MetaField;
import org.jboss.errai.codegen.meta.MetaMethod;
import org.jboss.errai.codegen.meta.impl.build.BuildMetaClass;
import org.jboss.errai.codegen.util.Bool;
import org.jboss.errai.codegen.util.Expr;
import org.jboss.errai.codegen.util.GenUtil;
import org.jboss.errai.codegen.util.If;
import org.jboss.errai.codegen.util.Implementations;
import org.jboss.errai.codegen.util.PrivateAccessUtil;
import org.jboss.errai.codegen.util.Refs;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.codegen.util.Str;
import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.GeneratedMarshaller;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.exceptions.InvalidMappingException;
import org.jboss.errai.marshalling.client.api.exceptions.MarshallingException;
import org.jboss.errai.marshalling.client.api.exceptions.NoAvailableMarshallerException;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.ObjectMarshaller;
import org.jboss.errai.marshalling.rebind.MarshallerGeneratorFactory;
import org.jboss.errai.marshalling.rebind.api.GeneratorMappingContext;
import org.jboss.errai.marshalling.rebind.api.MappingStrategy;
import org.jboss.errai.marshalling.rebind.api.ObjectMapper;
import org.jboss.errai.marshalling.rebind.api.model.ConstructorMapping;
import org.jboss.errai.marshalling.rebind.api.model.FactoryMapping;
import org.jboss.errai.marshalling.rebind.api.model.InstantiationMapping;
import org.jboss.errai.marshalling.rebind.api.model.Mapping;
import org.jboss.errai.marshalling.rebind.api.model.MappingDefinition;
import org.jboss.errai.marshalling.rebind.api.model.MemberMapping;
import org.jboss.errai.marshalling.rebind.util.MarshallingGenUtil;

/**
 * The Errai default Java-to-JSON-to-Java marshaling strategy.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 */
public class DefaultJavaMappingStrategy implements MappingStrategy {
  private final GeneratorMappingContext context;
  private final MetaClass toMap;
  private final boolean gwtTarget;

  public DefaultJavaMappingStrategy(final boolean gwtTarget,
                                    final GeneratorMappingContext context,
                                    final MetaClass toMap) {
    this.gwtTarget = gwtTarget;
    this.context = context;
    this.toMap = toMap;
  }

  @Override
  public ObjectMapper getMapper() {
    return generateJavaBeanMapper();
  }

  private ObjectMapper generateJavaBeanMapper() {
    final MappingDefinition mappingDefinition = context.getDefinitionsFactory().getDefinition(toMap);

    if (mappingDefinition == null) {
      throw new InvalidMappingException("no definition for: " + toMap.getFullyQualifiedName());
    }

    if ((toMap.isAbstract() || toMap.isInterface()) && !toMap.isEnum()) {
      throw new RuntimeException("cannot map an abstract class or interface: " + toMap.getFullyQualifiedName());
    }

    return new ObjectMapper() {
      @Override
      public ClassStructureBuilder<?> getMarshaller(String marshallerClassName) {
        ClassDefinitionStaticOption<?> staticOption = ClassBuilder.define(marshallerClassName).publicScope();

        ClassStructureBuilder<?> classStructureBuilder = null;
        BlockBuilder<?> initMethod = null;
        if (!gwtTarget)
          classStructureBuilder = staticOption.staticClass().implementsInterface(
              parameterizedAs(GeneratedMarshaller.class, typeParametersOf(toMap))).body();
        else {
          classStructureBuilder = staticOption.implementsInterface(
              parameterizedAs(GeneratedMarshaller.class, typeParametersOf(toMap))).body();
        }
        initMethod = classStructureBuilder.privateMethod(void.class, "lazyInit");

        final MetaClass arrayType = toMap.asArrayOf(1);
        classStructureBuilder.privateField("EMPTY_ARRAY", arrayType).initializesWith(Stmt.newArray(toMap, 0)).finish();

        classStructureBuilder.publicMethod(arrayType, "getEmptyArray")
            .append(Stmt.loadClassMember("EMPTY_ARRAY").returnValue())
            .finish();

        /**
         *
         * DEMARSHALL METHOD
         *
         */
        final BlockBuilder<?> builder =
            classStructureBuilder.publicMethod(toMap, "demarshall",
                Parameter.of(EJValue.class, "a0"), Parameter.of(MarshallingSession.class, "a1"));

        builder.append(Stmt.loadVariable("this").invoke("lazyInit"));
        builder.append(Stmt.declareVariable(EJObject.class).named("obj")
            .initializeWith(loadVariable("a0").invoke("isObject")));

        if (toMap.isEnum()) {
          builder.append(Stmt.declareVariable(toMap).named("entity")
              .initializeWith(demarshallEnum(loadVariable("obj"), loadVariable("a0"), toMap)));
        }
        else {
          builder.append(If.cond(Bool.isNull(Refs.get("obj"))).append(Stmt.load(null).returnValue()).finish());

          builder.append(Stmt.declareVariable(String.class).named("objId")
              .initializeWith(loadVariable("obj")
                  .invoke("get", SerializationParts.OBJECT_ID)
                  .invoke("isString").invoke("stringValue")));

          builder.append(
              Stmt.if_(Bool.expr(loadVariable("a1").invoke("hasObject", loadVariable("objId"))))
                  .append(loadVariable("a1")
                      .invoke("getObject", toMap, loadVariable("objId")).returnValue()).finish());

          final InstantiationMapping instantiationMapping = mappingDefinition.getInstantiationMapping();

          /**
           * Figure out how to construct this object.
           */
          final Mapping[] cMappings = instantiationMapping.getMappings();
          if (cMappings.length > 0) {
            // use constructor mapping.
            final List<String> memberKeys = new ArrayList<String>();
            for (MemberMapping memberMapping : mappingDefinition.getMemberMappings()) {
              memberKeys.add(memberMapping.getKey());
            }

            final Statement[] constructorParameters = new Statement[cMappings.length];

            for (final Mapping mapping : instantiationMapping.getMappingsInKeyOrder(memberKeys)) {
              int parmIndex = instantiationMapping.getIndex(mapping.getKey());
              final MetaClass type = mapping.getType().asBoxed();
              BlockBuilder<?> lazyInitMethod = (needsLazyInit(type)) ? initMethod : null;
              if (type.isArray()) {
                MetaClass toMap = type;
                while (toMap.isArray()) {
                  toMap = toMap.getComponentType();
                }
                if (context.canMarshal(toMap.getFullyQualifiedName())) {
                  if (gwtTarget) {
                    BuildMetaClass arrayMarshaller = MarshallerGeneratorFactory.createArrayMarshallerClass(type);

                    if (!containsInnerClass(classStructureBuilder, arrayMarshaller)) {
                      classStructureBuilder.declaresInnerClass(new InnerClass(arrayMarshaller));
                    }
                    Statement deferred = context.getArrayMarshallerCallback().deferred(type, arrayMarshaller);
                    MarshallingGenUtil.ensureMarshallerFieldCreated(classStructureBuilder, toMap, type, lazyInitMethod,
                        deferred);
                    constructorParameters[parmIndex] =
                        Stmt.loadVariable(MarshallingGenUtil.getVarName(type)).invoke("demarshall",
                            extractJSONObjectProperty(mapping.getKey(), EJObject.class), Stmt.loadVariable("a1"));
                  }
                  else {
                    MarshallingGenUtil.ensureMarshallerFieldCreated(classStructureBuilder, toMap, type, lazyInitMethod);
                    constructorParameters[parmIndex] = context.getArrayMarshallerCallback()
                        .demarshall(type, extractJSONObjectProperty(mapping.getKey(), EJObject.class));
                  }
                }
                else {
                  throw new MarshallingException("Encountered non-marshallable type " + toMap +
                          " while building a marshaller for " + mappingDefinition.getMappingClass());
                }
              }
              else {
                MarshallingGenUtil.ensureMarshallerFieldCreated(classStructureBuilder, toMap, type, lazyInitMethod);
                if (context.canMarshal(type.getFullyQualifiedName())) {
                  Statement s = maybeAddAssumedTypes(builder,
                      "c" + parmIndex,
                      mapping, fieldDemarshall(mapping, EJObject.class));

                  constructorParameters[parmIndex] =  s;
                }
                else {
                  throw new MarshallingException("Encountered non-marshallable type " + type +
                          " while building a marshaller for " + mappingDefinition.getMappingClass());
                }
              }
            }

            if (instantiationMapping instanceof ConstructorMapping) {
              final ConstructorMapping mapping = (ConstructorMapping) instantiationMapping;
              final MetaConstructor constructor = mapping.getMember();

              if (constructor.isPublic()) {
                builder
                    .append(Stmt.declareVariable(toMap).named("entity")
                        .initializeWith(
                            Stmt.newObject(toMap, (Object[]) constructorParameters)));
              }
              else {
                PrivateAccessUtil.addPrivateAccessStubs(gwtTarget ? "jsni" : "reflection", classStructureBuilder,
                    constructor);
                builder.append(Stmt.declareVariable(toMap).named("entity")
                    .initializeWith(
                        Stmt.invokeStatic(
                            classStructureBuilder.getClassDefinition(),
                            PrivateAccessUtil.getPrivateMethodName(constructor),
                            (Object[]) constructorParameters)));
              }
            }
            else if (instantiationMapping instanceof FactoryMapping) {
              builder.append(Stmt.declareVariable(toMap).named("entity")
                  .initializeWith(
                      Stmt.invokeStatic(toMap, ((FactoryMapping) instantiationMapping).getMember().getName(),
                              (Object[]) constructorParameters)));
            }
          }
          else {
            // use default constructor

            builder._(
                Stmt.declareVariable(toMap).named("entity").initializeWith(
                    Stmt.nestedCall(Stmt.newObject(toMap))));
          }

          builder._(loadVariable("a1").invoke("recordObject",
              loadVariable("objId"), loadVariable("entity")));
        }
        
        CaseBlockBuilder switchBlock = null;

        /**
         *
         * FIELD BINDINGS
         *
         */
        for (final MemberMapping memberMapping : mappingDefinition.getMemberMappings()) {
          if (!memberMapping.canWrite())
            continue;
        
          if (switchBlock == null) {
            builder.append(Stmt.declareFinalVariable("keys", new TypeLiteral<Set<String>>() {}, 
                                                     loadVariable("obj").invoke("keySet")));
            builder.append(StatementBuilder.create()
                .loadVariable("keys")
                .foreach("key", String.class)
                .append(Stmt.if_(Bool.or(loadVariable("key").invoke("equals", SerializationParts.ENCODED_TYPE), 
                                         loadVariable("key").invoke("equals", SerializationParts.OBJECT_ID)))
                            .append(Stmt.continue_()).finish())
                // objVal assignment in each switch's case clause leads to excessively large resulting js size!
                .append(Stmt.declareVariable("objVal", EJValue.class, extractJSONObjectProperty(loadVariable("key"),
                        EJObject.class, "getIfNotNull")))
                .append(Stmt.if_(Bool.isNull(loadVariable("objVal"))).append(Stmt.continue_()).finish())
                .append(switchBlock = Stmt.switch_(loadVariable("key")))
                .finish());
          }
        
          if (memberMapping.getTargetType().isConcrete() && !context.isRendered(memberMapping.getTargetType())) {
            context.getMarshallerGeneratorFactory().addMarshaller(memberMapping.getTargetType());
          }

          final Statement bindingStatement;
          final Statement val;

          context.getMarshallerGeneratorFactory().addOrMarkMarshallerUnlazy(
              memberMapping.getType().getOuterComponentType());

          BlockBuilder<?> lazyInitMethod = (needsLazyInit(memberMapping.getType())) ? initMethod : null;
          if (memberMapping.getType().isArray()) {
            if (gwtTarget) {
              BuildMetaClass arrayMarshaller =
                  MarshallerGeneratorFactory.createArrayMarshallerClass(memberMapping.getType().asBoxed());

              if (!containsInnerClass(classStructureBuilder, arrayMarshaller)) {
                classStructureBuilder.declaresInnerClass(new InnerClass(arrayMarshaller));
              }
              Statement deferred =
                  context.getArrayMarshallerCallback().deferred(memberMapping.getType().asBoxed(), arrayMarshaller);
              MarshallingGenUtil.ensureMarshallerFieldCreated(classStructureBuilder, toMap, memberMapping.getType()
                  .asBoxed(), lazyInitMethod, deferred);
            }
            else {
              MarshallingGenUtil.ensureMarshallerFieldCreated(classStructureBuilder, toMap, memberMapping.getType()
                  .asBoxed(), lazyInitMethod);
            }
            val = context.getArrayMarshallerCallback().demarshall(memberMapping.getType(), loadVariable("objVal"));
          }
          else {
            MarshallingGenUtil.ensureMarshallerFieldCreated(classStructureBuilder, toMap, memberMapping.getType()
                .asBoxed(), lazyInitMethod);
            val = fieldDemarshall(memberMapping, MetaClassFactory.get(EJObject.class), loadVariable("objVal"));
          }

          if (memberMapping.getBindingMember() instanceof MetaField) {
            final MetaField field = (MetaField) memberMapping.getBindingMember();

            // handle long case -- GWT does not support long in JSNI
            if (field.isPublic()) {
              BlockBuilder<CaseBlockBuilder> caseBlock = switchBlock.case_(memberMapping.getKey());
              caseBlock.append(loadVariable("entity").loadField(field.getName()).assignValue(val))
                  .append(Stmt.break_()).finish();
              continue;
            }
            else {
              final MetaMethod setterMeth = GenUtil.findCaseInsensitiveMatch(null,
                  field.getDeclaringClass(), "set" + field.getName(),
                  field.getType());

              if (setterMeth != null && !setterMeth.isPrivate()) {
                // Bind via setter
                bindingStatement =
                    loadVariable("entity").invoke(setterMeth, Cast.to(memberMapping.getTargetType(), val));
              }
              else if (field.getType().getCanonicalName().equals("long")) {
                throw new RuntimeException("cannot support private field marshalling of long type" +
                    " (not supported by JSNI) for field: "
                    + field.getDeclaringClass().getFullyQualifiedName() + "#" + field.getName());
              }
              else {
                if (!context.isExposed(field, classStructureBuilder.getClassDefinition().getName())) {
                  PrivateAccessUtil.addPrivateAccessStubs(gwtTarget ? "jsni" : "reflection", classStructureBuilder,
                      field);
                  context.markExposed(field, classStructureBuilder.getClassDefinition().getName());
                }

                // Bind via JSNI
                bindingStatement = Stmt.invokeStatic(classStructureBuilder.getClassDefinition(),
                    PrivateAccessUtil.getPrivateFieldAccessorName(field),
                    loadVariable("entity"), val);
              }

            }
          }
          else if (memberMapping.getBindingMember() instanceof MetaMethod) {
            bindingStatement = loadVariable("entity").invoke(((MetaMethod) memberMapping.getBindingMember()),
                Cast.to(memberMapping.getTargetType(), val));
          }
          else {
            throw new RuntimeException("unknown member mapping type: " + memberMapping.getType());
          }

          BlockBuilder<CaseBlockBuilder> caseBlock = switchBlock.case_(memberMapping.getKey());
          maybeAddAssumedTypes(caseBlock, null, memberMapping, bindingStatement);
          caseBlock.append(Stmt.break_()).finish();
        }

        builder.append(loadVariable("entity").returnValue());
        builder.finish();

        /**
         *
         * MARSHAL METHOD
         *
         */
        final BlockBuilder<?> marshallMethodBlock = classStructureBuilder.publicMethod(String.class, "marshall",
            Parameter.of(toMap, "a0"), Parameter.of(MarshallingSession.class, "a1"));

        marshallMethodBlock.append(Stmt.loadVariable("this").invoke("lazyInit"));
        marshallToJSON(marshallMethodBlock, toMap, mappingDefinition, classStructureBuilder, initMethod);

        marshallMethodBlock.finish();

        if (initMethod != null) {
          initMethod.finish();
        }
        return classStructureBuilder;
      }
    };
  }

  public Statement maybeAddAssumedTypes(BlockBuilder<?> blockBuilder, String varName, Mapping mapping,
      Statement statement) {
    final MetaClass elementType = MarshallingGenUtil.getConcreteCollectionElementType(mapping.getType());
    final MetaClass mapKeyType = MarshallingGenUtil.getConcreteMapKeyType(mapping.getType());
    final MetaClass mapValueType = MarshallingGenUtil.getConcreteMapValueType(mapping.getType());

    boolean assumedMapTypesSet = false;
    if (elementType != null) {
      blockBuilder.append(Stmt.loadVariable("a1").invoke("setAssumedElementType", elementType.getFullyQualifiedName()));
    }
    else if (mapKeyType != null && mapValueType != null) {
      blockBuilder.append(Stmt.loadVariable("a1").invoke("setAssumedMapKeyType", mapKeyType.getFullyQualifiedName()));
      blockBuilder.append(Stmt.loadVariable("a1")
          .invoke("setAssumedMapValueType", mapValueType.getFullyQualifiedName()));
      assumedMapTypesSet = true;
    }

    if (varName != null) {
      blockBuilder.append(Stmt.declareFinalVariable(varName, mapping.getTargetType(), statement));
    }
    else {
      blockBuilder.append(statement);
    }

    if (assumedMapTypesSet) {
      blockBuilder.append(Stmt.loadVariable("a1").invoke("resetAssumedTypes"));
    }

    return (varName != null) ? Stmt.loadVariable(varName) : statement;
  }

  public Statement fieldDemarshall(final Mapping mapping, final Class<?> fromType) {
    return fieldDemarshall(mapping, MetaClassFactory.get(fromType));
  }

  public Statement fieldDemarshall(final Mapping mapping, final MetaClass fromType) {
    final Statement statement =
        unwrapJSON(extractJSONObjectProperty(mapping.getKey(), fromType), mapping.getType(), mapping.getTargetType());
    return Cast.to(mapping.getTargetType(), statement);
  }
  
  public Statement fieldDemarshall(final Mapping mapping, final MetaClass fromType, final Statement valExtract) {
    final Statement statement =
        unwrapJSON(valExtract, mapping.getType(), mapping.getTargetType());
    return Cast.to(mapping.getTargetType(), statement);
  }

  public Statement extractJSONObjectProperty(final String fieldName, final Class fromType) {
    return extractJSONObjectProperty(fieldName, MetaClassFactory.get(fromType));
  }

  public Statement extractJSONObjectProperty(final String fieldName, final MetaClass fromType) {
    if (fromType.getFullyQualifiedName().equals(EJObject.class.getName())) {
      return loadVariable("obj").invoke("get", fieldName);
    }
    else {
      return Stmt.nestedCall(Cast.to(fromType, loadVariable("a0"))).invoke("get", fieldName);
    }
  }
  
  public Statement extractJSONObjectProperty(final Statement fieldName, final Class fromType) {
    return extractJSONObjectProperty(fieldName, MetaClassFactory.get(fromType), "get");
  }

  public Statement extractJSONObjectProperty(final Statement fieldName, final MetaClass fromType) {
    return extractJSONObjectProperty(fieldName, fromType, "get");
  }
  
  public Statement extractJSONObjectProperty(final Statement fieldName, final Class fromType, 
                                             final String getterName) {
    return extractJSONObjectProperty(fieldName, MetaClassFactory.get(fromType), getterName);
  }

  public Statement extractJSONObjectProperty(final Statement fieldName, final MetaClass fromType, 
                                             final String getterName) {
    if (fromType.getFullyQualifiedName().equals(EJObject.class.getName())) {
      return loadVariable("obj").invoke(getterName, fieldName);
    }
    else {
      return Stmt.nestedCall(Cast.to(fromType, loadVariable("a0"))).invoke(getterName, fieldName);
    }
  }

  private int calcBufferSize(final List<MappingDefinition> stack,
                             final MappingDefinition definition) {
    int bufSize = 128;

    if (!stack.contains(definition)) {
      stack.add(definition);

      for (final MemberMapping mapping : definition.getMemberMappings()) {
        MappingDefinition def = context.getDefinitionsFactory().getDefinition(mapping.getType());

        if (def == null) {
          if (mapping.getType().isArray()) {
            def = context.getDefinitionsFactory().getDefinition(mapping.getType().getOuterComponentType().asBoxed());

            // def could still be null in the case where the array component type is abstract or an
            // interface
            if (def != null) {
              bufSize += (calcBufferSize(stack, def)) * 4;
            }
          }

          continue;
        }

        bufSize += calcBufferSize(stack, def);
      }
    }
    return bufSize;
  }

  public void marshallToJSON(final BlockBuilder<?> builder,
                             final MetaClass toType,
                             final MappingDefinition definition,
                             final ClassStructureBuilder classStructureBuilder,
                             final BlockBuilder<?> initMethod) {

    if (!context.canMarshal(toType.getFullyQualifiedName())) {
      throw new NoAvailableMarshallerException(toType.getName());
    }

    builder.append(
        If.isNull(loadVariable("a0"))
            .append(Stmt.load("null").returnValue()).finish()
        );

    final int bufSize = calcBufferSize(new ArrayList<MappingDefinition>(), definition);

    if (toMap.isEnum()) {
      builder.append(Stmt.load(marshallEnum(loadVariable("a0"), toMap)).returnValue());
      return;
    }

    builder.append(Stmt.declareFinalVariable("ref", boolean.class,
        Stmt.loadVariable("a1").invoke("hasObject", Refs.get("a0"))));

    StringExpression json = Str.expr("", StringOperator.Concat, "{\"");
    json = Str.expr(json, StringOperator.Concat, SerializationParts.ENCODED_TYPE);
    json = Str.expr(json, StringOperator.Concat, "\":\"");
    json = Str.expr(json, StringOperator.Concat, toType.getFullyQualifiedName());
    json = Str.expr(json, StringOperator.Concat, "\"");
    json = Str.expr(json, StringOperator.Concat, ",\"");
    json = Str.expr(json, StringOperator.Concat, SerializationParts.OBJECT_ID);
    json = Str.expr(json, StringOperator.Concat, "\":\"");
    json = Str.expr(json, StringOperator.Concat, loadVariable("a1").invoke("getObject", Stmt.loadVariable("a0")));
    json = Str.expr(json, StringOperator.Concat, "\"");
    
    builder.append(Stmt.declareFinalVariable("prefix", String.class, json));
    builder.append(
        If.cond(loadVariable("ref"))
            .append(Stmt.load(Str.expr(loadVariable("prefix"), StringOperator.Concat, "}")).returnValue())
            .finish());

    StringExpression concat = null;

    boolean hasEncoded = false;

    int i = 0;
    for (final MemberMapping mapping : definition.getMemberMappings()) {
      if (!mapping.canRead()) {
        continue;
      }

      BlockBuilder<?> lazyInitMethod = (needsLazyInit(mapping.getType())) ? initMethod : null;
      MarshallingGenUtil.ensureMarshallerFieldCreated(classStructureBuilder, toMap, mapping.getType()
            .asBoxed(), lazyInitMethod);

      if (!hasEncoded) {
        concat = Str.expr("", StringOperator.Concat, ",\"");
        hasEncoded = true;
      }
      else if (i > 0) {
        concat = Str.expr(concat, StringOperator.Concat, ",\""); 
      }

      final MetaClass targetType = GenUtil.getPrimitiveWrapper(mapping.getType());

      final MetaClass compType =
          targetType.isArray() ? targetType.getOuterComponentType().asBoxed() : targetType.asBoxed();

      if (!(compType.isAbstract() || compType.isInterface() || compType.isEnum())
          && !context.canMarshal(compType.getFullyQualifiedName())) {
        throw new NoAvailableMarshallerException(compType.getFullyQualifiedName());
      }

      Statement valueStatement = valueAccessorFor(mapping.getReadingMember(), classStructureBuilder);
      if (targetType.isArray()) {
        valueStatement = context.getArrayMarshallerCallback().marshal(targetType, valueStatement);
      }
      
      concat = Str.expr(concat, StringOperator.Concat, mapping.getKey());
      concat = Str.expr(concat, StringOperator.Concat, "\":");

      if (targetType.isEnum()) {
        concat = Str.expr(concat, StringOperator.Concat, Expr.qualify(marshallEnum(valueStatement, targetType)));
      }
      else {
        concat = Str.expr(concat, StringOperator.Concat,
            loadVariable(MarshallingGenUtil.getVarName(targetType))
                .invoke("marshall", valueStatement, loadVariable("a1")));
      }

      i++;
    }

    StringExpression tail = concat;
   
    if (i == 0) {
      tail = Str.expr(tail == null ? "" : tail, StringOperator.Concat, ",\"");
      tail = Str.expr(tail, StringOperator.Concat, SerializationParts.INSTANTIATE_ONLY);
      tail = Str.expr(tail, StringOperator.Concat, "\":true");
    }
    
    tail = Str.expr(tail == null ? "" : tail, StringOperator.Concat, "}");
    builder.append(Stmt.load(Str.expr(loadVariable("prefix"), StringOperator.Concat, tail)).returnValue());
  }

  private static String keyValue(final String key, final String value) {
    return "\"" + key + "\":" + value + "";
  }

  private static String string(final String value) {
    return "\"" + value + "\"";
  }

  public Statement valueAccessorFor(final MetaClassMember member, ClassStructureBuilder<?> classStructureBuilder) {
    if (member instanceof MetaField) {
      final MetaField field = (MetaField) member;
      if (!field.isPublic()) {
        final MetaMethod getterMethod = GenUtil.findCaseInsensitiveMatch(field.getType(),
            field.getDeclaringClass(), "get" + field.getName());

        if (getterMethod != null) {
          return loadVariable("a0").invoke(getterMethod);
        }
        else {
          if (!context.isExposed(field, classStructureBuilder.getClassDefinition().getName())) {
            PrivateAccessUtil.addPrivateAccessStubs(gwtTarget ? "jsni" : "reflection", classStructureBuilder, field);
            context.markExposed(field, classStructureBuilder.getClassDefinition().getName());
          }

          return Stmt.invokeStatic(classStructureBuilder.getClassDefinition(), PrivateAccessUtil
              .getPrivateFieldAccessorName(field),
              loadVariable("a0"));
        }
      }
      else {
        return loadVariable("a0").loadField(field.getName());
      }
    }
    else {
      final MetaMethod method = (MetaMethod) member;
      if (!method.isPublic()) {
        if (!context.isExposed(method, classStructureBuilder.getClassDefinition().getName())) {
          PrivateAccessUtil.addPrivateAccessStubs(gwtTarget ? "jsni" : "reflection", classStructureBuilder, method);
          context.markExposed(method, classStructureBuilder.getClassDefinition().getName());
        }

        return Stmt.invokeStatic(classStructureBuilder.getClassDefinition(),
                PrivateAccessUtil.getPrivateMethodName(method), loadVariable("a0"));
      }
      else {
        return loadVariable("a0").invoke(method);
      }
    }
  }

  public Statement demarshallEnum(final Statement objStatement,
                                  final Statement valStatement,
                                  final MetaClass toType) {

    final Statement trueStatement = Stmt.invokeStatic(Enum.class, "valueOf", toType,
        Stmt.nestedCall(objStatement)
            .invoke("get", SerializationParts.ENUM_STRING_VALUE).invoke("isString").invoke("stringValue"));

    final Statement falseStatement =
        (valStatement != null) ?
            new TernaryStatement(Bool.isNotNull(Stmt.nestedCall(valStatement).invoke("isString")),
                Stmt.invokeStatic(Enum.class, "valueOf", toType,
                    Stmt.nestedCall(valStatement).invoke("isString").invoke("stringValue")),
                Stmt.load(null))
            : Stmt.load(null);

    return new TernaryStatement(Bool.isNotNull(objStatement), trueStatement, falseStatement);
  }

  public void marshallEnum(final ContextualStatementBuilder bb,
                           final Statement valueStatement,
                           final MetaClass toType) {

    final Implementations.StringBuilderBuilder internalSBB = Implementations.newStringBuilder()
            .append("{\"").append(SerializationParts.ENCODED_TYPE)
            .append( "\":\"").append( toType.getFullyQualifiedName()).append("\",\"")
            .append(SerializationParts.ENUM_STRING_VALUE).append("\":\"")
            .append(Stmt.nestedCall(valueStatement).invoke("name")).append("\"}");

    final TernaryStatement ternaryStatement = new TernaryStatement(
        Bool.isNotNull(valueStatement), internalSBB, Stmt.load("null"));

    bb.invoke("append", ternaryStatement);
  }
  
  public TernaryStatement marshallEnum(final Statement valueStatement, final MetaClass toType) {

    StringExpression s = Str.expr("{\"", StringOperator.Concat, SerializationParts.ENCODED_TYPE);
    s = Str.expr(s, StringOperator.Concat, "\":\"");
    s = Str.expr(s, StringOperator.Concat, toType.getFullyQualifiedName());
    s = Str.expr(s, StringOperator.Concat, "\",\"");
    s = Str.expr(s, StringOperator.Concat, SerializationParts.ENUM_STRING_VALUE);
    s = Str.expr(s, StringOperator.Concat, "\":\"");
    s = Str.expr(s, StringOperator.Concat, Stmt.nestedCall(valueStatement).invoke("name"));
    s = Str.expr(s, StringOperator.Concat, "\"}");

    final TernaryStatement ternaryStatement = new TernaryStatement(
        Bool.isNotNull(valueStatement), Stmt.load(s), Stmt.load("null"));

    return ternaryStatement;
  }

  public Statement unwrapJSON(final Statement valueStatement, final MetaClass toType, final MetaClass targetType) {
    if (toType.isEnum()) {
      return demarshallEnum(Stmt.nestedCall(valueStatement).invoke("isObject"), valueStatement, toType);
    }
    else {
      final String varName = MarshallingGenUtil.getVarName(toType);

      if (toType.equals(MetaClassFactory.get(Object.class))) {
        return Stmt.castTo(ObjectMarshaller.class, Stmt.loadVariable(varName))
            .invoke("demarshall", targetType.asClass(), valueStatement, loadVariable("a1"));
      }

      return Stmt.loadVariable(varName)
          .invoke("demarshall", valueStatement, loadVariable("a1"));
    }
  }

  private boolean needsLazyInit(MetaClass type) {
    MetaClass compType = type.getOuterComponentType().getErased();
    return (!compType.asUnboxed().isPrimitive() && !compType.equals(MetaClassFactory.get(String.class)) && !context
        .getDefinitionsFactory().hasBuiltInDefinition(compType));
  }

  private boolean containsInnerClass(ClassStructureBuilder<?> classStructureBuilder, BuildMetaClass inner) {
    MetaClass[] innerClasses = classStructureBuilder.getClassDefinition().getDeclaredClasses();
    for (MetaClass innerClass : innerClasses) {
      if (innerClass.getFullyQualifiedName().equals(inner.getFullyQualifiedName())) {
        return true;
      }
    }
    return false;
  }
}
