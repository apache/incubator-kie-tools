    public void testGetPackageForAtom() throws MalformedURLException, IOException {
        URL url = new URL("http://127.0.0.1:8080/guvnor-5.4.0-SNAPSHOT-jboss-as-7.0/rest/packages/mortgages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);

        InputStream in = connection.getInputStream();
        //System.out.println(IOUtils.toString(connection.getInputStream()));

        
        Document<Entry> doc = abdera.getParser().parse(in);
        Entry entry = doc.getRoot();
        System.out.println("BaseUri: " + entry.getBaseUri().getPath());
        System.out.println("Title: " + entry.getTitle());
        System.out.println("Published: " + entry.getPublished());
        System.out.println("Author: " + entry.getAuthor().getName());
        System.out.println("Summary: " + entry.getSummary());
        System.out.println("ContentSrcPath: " + entry.getContentSrc().getPath());

        List<Link> links = entry.getLinks();
        Map<String, Link> linksMap = new HashMap<String, Link>();
        for(Link link : links){
            System.out.println("Link Title: " + link.getTitle());
            System.out.println("Link Path: " + link.getHref().getPath());
            linksMap.put(link.getTitle(), link);
        }

        ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA);
        ExtensibleElement archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);
        System.out.println("ARCHIVED: " + archivedExtension.getSimpleExtension(Translator.VALUE));
        ExtensibleElement uuidExtension = metadataExtension.getExtension(Translator.UUID);
        System.out.println("UUID: " + uuidExtension.getSimpleExtension(Translator.VALUE));
        ExtensibleElement checkinCommentExtension = metadataExtension.getExtension(Translator.CHECKIN_COMMENT);
        System.out.println("CHECKIN_COMMENT: " + checkinCommentExtension.getSimpleExtension(Translator.VALUE));
        ExtensibleElement versionNumberExtension = metadataExtension.getExtension(Translator.VERSION_NUMBER);
        System.out.println("VERSION_NUMBER: " + versionNumberExtension.getSimpleExtension(Translator.VALUE));
    }
    
