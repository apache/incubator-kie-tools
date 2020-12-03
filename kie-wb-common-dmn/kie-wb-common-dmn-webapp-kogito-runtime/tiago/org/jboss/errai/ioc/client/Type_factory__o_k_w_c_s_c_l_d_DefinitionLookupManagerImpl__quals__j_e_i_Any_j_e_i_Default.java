package org.jboss.errai.ioc.client;

import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.LookupManager.LookupRequest;
import org.kie.workbench.common.stunner.core.lookup.LookupManager.LookupResponse;
import org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManager;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManagerImpl;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.definition.DefinitionRepresentation;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

public class Type_factory__o_k_w_c_s_c_l_d_DefinitionLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionLookupManagerImpl> { private class Type_factory__o_k_w_c_s_c_l_d_DefinitionLookupManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefinitionLookupManagerImpl implements Proxy<DefinitionLookupManagerImpl> {
    private final ProxyHelper<DefinitionLookupManagerImpl> proxyHelper = new ProxyHelperImpl<DefinitionLookupManagerImpl>("Type_factory__o_k_w_c_s_c_l_d_DefinitionLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefinitionLookupManagerImpl instance) {

    }

    public DefinitionLookupManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final DefinitionLookupManagerImpl instance) {
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

    @Override protected List getItems(DefinitionLookupRequest request) {
      if (proxyHelper != null) {
        final DefinitionLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = DefinitionLookupManagerImpl_getItems_DefinitionLookupRequest(proxiedInstance, request);
        return retVal;
      } else {
        return super.getItems(request);
      }
    }

    @Override protected DefinitionRepresentation buildResult(String defId) {
      if (proxyHelper != null) {
        final DefinitionLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionRepresentation retVal = DefinitionLookupManagerImpl_buildResult_String(proxiedInstance, defId);
        return retVal;
      } else {
        return super.buildResult(defId);
      }
    }

    @Override protected boolean matches(String key, String value, String defId) {
      if (proxyHelper != null) {
        final DefinitionLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = DefinitionLookupManagerImpl_matches_String_String_String(proxiedInstance, key, value, defId);
        return retVal;
      } else {
        return super.matches(key, value, defId);
      }
    }

    @Override public LookupResponse lookup(DefinitionLookupRequest request) {
      if (proxyHelper != null) {
        final DefinitionLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final LookupResponse retVal = proxiedInstance.lookup(request);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefinitionLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override protected boolean matches(String criteria, String item) {
      if (proxyHelper != null) {
        final DefinitionLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = AbstractCriteriaLookupManager_matches_String_String(proxiedInstance, criteria, item);
        return retVal;
      } else {
        return super.matches(criteria, item);
      }
    }

    @Override protected Collection toCollection(String s) {
      if (proxyHelper != null) {
        final DefinitionLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = AbstractCriteriaLookupManager_toCollection_String(proxiedInstance, s);
        return retVal;
      } else {
        return super.toCollection(s);
      }
    }

    @Override protected LookupResponse buildEmptyResponse(DefinitionLookupRequest request) {
      if (proxyHelper != null) {
        final DefinitionLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final LookupResponse retVal = AbstractLookupManager_buildEmptyResponse_DefinitionLookupRequest(proxiedInstance, request);
        return retVal;
      } else {
        return super.buildEmptyResponse(request);
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_d_DefinitionLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionLookupManagerImpl.class, "Type_factory__o_k_w_c_s_c_l_d_DefinitionLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionLookupManagerImpl.class, AbstractCriteriaLookupManager.class, AbstractLookupManager.class, Object.class, LookupManager.class, DefinitionLookupManager.class });
  }

  public DefinitionLookupManagerImpl createInstance(final ContextManager contextManager) {
    final DefinitionsCacheRegistry _registry_2 = (DefinitionsCacheRegistry) contextManager.getInstance("Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final FactoryManager _factoryManager_1 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionLookupManagerImpl instance = new DefinitionLookupManagerImpl(_definitionManager_0, _factoryManager_1, _registry_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefinitionLookupManagerImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_l_d_DefinitionLookupManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static boolean DefinitionLookupManagerImpl_matches_String_String_String(DefinitionLookupManagerImpl instance, String a0, String a1, String a2) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManagerImpl::matches(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(a0, a1, a2);
  }-*/;

  public native static boolean AbstractCriteriaLookupManager_matches_String_String(AbstractCriteriaLookupManager instance, String a0, Object a1) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager::matches(Ljava/lang/String;Ljava/lang/Object;)(a0, a1);
  }-*/;

  public native static Collection AbstractCriteriaLookupManager_toCollection_String(AbstractCriteriaLookupManager instance, String a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager::toCollection(Ljava/lang/String;)(a0);
  }-*/;

  public native static DefinitionRepresentation DefinitionLookupManagerImpl_buildResult_String(DefinitionLookupManagerImpl instance, String a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManagerImpl::buildResult(Ljava/lang/String;)(a0);
  }-*/;

  public native static List DefinitionLookupManagerImpl_getItems_DefinitionLookupRequest(DefinitionLookupManagerImpl instance, DefinitionLookupRequest a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.definition.DefinitionLookupManagerImpl::getItems(Lorg/kie/workbench/common/stunner/core/lookup/definition/DefinitionLookupRequest;)(a0);
  }-*/;

  public native static LookupResponse AbstractLookupManager_buildEmptyResponse_DefinitionLookupRequest(AbstractLookupManager instance, LookupRequest a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager::buildEmptyResponse(Lorg/kie/workbench/common/stunner/core/lookup/LookupManager$LookupRequest;)(a0);
  }-*/;
}