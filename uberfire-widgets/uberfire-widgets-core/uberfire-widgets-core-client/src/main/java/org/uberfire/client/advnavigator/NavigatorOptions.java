package org.uberfire.client.advnavigator;

/**
 * Created with IntelliJ IDEA.
 * Date: 7/12/13
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NavigatorOptions {

    public static final NavigatorOptions DEFAULT = new NavigatorOptions() {
        @Override
        public boolean showFiles() {
            return true;
        }

        @Override
        public boolean showHiddenFiles() {
            return false;
        }

        @Override
        public boolean showDirectories() {
            return true;
        }

        @Override
        public boolean allowUpLink() {
            return true;
        }

        @Override
        public boolean showItemAge() {
            return true;
        }

        @Override
        public boolean showItemLastUpdater() {
            return true;
        }

        @Override
        public boolean showItemMessage() {
            return true;
        }
    };

    boolean showFiles();

    boolean showHiddenFiles();

    boolean showDirectories();

    boolean allowUpLink();

    boolean showItemAge();

    boolean showItemMessage();

    boolean showItemLastUpdater();
}
