package org.uberfire.client.navigator;

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
        public boolean showDirectories() {
            return true;
        }

        @Override
        public boolean listRepositories() {
            return true;
        }

        @Override
        public boolean allowUpLink() {
            return true;
        }

        @Override
        public boolean showBreadcrumb() {
            return true;
        }

        @Override
        public boolean breadcrumbWithLink() {
            return true;
        }

        @Override
        public boolean allowAddIconOnBreadcrumb() {
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

    boolean showDirectories();

    boolean listRepositories();

    boolean allowUpLink();

    boolean showBreadcrumb();

    boolean breadcrumbWithLink();

    boolean allowAddIconOnBreadcrumb();

    boolean showItemAge();

    boolean showItemMessage();

    boolean showItemLastUpdater();
}
