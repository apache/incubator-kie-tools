package org.kie.workbench.common.services.backend.metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.thoughtworks.xstream.XStream;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.workbench.common.services.backend.exceptions.ExceptionUtilities;
import org.kie.workbench.common.services.shared.metadata.CategoriesService;
import org.kie.workbench.common.services.shared.metadata.model.Categories;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class CategoryServiceImpl implements CategoriesService {

    private final XStream xt = new XStream();

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Override
    public void save( final Path path,
                      final Categories content ) {
        try {
            ioService.write( paths.convert( path ),
                             xt.toXML( content ) );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Categories getContent( final Path path ) {
        try {
            final String content = ioService.readAllString( paths.convert( path ) );
            final Categories categories;

            if ( content.trim().equals( "" ) ) {
                categories = new Categories();
            } else {
                categories = (Categories) xt.fromXML( content );
            }

            return categories;

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

    @Override
    public Categories getCategoriesFromResource( final Path resource ) {
        try {
            final org.kie.commons.java.nio.file.Path categoriesPath = paths.convert( resource ).getRoot().resolve( "categories.xml" );

            return getContent( paths.convert( categoriesPath ) );

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }
}
