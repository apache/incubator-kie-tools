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
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemsProvider;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory;

public class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorItemsProvider> { private class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DecisionNavigatorItemsProvider implements Proxy<DecisionNavigatorItemsProvider> {
    private final ProxyHelper<DecisionNavigatorItemsProvider> proxyHelper = new ProxyHelperImpl<DecisionNavigatorItemsProvider>("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null);
    }

    public void initProxyProperties(final DecisionNavigatorItemsProvider instance) {

    }

    public DecisionNavigatorItemsProvider asBeanType() {
      return this;
    }

    public void setInstance(final DecisionNavigatorItemsProvider instance) {
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

    @Override public List getItems() {
      if (proxyHelper != null) {
        final DecisionNavigatorItemsProvider proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getItems();
        return retVal;
      } else {
        return super.getItems();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DecisionNavigatorItemsProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorItemsProvider.class, "Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorItemsProvider.class, Object.class });
  }

  public DecisionNavigatorItemsProvider createInstance(final ContextManager contextManager) {
    final DecisionNavigatorItemFactory _itemFactory_0 = (DecisionNavigatorItemFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorItemFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramUtils _dmnDiagramUtils_2 = (DMNDiagramUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_1 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorItemsProvider instance = new DecisionNavigatorItemsProvider(_itemFactory_0, _dmnDiagramsSession_1, _dmnDiagramUtils_2);
    registerDependentScopedReference(instance, _itemFactory_0);
    registerDependentScopedReference(instance, _dmnDiagramUtils_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemsProvider an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemsProvider ([org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorItemFactory, org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession, org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DecisionNavigatorItemsProvider> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}