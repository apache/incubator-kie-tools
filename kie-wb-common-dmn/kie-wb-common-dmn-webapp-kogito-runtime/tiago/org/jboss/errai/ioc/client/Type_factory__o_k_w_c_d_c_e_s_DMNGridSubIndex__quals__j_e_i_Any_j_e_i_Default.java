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
import org.kie.workbench.common.dmn.client.editors.search.DMNGridHelper;
import org.kie.workbench.common.dmn.client.editors.search.DMNGridSubIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSubIndex;
import org.kie.workbench.common.widgets.client.search.common.HasSearchableElements;

public class Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGridSubIndex> { private class Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNGridSubIndex implements Proxy<DMNGridSubIndex> {
    private final ProxyHelper<DMNGridSubIndex> proxyHelper = new ProxyHelperImpl<DMNGridSubIndex>("Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final DMNGridSubIndex instance) {

    }

    public DMNGridSubIndex asBeanType() {
      return this;
    }

    public void setInstance(final DMNGridSubIndex instance) {
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

    @Override public List getSearchableElements() {
      if (proxyHelper != null) {
        final DMNGridSubIndex proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getSearchableElements();
        return retVal;
      } else {
        return super.getSearchableElements();
      }
    }

    @Override public void onSearchClosed() {
      if (proxyHelper != null) {
        final DMNGridSubIndex proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onSearchClosed();
      } else {
        super.onSearchClosed();
      }
    }

    @Override public void onNoResultsFound() {
      if (proxyHelper != null) {
        final DMNGridSubIndex proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onNoResultsFound();
      } else {
        super.onNoResultsFound();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNGridSubIndex proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNGridSubIndex.class, "Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNGridSubIndex.class, Object.class, DMNSubIndex.class, HasSearchableElements.class });
  }

  public DMNGridSubIndex createInstance(final ContextManager contextManager) {
    final DMNGridHelper _dmnGridHelper_0 = (DMNGridHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_s_DMNGridHelper__quals__j_e_i_Any_j_e_i_Default");
    final DMNGridSubIndex instance = new DMNGridSubIndex(_dmnGridHelper_0);
    registerDependentScopedReference(instance, _dmnGridHelper_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_s_DMNGridSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.search.DMNGridSubIndex an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.search.DMNGridSubIndex ([org.kie.workbench.common.dmn.client.editors.search.DMNGridHelper])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNGridSubIndex> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}