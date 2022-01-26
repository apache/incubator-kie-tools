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


import java.util.Collection;


/**
 * An interface that every command is expected to support.
 * A command can be tested for executability, 
 * it can be executed, 
 * it can be tested for undoability, 
 * it can be undone, 
 * and can then be redone.
 * A command also provides access to a result collection, an affected-objects collection,
 * a label, and a description.
 *
 * <p>
 * There are important constraints on the valid order in which the various methods may be invoked,
 * e.g., you cannot ask for the result before you've executed the command.
 * These constraints are documented with the various methods.
 */
public interface Command 
{
  /**
   * Returns whether the command is valid to <code>execute</code>.
   * The {@link UnexecutableCommand#INSTANCE}.<code>canExecute()</code> always returns <code>false</code>.
   * This <b>must</b> be called before calling <code>execute</code>.
   * @return whether the command is valid to <code>execute</code>.
   */
  boolean canExecute();

  /**
   * Performs the command activity required for the effect.
   * The effect of calling <code>execute</code> when <code>canExecute</code> returns <code>false</code>, 
   * or when <code>canExecute</code> hasn't been called, is undefined.
   */
  void execute();

  /**
   * Returns whether the command can be undone.
   * The result of calling this before <code>execute</code> is well defined,
   * but the result of calling this before calling <code>canExecute</code> is undefined, i.e.,
   * a command that returns <code>false</code> for <code>canExecute</code> may return <code>true</code> for canUndo, 
   * even though that is a contradiction.
   * @return whether the command can be undone.
   */
  boolean canUndo();

  /**
   * Performs the command activity required to <code>undo</code> the effects of a preceding <code>execute</code> (or <code>redo</code>).
   * The effect, if any, of calling <code>undo</code> before <code>execute</code> or <code>redo</code> have been called, 
   * or when canUndo returns <code>false</code>, is undefined.
   */
  void undo();

  /**
   * Performs the command activity required to <code>redo</code> the effect after undoing the effect.
   * The effect, if any, of calling <code>redo</code> before <code>undo</code> is called is undefined.
   * Note that if you implement <code>redo</code> to call <code>execute</code> 
   * then any derived class will be restricted by that decision also.
   */
  void redo();

  /**
   * Returns a collection of things which this command wishes to present as it's result.
   * The result of calling this before an <code>execute</code> or <code>redo</code>, or after an <code>undo</code>, is undefined.
   * @return a collection of things which this command wishes to present as it's result.
   */
  Collection<?> getResult();

  /**
   * Returns the collection of things which this command wishes to present as the objects affected by the command.
   * Typically should could be used as the selection that should be highlighted to best illustrate the effect of the command.
   * The result of calling this before an <code>execute</code>, <code>redo</code>, or <code>undo</code> is undefined.
   * The result may be different after an <code>undo</code> than it is after an <code>execute</code> or <code>redo</code>,
   * but the result should be the same (equivalent) after either an <code>execute</code> or <code>redo</code>.
   * @return the collection of things which this command wishes to present as the objects affected by the command.
   */
  Collection<?> getAffectedObjects();

  /**
   * Returns a string suitable to represent the label that identifies this command.
   * @return a string suitable to represent the label that identifies this command.
   */
  String getLabel();

  /**
   * Returns a string suitable to help describe the effect of this command.
   * @return a string suitable to help describe the effect of this command.
   */
  String getDescription();

  /**
   * Called to indicate that the command will never be used again.
   * Calling any other method after this one has undefined results.
   */
  void dispose();

  /**
   * Returns a command that represents the composition of this command with the given command.
   * The resulting command may just be this, if this command is capable of composition.
   * Otherwise, it will be a new command created to compose the two.
   * <p>
   * Instead of the following pattern of usage
   * <pre>
   *   Command result = x;
   *   if (condition) result = result.chain(y);
   * </pre>
   * you should consider using a {@link org.eclipse.emf.common.command.CompoundCommand} 
   * and using {@link org.eclipse.emf.common.command.CompoundCommand#unwrap()} to optimize the result:
   * <pre>
   *   CompoundCommand subcommands = new CompoundCommand();
   *   subcommands.append(x);
   *   if (condition) subcommands.append(y);
   *   Command result = subcommands.unwrap();
   * </pre>
   * This gives you more control over how the compound command composes it's result and affected objects.
   * @param command the command to chain.
   * @return a command that represents the composition of this command with the given command.
   */
  Command chain(Command command);
}
