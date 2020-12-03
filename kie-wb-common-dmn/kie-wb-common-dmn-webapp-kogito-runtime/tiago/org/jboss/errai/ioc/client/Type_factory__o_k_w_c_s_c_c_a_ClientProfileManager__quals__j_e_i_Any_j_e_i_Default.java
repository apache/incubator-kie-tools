package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Function;
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
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.client.api.ClientProfileManager;
import org.kie.workbench.common.stunner.core.profile.AbstractProfileManager;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientProfileManager> { private class Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientProfileManager implements Proxy<ClientProfileManager> {
    private final ProxyHelper<ClientProfileManager> proxyHelper = new ProxyHelperImpl<ClientProfileManager>("Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final ClientProfileManager instance) {

    }

    public ClientProfileManager asBeanType() {
      return this;
    }

    public void setInstance(final ClientProfileManager instance) {
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

    @Override public void destroy() {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override protected Function getQualifier() {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final Function retVal = ClientProfileManager_getQualifier(proxiedInstance);
        return retVal;
      } else {
        return super.getQualifier();
      }
    }

    @Override protected Iterable getAllProfileInstances() {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final Iterable retVal = ClientProfileManager_getAllProfileInstances(proxiedInstance);
        return retVal;
      } else {
        return super.getAllProfileInstances();
      }
    }

    @Override protected Iterable selectProfileInstances(Annotation[] qualifiers) {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final Iterable retVal = ClientProfileManager_selectProfileInstances_Annotation_array(proxiedInstance, qualifiers);
        return retVal;
      } else {
        return super.selectProfileInstances(qualifiers);
      }
    }

    @Override public Collection getAllProfiles() {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getAllProfiles();
        return retVal;
      } else {
        return super.getAllProfiles();
      }
    }

    @Override public Profile getProfile(String id) {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final Profile retVal = proxiedInstance.getProfile(id);
        return retVal;
      } else {
        return super.getProfile(id);
      }
    }

    @Override public Profile getProfile(String definitionSetId, String id) {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final Profile retVal = proxiedInstance.getProfile(definitionSetId, id);
        return retVal;
      } else {
        return super.getProfile(definitionSetId, id);
      }
    }

    @Override public Collection getProfiles(String definitionSetId) {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getProfiles(definitionSetId);
        return retVal;
      } else {
        return super.getProfiles(definitionSetId);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientProfileManager.class, "Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientProfileManager.class, AbstractProfileManager.class, Object.class, ProfileManager.class });
  }

  public ClientProfileManager createInstance(final ContextManager contextManager) {
    final ManagedInstance<Profile> _profileInstances_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { Profile.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ClientProfileManager instance = new ClientProfileManager(_definitionUtils_0, _profileInstances_1);
    registerDependentScopedReference(instance, _profileInstances_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ClientProfileManager) instance, contextManager);
  }

  public void destroyInstanceHelper(final ClientProfileManager instance, final ContextManager contextManager) {
    instance.destroy();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.client.api.ClientProfileManager an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.client.api.ClientProfileManager ([org.kie.workbench.common.stunner.core.util.DefinitionUtils, org.jboss.errai.ioc.client.api.ManagedInstance])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientProfileManager> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static Function ClientProfileManager_getQualifier(ClientProfileManager instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.api.ClientProfileManager::getQualifier()();
  }-*/;

  public native static Iterable ClientProfileManager_selectProfileInstances_Annotation_array(ClientProfileManager instance, Annotation[] a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.api.ClientProfileManager::selectProfileInstances([Ljava/lang/annotation/Annotation;)(a0);
  }-*/;

  public native static Iterable ClientProfileManager_getAllProfileInstances(ClientProfileManager instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.api.ClientProfileManager::getAllProfileInstances()();
  }-*/;
}