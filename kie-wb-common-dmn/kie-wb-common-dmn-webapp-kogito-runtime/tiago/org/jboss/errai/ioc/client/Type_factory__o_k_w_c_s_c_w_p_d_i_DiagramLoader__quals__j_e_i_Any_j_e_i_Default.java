package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DiagramLoader;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServiceImpl;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.backend.vfs.Path;

public class Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramLoader> { private class Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DiagramLoader implements Proxy<DiagramLoader> {
    private final ProxyHelper<DiagramLoader> proxyHelper = new ProxyHelperImpl<DiagramLoader>("Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final DiagramLoader instance) {

    }

    public DiagramLoader asBeanType() {
      return this;
    }

    public void setInstance(final DiagramLoader instance) {
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

    @Override public void loadByPath(Path path, ServiceCallback callback) {
      if (proxyHelper != null) {
        final DiagramLoader proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadByPath(path, callback);
      } else {
        super.loadByPath(path, callback);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DiagramLoader proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramLoader.class, "Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramLoader.class, Object.class });
  }

  public DiagramLoader createInstance(final ContextManager contextManager) {
    final StunnerPreferencesRegistryLoader _preferencesRegistryLoader_1 = (StunnerPreferencesRegistryLoader) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_Default");
    final ClientDiagramService _clientDiagramServices_0 = (ClientDiagramServiceImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_ClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final DiagramLoader instance = new DiagramLoader(_clientDiagramServices_0, _preferencesRegistryLoader_1);
    registerDependentScopedReference(instance, _clientDiagramServices_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_w_p_d_i_DiagramLoader__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DiagramLoader an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.client.widgets.presenters.diagram.impl.DiagramLoader ([org.kie.workbench.common.stunner.core.client.service.ClientDiagramService, org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DiagramLoader> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}