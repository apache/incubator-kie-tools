
    public void testGetPackagesForAtom() throws MalformedURLException, IOException {
        URL url = new URL("http://127.0.0.1:8080/guvnor-5.4.0-SNAPSHOT-jboss-as-7.0/rest/packages");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "admin:admin".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", MediaType.APPLICATION_ATOM_XML);
        connection.connect();
        //System.out.println(IOUtils.toString(connection.getInputStream()));

        InputStream in = connection.getInputStream();
		Document<Feed> doc = abdera.getParser().parse(in);
		Feed feed = doc.getRoot();
		System.out.println("BaseUriPath: " + feed.getBaseUri().getPath());
		System.out.println("Title: " + feed.getTitle());

		Iterator<Entry> it = feed.getEntries().iterator();
		while (it.hasNext()) {
            Entry entry = it.next();
            System.out.println("Title: " + entry.getTitle());
            List<Link> links = entry.getLinks();
            System.out.println("Href: " + links.get(0).getHref().getPath());
		}
    }