package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.history.SaveButton;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.workbench.model.menu.HasEnabledStateChangeListeners;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuItem;

public class Type_factory__o_u_e_e_c_c_h_SaveButton__quals__j_e_i_Any_j_e_i_Default extends Factory<SaveButton> { public Type_factory__o_u_e_e_c_c_h_SaveButton__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SaveButton.class, "Type_factory__o_u_e_e_c_c_h_SaveButton__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SaveButton.class, Object.class, MenuCustom.class, MenuItem.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, HasEnabledStateChangeListeners.class });
  }

  public SaveButton createInstance(final ContextManager contextManager) {
    final SaveButton instance = new SaveButton();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}