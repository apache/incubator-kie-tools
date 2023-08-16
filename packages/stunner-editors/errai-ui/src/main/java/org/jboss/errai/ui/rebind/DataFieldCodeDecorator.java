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

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.codegen.Statement;
import org.jboss.errai.codegen.exception.GenerationException;
import org.jboss.errai.codegen.meta.MetaClass;
import org.jboss.errai.codegen.util.Stmt;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.common.client.ui.HasValue;
import org.jboss.errai.ioc.client.api.CodeDecorator;
import org.jboss.errai.ioc.rebind.ioc.extension.IOCDecoratorExtension;
import org.jboss.errai.ioc.rebind.ioc.injector.api.Decorable;
import org.jboss.errai.ioc.rebind.ioc.injector.api.FactoryController;
import org.jboss.errai.ioc.rebind.ioc.injector.api.InjectionContext;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.jboss.errai.codegen.util.Stmt.loadLiteral;


/**
 * Store all injected {@link DataField} {@link Statement} instances into the
 * aggregate {@link Map} for this composite {@link Template}.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@CodeDecorator
public class DataFieldCodeDecorator extends IOCDecoratorExtension<DataField> {

  public DataFieldCodeDecorator(final Class<DataField> decoratesWith) {
    super(decoratesWith);
  }

  @Override
  public void generateDecorator(final Decorable decorable, final FactoryController controller) {
    controller.ensureMemberExposed(decorable.get());
    Statement instance = decorable.getAccessStatement();
    final String name = getTemplateDataFieldName((DataField) decorable.getAnnotation(), decorable.getName());
    final boolean isWidget = decorable.getType().isAssignableTo(Widget.class);
    if (!isWidget && decorable.getType().isAnnotationPresent(Templated.class)) {
      instance = Stmt.invokeStatic(TemplateWidgetMapper.class, "get", instance);
    } else if (decorable.getType().isAssignableTo(Element.class)) {
      instance = Stmt.invokeStatic(ElementWrapperWidget.class, "getWidget", instance);
    } else if (decorable.getType().isAssignableTo(IsElement.class)) {
      instance = Stmt.invokeStatic(ElementWrapperWidget.class, "getWidget", Stmt.nestedCall(instance).invoke("getElement"));
    } else if (decorable.getType().isAssignableTo(org.jboss.errai.common.client.api.elemental2.IsElement.class)) {
      instance = Stmt.invokeStatic(ElementWrapperWidget.class, "getWidget", Stmt.nestedCall(instance).invoke("getElement"), null);
    } else if (RebindUtil.isNativeJsType(decorable.getType()) || RebindUtil.isElementalIface(decorable.getType())) {
      if (decorable.getType().isAssignableTo(HasValue.class)) {
        final MetaClass valueType = decorable.getType().getMethod("getValue", new Class[0]).getReturnType();
        instance = Stmt.invokeStatic(ElementWrapperWidget.class, "getWidget",
                Stmt.invokeStatic(TemplateUtil.class, "asElement", instance), loadLiteral(valueType));
      }
      else {
        instance = Stmt.invokeStatic(ElementWrapperWidget.class, "getWidget", Stmt.invokeStatic(TemplateUtil.class, "asElement", instance));
      }
    } else if (decorable.getType().isAssignableTo( IsWidget.class )) {
      instance = Stmt.nestedCall( instance ).invoke( "asWidget" );
    } else {
      if ( !isWidget ) {
        throw new GenerationException( "Unable to use [" + name + "] in class [" + decorable.getDecorableDeclaringType()
                + "] as a @DataField. The field must be a Widget, IsWidget, or a DOM element as either a JavaScriptObject, native @JsType, or IsElement." );
      }
    }

    saveDataField(decorable, decorable.getType(), name, decorable.getName(), instance);
  }

  private void saveDataField(final Decorable decorable, final MetaClass type, final String name, final String fieldName, final Statement instance) {
    dataFieldMap(decorable.getInjectionContext(), decorable.getDecorableDeclaringType()).put(name, instance);
    dataFieldTypeMap(decorable.getInjectionContext(), decorable.getDecorableDeclaringType()).put(name, type);
    dataFieldAnnotationMap(decorable.getInjectionContext(), decorable.getDecorableDeclaringType()).put(name, (DataField) decorable.getAnnotation());
  }

  private String getTemplateDataFieldName(final DataField annotation, final String deflt) {
    final String value = Strings.nullToEmpty(annotation.value()).trim();
    return value.isEmpty() ? deflt : value;
  }

  /**
   * Get the map of {@link DataField} names and {@link Statement} instances.
   */
  @SuppressWarnings("unchecked")
  private static Map<String, Statement> dataFieldMap(final InjectionContext context, final MetaClass templateType) {
    final String dataFieldMapName = dataFieldMapName(templateType);

    Map<String, Statement> dataFields = (Map<String, Statement>) context.getAttribute(
        dataFieldMapName);
    if (dataFields == null) {
      dataFields = new LinkedHashMap<>();
      context.setAttribute(dataFieldMapName, dataFields);
    }

    return dataFields;
  }

  /**
   * Get the map of {@link DataField} names and {@link MetaClass} types.
   */
  @SuppressWarnings("unchecked")
  private static Map<String, MetaClass> dataFieldTypeMap(final InjectionContext context, final MetaClass templateType) {
    final String dataFieldTypeMapName = dataFieldTypeMapName(templateType);

    Map<String, MetaClass> dataFieldTypes = (Map<String, MetaClass>) context.getAttribute(
        dataFieldTypeMapName);
    if (dataFieldTypes == null) {
      dataFieldTypes = new LinkedHashMap<>();
      context.setAttribute(dataFieldTypeMapName, dataFieldTypes);
    }

    return dataFieldTypes;
  }

  /**
   * Get the map of {@link DataField} fields/parameters to {@link DataField} instances.
   */
  private static Map<String, DataField> dataFieldAnnotationMap(final InjectionContext context, final MetaClass templateType) {
    final String dataFieldAnnoMapName = dataFieldAnnotationMapName(templateType);

    @SuppressWarnings("unchecked")
    Map<String, DataField> dataFieldTypes = (Map<String, DataField>) context.getAttribute(dataFieldAnnoMapName);
    if (dataFieldTypes == null) {
      dataFieldTypes = new LinkedHashMap<>();
      context.setAttribute(dataFieldAnnoMapName, dataFieldTypes);
    }

    return dataFieldTypes;
  }

  /**
   * Get the aggregate map of {@link DataField} names and {@link Statement}
   * instances for the given {@link MetaClass} type and all ancestors returned
   * by {@link MetaClass#getSuperClass()}.
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Statement> aggregateDataFieldMap(final Decorable decorable, final MetaClass componentType) {

    final Map<String, Statement> result = new LinkedHashMap<>();

    if (componentType.getSuperClass() != null) {
      result.putAll(aggregateDataFieldMap(decorable, componentType.getSuperClass()));
    }

    final Map<String, Statement> dataFields = (Map<String, Statement>) decorable.getInjectionContext().getAttribute(
        dataFieldMapName(componentType));
    if (dataFields != null) {
      result.putAll(dataFields);
    }

    return result;
  }

  /**
   * Get the aggregate map of {@link DataField} names and {@link MetaClass}
   * types for the given {@link MetaClass} component type and all ancestors
   * returned by {@link MetaClass#getSuperClass()}.
   */
  @SuppressWarnings("unchecked")
  public static Map<String, MetaClass> aggregateDataFieldTypeMap(final Decorable decorable, final MetaClass componentType) {

    final Map<String, MetaClass> result = new LinkedHashMap<>();

    if (componentType.getSuperClass() != null) {
      result.putAll(aggregateDataFieldTypeMap(decorable, componentType.getSuperClass()));
    }

    final Map<String, MetaClass> dataFields = (Map<String, MetaClass>) decorable.getInjectionContext().getAttribute(
        dataFieldTypeMapName(componentType));

    if (dataFields != null) {
      result.putAll(dataFields);
    }

    return result;
  }

  /**
   * Get the aggregate map of {@link DataField} names to instances for the given {@link MetaClass} component type and
   * all ancestors returned by {@link MetaClass#getSuperClass()}.
   */
  public static Map<String, DataField> aggregateDataFieldAnnotationMap(final Decorable decorable, final MetaClass componentType) {

    final Map<String, DataField> result = new LinkedHashMap<>();

    if (componentType.getSuperClass() != null) {
      result.putAll(aggregateDataFieldAnnotationMap(decorable, componentType.getSuperClass()));
    }

    @SuppressWarnings("unchecked")
    final Map<String, DataField> dataFields = (Map<String, DataField>) decorable.getInjectionContext().getAttribute(
        dataFieldAnnotationMapName(componentType));

    if (dataFields != null) {
      result.putAll(dataFields);
    }

    return result;
  }

  /**
   * Using the given composite {@link Template} type, return the name of the map
   * of {@link DataField} names and variable {@link Statement} instances.
   */
  private static final String dataFieldMapName(final MetaClass composite) {
    return DataFieldCodeDecorator.class.getName() + "_DATA_FIELD_MAP_" + composite.getFullyQualifiedName();
  }

  /**
   * Using the given composite {@link Template} type, return the name of the map
   * of {@link DataField} names and variable {@link MetaClass} types.
   */
  private static final String dataFieldTypeMapName(final MetaClass composite) {
    return DataFieldCodeDecorator.class.getName() + "_DATA_FIELD_TYPE_MAP_" + composite.getFullyQualifiedName();
  }

  /**
   * Using the given composite {@link Template} type, return the name of the map
   * of {@link DataField} names and variable {@link MetaClass} types.
   */
  private static final String dataFieldAnnotationMapName(final MetaClass composite) {
    return DataFieldCodeDecorator.class.getName() + "_DATA_FIELD_ANNOTATION_MAP_" + composite.getFullyQualifiedName();
  }
}
