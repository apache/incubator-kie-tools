/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 */
package org.eclipse.emf.common.command;


import org.eclipse.emf.common.CommonPlugin;


/**
 * A singleton {@link UnexecutableCommand#INSTANCE} that cannot execute.
 */
public class UnexecutableCommand extends AbstractCommand 
{
  /**
   * The one instance of this object.
   */
  public static final UnexecutableCommand INSTANCE = new UnexecutableCommand();

  /**
   * Only one private instance is created.
   */
  private UnexecutableCommand() 
  {
    super
      (CommonPlugin.INSTANCE.getString("_UI_UnexecutableCommand_label"), 
       CommonPlugin.INSTANCE.getString("_UI_UnexecutableCommand_description"));
  }

  /**
   * Returns <code>false</code>.
   * @return <code>false</code>.
   */
  @Override
  public boolean canExecute() 
  {
    return false;
  }

  /**
   * Throws an exception if it should ever be called.
   * @exception UnsupportedOperationException always.
   */
  public void execute() 
  {
    throw 
      new UnsupportedOperationException
        (CommonPlugin.INSTANCE.getString("_EXC_Method_not_implemented", new String [] { this.getClass().getName() + ".execute()" }));
  }

  /**
   * Returns <code>false</code>.
   * @return <code>false</code>.
   */
  @Override
  public boolean canUndo() 
  {
    return false;
  }

  /**
   * Throws an exception if it should ever be called.
   * @exception UnsupportedOperationException always.
   */
  public void redo() 
  {
    throw 
      new UnsupportedOperationException
        (CommonPlugin.INSTANCE.getString("_EXC_Method_not_implemented", new String [] { this.getClass().getName() + ".redo()" }));
  }
}
