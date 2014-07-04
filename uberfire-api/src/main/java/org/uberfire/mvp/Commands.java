package org.uberfire.mvp;

/**
 * A collection of generic operations that can be used anywhere a {@link Command} is called for.
 */
public class Commands {

    /**
     * Has no effect when executed.
     */
    public static final Command DO_NOTHING = new Command() {
        @Override
        public void execute() {}
    };

}
