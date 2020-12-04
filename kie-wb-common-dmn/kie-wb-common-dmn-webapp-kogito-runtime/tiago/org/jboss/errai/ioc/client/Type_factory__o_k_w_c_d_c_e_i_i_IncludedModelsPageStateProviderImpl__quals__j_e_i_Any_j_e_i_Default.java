package org.jboss.errai.ioc.client;

import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsPageStateProvider;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

public class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPageStateProviderImpl> { private class Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends IncludedModelsPageStateProviderImpl implements Proxy<IncludedModelsPageStateProviderImpl> {
    private final ProxyHelper<IncludedModelsPageStateProviderImpl> proxyHelper = new ProxyHelperImpl<IncludedModelsPageStateProviderImpl>("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final IncludedModelsPageStateProviderImpl instance) {

    }

    public IncludedModelsPageStateProviderImpl asBeanType() {
      return this;
    }

    public void setInstance(final IncludedModelsPageStateProviderImpl instance) {
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

    @Override public String getCurrentDiagramNamespace() {
      if (proxyHelper != null) {
        final IncludedModelsPageStateProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCurrentDiagramNamespace();
        return retVal;
      } else {
        return super.getCurrentDiagramNamespace();
      }
    }

    @Override public List generateIncludedModels() {
      if (proxyHelper != null) {
        final IncludedModelsPageStateProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.generateIncludedModels();
        return retVal;
      } else {
        return super.generateIncludedModels();
      }
    }

    @Override public List getImports() {
      if (proxyHelper != null) {
        final IncludedModelsPageStateProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getImports();
        return retVal;
      } else {
        return super.getImports();
      }
    }

    @Override public Optional getDiagram() {
      if (proxyHelper != null) {
        final IncludedModelsPageStateProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getDiagram();
        return retVal;
      } else {
        return super.getDiagram();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final IncludedModelsPageStateProviderImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelsPageStateProviderImpl.class, "Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelsPageStateProviderImpl.class, Object.class, IncludedModelsPageStateProvider.class });
  }

  public IncludedModelsPageStateProviderImpl createInstance(final ContextManager contextManager) {
    final DMNGraphUtils _dmnGraphUtils_0 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_2 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsFactory _factory_1 = (IncludedModelsFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsFactory__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsPageStateProviderImpl instance = new IncludedModelsPageStateProviderImpl(_dmnGraphUtils_0, _factory_1, _dmnDiagramsSession_2);
    registerDependentScopedReference(instance, _dmnGraphUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<IncludedModelsPageStateProviderImpl> proxyImpl = new Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}