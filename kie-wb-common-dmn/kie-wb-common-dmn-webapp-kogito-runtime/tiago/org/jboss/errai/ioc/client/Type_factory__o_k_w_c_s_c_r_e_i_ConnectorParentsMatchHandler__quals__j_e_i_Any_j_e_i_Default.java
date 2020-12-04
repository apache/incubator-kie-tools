package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionMultiHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchConnectionHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchContainmentHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchHandler;

public class Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorParentsMatchHandler> { private class Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ConnectorParentsMatchHandler implements Proxy<ConnectorParentsMatchHandler> {
    private final ProxyHelper<ConnectorParentsMatchHandler> proxyHelper = new ProxyHelperImpl<ConnectorParentsMatchHandler>("Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ConnectorParentsMatchHandler instance) {

    }

    public ConnectorParentsMatchHandler asBeanType() {
      return this;
    }

    public void setInstance(final ConnectorParentsMatchHandler instance) {
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
        final ConnectorParentsMatchHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public Class getExtensionType() {
      if (proxyHelper != null) {
        final ConnectorParentsMatchHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getExtensionType();
        return retVal;
      } else {
        return super.getExtensionType();
      }
    }

    @Override public Class getContextType() {
      if (proxyHelper != null) {
        final ConnectorParentsMatchHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getContextType();
        return retVal;
      } else {
        return super.getContextType();
      }
    }

    @Override public boolean accepts(RuleExtension rule, RuleEvaluationContext context) {
      if (proxyHelper != null) {
        final ConnectorParentsMatchHandler proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(rule, context);
        return retVal;
      } else {
        return super.accepts(rule, context);
      }
    }

    @Override public RuleViolations evaluate(RuleExtension rule, RuleEvaluationContext context) {
      if (proxyHelper != null) {
        final ConnectorParentsMatchHandler proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(rule, context);
        return retVal;
      } else {
        return super.evaluate(rule, context);
      }
    }

    @Override public Class getRuleType() {
      if (proxyHelper != null) {
        final ConnectorParentsMatchHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getRuleType();
        return retVal;
      } else {
        return super.getRuleType();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ConnectorParentsMatchHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConnectorParentsMatchHandler.class, "Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConnectorParentsMatchHandler.class, RuleExtensionHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public ConnectorParentsMatchHandler createInstance(final ContextManager contextManager) {
    final ConnectorParentsMatchConnectionHandler _connectionHandler_0 = (ConnectorParentsMatchConnectionHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_Default");
    final RuleExtensionMultiHandler _multiHandler_2 = (RuleExtensionMultiHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_r_e_RuleExtensionMultiHandler__quals__j_e_i_Any_j_e_i_Default");
    final ConnectorParentsMatchContainmentHandler _containmentHandler_1 = (ConnectorParentsMatchContainmentHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchContainmentHandler__quals__j_e_i_Any_j_e_i_Default");
    final ConnectorParentsMatchHandler instance = new ConnectorParentsMatchHandler(_connectionHandler_0, _containmentHandler_1, _multiHandler_2);
    registerDependentScopedReference(instance, _multiHandler_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ConnectorParentsMatchHandler instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ConnectorParentsMatchHandler> proxyImpl = new Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}