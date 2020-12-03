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
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.kie.workbench.common.stunner.core.lookup.LookupManager.LookupRequest;
import org.kie.workbench.common.stunner.core.lookup.LookupManager.LookupResponse;
import org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManager;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManagerImpl;
import org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupRequest;
import org.kie.workbench.common.stunner.core.rule.Rule;

public class Type_factory__o_k_w_c_s_c_l_r_RuleLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<RuleLookupManagerImpl> { private class Type_factory__o_k_w_c_s_c_l_r_RuleLookupManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends RuleLookupManagerImpl implements Proxy<RuleLookupManagerImpl> {
    private final ProxyHelper<RuleLookupManagerImpl> proxyHelper = new ProxyHelperImpl<RuleLookupManagerImpl>("Type_factory__o_k_w_c_s_c_l_r_RuleLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final RuleLookupManagerImpl instance) {

    }

    public RuleLookupManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final RuleLookupManagerImpl instance) {
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

    @Override protected List getItems(RuleLookupRequest request) {
      if (proxyHelper != null) {
        final RuleLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = RuleLookupManagerImpl_getItems_RuleLookupRequest(proxiedInstance, request);
        return retVal;
      } else {
        return super.getItems(request);
      }
    }

    @Override protected Rule buildResult(Rule rule) {
      if (proxyHelper != null) {
        final RuleLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Rule retVal = RuleLookupManagerImpl_buildResult_Rule(proxiedInstance, rule);
        return retVal;
      } else {
        return super.buildResult(rule);
      }
    }

    @Override protected boolean matches(String key, String value, Rule rule) {
      if (proxyHelper != null) {
        final RuleLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = RuleLookupManagerImpl_matches_String_String_Rule(proxiedInstance, key, value, rule);
        return retVal;
      } else {
        return super.matches(key, value, rule);
      }
    }

    @Override public LookupResponse lookup(RuleLookupRequest request) {
      if (proxyHelper != null) {
        final RuleLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final LookupResponse retVal = proxiedInstance.lookup(request);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final RuleLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override protected boolean matches(String criteria, Rule item) {
      if (proxyHelper != null) {
        final RuleLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = AbstractCriteriaLookupManager_matches_String_Rule(proxiedInstance, criteria, item);
        return retVal;
      } else {
        return super.matches(criteria, item);
      }
    }

    @Override protected Collection toCollection(String s) {
      if (proxyHelper != null) {
        final RuleLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = AbstractCriteriaLookupManager_toCollection_String(proxiedInstance, s);
        return retVal;
      } else {
        return super.toCollection(s);
      }
    }

    @Override protected LookupResponse buildEmptyResponse(RuleLookupRequest request) {
      if (proxyHelper != null) {
        final RuleLookupManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final LookupResponse retVal = AbstractLookupManager_buildEmptyResponse_RuleLookupRequest(proxiedInstance, request);
        return retVal;
      } else {
        return super.buildEmptyResponse(request);
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_r_RuleLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RuleLookupManagerImpl.class, "Type_factory__o_k_w_c_s_c_l_r_RuleLookupManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RuleLookupManagerImpl.class, AbstractCriteriaLookupManager.class, AbstractLookupManager.class, Object.class, LookupManager.class, RuleLookupManager.class });
  }

  public RuleLookupManagerImpl createInstance(final ContextManager contextManager) {
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final RuleLookupManagerImpl instance = new RuleLookupManagerImpl(_definitionManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<RuleLookupManagerImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_l_r_RuleLookupManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static LookupResponse AbstractLookupManager_buildEmptyResponse_RuleLookupRequest(AbstractLookupManager instance, LookupRequest a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager::buildEmptyResponse(Lorg/kie/workbench/common/stunner/core/lookup/LookupManager$LookupRequest;)(a0);
  }-*/;

  public native static Collection AbstractCriteriaLookupManager_toCollection_String(AbstractCriteriaLookupManager instance, String a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager::toCollection(Ljava/lang/String;)(a0);
  }-*/;

  public native static List RuleLookupManagerImpl_getItems_RuleLookupRequest(RuleLookupManagerImpl instance, RuleLookupRequest a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManagerImpl::getItems(Lorg/kie/workbench/common/stunner/core/lookup/rule/RuleLookupRequest;)(a0);
  }-*/;

  public native static Rule RuleLookupManagerImpl_buildResult_Rule(RuleLookupManagerImpl instance, Rule a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManagerImpl::buildResult(Lorg/kie/workbench/common/stunner/core/rule/Rule;)(a0);
  }-*/;

  public native static boolean RuleLookupManagerImpl_matches_String_String_Rule(RuleLookupManagerImpl instance, String a0, String a1, Rule a2) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.rule.RuleLookupManagerImpl::matches(Ljava/lang/String;Ljava/lang/String;Lorg/kie/workbench/common/stunner/core/rule/Rule;)(a0, a1, a2);
  }-*/;

  public native static boolean AbstractCriteriaLookupManager_matches_String_Rule(AbstractCriteriaLookupManager instance, String a0, Object a1) /*-{
    return instance.@org.kie.workbench.common.stunner.core.lookup.criteria.AbstractCriteriaLookupManager::matches(Ljava/lang/String;Ljava/lang/Object;)(a0, a1);
  }-*/;
}