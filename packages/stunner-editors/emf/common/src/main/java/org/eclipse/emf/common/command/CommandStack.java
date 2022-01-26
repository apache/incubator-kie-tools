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



/**
 * A simple and obvious interface for an undoable stack of commands with a listener.
 * See {@link Command} for more details about the command methods that this implementation uses
 * and {@link CommandStackListener} for details about the listener.
 */
public interface CommandStack 
{
  /**
   * Clears any redoable commands not yet redone, adds the command, and then executes the command.
   * @param command the command to execute.
   */
  void execute(Command command); 

  /**
   * Returns whether the top command on the stack can be undone.
   * @return whether the top command on the stack can be undone.
   */
  boolean canUndo();

  /**
   * Moves the top of the stack down, undoing what was formerly the top command.
   */
  void undo();

  /**
   * Returns whether there are commands past the top of the stack that can be redone.
   * @return whether there are commands past the top of the stack that can be redone.
   */
  boolean canRedo(); 

  /**
   * Returns the command that will be undone if {@link #undo} is called.
   * @return the command that will be undone if {@link #undo} is called.
   */
  public Command getUndoCommand();
  
  /**
   * Returns the command that will be redone if {@link #redo} is called.
   * @return the command that will be redone if {@link #redo} is called.
   */
  public Command getRedoCommand();
  
  /**
   * Returns the command most recently executed, undone, or redone.
   * @return the command most recently executed, undone, or redone.
   */
  public Command getMostRecentCommand();

  /**
   * Moves the top of the stack up, redoing the new top command.
   */
  void redo();

  /**
   * Disposes all the commands in the stack.
   */
  void flush();

  /**
   * Adds a listener to the command stack, which will be notified whenever a command has been processed on the stack.
   * @param listener the listener to add.
   */
  void addCommandStackListener(CommandStackListener listener);

  /**
   * Removes a listener from the command stack.
   * @param listener the listener to remove.
   */
  void removeCommandStackListener(CommandStackListener listener);
}
