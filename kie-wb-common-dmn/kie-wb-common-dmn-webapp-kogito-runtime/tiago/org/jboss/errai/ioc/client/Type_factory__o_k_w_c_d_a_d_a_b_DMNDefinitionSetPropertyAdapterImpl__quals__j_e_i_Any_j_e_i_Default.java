package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetPropertyAdapterImpl;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.definition.adapter.Adapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PriorityAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapterWrapper;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFunctions;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

public class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetPropertyAdapterImpl> { private class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDefinitionSetPropertyAdapterImpl implements Proxy<DMNDefinitionSetPropertyAdapterImpl> {
    private final ProxyHelper<DMNDefinitionSetPropertyAdapterImpl> proxyHelper = new ProxyHelperImpl<DMNDefinitionSetPropertyAdapterImpl>("Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final DMNDefinitionSetPropertyAdapterImpl instance) {

    }

    public DMNDefinitionSetPropertyAdapterImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNDefinitionSetPropertyAdapterImpl instance) {
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
        final DMNDefinitionSetPropertyAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public String getId(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetPropertyAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getId(pojo);
        return retVal;
      } else {
        return super.getId(pojo);
      }
    }

    @Override public String getCaption(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetPropertyAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCaption(pojo);
        return retVal;
      } else {
        return super.getCaption(pojo);
      }
    }

    @Override public Object getValue(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetPropertyAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.getValue(pojo);
        return retVal;
      } else {
        return super.getValue(pojo);
      }
    }

    @Override public void setValue(Object pojo, Object value) {
      if (proxyHelper != null) {
        final DMNDefinitionSetPropertyAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setValue(pojo, value);
      } else {
        super.setValue(pojo, value);
      }
    }

    @Override public int getPriority() {
      if (proxyHelper != null) {
        final DMNDefinitionSetPropertyAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getPriority();
        return retVal;
      } else {
        return super.getPriority();
      }
    }

    @Override public boolean accepts(Class type) {
      if (proxyHelper != null) {
        final DMNDefinitionSetPropertyAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(type);
        return retVal;
      } else {
        return super.accepts(type);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDefinitionSetPropertyAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDefinitionSetPropertyAdapterImpl.class, "Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDefinitionSetPropertyAdapterImpl.class, PropertyAdapterWrapper.class, Object.class, PropertyAdapter.class, PriorityAdapter.class, Adapter.class });
  }

  public DMNDefinitionSetPropertyAdapterImpl createInstance(final ContextManager contextManager) {
    final BindableAdapterFunctions _functions_1 = (ClientBindableAdapterFunctions) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableAdapterFunctions__quals__j_e_i_Any_j_e_i_Default");
    final StunnerTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DMNDefinitionSetPropertyAdapterImpl instance = new DMNDefinitionSetPropertyAdapterImpl(_translationService_0, _functions_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNDefinitionSetPropertyAdapterImpl instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetPropertyAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetPropertyAdapterImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetPropertyAdapterImpl ([org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService, org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFunctions])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDefinitionSetPropertyAdapterImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}