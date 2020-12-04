package org.jboss.errai.ioc.client;

import java.util.List;
import java.util.function.Predicate;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientProfileManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.profile.FullProfile;

public class Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default extends Factory<DomainProfileManager> { private class Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DomainProfileManager implements Proxy<DomainProfileManager> {
    private final ProxyHelper<DomainProfileManager> proxyHelper = new ProxyHelperImpl<DomainProfileManager>("Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null);
    }

    public void initProxyProperties(final DomainProfileManager instance) {

    }

    public DomainProfileManager asBeanType() {
      return this;
    }

    public void setInstance(final DomainProfileManager instance) {
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

    @Override public List getAllDefinitions(Metadata metadata) {
      if (proxyHelper != null) {
        final DomainProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getAllDefinitions(metadata);
        return retVal;
      } else {
        return super.getAllDefinitions(metadata);
      }
    }

    @Override public Predicate isDefinitionIdAllowed(Metadata metadata) {
      if (proxyHelper != null) {
        final DomainProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final Predicate retVal = proxiedInstance.isDefinitionIdAllowed(metadata);
        return retVal;
      } else {
        return super.isDefinitionIdAllowed(metadata);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DomainProfileManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DomainProfileManager.class, "Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DomainProfileManager.class, Object.class });
  }

  public DomainProfileManager createInstance(final ContextManager contextManager) {
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final ProfileManager _profileManager_1 = (ClientProfileManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientProfileManager__quals__j_e_i_Any_j_e_i_Default");
    final FullProfile _defaultProfile_2 = (FullProfile) contextManager.getInstance("Type_factory__o_k_w_c_s_c_p_FullProfile__quals__j_e_i_Any_j_e_i_Default");
    final DomainProfileManager instance = new DomainProfileManager(_definitionManager_0, _profileManager_1, _defaultProfile_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.profile.DomainProfileManager an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.profile.DomainProfileManager ([org.kie.workbench.common.stunner.core.api.DefinitionManager, org.kie.workbench.common.stunner.core.api.ProfileManager, org.kie.workbench.common.stunner.core.profile.FullProfile])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DomainProfileManager> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}