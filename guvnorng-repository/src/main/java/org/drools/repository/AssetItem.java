/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.repository;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.drools.java.nio.file.FileSystems;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.fs.base.GeneralPathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AssetItem class is used to abstract away the details of the underlying JCR
 * repository. It is used to pass information about assets stored in the
 * repository.
 */

public class AssetItem extends CategorisableItem {

    private Logger             log                                  = LoggerFactory.getLogger( AssetItem.class );
    /**
     * The name of the asset node type
     */
    public static final String ASSET_NODE_TYPE_NAME                  = "drools:assetNodeType";

    public static final String CONTENT_PROPERTY_NAME                = "drools:content";
    public static final String CONTENT_PROPERTY_BINARY_NAME         = "drools:binaryContent";
    public static final String CONTENT_PROPERTY_ATTACHMENT_FILENAME = "drools:attachmentFileName";

    /**
     * The name of the date effective property on the asset node type
     */
    public static final String DATE_EFFECTIVE_PROPERTY_NAME         = "drools:dateEffective";

    public static final String DISABLED_PROPERTY_NAME               = "drools:disabled";

    /**
     * The name of the date expired property on the asset node type
     */
    public static final String DATE_EXPIRED_PROPERTY_NAME           = "drools:dateExpired";

    public static final String MODULE_NAME_PROPERTY                = "drools:packageName";

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * Constructs a AssetItem object, setting its node attribute to the specified
     * node.
     * 
     * @param rulesRepository
     *            the rulesRepository that instantiated this object
     * @param node
     *            the node in the repository that this AssetItem corresponds to
     * @throws RulesRepositoryException
     */
    public AssetItem(Path assetPath) throws RulesRepositoryException {
        super(assetPath);
/*        super( rulesRepository,
               node );*/
        
           //JLIU: check this Path is not a directory.         
/*         try {
            // make sure this node is an asset node
            if ( !(this.node.getPrimaryNodeType().getName().equals( ASSET_NODE_TYPE_NAME ) || isHistoricalVersion()) ) {
                String message = this.node.getName() + " is not a node of type " + ASSET_NODE_TYPE_NAME + " nor nt:version. It is a node of type: " + this.node.getPrimaryNodeType().getName();
                log.error( message );
                throw new RulesRepositoryException( message );
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    public AssetItem() {
        super( null);
    }

    /**
     * returns the string contents of the asset node. If this is a binary asset,
     * this will return null (use getBinaryContent instead).
     */
    public String getContent() throws RulesRepositoryException {
        final List<String> lines = Files.readAllLines(assetPath, UTF_8);
        final StringBuilder sb = new StringBuilder();
        if (lines != null ){
            for (final String s : lines) {
                sb.append(s).append('\n');
            }
        }
        return sb.toString();
    }

    //JLIU: Not needed
/*    *//**
     * Only for use in the StorageEventManager, for passing the fromRepo
     * parameter
     * 
     * returns the string contents of the asset node. If this is a binary asset,
     * this will return null (use getBinaryContent instead).
     *//*    
    //REVISIT: This return the binary data as a byte array if its binary asset. 
    //Return null is better?
    public String getContent(Boolean fromRepo) throws RulesRepositoryException {
        try {

            if ( StorageEventManager.hasLoadEvent() && !fromRepo ) {
                return IOUtils.toString( StorageEventManager.getLoadEvent().loadContent( this ) );
            }

            if ( isBinary() ) {
                return new String( this.getBinaryContentAsBytes() );
            }
            Node ruleNode = getVersionContentNode();
            if ( ruleNode.hasProperty( CONTENT_PROPERTY_NAME ) ) {
                Property data = ruleNode.getProperty( CONTENT_PROPERTY_NAME );
                return data.getValue().getString();

            } else {
                return "";
            }
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }
*/
    /**
     * returns the number of bytes of the content.
     */
    public long getContentLength() {
        //JLIU: size is not implemented yet 
        return Files.size(assetPath);
/*        try {
            Node assetNode = getVersionContentNode();
            if ( assetNode.hasProperty( CONTENT_PROPERTY_BINARY_NAME ) ) {
                Property data = assetNode.getProperty( CONTENT_PROPERTY_BINARY_NAME );
                return data.getLength();
            } else {
                if ( assetNode.hasProperty( CONTENT_PROPERTY_NAME ) ) {
                    Property data = assetNode.getProperty( CONTENT_PROPERTY_NAME );
                    return data.getLength();
                } else {
                    return 0;
                }
            }
        } catch ( RepositoryException e ) {
            log.error( e.getMessage(),
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * True if this is a binary asset (or has binary content).
     */
    public boolean isBinary() {
        //JLIU: this method may not be needed anymore? somehow we can say every asset is a binary asset as it always has an associated file to store its content?
        //Or maybe we say an asset is binary when its content can not be represented by plain text.
        return true;
/*        try {
            Node assetNode = getVersionContentNode();
            return assetNode.hasProperty( CONTENT_PROPERTY_BINARY_NAME );
        } catch ( RepositoryException e ) {
            log.error( e.getMessage(),
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * If this asset contains binary data, this is how you return it. Otherwise
     * it will return null.
     */
    public InputStream getBinaryContentAttachment() {
        return Files.newInputStream(assetPath);
/*        try {
            if ( StorageEventManager.hasLoadEvent() ) {
                return StorageEventManager.getLoadEvent().loadContent( this );
            }
            Node assetNode = getVersionContentNode();
            if ( assetNode.hasProperty( CONTENT_PROPERTY_BINARY_NAME ) ) {
                Property data = assetNode.getProperty( CONTENT_PROPERTY_BINARY_NAME );
                return data.getBinary().getStream();
            } else {
                if ( assetNode.hasProperty( CONTENT_PROPERTY_NAME ) ) {
                    Property data = assetNode.getProperty( CONTENT_PROPERTY_NAME );
                    return data.getBinary().getStream();
                }
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    //JLIU: no need for this method anymore
/*    *//** Get the name of the "file" attachment, if one is set. Null otherwise *//*
    public String getBinaryContentAttachmentFileName() {
        return getStringProperty( CONTENT_PROPERTY_ATTACHMENT_FILENAME );
    }*/

    /**
     * This is a convenience method for returning the binary data as a byte
     * array.
     */
    public byte[] getBinaryContentAsBytes() {
    	//JLIU: TODO
    	return null;
    	
/*        try {
            Node assetNode = getVersionContentNode();
            if ( StorageEventManager.hasLoadEvent() ) {
                return IOUtils.toByteArray( StorageEventManager.getLoadEvent().loadContent( this ) );
            }
            if ( isBinary() ) {
                //Property data = assetNode.getProperty( CONTENT_PROPERTY_BINARY_NAME );
                InputStream in = vfsService.newInputStream(assetPath);

                // Create the byte array to hold the data
                byte[] bytes = new byte[(int) getContentLength()];

                // Read in the bytes
                int offset = 0;
                int numRead = 0;
                while ( offset < bytes.length && (numRead = in.read( bytes,
                                                                     offset,
                                                                     bytes.length - offset )) >= 0 ) {
                    offset += numRead;
                }

                // Ensure all the bytes have been read in
                if ( offset < bytes.length ) {
                    throw new RulesRepositoryException( "Could not completely read asset " + getName() );
                }

                // Close the input stream and return bytes
                in.close();
                return bytes;
            } else {
                return getContent().getBytes();
            }
        } catch ( Exception e ) {
            log.error( e.getMessage(),
                       e );
            if ( e instanceof RuntimeException ) throw (RuntimeException) e;
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * @return the date the rule becomes effective
     * @throws RulesRepositoryException
     */
    public Calendar getDateEffective() throws RulesRepositoryException {
        Path metadataFile = getMetaDataFilePath(assetPath);
        Date value =  MetaDataUtil.getProperty(metadataFile, DATE_EFFECTIVE_PROPERTY_NAME, Date.class);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        return calendar;       
        
        
/*        try {
            Node assetNode = getVersionContentNode();

            Property dateEffectiveProperty = assetNode.getProperty( DATE_EFFECTIVE_PROPERTY_NAME );
            return dateEffectiveProperty.getDate();
        } catch ( PathNotFoundException e ) {
            // doesn't have this property
            return null;
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }
    
    private Path getMetaDataFilePath(Path assetPath) {
    	Path fileName = assetPath.getFileName();
    	Path parentName = assetPath.getParent();
    	
    	Path path = FileSystems.getFileSystem(URI.create("jgit:///guvnorng-playground")).getPath(parentName.toString(), fileName.toString());
    	//Path path = GeneralPathImpl.create(fileSystem, rootURI.getPath() + "/" + moduleName, false);
        return path;
    }

    /**
     * @return if this asset is disabled
     * @throws RulesRepositoryException
     */
    public boolean getDisabled() throws RulesRepositoryException {
        //JLIU: read from metadata file (dot file)
        Path metadataFile = getMetaDataFilePath(assetPath);
        boolean value = MetaDataUtil.getProperty(metadataFile, DISABLED_PROPERTY_NAME, Boolean.class);
        return value;
        
/*        try {
            Node assetNode = getVersionContentNode();

            Property disabled = assetNode.getProperty( DISABLED_PROPERTY_NAME );
            return disabled.getBoolean();
        } catch ( PathNotFoundException e ) {
            // doesn't have this property
            return false;
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * Creates a new version of this asset node, updating the effective
     * date for the asset node.
     *
     * @param newDateEffective
     *            the new effective date for the rule
     * @throws RulesRepositoryException
     */
    public void updateDateEffective(Calendar newDateEffective)
            throws RulesRepositoryException {
        Path metadataFile = getMetaDataFilePath(assetPath);
        Date value = newDateEffective.getTime();
        MetaDataUtil.setProperty(metadataFile, DATE_EFFECTIVE_PROPERTY_NAME, value);
        
/*        checkIsUpdateable();
        checkout();
        try {
            if (newDateEffective!=null || this.node.hasProperty(DATE_EFFECTIVE_PROPERTY_NAME)) {
                this.node.setProperty(DATE_EFFECTIVE_PROPERTY_NAME,
                    newDateEffective);
            }
        } catch (RepositoryException e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }*/
    }

    /**
     * Creates a new version of this asset node, updating the disable
     * value for the asset node.
     * 
     * @param disabled
     *            is this asset disabled
     * @throws RulesRepositoryException
     */
    public void updateDisabled(boolean disabled) throws RulesRepositoryException {
        //JLIU: update metadata file (dot file)
        
/*        checkIsUpdateable();
        checkout();
        try {
            this.node.setProperty( DISABLED_PROPERTY_NAME,
                                   disabled );
        } catch ( RepositoryException e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * @return the date the rule becomes expired
     * @throws RulesRepositoryException
     */
    public Calendar getDateExpired() throws RulesRepositoryException {
        //JLIU: read from metadata file (dot file)
        return null;
        
/*        try {
            Node assetNode = getVersionContentNode();

            Property dateExpiredProperty = assetNode.getProperty( DATE_EXPIRED_PROPERTY_NAME );
            return dateExpiredProperty.getDate();
        } catch ( PathNotFoundException e ) {
            // doesn't have this property
            return null;
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * Creates a new version of this asset node, updating the expired
     * date for the asset node.
     * 
     * @param newDateExpired
     *            the new expired date for the rule
     * @throws RulesRepositoryException
     */
    public void updateDateExpired(Calendar newDateExpired) throws RulesRepositoryException {
        //JLIU: update metadata file (dot file)
        
/*        checkout();

        try {
            if (newDateExpired!=null || this.node.hasProperty(DATE_EXPIRED_PROPERTY_NAME)) {
                this.node.setProperty(DATE_EXPIRED_PROPERTY_NAME, newDateExpired);
            }
        } catch (Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }*/
    }

    /**
     * This will update the asset's content (checking it out if it is not
     * already). This will not save the session or create a new version of the
     * node (this has to be done seperately, as several properties may change as
     * part of one edit). This is only used if the asset is a textual asset. For
     * binary, use the updateBinaryContent method instead.
     */
    public AssetItem updateContent(String newRuleContent) throws RulesRepositoryException {
        //JLIU: check in and commit related
        Files.write(assetPath, newRuleContent, UTF_8);
        return this;
        
/*        checkout();
        try {
            if ( this.isBinary() ) {
                this.updateBinaryContentAttachment( new ByteArrayInputStream( newRuleContent.getBytes() ) );
            }
            this.node.setProperty( CONTENT_PROPERTY_NAME,
                                   newRuleContent );
            return this;
        } catch ( RepositoryException e ) {
            log.error( "Unable to update the asset content",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * If the asset is a binary asset, then use this to update the content (do
     * NOT use text).
     */
    public AssetItem updateBinaryContentAttachment(InputStream data) {
        //JLIU: implement write(InputStream) in vfs
        //vfsService.write(assetPath, newRuleContent);
        return this;
/*        checkout();
        try {
            Binary is = this.node.getSession().getValueFactory().createBinary( data );
            this.node.setProperty( CONTENT_PROPERTY_BINARY_NAME,
                                   is );
            return this;
        } catch ( RepositoryException e ) {
            log.error( "Unable to update the assets binary content",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    //JLIU: No need for this method anymore
/*    *//**
     * Optionally set the filename to be associated with the binary content.
     *//*
    public void updateBinaryContentAttachmentFileName(String name) {
        updateStringProperty( name,
                              CONTENT_PROPERTY_ATTACHMENT_FILENAME );
    }
*/
    /**
     * This updates a user defined property (not one of the intrinsic ones).
     */
    public void updateUserProperty(String propertyName,
                                   String value) {
        //JLIU: update metadata file (dot file)
        
/*        if ( propertyName.startsWith( "drools:" ) ) {
            throw new IllegalArgumentException( "Can only set the pre defined fields using the appropriate methods." );
        }
        updateStringProperty( value,
                              propertyName );*/

    }

    /**
     * Nicely formats the information contained by the node that this object
     * encapsulates
     */
    public String toString() {
        try {
            StringBuilder returnString = new StringBuilder();
            returnString.append( "Content of asset item named '" ).append( this.getName() ).append( "':\n" );
            returnString.append( "Content: " ).append( this.getContent() ).append( "\n" );
            returnString.append( "------\n" );

            returnString.append( "Archived: " ).append( this.isArchived() ).append( "\n" );
            returnString.append( "------\n" );
            
            returnString.append( "Version: " ).append( this.getVersionNumber() ).append( "\n" );
            returnString.append( "------\n" );
            
            returnString.append( "Date Effective: " ).append( this.getDateEffective() ).append( "\n" );
            returnString.append( "Date Expired: " ).append( this.getDateExpired() ).append( "\n" );
            returnString.append( "------\n" );

            returnString.append( "Asset State: " );
            StateItem stateItem = this.getState();
            if ( stateItem != null ) {
                returnString.append( this.getState().getName() ).append( "\n" );
            } else {
                returnString.append( "NO STATE SET FOR THIS NODE\n" );
            }
            returnString.append( "------\n" );
            returnString.append("Valid: ").append(this.getValid());
            returnString.append( "------\n" );
            returnString.append( "Asset tags:\n" );
            for ( Iterator it = this.getCategories().iterator(); it.hasNext(); ) {
                CategoryItem currentTag = (CategoryItem) it.next();
                returnString.append( currentTag.getName() ).append( "\n" );
            }
            returnString.append( "--------------\n" );
            return returnString.toString();
        } catch ( Exception e ) {
            throw new RulesRepositoryException( e );
        }
    }

    public VersionableItem getPrecedingVersion() throws RulesRepositoryException {
        //JLIU: HOW? get the succeeding version of this file from jgit then return it? but does the Path contain version info? 
        //or we have to have sth like AssetItem(Path path, String objectId
        return null;
        
/*        
        try {
            Node precedingVersionNode = this.getPrecedingVersionNode();
            if ( precedingVersionNode != null ) {
                return new AssetItem( this.rulesRepository,
                                      precedingVersionNode );
            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    public VersionableItem getSucceedingVersion() throws RulesRepositoryException {
        //JLIU: HOW? get the succeeding version of this file from jgit then return it? but does the Path contain version info? 
        //or we have to have sth like AssetItem(Path path, String objectId
        return null;
        
/*        try {
            Node succeedingVersionNode = this.getSucceedingVersionNode();
            if ( succeedingVersionNode != null ) {
                return new AssetItem( this.rulesRepository,
                                      succeedingVersionNode );
            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * Get the name of the enclosing module. As assets are stored in
     * versionable subfolders, this means walking up 2 levels in the hierarchy
     * to get to the enclosing "module" node.
     */
    public String getModuleName() {
        //JLIU: get module path by parsing path? 
        return null;
        
        //return super.getStringProperty( MODULE_NAME_PROPERTY );
    }

    /**
     * @return A property value (for a user defined property).
     */
    public String getUserProperty(String property) {
        //JLIU: read from metadata file (dot file)
        return null;
        
        //return getStringProperty( property );
    }

    /**
     * This will remove the item. The repository will need to be saved for this
     * to take effect. Typically the package that contains this should be
     * versioned before removing this, to make it easy to roll back.
     */
    public void remove() {
        Files.delete(assetPath);

/*        if ( StorageEventManager.hasSaveEvent() ) {
            StorageEventManager.getSaveEvent().onAssetDelete( this );
        }

        checkIsUpdateable();
        if ( this.getDateExpired() != null ) {
            if ( Calendar.getInstance().before( this.getDateExpired() ) ) {
                throw new RulesRepositoryException( "Can't delete an item before its expiry date." );
            }
        }
        try {
            this.node.remove();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * 
     * @return An iterator over the nodes history.
     */
    public AssetHistoryIterator getHistory() {    	
        //JLIU: create a lazy-load iterator to represent the history of this file.
        return null;
        
/*        if(isHistoricalVersion()) {
			Node frozenNode = getNode();
			try {
				Node headNode = frozenNode.getSession().getNodeByIdentifier(
						frozenNode.getProperty("jcr:frozenUuid")
								.getString());
				return new AssetHistoryIterator( this.rulesRepository,
						headNode );
			} catch (RepositoryException e) {
				throw new RulesRepositoryException(e);
			}     	
        } 
        
        return new AssetHistoryIterator( this.rulesRepository,
                                         this.node );*/
    }

    /**
     * This will get the module an asset item belongs to.
     */
    public ModuleItem getModule() {
        //JLIU: 
        //String moduleName = getModuleName();
        //Path modulePath = toPath();
        //return new ModuleItem(modulePath);
        return null;

/*        try {
            if ( this.isHistoricalVersion() ) {
                return this.rulesRepository.loadModule( this.getModuleName() );
            }
            return new ModuleItem( this.rulesRepository,
                                    this.node.getParent().getParent() );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }*/
    }

    /**
     * This converts a "filename" to an asset name.
     * 
     * File name is foo.drl -> ["foo", "drl"]
     * 
     * @param fileName
     * @return
     */
    public static String[] getAssetNameFromFileName(String fileName) {

        String[] r = new String[]{"", ""};
        if ( !fileName.contains( "." ) ) {
            r[0] = fileName;
        } else if ( fileName.endsWith( ".model.drl" ) ) {
            r[0] = fileName.substring( 0,
                                       fileName.lastIndexOf( ".model.drl" ) );
            r[1] = "model.drl";
        } else {
            r[0] = fileName.substring( 0,
                                       fileName.lastIndexOf( "." ) );
            r[1] = fileName.substring( fileName.lastIndexOf( "." ) + 1 );

        }
        return r;

    }
    
    protected String getPath() {
        return assetPath.toString();
/*        try {
            return this.node.getPath();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }*/
    }
}
