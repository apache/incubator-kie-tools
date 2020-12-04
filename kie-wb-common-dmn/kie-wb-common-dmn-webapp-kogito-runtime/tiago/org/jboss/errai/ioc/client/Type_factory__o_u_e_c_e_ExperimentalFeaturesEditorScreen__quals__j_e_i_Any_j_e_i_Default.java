package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreen;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenView;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenView.Presenter;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenViewImpl;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroup;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.client.service.impl.CDIClientFeatureDefRegistry;
import org.uberfire.experimental.client.service.impl.ClientExperimentalFeaturesRegistryServiceImpl;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.editor.EditableExperimentalFeature;
import org.uberfire.experimental.service.editor.FeaturesEditorService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesEditorScreen> { private class Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ExperimentalFeaturesEditorScreen implements Proxy<ExperimentalFeaturesEditorScreen> {
    private final ProxyHelper<ExperimentalFeaturesEditorScreen> proxyHelper = new ProxyHelperImpl<ExperimentalFeaturesEditorScreen>("Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null, null, null, null);
    }

    public void initProxyProperties(final ExperimentalFeaturesEditorScreen instance) {

    }

    public ExperimentalFeaturesEditorScreen asBeanType() {
      return this;
    }

    public void setInstance(final ExperimentalFeaturesEditorScreen instance) {
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
        final ExperimentalFeaturesEditorScreen proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public void show() {
      if (proxyHelper != null) {
        final ExperimentalFeaturesEditorScreen proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show();
      } else {
        super.show();
      }
    }

    @Override protected void doSave(EditableExperimentalFeature feature) {
      if (proxyHelper != null) {
        final ExperimentalFeaturesEditorScreen proxiedInstance = proxyHelper.getInstance(this);
        ExperimentalFeaturesEditorScreen_doSave_EditableExperimentalFeature(proxiedInstance, feature);
      } else {
        super.doSave(feature);
      }
    }

    @Override public String getTitle() {
      if (proxyHelper != null) {
        final ExperimentalFeaturesEditorScreen proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public ExperimentalFeaturesEditorScreenView getView() {
      if (proxyHelper != null) {
        final ExperimentalFeaturesEditorScreen proxiedInstance = proxyHelper.getInstance(this);
        final ExperimentalFeaturesEditorScreenView retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final ExperimentalFeaturesEditorScreen proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ExperimentalFeaturesEditorScreen proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExperimentalFeaturesEditorScreen.class, "Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeaturesEditorScreen.class, Object.class, Presenter.class });
  }

  public ExperimentalFeaturesEditorScreen createInstance(final ContextManager contextManager) {
    final Caller<FeaturesEditorService> _editorService_5 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { FeaturesEditorService.class }, new Annotation[] { });
    final ExperimentalFeaturesEditorScreenView _view_3 = (ExperimentalFeaturesEditorScreenViewImpl) contextManager.getInstance("Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final AuthorizationManager _authorizationManager_7 = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<ExperimentalFeaturesGroup> _groupsInstance_4 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ExperimentalFeaturesGroup.class }, new Annotation[] { });
    final ExperimentalFeatureDefRegistry _defRegistry_2 = (CDIClientFeatureDefRegistry) contextManager.getInstance("Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default");
    final SessionInfo _sessionInfo_6 = (SessionInfo) contextManager.getInstance("Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default");
    final ClientExperimentalFeaturesRegistryService _registryService_1 = (ClientExperimentalFeaturesRegistryServiceImpl) contextManager.getInstance("Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalFeaturesEditorScreen instance = new ExperimentalFeaturesEditorScreen(_translationService_0, _registryService_1, _defRegistry_2, _view_3, _groupsInstance_4, _editorService_5, _sessionInfo_6, _authorizationManager_7);
    registerDependentScopedReference(instance, _editorService_5);
    registerDependentScopedReference(instance, _view_3);
    registerDependentScopedReference(instance, _translationService_0);
    registerDependentScopedReference(instance, _groupsInstance_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ExperimentalFeaturesEditorScreen) instance, contextManager);
  }

  public void destroyInstanceHelper(final ExperimentalFeaturesEditorScreen instance, final ContextManager contextManager) {
    instance.clear();
  }

  public void invokePostConstructs(final ExperimentalFeaturesEditorScreen instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreen an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreen ([org.jboss.errai.ui.client.local.spi.TranslationService, org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService, org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry, org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenView, org.jboss.errai.ioc.client.api.ManagedInstance, org.jboss.errai.common.client.api.Caller, org.uberfire.rpc.SessionInfo, org.uberfire.security.authz.AuthorizationManager])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ExperimentalFeaturesEditorScreen> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ExperimentalFeaturesEditorScreen_doSave_EditableExperimentalFeature(ExperimentalFeaturesEditorScreen instance, EditableExperimentalFeature a0) /*-{
    instance.@org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreen::doSave(Lorg/uberfire/experimental/service/editor/EditableExperimentalFeature;)(a0);
  }-*/;
}