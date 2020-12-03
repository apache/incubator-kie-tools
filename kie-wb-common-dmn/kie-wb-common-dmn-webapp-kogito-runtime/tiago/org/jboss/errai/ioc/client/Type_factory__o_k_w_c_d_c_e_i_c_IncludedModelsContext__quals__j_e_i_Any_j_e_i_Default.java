package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.appformer.client.context.EditorContextProvider;
import org.appformer.kogito.bridge.client.context.impl.KogitoEditorContextProviderImpl;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;

public class Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsContext> { private class Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends IncludedModelsContext implements Proxy<IncludedModelsContext> {
    private final ProxyHelper<IncludedModelsContext> proxyHelper = new ProxyHelperImpl<IncludedModelsContext>("Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final IncludedModelsContext instance) {

    }

    public IncludedModelsContext asBeanType() {
      return this;
    }

    public void setInstance(final IncludedModelsContext instance) {
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

    @Override public boolean isIncludedModelChannel() {
      if (proxyHelper != null) {
        final IncludedModelsContext proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isIncludedModelChannel();
        return retVal;
      } else {
        return super.isIncludedModelChannel();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final IncludedModelsContext proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelsContext.class, "Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelsContext.class, Object.class });
  }

  public IncludedModelsContext createInstance(final ContextManager contextManager) {
    final EditorContextProvider _contextProvider_0 = (KogitoEditorContextProviderImpl) contextManager.getInstance("Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsContext instance = new IncludedModelsContext(_contextProvider_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<IncludedModelsContext> proxyImpl = new Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}