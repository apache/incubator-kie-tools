package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer;
import org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELVariableSuggestions;

public class Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<MonacoFEELInitializer> { private class Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends MonacoFEELInitializer implements Proxy<MonacoFEELInitializer> {
    private final ProxyHelper<MonacoFEELInitializer> proxyHelper = new ProxyHelperImpl<MonacoFEELInitializer>("Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final MonacoFEELInitializer instance) {

    }

    public MonacoFEELInitializer asBeanType() {
      return this;
    }

    public void setInstance(final MonacoFEELInitializer instance) {
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

    @Override public void initializeFEELEditor() {
      if (proxyHelper != null) {
        final MonacoFEELInitializer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initializeFEELEditor();
      } else {
        super.initializeFEELEditor();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final MonacoFEELInitializer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MonacoFEELInitializer.class, "Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MonacoFEELInitializer.class, Object.class });
  }

  public MonacoFEELInitializer createInstance(final ContextManager contextManager) {
    final MonacoFEELVariableSuggestions _variableSuggestions_0 = (MonacoFEELVariableSuggestions) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_c_MonacoFEELVariableSuggestions__quals__j_e_i_Any_j_e_i_Default");
    final MonacoFEELInitializer instance = new MonacoFEELInitializer(_variableSuggestions_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_w_c_MonacoFEELInitializer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELInitializer ([org.kie.workbench.common.dmn.client.widgets.codecompletion.MonacoFEELVariableSuggestions])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<MonacoFEELInitializer> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}