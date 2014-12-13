package org.uberfire.ext.editor.commons.backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.validation.FileNameValidator;
import org.uberfire.ext.editor.commons.service.ValidationService;

import static java.util.Collections.*;

@Service
@ApplicationScoped
public class ValidationServiceImpl implements ValidationService {

    @Any
    @Inject
    private Instance<FileNameValidator> fileNameValidatorBeans;
    private List<FileNameValidator> sortedValidators = new ArrayList<FileNameValidator>();

    @PostConstruct
    public void configureValidators() {
        for ( FileNameValidator fileNameValidator : fileNameValidatorBeans ) {
            sortedValidators.add( fileNameValidator );
        }

        //Sort ascending, so we can check which validator supports a particular case by priority
        sort( sortedValidators,
              new Comparator<FileNameValidator>() {
                  @Override
                  public int compare( final FileNameValidator o1,
                                      final FileNameValidator o2 ) {
                      return o2.getPriority() - o1.getPriority();
                  }
              } );
    }

    @Override
    public boolean isFileNameValid( final String fileName ) {
        for ( final FileNameValidator fileNameValidator : sortedValidators ) {
            if ( fileNameValidator.accept( fileName ) ) {
                return fileNameValidator.isValid( fileName );
            }
        }
        return false;
    }

    @Override
    public boolean isFileNameValid( final Path path,
                                    final String fileName ) {
        for ( final FileNameValidator fileNameValidator : sortedValidators ) {
            if ( fileNameValidator.accept( path ) ) {
                return fileNameValidator.isValid( fileName );
            }
        }
        return false;
    }

}
