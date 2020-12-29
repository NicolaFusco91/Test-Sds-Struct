/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsdsstruct;

import JDBC.SDSConnection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Administrator
 */
public class SadasConn {
    private final String classe = "SadasConn";
    private SDSConnection dbConn;

    public SadasConn(String host, String port, String dbName,
                     String user, String password, String newPassword,
                     String clientIP, String application,
                     String registerDriverSadas, String idConn) throws Exception {
        final String metodo = "SadasConn";
        if (clientIP==null) {
            clientIP="";
        }
        if (application==null || application.isEmpty()) {
            application="SADASWEB";
        }
//
        try {
            if (registerDriverSadas == null
            || !registerDriverSadas.equalsIgnoreCase("S")){
                Class.forName("JDBC.SDSDriver");
            } else {
// verifica l'esistenza del driver JDBC di SADAS
                Driver sdsDriver = new JDBC.SDSDriver();
                DriverManager.registerDriver(sdsDriver);
            }
            String Url = "jdbc:SADAS:@"+host+":"+port+":"+dbName;
            Properties loginProp = new Properties();
            loginProp.setProperty("user", user);
            loginProp.setProperty("password", password);
            loginProp.setProperty("newpassword", newPassword);
            loginProp.setProperty("application", application);
            loginProp.setProperty("clientIP", clientIP);
            dbConn = (SDSConnection) DriverManager.getConnection(Url, loginProp);

        } catch (ClassNotFoundException | SQLException e) {
            String errore = "Errore nella classe "+classe+" metodo "+metodo+"\n"+
                "Connessione: "+idConn+"\n"+
                "host="+host+"; port="+port+"; Dbn="+dbName+"; uid="+user+"\n"+
                e.getMessage();
            Exception ne = new Exception(errore);
            throw(ne);
        }
    }

    public SDSConnection getConnection() {
        return dbConn;
    }

}
