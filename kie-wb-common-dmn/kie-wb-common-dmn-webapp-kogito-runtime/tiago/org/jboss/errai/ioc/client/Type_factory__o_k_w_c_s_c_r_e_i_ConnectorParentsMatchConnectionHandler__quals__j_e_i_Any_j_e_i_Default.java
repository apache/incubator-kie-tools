package org.jboss.errai.ioc.client;

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
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationHandler;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtensionHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.AbstractParentsMatchHandler;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchConnectionHandler;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;

public class Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectorParentsMatchConnectionHandler> { private class Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ConnectorParentsMatchConnectionHandler implements Proxy<ConnectorParentsMatchConnectionHandler> {
    private final ProxyHelper<ConnectorParentsMatchConnectionHandler> proxyHelper = new ProxyHelperImpl<ConnectorParentsMatchConnectionHandler>("Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ConnectorParentsMatchConnectionHandler instance) {

    }

    public ConnectorParentsMatchConnectionHandler asBeanType() {
      return this;
    }

    public void setInstance(final ConnectorParentsMatchConnectionHandler instance) {
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

    @Override public Class getExtensionType() {
      if (proxyHelper != null) {
        final ConnectorParentsMatchConnectionHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getExtensionType();
        return retVal;
      } else {
        return super.getExtensionType();
      }
    }

    @Override public Class getContextType() {
      if (proxyHelper != null) {
        final ConnectorParentsMatchConnectionHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getContextType();
        return retVal;
      } else {
        return super.getContextType();
      }
    }

    @Override public boolean accepts(RuleExtension rule, GraphConnectionContext context) {
      if (proxyHelper != null) {
        final ConnectorParentsMatchConnectionHandler proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accepts(rule, context);
        return retVal;
      } else {
        return super.accepts(rule, context);
      }
    }

    @Override public RuleViolations evaluate(RuleExtension rule, GraphConnectionContext context) {
      if (proxyHelper != null) {
        final ConnectorParentsMatchConnectionHandler proxiedInstance = proxyHelper.getInstance(this);
        final RuleViolations retVal = proxiedInstance.evaluate(rule, context);
        return retVal;
      } else {
        return super.evaluate(rule, context);
      }
    }

    @Override protected String getViolationMessage(RuleExtension rule) {
      if (proxyHelper != null) {
        final ConnectorParentsMatchConnectionHandler proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = AbstractParentsMatchHandler_getViolationMessage_RuleExtension(proxiedInstance, rule);
        return retVal;
      } else {
        return super.getViolationMessage(rule);
      }
    }

    @Override protected void addViolation(String uuid, RuleExtension rule, DefaultRuleViolations result) {
      if (proxyHelper != null) {
        final ConnectorParentsMatchConnectionHandler proxiedInstance = proxyHelper.getInstance(this);
        AbstractParentsMatchHandler_addViolation_String_RuleExtension_DefaultRuleViolations(proxiedInstance, uuid, rule, result);
      } else {
        super.addViolation(uuid, rule, result);
      }
    }

    @Override public Class getRuleType() {
      if (proxyHelper != null) {
        final ConnectorParentsMatchConnectionHandler proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getRuleType();
        return retVal;
      } else {
        return super.getRuleType();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ConnectorParentsMatchConnectionHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConnectorParentsMatchConnectionHandler.class, "Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConnectorParentsMatchConnectionHandler.class, AbstractParentsMatchHandler.class, RuleExtensionHandler.class, Object.class, RuleEvaluationHandler.class });
  }

  public ConnectorParentsMatchConnectionHandler createInstance(final ContextManager contextManager) {
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final ConnectorParentsMatchConnectionHandler instance = new ConnectorParentsMatchConnectionHandler(_definitionManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ConnectorParentsMatchConnectionHandler> proxyImpl = new Type_factory__o_k_w_c_s_c_r_e_i_ConnectorParentsMatchConnectionHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static String AbstractParentsMatchHandler_getViolationMessage_RuleExtension(AbstractParentsMatchHandler instance, RuleExtension a0) /*-{
    return instance.@org.kie.workbench.common.stunner.core.rule.ext.impl.AbstractParentsMatchHandler::getViolationMessage(Lorg/kie/workbench/common/stunner/core/rule/ext/RuleExtension;)(a0);
  }-*/;

  public native static void AbstractParentsMatchHandler_addViolation_String_RuleExtension_DefaultRuleViolations(AbstractParentsMatchHandler instance, String a0, RuleExtension a1, DefaultRuleViolations a2) /*-{
    instance.@org.kie.workbench.common.stunner.core.rule.ext.impl.AbstractParentsMatchHandler::addViolation(Ljava/lang/String;Lorg/kie/workbench/common/stunner/core/rule/ext/RuleExtension;Lorg/kie/workbench/common/stunner/core/rule/violations/DefaultRuleViolations;)(a0, a1, a2);
  }-*/;
}