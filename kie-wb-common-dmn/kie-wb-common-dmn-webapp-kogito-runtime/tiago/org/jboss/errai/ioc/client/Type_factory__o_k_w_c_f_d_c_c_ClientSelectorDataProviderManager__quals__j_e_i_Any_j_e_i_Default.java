package org.jboss.errai.ioc.client;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.forms.dynamic.client.config.ClientSelectorDataProviderManager;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.AbstractSelectorDataProviderManager;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.SelectorDataProviderManager;

public class Type_factory__o_k_w_c_f_d_c_c_ClientSelectorDataProviderManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientSelectorDataProviderManager> { private class Type_factory__o_k_w_c_f_d_c_c_ClientSelectorDataProviderManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientSelectorDataProviderManager implements Proxy<ClientSelectorDataProviderManager> {
    private final ProxyHelper<ClientSelectorDataProviderManager> proxyHelper = new ProxyHelperImpl<ClientSelectorDataProviderManager>("Type_factory__o_k_w_c_f_d_c_c_ClientSelectorDataProviderManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientSelectorDataProviderManager instance) {

    }

    public ClientSelectorDataProviderManager asBeanType() {
      return this;
    }

    public void setInstance(final ClientSelectorDataProviderManager instance) {
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
        final ClientSelectorDataProviderManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public String getPreffix() {
      if (proxyHelper != null) {
        final ClientSelectorDataProviderManager proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getPreffix();
        return retVal;
      } else {
        return super.getPreffix();
      }
    }

    @Override protected void registerProvider(SelectorDataProvider provider) {
      if (proxyHelper != null) {
        final ClientSelectorDataProviderManager proxiedInstance = proxyHelper.getInstance(this);
        AbstractSelectorDataProviderManager_registerProvider_SelectorDataProvider(proxiedInstance, provider);
      } else {
        super.registerProvider(provider);
      }
    }

    @Override public Map availableProviders() {
      if (proxyHelper != null) {
        final ClientSelectorDataProviderManager proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.availableProviders();
        return retVal;
      } else {
        return super.availableProviders();
      }
    }

    @Override public SelectorData getDataFromProvider(FormRenderingContext context, String provider) {
      if (proxyHelper != null) {
        final ClientSelectorDataProviderManager proxiedInstance = proxyHelper.getInstance(this);
        final SelectorData retVal = proxiedInstance.getDataFromProvider(context, provider);
        return retVal;
      } else {
        return super.getDataFromProvider(context, provider);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientSelectorDataProviderManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_f_d_c_c_ClientSelectorDataProviderManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientSelectorDataProviderManager.class, "Type_factory__o_k_w_c_f_d_c_c_ClientSelectorDataProviderManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientSelectorDataProviderManager.class, AbstractSelectorDataProviderManager.class, Object.class, SelectorDataProviderManager.class });
  }

  public ClientSelectorDataProviderManager createInstance(final ContextManager contextManager) {
    final ClientSelectorDataProviderManager instance = new ClientSelectorDataProviderManager();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientSelectorDataProviderManager instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientSelectorDataProviderManager> proxyImpl = new Type_factory__o_k_w_c_f_d_c_c_ClientSelectorDataProviderManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void AbstractSelectorDataProviderManager_registerProvider_SelectorDataProvider(AbstractSelectorDataProviderManager instance, SelectorDataProvider a0) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.service.shared.AbstractSelectorDataProviderManager::registerProvider(Lorg/kie/workbench/common/forms/dynamic/model/config/SelectorDataProvider;)(a0);
  }-*/;
}