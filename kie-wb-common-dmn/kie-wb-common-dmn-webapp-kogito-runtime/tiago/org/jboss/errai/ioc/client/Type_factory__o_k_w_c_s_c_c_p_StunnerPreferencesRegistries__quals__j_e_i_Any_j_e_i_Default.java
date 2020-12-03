package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerPreferencesRegistries> { private class Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends StunnerPreferencesRegistries implements Proxy<StunnerPreferencesRegistries> {
    private final ProxyHelper<StunnerPreferencesRegistries> proxyHelper = new ProxyHelperImpl<StunnerPreferencesRegistries>("Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final StunnerPreferencesRegistries instance) {

    }

    public StunnerPreferencesRegistries asBeanType() {
      return this;
    }

    public void setInstance(final StunnerPreferencesRegistries instance) {
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

    @Override public Object get(String definitionSetId, Class preferenceType) {
      if (proxyHelper != null) {
        final StunnerPreferencesRegistries proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.get(definitionSetId, preferenceType);
        return retVal;
      } else {
        return super.get(definitionSetId, preferenceType);
      }
    }

    @Override public Object get(Annotation qualifier, Class preferenceType) {
      if (proxyHelper != null) {
        final StunnerPreferencesRegistries proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.get(qualifier, preferenceType);
        return retVal;
      } else {
        return super.get(qualifier, preferenceType);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final StunnerPreferencesRegistries proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerPreferencesRegistries.class, "Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerPreferencesRegistries.class, Object.class });
  }

  public StunnerPreferencesRegistries createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<StunnerPreferencesRegistry> _preferencesRegistries_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { StunnerPreferencesRegistry.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final StunnerPreferencesRegistries instance = new StunnerPreferencesRegistries(_definitionUtils_0, _preferencesRegistries_1);
    registerDependentScopedReference(instance, _preferencesRegistries_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries ([org.kie.workbench.common.stunner.core.util.DefinitionUtils, org.jboss.errai.ioc.client.api.ManagedInstance])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<StunnerPreferencesRegistries> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}