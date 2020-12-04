package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.common.persistence.RecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.CreationType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeCreateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionCreateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionUpdateHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator;

public class Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionRecordEngine> { private class Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ItemDefinitionRecordEngine implements Proxy<ItemDefinitionRecordEngine> {
    private final ProxyHelper<ItemDefinitionRecordEngine> proxyHelper = new ProxyHelperImpl<ItemDefinitionRecordEngine>("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null, null, null, null);
    }

    public void initProxyProperties(final ItemDefinitionRecordEngine instance) {

    }

    public ItemDefinitionRecordEngine asBeanType() {
      return this;
    }

    public void setInstance(final ItemDefinitionRecordEngine instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public List update(DataType dataType) {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.update(dataType);
        return retVal;
      } else {
        return super.update(dataType);
      }
    }

    @Override public List destroy(DataType dataType) {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.destroy(dataType);
        return retVal;
      } else {
        return super.destroy(dataType);
      }
    }

    @Override public List destroyWithoutDependentTypes(DataType dataType) {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.destroyWithoutDependentTypes(dataType);
        return retVal;
      } else {
        return super.destroyWithoutDependentTypes(dataType);
      }
    }

    @Override public List create(DataType dataType) {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.create(dataType);
        return retVal;
      } else {
        return super.create(dataType);
      }
    }

    @Override public List create(DataType record, DataType reference, CreationType creationType) {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.create(record, reference, creationType);
        return retVal;
      } else {
        return super.create(record, reference, creationType);
      }
    }

    @Override public boolean isValid(DataType dataType) {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isValid(dataType);
        return retVal;
      } else {
        return super.isValid(dataType);
      }
    }

    @Override public void doUpdate(DataType dataType, ItemDefinition itemDefinition) {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.doUpdate(dataType, itemDefinition);
      } else {
        super.doUpdate(dataType, itemDefinition);
      }
    }

    @Override public void doDestroy(DataType dataType) {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.doDestroy(dataType);
      } else {
        super.doDestroy(dataType);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ItemDefinitionRecordEngine proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ItemDefinitionRecordEngine.class, "Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ItemDefinitionRecordEngine.class, Object.class, DataTypeRecordEngine.class, RecordEngine.class });
  }

  public ItemDefinitionRecordEngine createInstance(final ContextManager contextManager) {
    final DataTypeUpdateHandler _dataTypeUpdateHandler_5 = (DataTypeUpdateHandler) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeUpdateHandler__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionDestroyHandler _itemDefinitionDestroyHandler_1 = (ItemDefinitionDestroyHandler) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionDestroyHandler__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionUpdateHandler _itemDefinitionUpdateHandler_2 = (ItemDefinitionUpdateHandler) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionUpdateHandler__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionCreateHandler _itemDefinitionCreateHandler_3 = (ItemDefinitionCreateHandler) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionCreateHandler__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeNameValidator _dataTypeNameValidator_7 = (DataTypeNameValidator) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_v_DataTypeNameValidator__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionStore _itemDefinitionStore_0 = (ItemDefinitionStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeCreateHandler _dataTypeCreateHandler_6 = (DataTypeCreateHandler) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeCreateHandler__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeDestroyHandler _dataTypeDestroyHandler_4 = (DataTypeDestroyHandler) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeDestroyHandler__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionRecordEngine instance = new ItemDefinitionRecordEngine(_itemDefinitionStore_0, _itemDefinitionDestroyHandler_1, _itemDefinitionUpdateHandler_2, _itemDefinitionCreateHandler_3, _dataTypeDestroyHandler_4, _dataTypeUpdateHandler_5, _dataTypeCreateHandler_6, _dataTypeNameValidator_7);
    registerDependentScopedReference(instance, _dataTypeUpdateHandler_5);
    registerDependentScopedReference(instance, _itemDefinitionDestroyHandler_1);
    registerDependentScopedReference(instance, _itemDefinitionUpdateHandler_2);
    registerDependentScopedReference(instance, _itemDefinitionCreateHandler_3);
    registerDependentScopedReference(instance, _dataTypeNameValidator_7);
    registerDependentScopedReference(instance, _dataTypeCreateHandler_6);
    registerDependentScopedReference(instance, _dataTypeDestroyHandler_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ItemDefinitionRecordEngine instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionRecordEngine__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionRecordEngine ([org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore, org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionDestroyHandler, org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionUpdateHandler, org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionCreateHandler, org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeDestroyHandler, org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeUpdateHandler, org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeCreateHandler, org.kie.workbench.common.dmn.client.editors.types.persistence.validation.DataTypeNameValidator])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ItemDefinitionRecordEngine> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}