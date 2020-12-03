package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.kogito.webapp.base.shared.PreferenceScopeResolutionStrategyMock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.preferences.client.admin.page.AdminPageImpl;
import org.uberfire.ext.preferences.client.admin.page.AdminPageOptions;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;

public class Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageImpl> { private class Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AdminPageImpl implements Proxy<AdminPageImpl> {
    private final ProxyHelper<AdminPageImpl> proxyHelper = new ProxyHelperImpl<AdminPageImpl>("Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AdminPageImpl instance) {

    }

    public AdminPageImpl asBeanType() {
      return this;
    }

    public void setInstance(final AdminPageImpl instance) {
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

    @Override public void addScreen(String identifier, String title) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addScreen(identifier, title);
      } else {
        super.addScreen(identifier, title);
      }
    }

    @Override public void addTool(String screen, String title, Set iconCss, String category, Command command, ParameterizedCommand counterCommand) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addTool(screen, title, iconCss, category, command, counterCommand);
      } else {
        super.addTool(screen, title, iconCss, category, command, counterCommand);
      }
    }

    @Override public void addTool(String screen, String title, Set iconCss, String category, Command command) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addTool(screen, title, iconCss, category, command);
      } else {
        super.addTool(screen, title, iconCss, category, command);
      }
    }

    @Override public void addPreference(String screen, String identifier, String title, Set iconCss, String category, AdminPageOptions[] options) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addPreference(screen, identifier, title, iconCss, category, options);
      } else {
        super.addPreference(screen, identifier, title, iconCss, category, options);
      }
    }

    @Override public void addPreference(String screen, String identifier, String title, Set iconCss, String category, Supplier customScopeResolutionStrategySupplier, AdminPageOptions[] options) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addPreference(screen, identifier, title, iconCss, category, customScopeResolutionStrategySupplier, options);
      } else {
        super.addPreference(screen, identifier, title, iconCss, category, customScopeResolutionStrategySupplier, options);
      }
    }

    @Override public void addPreference(String screen, String identifier, String title, Set iconCss, String category, PreferenceScope preferenceScope, AdminPageOptions[] options) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addPreference(screen, identifier, title, iconCss, category, preferenceScope, options);
      } else {
        super.addPreference(screen, identifier, title, iconCss, category, preferenceScope, options);
      }
    }

    @Override public void addPreference(String screen, String identifier, String title, Set iconCss, String category, Supplier customScopeResolutionStrategySupplier, PreferenceScope preferenceScope, AdminPageOptions[] options) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addPreference(screen, identifier, title, iconCss, category, customScopeResolutionStrategySupplier, preferenceScope, options);
      } else {
        super.addPreference(screen, identifier, title, iconCss, category, customScopeResolutionStrategySupplier, preferenceScope, options);
      }
    }

    @Override public Map getToolsByCategory(String screen) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.getToolsByCategory(screen);
        return retVal;
      } else {
        return super.getToolsByCategory(screen);
      }
    }

    @Override public String getScreenTitle(String screen) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getScreenTitle(screen);
        return retVal;
      } else {
        return super.getScreenTitle(screen);
      }
    }

    @Override public String getDefaultScreen() {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDefaultScreen();
        return retVal;
      } else {
        return super.getDefaultScreen();
      }
    }

    @Override public void setDefaultScreen(String defaultScreen) {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setDefaultScreen(defaultScreen);
      } else {
        super.setDefaultScreen(defaultScreen);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AdminPageImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdminPageImpl.class, "Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdminPageImpl.class, Object.class, AdminPage.class });
  }

  public AdminPageImpl createInstance(final ContextManager contextManager) {
    final PreferenceScopeResolutionStrategy _resolutionStrategy_2 = (PreferenceScopeResolutionStrategyMock) contextManager.getInstance("Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeResolutionStrategyMock__quals__j_e_i_Any_o_u_a_Customizable");
    final UberfireBreadcrumbs _breadcrumbs_3 = (UberfireBreadcrumbs) contextManager.getInstance("Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbs__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_4 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final Event<PreferencesCentralInitializationEvent> _preferencesCentralInitializationEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PreferencesCentralInitializationEvent.class }, new Annotation[] { });
    final AdminPageImpl instance = new AdminPageImpl(_placeManager_0, _preferencesCentralInitializationEvent_1, _resolutionStrategy_2, _breadcrumbs_3, _translationService_4);
    registerDependentScopedReference(instance, _resolutionStrategy_2);
    registerDependentScopedReference(instance, _translationService_4);
    registerDependentScopedReference(instance, _preferencesCentralInitializationEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AdminPageImpl> proxyImpl = new Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}