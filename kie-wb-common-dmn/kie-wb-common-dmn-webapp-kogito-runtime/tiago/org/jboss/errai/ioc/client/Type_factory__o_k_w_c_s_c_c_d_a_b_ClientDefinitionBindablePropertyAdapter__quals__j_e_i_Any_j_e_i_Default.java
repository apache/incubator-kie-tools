package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientDefinitionBindablePropertyAdapter;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.definition.adapter.Adapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PriorityAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.DefinitionBindableProperty;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.DefinitionBindablePropertyAdapter;

public class Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDefinitionBindablePropertyAdapter> { private class Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientDefinitionBindablePropertyAdapter implements Proxy<ClientDefinitionBindablePropertyAdapter> {
    private final ProxyHelper<ClientDefinitionBindablePropertyAdapter> proxyHelper = new ProxyHelperImpl<ClientDefinitionBindablePropertyAdapter>("Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ClientDefinitionBindablePropertyAdapter instance) {

    }

    public ClientDefinitionBindablePropertyAdapter asBeanType() {
      return this;
    }

    public void setInstance(final ClientDefinitionBindablePropertyAdapter instance) {
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

    @Override public String getCaption(DefinitionBindableProperty property) {
      if (proxyHelper != null) {
        final ClientDefinitionBindablePropertyAdapter proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCaption(property);
        return retVal;
      } else {
        return super.getCaption(property);
      }
    }

    @Override public Object getValue(DefinitionBindableProperty property) {
      if (proxyHelper != null) {
        final ClientDefinitionBindablePropertyAdapter proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.getValue(property);
        return retVal;
      } else {
        return super.getValue(property);
      }
    }

    @Override public void setValue(DefinitionBindableProperty property, Object value) {
      if (proxyHelper != null) {
        final ClientDefinitionBindablePropertyAdapter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setValue(property, value);
      } else {
        super.setValue(property, value);
      }
    }

    @Override public String getId(DefinitionBindableProperty pojo) {
      if (proxyHelper != null) {
        final ClientDefinitionBindablePropertyAdapter proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getId(pojo);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int getPriority() {
      if (proxyHelper != null) {
        final ClientDefinitionBindablePropertyAdapter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getPriority();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public boolean accepts(Class type) {
      if (proxyHelper != null) {
        final ClientDefinitionBindablePropertyAdapter proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(type);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientDefinitionBindablePropertyAdapter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientDefinitionBindablePropertyAdapter.class, "Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientDefinitionBindablePropertyAdapter.class, Object.class, DefinitionBindablePropertyAdapter.class, PropertyAdapter.class, PriorityAdapter.class, Adapter.class });
  }

  public ClientDefinitionBindablePropertyAdapter createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ClientDefinitionBindablePropertyAdapter instance = new ClientDefinitionBindablePropertyAdapter(_translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_c_d_a_b_ClientDefinitionBindablePropertyAdapter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientDefinitionBindablePropertyAdapter an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientDefinitionBindablePropertyAdapter ([org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientDefinitionBindablePropertyAdapter> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}