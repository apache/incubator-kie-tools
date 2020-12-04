package org.jboss.errai.ioc.client;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorModelEnricher;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableEditorDefinitionEnricher;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;

public class Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinitionEnricher__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionTableEditorDefinitionEnricher> { private class Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinitionEnricher__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DecisionTableEditorDefinitionEnricher implements Proxy<DecisionTableEditorDefinitionEnricher> {
    private final ProxyHelper<DecisionTableEditorDefinitionEnricher> proxyHelper = new ProxyHelperImpl<DecisionTableEditorDefinitionEnricher>("Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinitionEnricher__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DecisionTableEditorDefinitionEnricher instance) {

    }

    public DecisionTableEditorDefinitionEnricher asBeanType() {
      return this;
    }

    public void setInstance(final DecisionTableEditorDefinitionEnricher instance) {
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

    @Override public void enrich(Optional nodeUUID, HasExpression hasExpression, Optional expression) {
      if (proxyHelper != null) {
        final DecisionTableEditorDefinitionEnricher proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enrich(nodeUUID, hasExpression, expression);
      } else {
        super.enrich(nodeUUID, hasExpression, expression);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DecisionTableEditorDefinitionEnricher proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinitionEnricher__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionTableEditorDefinitionEnricher.class, "Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinitionEnricher__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionTableEditorDefinitionEnricher.class, Object.class, ExpressionEditorModelEnricher.class });
  }

  public DecisionTableEditorDefinitionEnricher createInstance(final ContextManager contextManager) {
    final DMNGraphUtils _dmnGraphUtils_1 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionUtils _itemDefinitionUtils_2 = (ItemDefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final DecisionTableEditorDefinitionEnricher instance = new DecisionTableEditorDefinitionEnricher(_sessionManager_0, _dmnGraphUtils_1, _itemDefinitionUtils_2);
    registerDependentScopedReference(instance, _dmnGraphUtils_1);
    registerDependentScopedReference(instance, _itemDefinitionUtils_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DecisionTableEditorDefinitionEnricher> proxyImpl = new Type_factory__o_k_w_c_d_c_e_e_t_d_DecisionTableEditorDefinitionEnricher__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}