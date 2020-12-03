package org.jboss.errai.ioc.client;

import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientMetaDataEntryManager;
import org.kie.workbench.common.forms.dynamic.client.service.ClientFieldManagerImpl;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldManager;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FieldType;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryManager;

public class Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientFieldManagerImpl> { private class Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientFieldManagerImpl implements Proxy<ClientFieldManagerImpl> {
    private final ProxyHelper<ClientFieldManagerImpl> proxyHelper = new ProxyHelperImpl<ClientFieldManagerImpl>("Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ClientFieldManagerImpl instance) {

    }

    public ClientFieldManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final ClientFieldManagerImpl instance) {
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

    @Override protected void init() {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        ClientFieldManagerImpl_init(proxiedInstance);
      } else {
        super.init();
      }
    }

    @Override protected void registerFieldProvider(FieldProvider provider) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        AbstractFieldManager_registerFieldProvider_FieldProvider(proxiedInstance, provider);
      } else {
        super.registerFieldProvider(provider);
      }
    }

    @Override public Collection getBaseFieldTypes() {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getBaseFieldTypes();
        return retVal;
      } else {
        return super.getBaseFieldTypes();
      }
    }

    @Override public FieldDefinition getDefinitionByFieldType(FieldType fieldType) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final FieldDefinition retVal = proxiedInstance.getDefinitionByFieldType(fieldType);
        return retVal;
      } else {
        return super.getDefinitionByFieldType(fieldType);
      }
    }

    @Override public FieldDefinition getDefinitionByFieldType(Class fieldType, TypeInfo typeInfo) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final FieldDefinition retVal = proxiedInstance.getDefinitionByFieldType(fieldType, typeInfo);
        return retVal;
      } else {
        return super.getDefinitionByFieldType(fieldType, typeInfo);
      }
    }

    @Override public FieldDefinition getDefinitionByFieldTypeName(String fieldTypeCode) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final FieldDefinition retVal = proxiedInstance.getDefinitionByFieldTypeName(fieldTypeCode);
        return retVal;
      } else {
        return super.getDefinitionByFieldTypeName(fieldTypeCode);
      }
    }

    @Override public FieldDefinition getDefinitionByDataType(TypeInfo typeInfo) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final FieldDefinition retVal = proxiedInstance.getDefinitionByDataType(typeInfo);
        return retVal;
      } else {
        return super.getDefinitionByDataType(typeInfo);
      }
    }

    @Override protected FieldDefinition getFieldDefinitionFromBasicProvider(TypeInfo typeInfo) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final FieldDefinition retVal = AbstractFieldManager_getFieldDefinitionFromBasicProvider_TypeInfo(proxiedInstance, typeInfo);
        return retVal;
      } else {
        return super.getFieldDefinitionFromBasicProvider(typeInfo);
      }
    }

    @Override public FieldDefinition getDefinitionByModelProperty(ModelProperty modelProperty) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final FieldDefinition retVal = proxiedInstance.getDefinitionByModelProperty(modelProperty);
        return retVal;
      } else {
        return super.getDefinitionByModelProperty(modelProperty);
      }
    }

    @Override public Collection getCompatibleFields(FieldDefinition fieldDefinition) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getCompatibleFields(fieldDefinition);
        return retVal;
      } else {
        return super.getCompatibleFields(fieldDefinition);
      }
    }

    @Override public Collection getCompatibleTypes(FieldDefinition fieldDefinition) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getCompatibleTypes(fieldDefinition);
        return retVal;
      } else {
        return super.getCompatibleTypes(fieldDefinition);
      }
    }

    @Override protected List getCompatibleTypes(String className) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = AbstractFieldManager_getCompatibleTypes_String(proxiedInstance, className);
        return retVal;
      } else {
        return super.getCompatibleTypes(className);
      }
    }

    @Override public FieldDefinition getFieldFromProvider(String typeCode, TypeInfo typeInfo) {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final FieldDefinition retVal = proxiedInstance.getFieldFromProvider(typeCode, typeInfo);
        return retVal;
      } else {
        return super.getFieldFromProvider(typeCode, typeInfo);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientFieldManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientFieldManagerImpl.class, "Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientFieldManagerImpl.class, AbstractFieldManager.class, Object.class, FieldManager.class });
  }

  public ClientFieldManagerImpl createInstance(final ContextManager contextManager) {
    final MetaDataEntryManager _metaDataEntryManager_0 = (ClientMetaDataEntryManager) contextManager.getInstance("Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientFieldManagerImpl instance = new ClientFieldManagerImpl(_metaDataEntryManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientFieldManagerImpl instance) {
    ClientFieldManagerImpl_init(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_f_d_c_s_ClientFieldManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.forms.dynamic.client.service.ClientFieldManagerImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.forms.dynamic.client.service.ClientFieldManagerImpl ([org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryManager])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientFieldManagerImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ClientFieldManagerImpl_init(ClientFieldManagerImpl instance) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.service.ClientFieldManagerImpl::init()();
  }-*/;

  public native static void AbstractFieldManager_registerFieldProvider_FieldProvider(AbstractFieldManager instance, FieldProvider a0) /*-{
    instance.@org.kie.workbench.common.forms.fields.shared.AbstractFieldManager::registerFieldProvider(Lorg/kie/workbench/common/forms/fields/shared/FieldProvider;)(a0);
  }-*/;

  public native static FieldDefinition AbstractFieldManager_getFieldDefinitionFromBasicProvider_TypeInfo(AbstractFieldManager instance, TypeInfo a0) /*-{
    return instance.@org.kie.workbench.common.forms.fields.shared.AbstractFieldManager::getFieldDefinitionFromBasicProvider(Lorg/kie/workbench/common/forms/model/TypeInfo;)(a0);
  }-*/;

  public native static List AbstractFieldManager_getCompatibleTypes_String(AbstractFieldManager instance, String a0) /*-{
    return instance.@org.kie.workbench.common.forms.fields.shared.AbstractFieldManager::getCompatibleTypes(Ljava/lang/String;)(a0);
  }-*/;
}