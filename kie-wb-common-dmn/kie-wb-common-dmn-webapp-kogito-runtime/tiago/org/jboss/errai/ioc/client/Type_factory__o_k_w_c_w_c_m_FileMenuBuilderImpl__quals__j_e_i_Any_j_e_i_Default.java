package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilderImpl;
import org.uberfire.ext.editor.commons.client.menu.HasLockSyncMenuStateHelper;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.editor.commons.version.CurrentBranch;

public class Type_factory__o_k_w_c_w_c_m_FileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FileMenuBuilderImpl> { public Type_factory__o_k_w_c_w_c_m_FileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FileMenuBuilderImpl.class, "Type_factory__o_k_w_c_w_c_m_FileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FileMenuBuilderImpl.class, Object.class, FileMenuBuilder.class, HasLockSyncMenuStateHelper.class });
  }

  public FileMenuBuilderImpl createInstance(final ContextManager contextManager) {
    final FileMenuBuilderImpl instance = new FileMenuBuilderImpl();
    setIncompleteInstance(instance);
    final BasicFileMenuBuilderImpl FileMenuBuilderImpl_menuBuilder = (BasicFileMenuBuilderImpl) contextManager.getInstance("Type_factory__o_u_e_e_c_c_m_BasicFileMenuBuilderImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, FileMenuBuilderImpl_menuBuilder);
    FileMenuBuilderImpl_BasicFileMenuBuilder_menuBuilder(instance, FileMenuBuilderImpl_menuBuilder);
    final Caller FileMenuBuilderImpl_copyService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { CopyService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, FileMenuBuilderImpl_copyService);
    FileMenuBuilderImpl_Caller_copyService(instance, FileMenuBuilderImpl_copyService);
    final CurrentBranch FileMenuBuilderImpl_currentBranch = (CurrentBranch) contextManager.getInstance("Producer_factory__o_u_e_e_c_v_CurrentBranch__quals__j_e_i_Any_o_u_a_Customizable");
    registerDependentScopedReference(instance, FileMenuBuilderImpl_currentBranch);
    FileMenuBuilderImpl_CurrentBranch_currentBranch(instance, FileMenuBuilderImpl_currentBranch);
    final Caller FileMenuBuilderImpl_deleteService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { DeleteService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, FileMenuBuilderImpl_deleteService);
    FileMenuBuilderImpl_Caller_deleteService(instance, FileMenuBuilderImpl_deleteService);
    final Caller FileMenuBuilderImpl_renameService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { RenameService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, FileMenuBuilderImpl_renameService);
    FileMenuBuilderImpl_Caller_renameService(instance, FileMenuBuilderImpl_renameService);
    setIncompleteInstance(null);
    return instance;
  }

  native static Caller FileMenuBuilderImpl_Caller_renameService(FileMenuBuilderImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::renameService;
  }-*/;

  native static void FileMenuBuilderImpl_Caller_renameService(FileMenuBuilderImpl instance, Caller<RenameService> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::renameService = value;
  }-*/;

  native static CurrentBranch FileMenuBuilderImpl_CurrentBranch_currentBranch(FileMenuBuilderImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::currentBranch;
  }-*/;

  native static void FileMenuBuilderImpl_CurrentBranch_currentBranch(FileMenuBuilderImpl instance, CurrentBranch value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::currentBranch = value;
  }-*/;

  native static Caller FileMenuBuilderImpl_Caller_copyService(FileMenuBuilderImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::copyService;
  }-*/;

  native static void FileMenuBuilderImpl_Caller_copyService(FileMenuBuilderImpl instance, Caller<CopyService> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::copyService = value;
  }-*/;

  native static BasicFileMenuBuilder FileMenuBuilderImpl_BasicFileMenuBuilder_menuBuilder(FileMenuBuilderImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::menuBuilder;
  }-*/;

  native static void FileMenuBuilderImpl_BasicFileMenuBuilder_menuBuilder(FileMenuBuilderImpl instance, BasicFileMenuBuilder value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::menuBuilder = value;
  }-*/;

  native static Caller FileMenuBuilderImpl_Caller_deleteService(FileMenuBuilderImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::deleteService;
  }-*/;

  native static void FileMenuBuilderImpl_Caller_deleteService(FileMenuBuilderImpl instance, Caller<DeleteService> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl::deleteService = value;
  }-*/;
}