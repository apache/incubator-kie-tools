package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemsProvider;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter.View;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorView;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.uberfire.workbench.model.Position;

public class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorPresenter> { private class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DecisionNavigatorPresenter implements Proxy<DecisionNavigatorPresenter> {
    private final ProxyHelper<DecisionNavigatorPresenter> proxyHelper = new ProxyHelperImpl<DecisionNavigatorPresenter>("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null, null, null, null);
    }

    public void initProxyProperties(final DecisionNavigatorPresenter instance) {

    }

    public DecisionNavigatorPresenter asBeanType() {
      return this;
    }

    public void setInstance(final DecisionNavigatorPresenter instance) {
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

    @Override public View getView() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        final View retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public String getTitle() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public Position getDefaultPosition() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        final Position retVal = proxiedInstance.getDefaultPosition();
        return retVal;
      } else {
        return super.getDefaultPosition();
      }
    }

    @Override public void onRefreshDecisionComponents(RefreshDecisionComponents events) {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onRefreshDecisionComponents(events);
      } else {
        super.onRefreshDecisionComponents(events);
      }
    }

    @Override public void onElementAdded(CanvasElementAddedEvent event) {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onElementAdded(event);
      } else {
        super.onElementAdded(event);
      }
    }

    @Override public DecisionNavigatorTreePresenter getTreePresenter() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        final DecisionNavigatorTreePresenter retVal = proxiedInstance.getTreePresenter();
        return retVal;
      } else {
        return super.getTreePresenter();
      }
    }

    @Override public void removeAllElements() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeAllElements();
      } else {
        super.removeAllElements();
      }
    }

    @Override public void refresh() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.refresh();
      } else {
        super.refresh();
      }
    }

    @Override public void refreshTreeView() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.refreshTreeView();
      } else {
        super.refreshTreeView();
      }
    }

    @Override public void enableRefreshHandlers() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enableRefreshHandlers();
      } else {
        super.enableRefreshHandlers();
      }
    }

    @Override public void disableRefreshHandlers() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.disableRefreshHandlers();
      } else {
        super.disableRefreshHandlers();
      }
    }

    @Override public void clearSelections() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clearSelections();
      } else {
        super.clearSelections();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DecisionNavigatorPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorPresenter.class, "Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorPresenter.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents", new AbstractCDIEventCallback<RefreshDecisionComponents>() {
      public void fireEvent(final RefreshDecisionComponents event) {
        final DecisionNavigatorPresenter instance = Factory.maybeUnwrapProxy((DecisionNavigatorPresenter) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default"));
        instance.onRefreshDecisionComponents(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent", new AbstractCDIEventCallback<CanvasElementAddedEvent>() {
      public void fireEvent(final CanvasElementAddedEvent event) {
        final DecisionNavigatorPresenter instance = Factory.maybeUnwrapProxy((DecisionNavigatorPresenter) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default"));
        instance.onElementAdded(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent []";
      }
    });
  }

  public DecisionNavigatorPresenter createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_4 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorObserver _decisionNavigatorObserver_3 = (DecisionNavigatorObserver) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DecisionNavigatorView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorView__quals__j_e_i_Any_j_e_i_Default");
    final DecisionComponents _decisionComponents_2 = (DecisionComponents) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponents__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_7 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorTreePresenter _treePresenter_1 = (DecisionNavigatorTreePresenter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreePresenter__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsContext _includedModelContext_5 = (IncludedModelsContext) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_c_IncludedModelsContext__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorItemsProvider _navigatorItemsProvider_6 = (DecisionNavigatorItemsProvider) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorItemsProvider__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorPresenter instance = new DecisionNavigatorPresenter(_view_0, _treePresenter_1, _decisionComponents_2, _decisionNavigatorObserver_3, _translationService_4, _includedModelContext_5, _navigatorItemsProvider_6, _dmnDiagramsSession_7);
    registerDependentScopedReference(instance, _translationService_4);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _decisionComponents_2);
    registerDependentScopedReference(instance, _treePresenter_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DecisionNavigatorPresenter instance) {
    DecisionNavigatorPresenter_setup(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter ([org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter$View, org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter, org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents, org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver, org.jboss.errai.ui.client.local.spi.TranslationService, org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsContext, org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItemsProvider, org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DecisionNavigatorPresenter> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void DecisionNavigatorPresenter_setup(DecisionNavigatorPresenter instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter::setup()();
  }-*/;
}