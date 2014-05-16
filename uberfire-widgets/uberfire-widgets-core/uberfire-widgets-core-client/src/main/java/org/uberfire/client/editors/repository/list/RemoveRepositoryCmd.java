package org.uberfire.client.editors.repository.list;

import com.google.gwt.user.client.Command;
import org.uberfire.backend.repositories.Repository;

public class RemoveRepositoryCmd implements Command {

    private Repository repository;
    private RepositoriesPresenter presenter;
    public RemoveRepositoryCmd(Repository repository, RepositoriesPresenter presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute() {
        presenter.removeRepository(repository);
    }
}
