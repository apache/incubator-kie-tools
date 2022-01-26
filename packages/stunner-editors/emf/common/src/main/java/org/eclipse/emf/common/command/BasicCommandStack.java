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


import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.WrappedException;


/**
 * A basic and obvious implementation of an undoable stack of commands.
 * See {@link Command} for more details about the command methods that this implementation uses.
 */
public class BasicCommandStack implements CommandStack 
{
  /**
   * The list of commands.
   */
  protected List<Command> commandList;

  /**
   * The current position within the list from which the next execute, undo, or redo, will be performed. 
   */
  protected int top;

  /**
   * The command most recently executed, undone, or redone.
   */
  protected Command mostRecentCommand;

  /**
   * The {@link CommandStackListener}s.
   */
  protected Collection<CommandStackListener> listeners;

  /**
   * The value of {@link #top} when {@link #saveIsDone} is called.
   */
  protected int saveIndex = -1;

  /**
   * Creates a new empty instance.
   */
  public BasicCommandStack() 
  {
    commandList = new ArrayList<Command>();
    top = -1;
    listeners = new ArrayList<CommandStackListener>();
  }

  /*
   * Javadoc copied from interface.
   */
  public void execute(Command command) 
  {
    // If the command is executable, record and execute it.
    //
    if (command != null)
    {
      if (command.canExecute())
      {
        try
        {
          command.execute();

          // Clear the list past the top.
          //
          for (Iterator<Command> commands = commandList.listIterator(top + 1); commands.hasNext(); commands.remove())
          {
            Command otherCommand = commands.next();
            otherCommand.dispose();
          }

          // Record the successfully executed command.
          //
          mostRecentCommand = command;
          commandList.add(command);
          ++top;

          // This is kind of tricky.
          // If the saveIndex was in the redo part of the command list which has now been wiped out,
          // then we can never reach a point where a save is not necessary, not even if we undo all the way back to the beginning.
          //
          if (saveIndex >= top)
          {
            // This forces isSaveNeded to always be true.
            //
            saveIndex = -2;
          }
          notifyListeners();
        }
        catch (AbortExecutionException exception)
        {
          command.dispose();
        }
        catch (RuntimeException exception)
        {
          handleError(exception);  
          mostRecentCommand = null;
          command.dispose();
          notifyListeners();
        }
      }
      else
      {
        command.dispose();
      }
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public boolean canUndo() 
  {
    return top != -1 && commandList.get(top).canUndo();
  }

  /*
   * Javadoc copied from interface.
   */
  public void undo() 
  {
    if (canUndo())
    {
      Command command = commandList.get(top--);
      try
      {
        command.undo();
        mostRecentCommand = command;
      }
      catch (RuntimeException exception)
      {
        handleError(exception);

        mostRecentCommand = null;
        flush();
      }

      notifyListeners();
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public boolean canRedo() 
  {
    return top < commandList.size() - 1;
  }

  /*
   * Javadoc copied from interface.
   */
  public void redo() 
  {
    if (canRedo())
    {
      Command command = commandList.get(++top);
      try
      {
        command.redo();
        mostRecentCommand = command;
      }
      catch (RuntimeException exception)
      {
        handleError(exception);

        mostRecentCommand = null;

        // Clear the list past the top.
        //
        for (Iterator<Command> commands = commandList.listIterator(top--); commands.hasNext(); commands.remove())
        {
          Command otherCommand = commands.next();
          otherCommand.dispose();
        }
      }

      notifyListeners();
    }
  }

  /*
   * Javadoc copied from interface.
   */
  public void flush()
  {
    // Clear the list.
    //
    for (Iterator<Command> commands = commandList.listIterator(); commands.hasNext(); commands.remove())
    {
      Command command = commands.next();
      command.dispose();
    }
    commandList.clear();
    top = -1;
    saveIndex = -1;
    mostRecentCommand = null;
    notifyListeners();
  }

  /*
   * Javadoc copied from interface.
   */
  public Command getUndoCommand()
  {
    return 
      top == -1 || top == commandList.size() ?
        null :
        (Command)commandList.get(top);
  }

  /*
   * Javadoc copied from interface.
   */
  public Command getRedoCommand()
  {
    return
      top + 1 >= commandList.size() ?
        null :
        (Command)commandList.get(top + 1);
  }

  /*
   * Javadoc copied from interface.
   */
  public Command getMostRecentCommand()
  {
    return mostRecentCommand;
  }
  
  /*
   * Javadoc copied from interface.
   */
  public void addCommandStackListener(CommandStackListener listener) 
  {
    listeners.add(listener);
  }

  /*
   * Javadoc copied from interface.
   */
  public void removeCommandStackListener(CommandStackListener listener) 
  {
    listeners.remove(listener);
  }
  
  /** 
   * This is called to ensure that {@link CommandStackListener#commandStackChanged} is called for each listener.
   */
  protected void notifyListeners()
  {
    for (CommandStackListener commandStackListener : listeners)
    {
      commandStackListener.commandStackChanged(new EventObject(this));
    }
  }

  /**
   * Handles an exception thrown during command execution by logging it with the plugin.
   */
  protected void handleError(Exception exception) 
  {
    CommonPlugin.INSTANCE.log
      (new WrappedException
         (CommonPlugin.INSTANCE.getString("_UI_IgnoreException_exception"), exception).fillInStackTrace());
  }

  /**
   * Called after a save has been successfully performed.
   */
  public void saveIsDone()
  {
    //  Remember where we are now.
    //
    saveIndex = top;
  }

  /**
   * Returns whether the model has changes since {@link #saveIsDone} was call the last.
   * @return whether the model has changes since <code>saveIsDone</code> was call the last.
   */
  public boolean isSaveNeeded()
  {
    // Only if we are at the remembered index do we NOT need to save.
    //
    //return top != saveIndex;

    if (saveIndex < -1)
    {
      return true;
    }

    if (top > saveIndex)
    {
      for (int i = top; i > saveIndex; --i)
      {
        if (!(commandList.get(i) instanceof AbstractCommand.NonDirtying))
        {
          return true;
        }
      }
    }
    else
    {
      for (int i = saveIndex; i > top; --i)
      {
        if (!(commandList.get(i) instanceof AbstractCommand.NonDirtying))
        {
          return true;
        }
      }
    }

    return false;
  }
}
