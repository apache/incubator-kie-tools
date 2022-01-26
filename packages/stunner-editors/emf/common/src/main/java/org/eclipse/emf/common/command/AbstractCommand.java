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
import java.util.Collections;

import org.eclipse.emf.common.CommonPlugin;


/**
 * An abstract implementation of a basic command.
 * Each derived class <bold>must</bold> implement {@link Command#execute} and {@link Command#redo}, 
 * <bold>must</bold> either implement {@link #undo} or implement {@link #canUndo} to return false,
 * and <bold>must</bold> either override {@link #prepare} (this is the preferred approach) or can override {@link #canExecute} directly.
 *
 * <p>
 * It is very convenient to use prepare, as it is guaranteed to be called only once just before canExecute is to be tested.
 * It can be implemented to create any additional commands that need to be executed, 
 * and the result it yields becomes the permanent cached return value for canExecute.
 *
 */
public abstract class AbstractCommand implements Command 
{
  /**
   * Keeps track of whether prepare needs to be called.
   * It is tested in {@link #canExecute} so that {@link #prepare} is called exactly once to ready the command for execution.
   */ 
  protected boolean isPrepared;

  /**
   * Keeps track of whether the command is executable.
   * It is set in {@link #canExecute} to the result of calling {@link #prepare}.
   */
  protected boolean isExecutable;

  /**
   * Holds a short textual description of the command 
   * as returned by {@link #getDescription} and set by {@link #setDescription}.
   */
  protected String description;

  /**
   * Holds the label of the command as returned by {@link #getLabel} and set by {@link #setLabel}.
   */
  protected String label;
        
  /**
   * Creates an empty instance.
   */
  protected AbstractCommand()
  {
    super();
  }

  /**
   * Creates an instance with the given label.
   * @param label the label.
   */
  protected AbstractCommand(String label) 
  {
    this.label = label;
  }
  
  /**
   * Creates and instance with the given label and description.
   * @param label the label.
   * @param description the description.
   */
  protected AbstractCommand(String label, String description) 
  {
    this.label = label;
    this.description = description;
  }

  /**
   * Called at most once in {@link #canExecute} to give the command an opportunity to ready itself for execution.
   * The returned value is stored in {@link #canExecute}.
   * In other words, you can override this method to initialize 
   * and to yield a cached value for the all subsequent calls to canExecute.
   * @return whether the command is executable.
   */
  protected boolean prepare()
  {
    return false;
  }

  /**
   * Calls {@link #prepare}, 
   * caches the result in {@link #isExecutable}, 
   * and sets {@link #isPrepared} to <code>true</code>; 
   * from then on, it will yield the value of isExecutable.
   * @return whether the command can execute.
   */
  public boolean canExecute() 
  {
    if (!isPrepared)
    {
      isExecutable = prepare();
      isPrepared = true;
    }

    return isExecutable;
  }

  /**
   * Returns <code>true</code> because most command should be undoable.
   * @return <code>true</code>.
   */
  public boolean canUndo() 
  {
    return true;
  }

  /**
   * Throws a runtime exception.
   * @exception UnsupportedOperationException always.
   */
  public void undo() 
  {
    throw 
      new UnsupportedOperationException
        (CommonPlugin.INSTANCE.getString
           ("_EXC_Method_not_implemented", new String [] { this.getClass().getName() + ".undo()" }));
  }

  /**
   * Returns an empty list.
   * @return an empty list.
   */
  public Collection<?> getResult()
  {
    return Collections.EMPTY_LIST;
  }

  /**
   * Returns an empty list.
   * @return an empty list.
   */
  public Collection<?> getAffectedObjects()
  {
    return Collections.EMPTY_LIST;
  }

  /*
   * Javadoc copied from interface.
   */
  public String getLabel()
  {
    return label == null ? CommonPlugin.INSTANCE.getString("_UI_AbstractCommand_label") : label;
  }

  /**
   * Sets the label after construction.
   * @param label the new label.
   */
  public void setLabel(String label)
  {
    this.label = label;
  }
  
  /*
   * Javadoc copied from interface.
   */
  public String getDescription()
  {
    return description == null ? CommonPlugin.INSTANCE.getString("_UI_AbstractCommand_description") : description;
  }
        
  /**
   * Sets the description after construction.
   * @param description the new description.
   */
  public void setDescription(String description)
  {
    this.description = description;     
  }
  
  /**
   * Creates a new compound command, containing this command and the given command,
   * that delegates chain to {@link CompoundCommand#append}.
   * @param command the command to chain with this one.
   * @return a new chained compound command.
   */
  public Command chain(Command command)
  {
    class ChainedCompoundCommand extends CompoundCommand
    {
      public ChainedCompoundCommand()
      {
        super();
      }

      @Override
      public Command chain(Command c)
      {
        append(c);
        return this;
      }
    }

    CompoundCommand result = new ChainedCompoundCommand();
    result.append(this);
    result.append(command);
    return result;
  }

  /*
   * Javadoc copied from interface.
   */
  public void dispose()
  {
    // Do nothing.
  }
  
  /**
   * Returns an abbreviated name using this object's own class' name, without package qualification,
   * followed by a space separated list of <tt>field:value</tt> pairs.
   * @return string representation.
   */
  @Override
  public String toString()
  {
    String className = getClass().getName();
    int lastDotIndex = className.lastIndexOf('.'); 
    StringBuffer result = new StringBuffer(lastDotIndex == -1 ?  className : className.substring(lastDotIndex + 1));
    result.append(" (label: " + label + ")");
    result.append(" (description: " + description + ")");
    result.append(" (isPrepared: " + isPrepared + ")");
    result.append(" (isExecutable: " + isExecutable + ")");

    return result.toString();
  }

  /**
   * A marker interface implemented by commands that don't dirty the model.
   */
  public static interface NonDirtying
  {
    // This is just a marker interface.
  }
}
