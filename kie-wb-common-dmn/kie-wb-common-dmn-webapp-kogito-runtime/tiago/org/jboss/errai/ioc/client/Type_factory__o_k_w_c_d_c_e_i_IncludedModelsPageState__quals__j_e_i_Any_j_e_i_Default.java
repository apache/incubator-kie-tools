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
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsPageStateProvider;

public class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPageState> { private class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends IncludedModelsPageState implements Proxy<IncludedModelsPageState> {
    private final ProxyHelper<IncludedModelsPageState> proxyHelper = new ProxyHelperImpl<IncludedModelsPageState>("Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final IncludedModelsPageState instance) {

    }

    public IncludedModelsPageState asBeanType() {
      return this;
    }

    public void setInstance(final IncludedModelsPageState instance) {
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

    @Override public void init(IncludedModelsPageStateProvider pageProvider) {
      if (proxyHelper != null) {
        final IncludedModelsPageState proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(pageProvider);
      } else {
        super.init(pageProvider);
      }
    }

    @Override public String getCurrentDiagramNamespace() {
      if (proxyHelper != null) {
        final IncludedModelsPageState proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCurrentDiagramNamespace();
        return retVal;
      } else {
        return super.getCurrentDiagramNamespace();
      }
    }

    @Override public List generateIncludedModels() {
      if (proxyHelper != null) {
        final IncludedModelsPageState proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.generateIncludedModels();
        return retVal;
      } else {
        return super.generateIncludedModels();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final IncludedModelsPageState proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelsPageState.class, "Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelsPageState.class, Object.class });
  }

  public IncludedModelsPageState createInstance(final ContextManager contextManager) {
    final IncludedModelsPageState instance = new IncludedModelsPageState();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<IncludedModelsPageState> proxyImpl = new Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}