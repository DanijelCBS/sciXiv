package xml.web.services.team2.sciXiv.utils.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AuthenticationUtilities {

	private static String connectionUri = "xmldb:exist://%1$s:%2$s/exist/xmlrpc";

	public static class XMLConnectionProperties {

		// conn.user
		public String user;

		// conn.password
		public String password;

		// conn.host
		public String host;

		// conn.driver
		public String driver;

		// conn.port
		public int port;

		public String uri;

		public XMLConnectionProperties(Properties props) {
			super();

			user = props.getProperty("conn.user").trim();
			password = props.getProperty("conn.password").trim();

			host = props.getProperty("conn.host").trim();
			port = Integer.parseInt(props.getProperty("conn.port"));

			uri = String.format(connectionUri, host, port);

			driver = props.getProperty("conn.driver").trim();
		}
	}

	public static InputStream openStream(String fileName) throws IOException {
		return AuthenticationUtilities.class.getClassLoader().getResourceAsStream(fileName);
	}

	public static XMLConnectionProperties loadProperties() throws IOException {
		String existProperties = "exist.properties";

		InputStream propertiesStream = openStream(existProperties);
		if (propertiesStream == null)
			throw new IOException("Could not read properties " + existProperties);

		Properties properties = new Properties();
		properties.load(propertiesStream);

		return new XMLConnectionProperties(properties);

	}

}
