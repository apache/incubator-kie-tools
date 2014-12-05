package org.drools.workbench.jcr2vfsmigration.util;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.common.base.Charsets;
import org.drools.workbench.models.commons.backend.imports.ImportsParser;
import org.drools.workbench.models.commons.backend.packages.PackageNameParser;
import org.drools.workbench.models.commons.backend.packages.PackageNameWriter;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.packages.HasPackageName;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.EncodingUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PackageImportHelper {

    @Inject
    private ProjectService projectService;

    //Check if the xml contains a Package declaration, appending one if it does not exist
    public String assertPackageNameXML( final String xml,
                                        final Path resource ) {
        final Package pkg = projectService.resolvePackage( resource );
        String pkName =null;
        try{
            pkName =pkg.getPackageName();

            if(pkName!=null && pkg.getPackageName().endsWith( EncodingUtil.decode( resource.getFileName() ))){
                pkName = pkg.getPackageName().substring(0,pkg.getPackageName().indexOf(EncodingUtil.decode(resource.getFileName()))-1);
            }
        }catch (Exception e){
        }
        final String requiredPackageName = pkName;

        if ( requiredPackageName == null || "".equals( requiredPackageName ) ) {
            return xml;
        }

        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();

            Document doc = dombuilder.parse( new ByteArrayInputStream( xml.getBytes( Charsets.UTF_8 ) ) );

            if ( doc.getElementsByTagName( "packageName" ).getLength() != 0 ) {
                return xml;
            }

            Element root = doc.getDocumentElement();
//            Element nameElement = doc.createElement( "name" );
//            nameElement.appendChild( doc.createTextNode( resource.getFileName()) );
//            root.appendChild( nameElement );

            Element packageElement = doc.createElement( "packageName" );
            packageElement.appendChild( doc.createTextNode( requiredPackageName ) );
            root.appendChild( packageElement );

            //output xml with pretty format
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty( OutputKeys.METHOD, "xml" );
            trans.setOutputProperty( OutputKeys.INDENT, "yes" );
            trans.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", Integer.toString( 2 ) );

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult( sw );
            DOMSource s = new DOMSource( root );

            trans.transform( s, result );
            String xmlString = sw.toString();
            if(xmlString!=null)
                xmlString =xmlString.substring(xmlString.indexOf(">")+1);
            return xmlString;
        } catch ( TransformerConfigurationException e ) {
            e.printStackTrace();
        } catch ( ParserConfigurationException e ) {
            e.printStackTrace();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( SAXException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( TransformerException e ) {
            e.printStackTrace();
        }

        return xml;
    }

    public String assertPackageImportDRL( final String drl,
                                          final String packageHeader,
                                          final Path resource ) {
        if ( packageHeader == null ) {
            return drl;
        }

        final Imports imports = ImportsParser.parseImports( packageHeader );
        if ( imports == null || drl.toLowerCase().indexOf("import ")!=-1) {
            return drl;
        }
        StringBuilder sb = new StringBuilder();
        sb.append( imports.toString() );
        if ( imports.getImports().size() > 0 ) {
            sb.append( "\n" );
        }

        sb.append( drl );
        return sb.toString();
    }

    public String assertPackageImportXML( final String xml,
                                          final String packageHeader,
                                          final Path resource ) {
        if ( packageHeader == null ) {
            return xml;
        }

        final Imports imports = ImportsParser.parseImports( packageHeader );
        if ( imports == null ) {
            return xml;
        }

        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            Document doc = dombuilder.parse( new ByteArrayInputStream( xml.getBytes( Charsets.UTF_8 ) ) );

            if ( doc.getElementsByTagName( "imports" ).getLength() != 0 ) {
                return xml;
            }

            /* The imports should have following format (used by the workbench):
             *  <imports>
             *    <imports>
             *      <org.drools.workbench.models.datamodel.imports.Import>
             *        <type>java.lang.Number</type>
             *      </org.drools.workbench.models.datamodel.imports.Import>
             *   </imports>
             *  </imports>
             */
            Element root = doc.getDocumentElement();
            Element topImportsElement = doc.createElement( "imports" );
            Element nestedImportsElement = doc.createElement( "imports" );
            topImportsElement.appendChild(nestedImportsElement);

            for ( final Import i : imports.getImports() ) {
                Element importElement = doc.createElement( Import.class.getCanonicalName() );
                Element typeElement = doc.createElement( "type" );
                typeElement.appendChild( doc.createTextNode( i.getType() ) );
                importElement.appendChild( typeElement );
                nestedImportsElement.appendChild( importElement );
            }

            root.appendChild( topImportsElement );

            //output xml with pretty format
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty( OutputKeys.METHOD, "xml" );
            trans.setOutputProperty( OutputKeys.INDENT, "yes" );
            trans.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", Integer.toString( 2 ) );

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult( sw );
            DOMSource s = new DOMSource( root );

            trans.transform( s, result );
            String xmlString = sw.toString();
            if(xmlString!=null)
                xmlString =xmlString.substring(xmlString.indexOf(">")+1);
            return xmlString;
        } catch ( TransformerConfigurationException e ) {
            e.printStackTrace();
        } catch ( ParserConfigurationException e ) {
            e.printStackTrace();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( SAXException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( TransformerException e ) {
            e.printStackTrace();
        }

        return xml;
    }

    public String assertPackageName( final String drl,
                                     final Path resource ) {
        try {
            final String existingPackageName = PackageNameParser.parsePackageName(drl);
            if ( !"".equals( existingPackageName ) ) {
                return drl;
            }

            final Package pkg = projectService.resolvePackage( resource );
            String pkName =null;
            try{
                pkName =pkg.getPackageName();
                if(pkName!=null && pkg.getPackageName().endsWith(EncodingUtil.decode(resource.getFileName()))){
                    pkName = pkg.getPackageName().substring(0,pkg.getPackageName().indexOf(EncodingUtil.decode(resource.getFileName()))-1);
                }
            }catch (Exception e){

            }
            final String requiredPackageName = pkName;

            final HasPackageName mockHasPackageName = new HasPackageName() {

                @Override
                public String getPackageName() {
                    return requiredPackageName;
                }

                @Override
                public void setPackageName( final String packageName ) {
                    //Nothing to do here
                }
            };
            final StringBuilder sb = new StringBuilder();
            PackageNameWriter.write(sb,
                    mockHasPackageName);
            sb.append( drl );
            return sb.toString();

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException(e);
        }
    }
}
