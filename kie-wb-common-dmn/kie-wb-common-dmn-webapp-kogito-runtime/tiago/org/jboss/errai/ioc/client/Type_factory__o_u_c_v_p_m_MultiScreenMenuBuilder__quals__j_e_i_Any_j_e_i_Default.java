package org.jboss.errai.ioc.client;

import elemental2.dom.HTMLDocument;
import java.lang.annotation.Annotation;
import java.util.function.Function;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.KebabMenu;
import org.uberfire.client.views.pfly.widgets.KebabMenuItem;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_c_v_p_m_MultiScreenMenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiScreenMenuBuilder> { public Type_factory__o_u_c_v_p_m_MultiScreenMenuBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultiScreenMenuBuilder.class, "Type_factory__o_u_c_v_p_m_MultiScreenMenuBuilder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultiScreenMenuBuilder.class, Object.class, Function.class });
  }

  public MultiScreenMenuBuilder createInstance(final ContextManager contextManager) {
    final MultiScreenMenuBuilder instance = new MultiScreenMenuBuilder();
    setIncompleteInstance(instance);
    final DefaultAuthorizationManager MultiScreenMenuBuilder_authManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    MultiScreenMenuBuilder_AuthorizationManager_authManager(instance, MultiScreenMenuBuilder_authManager);
    final ManagedInstance MultiScreenMenuBuilder_kebabMenuItems = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { KebabMenuItem.class }, new Annotation[] { });
    registerDependentScopedReference(instance, MultiScreenMenuBuilder_kebabMenuItems);
    MultiScreenMenuBuilder_ManagedInstance_kebabMenuItems(instance, MultiScreenMenuBuilder_kebabMenuItems);
    final HTMLDocument MultiScreenMenuBuilder_document = (HTMLDocument) contextManager.getInstance("Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MultiScreenMenuBuilder_document);
    MultiScreenMenuBuilder_HTMLDocument_document(instance, MultiScreenMenuBuilder_document);
    final ManagedInstance MultiScreenMenuBuilder_kebabMenus = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { KebabMenu.class }, new Annotation[] { });
    registerDependentScopedReference(instance, MultiScreenMenuBuilder_kebabMenus);
    MultiScreenMenuBuilder_ManagedInstance_kebabMenus(instance, MultiScreenMenuBuilder_kebabMenus);
    final ManagedInstance MultiScreenMenuBuilder_buttons = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { Button.class }, new Annotation[] { });
    registerDependentScopedReference(instance, MultiScreenMenuBuilder_buttons);
    MultiScreenMenuBuilder_ManagedInstance_buttons(instance, MultiScreenMenuBuilder_buttons);
    final User MultiScreenMenuBuilder_identity = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MultiScreenMenuBuilder_identity);
    MultiScreenMenuBuilder_User_identity(instance, MultiScreenMenuBuilder_identity);
    setIncompleteInstance(null);
    return instance;
  }

  native static ManagedInstance MultiScreenMenuBuilder_ManagedInstance_kebabMenus(MultiScreenMenuBuilder instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::kebabMenus;
  }-*/;

  native static void MultiScreenMenuBuilder_ManagedInstance_kebabMenus(MultiScreenMenuBuilder instance, ManagedInstance<KebabMenu> value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::kebabMenus = value;
  }-*/;

  native static HTMLDocument MultiScreenMenuBuilder_HTMLDocument_document(MultiScreenMenuBuilder instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::document;
  }-*/;

  native static void MultiScreenMenuBuilder_HTMLDocument_document(MultiScreenMenuBuilder instance, HTMLDocument value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::document = value;
  }-*/;

  native static ManagedInstance MultiScreenMenuBuilder_ManagedInstance_buttons(MultiScreenMenuBuilder instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::buttons;
  }-*/;

  native static void MultiScreenMenuBuilder_ManagedInstance_buttons(MultiScreenMenuBuilder instance, ManagedInstance<Button> value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::buttons = value;
  }-*/;

  native static User MultiScreenMenuBuilder_User_identity(MultiScreenMenuBuilder instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::identity;
  }-*/;

  native static void MultiScreenMenuBuilder_User_identity(MultiScreenMenuBuilder instance, User value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::identity = value;
  }-*/;

  native static AuthorizationManager MultiScreenMenuBuilder_AuthorizationManager_authManager(MultiScreenMenuBuilder instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::authManager;
  }-*/;

  native static void MultiScreenMenuBuilder_AuthorizationManager_authManager(MultiScreenMenuBuilder instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::authManager = value;
  }-*/;

  native static ManagedInstance MultiScreenMenuBuilder_ManagedInstance_kebabMenuItems(MultiScreenMenuBuilder instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::kebabMenuItems;
  }-*/;

  native static void MultiScreenMenuBuilder_ManagedInstance_kebabMenuItems(MultiScreenMenuBuilder instance, ManagedInstance<KebabMenuItem> value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder::kebabMenuItems = value;
  }-*/;
}