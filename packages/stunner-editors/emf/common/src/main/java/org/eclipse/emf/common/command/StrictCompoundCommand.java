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


import java.util.List;
import java.util.ListIterator;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.WrappedException;


/**
 * A composite command which assumes that later commands in the list 
 * may depend on the results and side-effects of earlier commands in the list.
 * Because of this, it must implement {@link Command#canExecute} more carefully,
 * i.e., in order to determine canExecute for the composite, it doesn't simply test each command.
 * It tests the first command to see if it can execute;
 * then, if there is another command in the list, it checks if the first command can undo and then goes ahead and executes it!
 * This process is repeated until the last command that is not followed by another, which then determines the final result.
 * (For efficiency, when this processing gets to the last command, that command is tested for canUndo too and that result is cached.)
 * All the commands that have been executed are then undone, if {@link #isPessimistic} is <code>true</code>; 
 * by default it's <code>false</code>.
 *
 * <p>
 * It is important for all but the last command to have no visible side-effect!
 * Multiple commands with visible side-effects must be composed into a single command using just a {@link CompoundCommand}
 * and that composite could be the last command of a strict composite.
 *
 * <p>
 * Here is an example of how this can be used in conjunction with a {@link CommandWrapper}.
 * <pre>
 *   Command strictCompoundCommand = new StrictCompoundCommand();
 *   Command copyCommand = new CopyCommand(...);
 *   strictCompoundCommand.add(copyCommand);
 *
 *   Command addCommand =
 *     new CommandWrapper()
 *     {
 *       public Command createCommand()
 *       {
 *         new AddCommand(parent, copyCommand.getResult()); 
 *       }
 *     };
 *   strictCompoundCommand.append(addCommand);
 * </pre>
 * Here the add command won't know which command to create until it has the result of the copy command.
 * The proxy makes sure the creation of the add command is deferred and the strict composite ensures that execution dependencies are met.
 */
public class StrictCompoundCommand extends CompoundCommand 
{
  /**
   * The result for {@link Command#canUndo}.
   */
  protected boolean isUndoable;

  /**
   * Whether commands that have been tentatively executed need to be undone.
   */
  protected boolean isPessimistic;

  /**
   * Remember to call redo instead of execute for any command at or before this index in the list.
   */
  protected int rightMostExecutedCommandIndex = -1;

  /**
   * Creates an empty instance.
   */
  public StrictCompoundCommand()
  {
    super();
    resultIndex = LAST_COMMAND_ALL;
  }

  /**
   * Creates an instance with the given label.
   * @param label the label.
   */
  public StrictCompoundCommand(String label)
  {
    super(label);
    resultIndex = LAST_COMMAND_ALL;
  }

  /**
   * Creates an instance with the given label and description.
   * @param label the label.
   * @param description the description.
   */
  public StrictCompoundCommand(String label, String description)
  {
    super(label, description);
    resultIndex = LAST_COMMAND_ALL;
  }

  /**
   * Creates an instance with the given command list.
   * @param commandList the list of commands.
   */
  public StrictCompoundCommand(List<Command> commandList)
  {     
    super(commandList);
    resultIndex = LAST_COMMAND_ALL;
  }

  /**
   * Creates an instance with the given label and command list.
   * @param label the label.
   * @param commandList the list of commands.
   */
  public StrictCompoundCommand(String label, List<Command> commandList)
  {     
    super(label, commandList);
    resultIndex = LAST_COMMAND_ALL;
  }

  /**
   * Creates an instance with the given label, description, and command list.
   * @param label the label.
   * @param description the description.
   * @param commandList the list of commands.
   */
  public StrictCompoundCommand(String label, String description, List<Command> commandList)
  {     
    super(label, description, commandList);
    resultIndex = LAST_COMMAND_ALL;
  }

  /**
   * Returns <code>false</code> if any command on the list returns <code>false</code> for {@link Command#canExecute},
   * or if some command before the last one can't be undone and hence we can't test all the commands for executability.
   * @return whether the command can execute.
   */
  @Override
  protected boolean prepare() 
  {
    // Go through the commands of the list.
    //
    ListIterator<Command> commands = commandList.listIterator(); 

    // If there are some...
    //
    if (commands.hasNext())
    {
      boolean result = true;

      // The termination guard is in the body.
      //
      for (;;)
      {
        Command command = commands.next();
        if (command.canExecute())
        {
          if (commands.hasNext())
          {
            if (command.canUndo())
            {
              try
              {
                if (commands.previousIndex() <= rightMostExecutedCommandIndex)
                {
                  command.redo();
                }
                else
                {
                  ++rightMostExecutedCommandIndex;
                  command.execute();
                }
              }
              catch (RuntimeException exception)
              {
                CommonPlugin.INSTANCE.log
                  (new WrappedException
                    (CommonPlugin.INSTANCE.getString("_UI_IgnoreException_exception"), exception).fillInStackTrace());

                result = false;
                break;
              }
            }
            else
            {
              // We can't undo it, so we'd better give up.
              //
              result = false;
              break;
            }
          }
          else
          {
            // Now is the best time to record isUndoable because later we would have to do all the executes again!
            // This makes canUndo very simple!
            //
            isUndoable = command.canUndo();
            break;
          }
        }
        else
        {
          // If we can't execute this one, we just can't do it at all.
          //
          result = false;
          break;
        }
      }

      // If we are pessimistic, then we need to undo all the commands that we have executed so far.
      //
      if (isPessimistic)
      {
        // The most recently processed command will never have been executed.
        //
        commands.previous();
  
        // We want to unroll all the effects of the previous commands.
        //
        while (commands.hasPrevious()) 
        {
          Command command = commands.previous();
          command.undo();
        }
      }

      return result;
    }
    else
    {
      isUndoable = false;
      return false;
    }
  }

  /**
   * Calls {@link Command#execute} for each command in the list, 
   * but makes sure to call redo for any commands that were previously executed to compute canExecute.
   * In the case that {@link #isPessimistic} is false, only the last command will be executed
   * since the others will have been executed but not undone during {@link #prepare}.
   */
  @Override
  public void execute() 
  {
    if (isPessimistic)
    {
      for (ListIterator<Command> commands = commandList.listIterator(); commands.hasNext(); ) 
      {
        try
        {
          // Either execute or redo the command, as appropriate.
          //
          Command command = commands.next();
          if (commands.previousIndex() <= rightMostExecutedCommandIndex)
          {
            command.redo();
          }
          else
          {
            command.execute();
          }
        }
        catch (RuntimeException exception)
        {
          // Skip over the command that threw the exception.
          //
          commands.previous();

          // Iterate back over the executed commands to undo them.
          //
          while (commands.hasPrevious())
          {
            commands.previous();
            Command command = commands.previous();
            if (command.canUndo())
            {
              command.undo();
            }
            else
            {
              break;
            }
          }

          throw exception;
        }
      }
    }
    else if (!commandList.isEmpty())
    {
      Command command = commandList.get(commandList.size() - 1);
      command.execute();
    }
  }

  /**
   * Calls {@link Command#undo} for each command in the list.
   * In the case that {@link #isPessimistic} is false, only the last command will be undone
   * since the others will have been executed and not undo during {@link #prepare}.
   */
  @Override
  public void undo() 
  {
    if (isPessimistic)
    {
      super.undo();
    }
    else if (!commandList.isEmpty())
    {
      Command command = commandList.get(commandList.size() - 1);
      command.undo();
    }
  }

  /**
   * Calls {@link Command#redo} for each command in the list.
   * In the case that {@link #isPessimistic} is false, only the last command will be redone
   * since the others will have been executed and not undo during {@link #prepare}.
   */
  @Override
  public void redo() 
  {
    if (isPessimistic)
    {
      super.redo();
    }
    else if (!commandList.isEmpty())
    {
      Command command = commandList.get(commandList.size() - 1);
      command.redo();
    }
  }

  /**
   * Checks if the command can execute; 
   * if so, it is executed, appended to the list, and <code>true</code> is returned,
   * if not, it is just disposed and <code>false</code> is returned.
   * A typical use for this is to execute commands created during the execution of another command, e.g.,
   * <pre>
   *   class MyCommand extends AbstractCommand
   *   {
   *     protected Command subcommand;
   *
   *     //...
   *
   *     public void execute()
   *     {
   *       // ...
   *       StrictCompoundCommand subcommands = new StrictCompoundCommand();
   *       subcommands.appendAndExecute(new AddCommand(...));
   *       if (condition) subcommands.appendAndExecute(new AddCommand(...));
   *       subcommand = subcommands.unwrap();
   *     }
   *
   *     public void undo()
   *     {
   *       // ...
   *       subcommand.undo();
   *     }
   *
   *     public void redo()
   *     {
   *       // ...
   *       subcommand.redo();
   *     }
   *
   *     public void dispose()
   *     {
   *       // ...
   *       if (subcommand != null)
   *      {
   *         subcommand.dispose();
   *       }
   *     }
   *   }
   * </pre>
   * @return whether the command was successfully executed and appended.
   */
  @Override
  public boolean appendAndExecute(Command command)
  {
    if (command != null)
    {
      if (!isPrepared)
      {
        if (commandList.isEmpty())
        {
          isPrepared = true;
          isExecutable = true;
        }
        else
        {
          isExecutable = prepare();
          isPrepared = true;
          isPessimistic = true;
          if (isExecutable)
          {
            execute();
          }
        }
      }
  
      if (command.canExecute())
      {
        try
        {
          command.execute();
          commandList.add(command);
          ++rightMostExecutedCommandIndex;
          isUndoable = command.canUndo();
          return true;
        }
        catch (RuntimeException exception)
        {
          CommonPlugin.INSTANCE.log
            (new WrappedException
              (CommonPlugin.INSTANCE.getString("_UI_IgnoreException_exception"), exception).fillInStackTrace());
        }
      }
  
      command.dispose();
    }

    return false;
  }

  /*
   * Javadoc copied from base class.
   */
  @Override
  public String toString()
  {
    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (isUndoable: " + isUndoable + ")");
    result.append(" (isPessimistic: " + isPessimistic + ")");
    result.append(" (rightMostExecutedCommandIndex: " + rightMostExecutedCommandIndex + ")");

    return result.toString();
  }
}
