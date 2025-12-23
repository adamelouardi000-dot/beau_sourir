package ma.dentalTech;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DBConnectionTest {

    public static void main(String[] args) {
        String propsPath = "config/db.properties"; // doit être dans resources/config/db.properties

        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propsPath)) {

            if (in == null) {
                throw new RuntimeException("❌ Fichier introuvable dans resources: " + propsPath);
            }

            Properties p = new Properties();
            p.load(in);

            String driver = p.getProperty("datasource.driver");
            String url = p.getProperty("datasource.url");
            String user = p.getProperty("datasource.user");
            String pass = p.getProperty("datasource.password");

            System.out.println("Driver  : " + driver);
            System.out.println("URL     : " + url);
            System.out.println("User    : " + user);

            // charger driver
            Class.forName(driver);

            try (Connection cn = DriverManager.getConnection(url, user, pass);
                 Statement st = cn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT DATABASE() db, CURRENT_USER() u, VERSION() v")) {

                if (rs.next()) {
                    System.out.println("✅ CONNECTÉ !");
                    System.out.println("DB      : " + rs.getString("db"));
                    System.out.println("USER    : " + rs.getString("u"));
                    System.out.println("VERSION : " + rs.getString("v"));
                }
            }

        } catch (Exception e) {
            System.out.println("❌ ECHEC CONNEXION");
            e.printStackTrace();
        }
    }
}
