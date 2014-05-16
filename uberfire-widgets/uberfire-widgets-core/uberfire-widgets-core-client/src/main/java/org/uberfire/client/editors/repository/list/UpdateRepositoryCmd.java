package org.uberfire.client.editors.repository.list;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Command;
import org.uberfire.backend.repositories.Repository;

public class UpdateRepositoryCmd implements Command {

    private Repository repository;
    private RepositoriesPresenter presenter;
    private Map<String, Object> data = new HashMap<String, Object>();

    public UpdateRepositoryCmd(Repository repository, RepositoriesPresenter presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    public void add(String name, Object value) {
        this.data.put(name, value);
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute() {
        presenter.updateRepository(repository, data);
    }
}
