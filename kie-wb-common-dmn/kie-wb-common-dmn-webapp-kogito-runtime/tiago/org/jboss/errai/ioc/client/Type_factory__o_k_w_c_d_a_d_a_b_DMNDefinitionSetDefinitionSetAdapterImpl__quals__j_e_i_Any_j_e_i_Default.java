package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetDefinitionSetAdapterImpl;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.definition.adapter.Adapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapterWrapper;
import org.kie.workbench.common.stunner.core.definition.adapter.PriorityAdapter;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

public class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDefinitionSetDefinitionSetAdapterImpl> { private class Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDefinitionSetDefinitionSetAdapterImpl implements Proxy<DMNDefinitionSetDefinitionSetAdapterImpl> {
    private final ProxyHelper<DMNDefinitionSetDefinitionSetAdapterImpl> proxyHelper = new ProxyHelperImpl<DMNDefinitionSetDefinitionSetAdapterImpl>("Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final DMNDefinitionSetDefinitionSetAdapterImpl instance) {

    }

    public DMNDefinitionSetDefinitionSetAdapterImpl asBeanType() {
      return this;
    }

    public void setInstance(final DMNDefinitionSetDefinitionSetAdapterImpl instance) {
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
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public String getId(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getId(pojo);
        return retVal;
      } else {
        return super.getId(pojo);
      }
    }

    @Override public String getDomain(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDomain(pojo);
        return retVal;
      } else {
        return super.getDomain(pojo);
      }
    }

    @Override public String getDescription(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDescription(pojo);
        return retVal;
      } else {
        return super.getDescription(pojo);
      }
    }

    @Override public Set getDefinitions(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getDefinitions(pojo);
        return retVal;
      } else {
        return super.getDefinitions(pojo);
      }
    }

    @Override public Class getGraphFactoryType(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getGraphFactoryType(pojo);
        return retVal;
      } else {
        return super.getGraphFactoryType(pojo);
      }
    }

    @Override public Annotation getQualifier(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final Annotation retVal = proxiedInstance.getQualifier(pojo);
        return retVal;
      } else {
        return super.getQualifier(pojo);
      }
    }

    @Override public int getPriority() {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getPriority();
        return retVal;
      } else {
        return super.getPriority();
      }
    }

    @Override public boolean accepts(Class type) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(type);
        return retVal;
      } else {
        return super.accepts(type);
      }
    }

    @Override public Optional getSvgNodeId(Object pojo) {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final Optional retVal = proxiedInstance.getSvgNodeId(pojo);
        return retVal;
      } else {
        return super.getSvgNodeId(pojo);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDefinitionSetDefinitionSetAdapterImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDefinitionSetDefinitionSetAdapterImpl.class, "Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDefinitionSetDefinitionSetAdapterImpl.class, DefinitionSetAdapterWrapper.class, Object.class, DefinitionSetAdapter.class, PriorityAdapter.class, Adapter.class });
  }

  public DMNDefinitionSetDefinitionSetAdapterImpl createInstance(final ContextManager contextManager) {
    final StunnerTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DMNDefinitionSetDefinitionSetAdapterImpl instance = new DMNDefinitionSetDefinitionSetAdapterImpl(_translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNDefinitionSetDefinitionSetAdapterImpl instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_a_d_a_b_DMNDefinitionSetDefinitionSetAdapterImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetDefinitionSetAdapterImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.api.definition.adapter.binding.DMNDefinitionSetDefinitionSetAdapterImpl ([org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDefinitionSetDefinitionSetAdapterImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}