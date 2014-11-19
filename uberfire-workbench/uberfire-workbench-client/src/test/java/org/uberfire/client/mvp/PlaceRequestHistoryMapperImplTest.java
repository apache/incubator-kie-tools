package org.uberfire.client.mvp;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.ioc.client.container.CreationalContext;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import com.google.common.collect.ImmutableMap;

public class PlaceRequestHistoryMapperImplTest {

    private PlaceRequestHistoryMapperImpl placeRequestHistoryMapper;

    @BeforeClass
    public static void setupBeans() {
        IOC.getBeanManager().destroyAllBeans();

        final ObservablePath opath = new ObservablePathImpl();

        IOC.getBeanManager().destroyAllBeans();
        IOC.getBeanManager().registerBean( new IOCBeanDef<ObservablePath>() {
            @Override
            public Class<ObservablePath> getType() {
                return ObservablePath.class;
            }

            @Override
            public Class<?> getBeanClass() {
                return ObservablePath.class;
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return null;
            }

            @Override
            public ObservablePath getInstance() {
                return opath;
            }

            @Override
            public ObservablePath getInstance( CreationalContext creationalContext ) {
                return opath;
            }

            @Override
            public ObservablePath newInstance() {
                return opath;
            }

            @Override
            public Set<Annotation> getQualifiers() {
                return Collections.emptySet();
            }

            @Override
            public boolean matches( Set<Annotation> annotations ) {
                return annotations.isEmpty();
            }

            @Override
            public String getName() {
                return ObservablePath.class.getName();
            }

            @Override
            public boolean isConcrete() {
                return false;
            }

            @Override
            public boolean isActivated() {
                return true;
            }
        } );
    }

    @Before
    public void setup() {
        placeRequestHistoryMapper = new PlaceRequestHistoryMapperImpl() {
            @Override
            String urlDecode( String value ) {
                try {
                    return URLDecoder.decode( value, "UTF-8" );
                } catch ( UnsupportedEncodingException e ) {
                    throw new RuntimeException( e );
                }
            }
        };
    }

    @Test
    public void createPlaceRequest() throws Exception {
        String url = " http://127.0.0.1:8888/org.uberfire.UberfireShowcase/out.16590-4829.erraiBus?z=12&clientId=16590-4829";
        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( url );
        assertEquals( url, placeRequest.getFullIdentifier() );

        Map<String, String> parameters = placeRequest.getParameters();

        assertEquals( "12", parameters.get( "z" ) );
        assertEquals( "16590-4829", parameters.get( "clientId" ) );
    }

    @Test
    public void createPathPlaceRequest() throws Exception {
        final Path path = PathFactory.newPath( "file", "default://master@repo/path/to/file" );
        final PlaceRequest placeRequestOriginal = new PathPlaceRequest( path );

        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( placeRequestOriginal.getFullIdentifier() );
        assertEquals( placeRequestOriginal.getFullIdentifier(), placeRequest.getFullIdentifier() );

        assertTrue( placeRequest.getParameters().isEmpty() );
    }

    @Test
    public void createPathPlaceRequestWithSpaces() throws Exception {
        final Path path = PathFactory.newPath( "Dummy rule.drl", "default://master@uf-playground/mortgages/src/main/resources/org/mortgages/Dummy%20rule.drl" );
        final PlaceRequest placeRequestOriginal = new PathPlaceRequest( path );

        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( placeRequestOriginal.getFullIdentifier() );
        assertEquals( placeRequestOriginal.getFullIdentifier(), placeRequest.getFullIdentifier() );

        assertTrue( placeRequest.getParameters().isEmpty() );
    }

    @Test
    public void identifierAndParametersShouldBeUrlDecoded() throws Exception {
        PlaceRequest placeRequest = placeRequestHistoryMapper.getPlaceRequest( "place%20id?par%26am%201=value%201" );
        assertEquals( "place id", placeRequest.getIdentifier() );
        assertEquals( ImmutableMap.of( "par&am 1", "value 1" ), placeRequest.getParameters() );
    }
}
