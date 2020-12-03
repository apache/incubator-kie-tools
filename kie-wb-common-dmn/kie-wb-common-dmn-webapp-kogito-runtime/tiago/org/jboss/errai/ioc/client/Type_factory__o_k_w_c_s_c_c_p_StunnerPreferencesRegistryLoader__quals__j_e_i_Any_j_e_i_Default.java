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
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryHolder;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerTextPreferences;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferencesBeanGeneratedImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.ParameterizedCommand;

public class Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerPreferencesRegistryLoader> { private class Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends StunnerPreferencesRegistryLoader implements Proxy<StunnerPreferencesRegistryLoader> {
    private final ProxyHelper<StunnerPreferencesRegistryLoader> proxyHelper = new ProxyHelperImpl<StunnerPreferencesRegistryLoader>("Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null);
    }

    public void initProxyProperties(final StunnerPreferencesRegistryLoader instance) {

    }

    public StunnerPreferencesRegistryLoader asBeanType() {
      return this;
    }

    public void setInstance(final StunnerPreferencesRegistryLoader instance) {
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

    @Override public void load(Metadata metadata, ParameterizedCommand loadCompleteCallback, ParameterizedCommand errorCallback) {
      if (proxyHelper != null) {
        final StunnerPreferencesRegistryLoader proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.load(metadata, loadCompleteCallback, errorCallback);
      } else {
        super.load(metadata, loadCompleteCallback, errorCallback);
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final StunnerPreferencesRegistryLoader proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final StunnerPreferencesRegistryLoader proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerPreferencesRegistryLoader.class, "Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerPreferencesRegistryLoader.class, Object.class });
  }

  public StunnerPreferencesRegistryLoader createInstance(final ContextManager contextManager) {
    final ManagedInstance<StunnerPreferencesRegistryHolder> _preferencesHolders_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { StunnerPreferencesRegistryHolder.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final StunnerPreferences _preferences_2 = (StunnerPreferencesBeanGeneratedImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_p_StunnerPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default");
    final StunnerTextPreferences _textPreferences_3 = (StunnerTextPreferences) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_p_StunnerTextPreferences__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final StunnerPreferencesRegistryLoader instance = new StunnerPreferencesRegistryLoader(_definitionUtils_0, _preferencesHolders_1, _preferences_2, _textPreferences_3);
    registerDependentScopedReference(instance, _preferencesHolders_1);
    registerDependentScopedReference(instance, _preferences_2);
    registerDependentScopedReference(instance, _textPreferences_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((StunnerPreferencesRegistryLoader) instance, contextManager);
  }

  public void destroyInstanceHelper(final StunnerPreferencesRegistryLoader instance, final ContextManager contextManager) {
    instance.destroy();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistryLoader__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader ([org.kie.workbench.common.stunner.core.util.DefinitionUtils, org.jboss.errai.ioc.client.api.ManagedInstance, org.kie.workbench.common.stunner.core.preferences.StunnerPreferences, org.kie.workbench.common.stunner.core.client.preferences.StunnerTextPreferences])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<StunnerPreferencesRegistryLoader> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}