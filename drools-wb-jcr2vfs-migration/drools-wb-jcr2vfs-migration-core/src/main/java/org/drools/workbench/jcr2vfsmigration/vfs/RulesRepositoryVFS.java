/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.jcr2vfsmigration.vfs;

//import org.apache.commons.lang.StringEscapeUtils;
//import org.drools.guvnor.client.rpc.Asset;
//import org.drools.guvnor.client.rpc.DiscussionRecord;
//import org.drools.guvnor.server.util.Discussion;
//import org.drools.repository.AssetItem;

// TODO delete this class once we have all of its functionality in other classes
//@ApplicationScoped
@Deprecated
public class RulesRepositoryVFS {

//    @Inject
//    @Named("ioStrategy")
//    private IOService ioService;
//
//    private Path root;
//
//    @PostConstruct
//    protected void init() {
//        setupGitRepos();
//    }
//
//    private void setupGitRepos() {
//        final Iterator<FileSystem> fsIterator = ioService.getFileSystems( MIGRATION_INSTANCE ).iterator();
//        if ( fsIterator.hasNext() ) {
//            final FileSystem bootstrap = fsIterator.next();
//            final Iterator<Path> rootIterator = bootstrap.getRootDirectories().iterator();
//            if ( rootIterator.hasNext() ) {
//                this.root = rootIterator.next();
//            }
//        }
//    }
//
//    public String checkinVersion( final Asset asset ) {
//        final Path assetPath = convertUUIDToPath( asset );
//
//        //TODO: what format to use to convert Date to String?
//        Map<String, Object> attrs = new HashMap<String, Object>() {{
//            put( "checkinComment", asset.getCheckinComment() );
//            put( "description", asset.getDescription() );
//            put( "state", asset.getState() );
//            put( "format", asset.getFormat() );
//            put( "lastContributor", asset.getLastContributor() );
//            put( "lastModified", asset.getLastModified() );
//            put( "title", asset.getName());
//            put( "created", asset.getDateCreated());
//            //put( "state", asset.isReadonly() );
//            put( "creator", asset.getMetaData().getCreator() );
//            put( "coverage", asset.getMetaData().getCoverage());
//            put( "dateEffective", asset.getMetaData().getDateEffective() );
//            put( "dateExpired", asset.getMetaData().getDateExpired() );
//            put( "valid", asset.getMetaData().getValid() );
//            //if the node has an "drools:binaryContent" attribute, Guvnor JCR returns true for asset.getMetaData().isBinary()
//            //put( "state", asset.getMetaData().isBinary() );
//            put( "disabled", asset.getMetaData().isDisabled() );
//        }};
//
//        //In old Guvnor, we convert domain object to binary by using content handler:
///*        ContentHandler handler = ContentManager.getHandler(asset.getFormat());
//        handler.storeAssetContent(asset,  repoAsset);*/
//        //Domain object to binary
//        String assetContent = null;
//
//
//        //TODO: vfsService needs a write(Path path, byte[] content) method.
//        final OpenOption commentedOption = new CommentedOption( asset.getLastContributor(), null, asset.getCheckinComment(), asset.getLastModified() );
//
//        //single commit for metadata and content
//        ioService.write( assetPath, assetContent, attrs, commentedOption );
//
//        return "";//old Guvnor returns uuid
//
//        /*****************IOService examples****************************************/
//        /*
//        //commits the change, if many changes at once, use other options
//        ioService.setAttribute( assetPath, "checkinComment", asset.getCheckinComment() );
//
//        //single commit for metadata and content
//        ioService.write( assetPath, assetContent, attrs, commentedOption );
//
//        //commits only metadata
//        ioService.setAttributes( assetPath, attrs );
//
//        //commits only content
//        ioService.write( assetPath, assetContent );
//
//        //commits only content and uses `commentedOption` to customize commit message, user and related
//        ioService.write( assetPath, assetContent, commentedOption );
//        */
//
//    }
//
//    public List<DiscussionRecord> addToDiscussionForAsset(Asset assetVFS, List<DiscussionRecord> discussions) {
//        final Path assetPath = convertUUIDToPath( assetVFS );
//
//        Discussion dp = new Discussion();
//        //Adding a new Discussion has *never* updated the Last Modified Date.
//        //clearAllDiscussionsForAsset has been made consistent with this behaviour.
//        //Make sure the behavior is consistent.
//        ioService.setAttribute( assetPath, "discussion", dp.toString(discussions) );
//        return discussions;
//    }
//
//    public Path convertUUIDToPath( Asset asset ) {
//        String packageName = asset.getMetaData().getModuleName();
//        String assetName = asset.getName();
//        return root.resolve( packageName + "/" + assetName );
//    }
}
