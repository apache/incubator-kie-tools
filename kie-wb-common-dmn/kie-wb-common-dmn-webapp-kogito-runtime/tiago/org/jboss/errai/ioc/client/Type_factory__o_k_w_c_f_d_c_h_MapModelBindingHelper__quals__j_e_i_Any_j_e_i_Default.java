package org.jboss.errai.ioc.client;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.databinding.client.MapPropertyType;
import org.jboss.errai.databinding.client.PropertyType;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

public class Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<MapModelBindingHelper> { private class Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends MapModelBindingHelper implements Proxy<MapModelBindingHelper> {
    private final ProxyHelper<MapModelBindingHelper> proxyHelper = new ProxyHelperImpl<MapModelBindingHelper>("Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final MapModelBindingHelper instance) {

    }

    public MapModelBindingHelper asBeanType() {
      return this;
    }

    public void setInstance(final MapModelBindingHelper instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void initialize() {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initialize();
      } else {
        super.initialize();
      }
    }

    @Override protected void lookupPropertyGenerators() {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        MapModelBindingHelper_lookupPropertyGenerators(proxiedInstance);
      } else {
        super.lookupPropertyGenerators();
      }
    }

    @Override public void initContext(MapModelRenderingContext context) {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initContext(context);
      } else {
        super.initContext(context);
      }
    }

    @Override public void prepareMapContent(Map formData, FormDefinition form, Map contextData, MapModelRenderingContext context) {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.prepareMapContent(formData, form, contextData, context);
      } else {
        super.prepareMapContent(formData, form, contextData, context);
      }
    }

    @Override protected void prepareMapContentFor(SubFormFieldDefinition field, Object fieldValue, Map formData, MapModelRenderingContext context) {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        MapModelBindingHelper_prepareMapContentFor_SubFormFieldDefinition_Object_Map_MapModelRenderingContext(proxiedInstance, field, fieldValue, formData, context);
      } else {
        super.prepareMapContentFor(field, fieldValue, formData, context);
      }
    }

    @Override protected void prepareMapContentFor(MultipleSubFormFieldDefinition field, Object fieldValue, Map formData, MapModelRenderingContext context) {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        MapModelBindingHelper_prepareMapContentFor_MultipleSubFormFieldDefinition_Object_Map_MapModelRenderingContext(proxiedInstance, field, fieldValue, formData, context);
      } else {
        super.prepareMapContentFor(field, fieldValue, formData, context);
      }
    }

    @Override protected Map generateModelDefinition(FormDefinition form, MapModelRenderingContext context) {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = MapModelBindingHelper_generateModelDefinition_FormDefinition_MapModelRenderingContext(proxiedInstance, form, context);
        return retVal;
      } else {
        return super.generateModelDefinition(form, context);
      }
    }

    @Override protected Map generateModelDefinition(FormDefinition form, MapModelRenderingContext context, Map availableModels) {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = MapModelBindingHelper_generateModelDefinition_FormDefinition_MapModelRenderingContext_Map(proxiedInstance, form, context, availableModels);
        return retVal;
      } else {
        return super.generateModelDefinition(form, context, availableModels);
      }
    }

    @Override public MapPropertyType getModeldefinitionFor(SubFormFieldDefinition subFormField, MapModelRenderingContext context, Map availableModels) {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        final MapPropertyType retVal = proxiedInstance.getModeldefinitionFor(subFormField, context, availableModels);
        return retVal;
      } else {
        return super.getModeldefinitionFor(subFormField, context, availableModels);
      }
    }

    @Override public MapPropertyType getModeldefinitionFor(MultipleSubFormFieldDefinition subFormField, MapModelRenderingContext context, Map availableModels) {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        final MapPropertyType retVal = proxiedInstance.getModeldefinitionFor(subFormField, context, availableModels);
        return retVal;
      } else {
        return super.getModeldefinitionFor(subFormField, context, availableModels);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final MapModelBindingHelper proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MapModelBindingHelper.class, "Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MapModelBindingHelper.class, Object.class });
  }

  public MapModelBindingHelper createInstance(final ContextManager contextManager) {
    final MapModelBindingHelper instance = new MapModelBindingHelper();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final MapModelBindingHelper instance) {
    instance.initialize();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<MapModelBindingHelper> proxyImpl = new Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void MapModelBindingHelper_prepareMapContentFor_MultipleSubFormFieldDefinition_Object_Map_MapModelRenderingContext(MapModelBindingHelper instance, MultipleSubFormFieldDefinition a0, Object a1, Map a2, MapModelRenderingContext a3) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper::prepareMapContentFor(Lorg/kie/workbench/common/forms/fields/shared/fieldTypes/relations/multipleSubform/definition/MultipleSubFormFieldDefinition;Ljava/lang/Object;Ljava/util/Map;Lorg/kie/workbench/common/forms/dynamic/service/shared/impl/MapModelRenderingContext;)(a0, a1, a2, a3);
  }-*/;

  public native static Map MapModelBindingHelper_generateModelDefinition_FormDefinition_MapModelRenderingContext_Map(MapModelBindingHelper instance, FormDefinition a0, MapModelRenderingContext a1, Map<String, Map<String, PropertyType>> a2) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper::generateModelDefinition(Lorg/kie/workbench/common/forms/model/FormDefinition;Lorg/kie/workbench/common/forms/dynamic/service/shared/impl/MapModelRenderingContext;Ljava/util/Map;)(a0, a1, a2);
  }-*/;

  public native static Map MapModelBindingHelper_generateModelDefinition_FormDefinition_MapModelRenderingContext(MapModelBindingHelper instance, FormDefinition a0, MapModelRenderingContext a1) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper::generateModelDefinition(Lorg/kie/workbench/common/forms/model/FormDefinition;Lorg/kie/workbench/common/forms/dynamic/service/shared/impl/MapModelRenderingContext;)(a0, a1);
  }-*/;

  public native static void MapModelBindingHelper_prepareMapContentFor_SubFormFieldDefinition_Object_Map_MapModelRenderingContext(MapModelBindingHelper instance, SubFormFieldDefinition a0, Object a1, Map a2, MapModelRenderingContext a3) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper::prepareMapContentFor(Lorg/kie/workbench/common/forms/fields/shared/fieldTypes/relations/subForm/definition/SubFormFieldDefinition;Ljava/lang/Object;Ljava/util/Map;Lorg/kie/workbench/common/forms/dynamic/service/shared/impl/MapModelRenderingContext;)(a0, a1, a2, a3);
  }-*/;

  public native static void MapModelBindingHelper_lookupPropertyGenerators(MapModelBindingHelper instance) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper::lookupPropertyGenerators()();
  }-*/;
}