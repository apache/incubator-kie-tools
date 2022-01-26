/**
 * Copyright (c) 2002-2012 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common;


import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;

/**
 * The <b>Plugin</b> for the model EMF.Common library.
 * EMF must run 
 * within an Eclipse workbench,
 * within a headless Eclipse workspace,
 * or just stand-alone as part of some other application.
 * To support this, all resource access should be directed to the resource locator,
 * which can redirect the service as appropriate to the runtime.
 * During stand-alone invocation no plugin initialization takes place.
 * In this case, common.resources.jar must be on the CLASSPATH.
 * @see #INSTANCE
 */
public final class CommonPlugin extends EMFPlugin 
{
  /**
   * The singleton instance of the plugin.
   */
  public static final CommonPlugin INSTANCE = new CommonPlugin();

  /**
   * Creates the singleton instance.
   */
  private CommonPlugin()
  {
    super(new ResourceLocator[] {});
  }

  @Override
  public ResourceLocator getPluginResourceLocator()
  {
    return null;
  }

  protected static final CommonPluginProperties PROPERTIES = null;

  @Override
  public String getString(String key, boolean translate)
  {
    if ("_UI_AbstractCommand_label".equals(key)) return PROPERTIES.abstractCommandLabel();
    else if ("_UI_AbstractCommand_description".equals(key)) return PROPERTIES.abstractCommandDescription();
    else if ("_UI_CommandWrapper_label".equals(key)) return PROPERTIES.commandWrapperLabel();
    else if ("_UI_CommandWrapper_description".equals(key)) return PROPERTIES.commandWrapperDescription();
    else if ("_UI_CompoundCommand_label".equals(key)) return PROPERTIES.compoundCommandLabel();
    else if ("_UI_CompoundCommand_description".equals(key)) return PROPERTIES.compoundCommandDescription();
    else if ("_UI_IdentityCommand_label".equals(key)) return PROPERTIES.identityCommandLabel();
    else if ("_UI_IdentityCommand_description".equals(key)) return PROPERTIES.identityCommandDescription();
    else if ("_UI_UnexecutableCommand_label".equals(key)) return PROPERTIES.unexecutableCommandLabel();
    else if ("_UI_UnexecutableCommand_description".equals(key)) return PROPERTIES.unexecutableCommandDescription();
    else if ("_UI_IgnoreException_exception".equals(key)) return PROPERTIES.ignoreExceptionException();
    else if ("_UI_NullLogEntry_exception".equals(key)) return PROPERTIES.nullLogEntryException();
    else if ("_UI_OK_diagnostic_0".equals(key)) return PROPERTIES.okDiagnostic0();
    else if ("_UI_Cancel_diagnostic_0".equals(key)) return PROPERTIES.cancelDiagnostic0();
    else return key;
  }

  @Override
  public String getString(String key, Object [] substitutions, boolean translate)
  {
    if ("_EXC_Method_not_implemented".equals(key)) return PROPERTIES.excMethodNotImplemented(substitutions[0]);
    else if ("_UI_StringResourceNotFound_exception".equals(key)) return PROPERTIES.stringResourceNotFoundException(substitutions[0]);
    else if ("_UI_ImageResourceNotFound_exception".equals(key)) return PROPERTIES.imageResourceNotFoundException(substitutions[0]);
    else return key;
  }

  /**
   * Use the platform, if available, to convert to a local URI.
   */
  public static URI asLocalURI(URI uri)
  {
    return uri;
  }

  /**
   * Use the platform, if available, to resolve the URI.
   */
  public static URI resolve(URI uri)
  {
    return uri;
  }
}
