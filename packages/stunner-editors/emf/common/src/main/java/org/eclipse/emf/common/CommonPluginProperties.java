/**
 * Copyright (c) 2002-2010 Ed Merks and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Ed Merks - Initial API and implementation
 */
package org.eclipse.emf.common;

import org.gwtproject.i18n.client.Messages;

public interface CommonPluginProperties extends Messages
{
  @Key("_EXC_Method_not_implemented")
  @DefaultMessage("The method {0} is not implemented")
  String excMethodNotImplemented(Object substitution);

  @Key("_UI_AbstractCommand_label")
  @DefaultMessage("Do Command")
  String abstractCommandLabel();

  @Key("_UI_AbstractCommand_description")
  @DefaultMessage("Execute a command")
  String abstractCommandDescription();

  @Key("_UI_CommandWrapper_label")
  @DefaultMessage("Wrapper")
  String commandWrapperLabel();

  @Key("_UI_CommandWrapper_description")
  @DefaultMessage("Execute an empty command wrapper")
  String commandWrapperDescription();

  @Key("_UI_CompoundCommand_label")
  @DefaultMessage("Do Commands")
  String compoundCommandLabel();

  @Key("_UI_CompoundCommand_description")
  @DefaultMessage("Execute a series of commands")
  String compoundCommandDescription();

  @Key("_UI_IdentityCommand_label")
  @DefaultMessage("Identity")
  String identityCommandLabel();

  @Key("_UI_IdentityCommand_description")
  @DefaultMessage("Execute nothing but yield a result")
  String identityCommandDescription();

  @Key("_UI_UnexecutableCommand_label")
  @DefaultMessage("Do Nothing")
  String unexecutableCommandLabel();

  @Key("_UI_UnexecutableCommand_description")
  @DefaultMessage("Execute nothing whatsoever")
  String unexecutableCommandDescription();

  @Key("_UI_IgnoreException_exception")
  @DefaultMessage("An exception was ignored during command execution")
  String ignoreExceptionException();

  @Key("_UI_NullLogEntry_exception")
  @DefaultMessage("A null log entry was logged")
  String nullLogEntryException();

  @Key("_UI_StringResourceNotFound_exception")
  @DefaultMessage("The string resource ''{0}'' could not be located")
  String stringResourceNotFoundException(Object substitution);

  @Key("_UI_ImageResourceNotFound_exception")
  @DefaultMessage("The image resource ''{0}'' could not be located")
  String imageResourceNotFoundException(Object substitution);

  @Key("_UI_OK_diagnostic_0")
  @DefaultMessage("OK")
  String okDiagnostic0();

  @Key("_UI_Cancel_diagnostic_0")
  @DefaultMessage("Cancel")
  String cancelDiagnostic0();
}
