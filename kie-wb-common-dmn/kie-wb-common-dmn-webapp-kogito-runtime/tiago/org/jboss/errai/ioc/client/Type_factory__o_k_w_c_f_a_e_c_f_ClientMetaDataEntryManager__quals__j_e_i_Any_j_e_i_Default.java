package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.ClientMetaDataEntryManager;
import org.kie.workbench.common.forms.model.MetaDataEntry;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryManager;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.AbstractMetaDataEntryManager;

public class Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientMetaDataEntryManager> { private class Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientMetaDataEntryManager implements Proxy<ClientMetaDataEntryManager> {
    private final ProxyHelper<ClientMetaDataEntryManager> proxyHelper = new ProxyHelperImpl<ClientMetaDataEntryManager>("Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientMetaDataEntryManager instance) {

    }

    public ClientMetaDataEntryManager asBeanType() {
      return this;
    }

    public void setInstance(final ClientMetaDataEntryManager instance) {
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
        final ClientMetaDataEntryManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override protected void registerProcessor(MetaDataEntryProcessor processor) {
      if (proxyHelper != null) {
        final ClientMetaDataEntryManager proxiedInstance = proxyHelper.getInstance(this);
        AbstractMetaDataEntryManager_registerProcessor_MetaDataEntryProcessor(proxiedInstance, processor);
      } else {
        super.registerProcessor(processor);
      }
    }

    @Override public Class getMetaDataEntryClass(String entryName) {
      if (proxyHelper != null) {
        final ClientMetaDataEntryManager proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getMetaDataEntryClass(entryName);
        return retVal;
      } else {
        return super.getMetaDataEntryClass(entryName);
      }
    }

    @Override public MetaDataEntryProcessor getProcessorForEntry(MetaDataEntry entry) {
      if (proxyHelper != null) {
        final ClientMetaDataEntryManager proxiedInstance = proxyHelper.getInstance(this);
        final MetaDataEntryProcessor retVal = proxiedInstance.getProcessorForEntry(entry);
        return retVal;
      } else {
        return super.getProcessorForEntry(entry);
      }
    }

    @Override public MetaDataEntryProcessor getProcessorForEntry(String entryName) {
      if (proxyHelper != null) {
        final ClientMetaDataEntryManager proxiedInstance = proxyHelper.getInstance(this);
        final MetaDataEntryProcessor retVal = proxiedInstance.getProcessorForEntry(entryName);
        return retVal;
      } else {
        return super.getProcessorForEntry(entryName);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientMetaDataEntryManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientMetaDataEntryManager.class, "Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientMetaDataEntryManager.class, AbstractMetaDataEntryManager.class, Object.class, MetaDataEntryManager.class });
  }

  public ClientMetaDataEntryManager createInstance(final ContextManager contextManager) {
    final ClientMetaDataEntryManager instance = new ClientMetaDataEntryManager();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientMetaDataEntryManager instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientMetaDataEntryManager> proxyImpl = new Type_factory__o_k_w_c_f_a_e_c_f_ClientMetaDataEntryManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void AbstractMetaDataEntryManager_registerProcessor_MetaDataEntryProcessor(AbstractMetaDataEntryManager instance, MetaDataEntryProcessor a0) /*-{
    instance.@org.kie.workbench.common.forms.service.shared.meta.processing.impl.AbstractMetaDataEntryManager::registerProcessor(Lorg/kie/workbench/common/forms/service/shared/meta/processing/MetaDataEntryProcessor;)(a0);
  }-*/;
}