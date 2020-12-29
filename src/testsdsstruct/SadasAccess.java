/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testsdsstruct;

import JDBC.SDSConnection;
import JDBC.SDSQueryStruct;
import JDBC.SDSResultSet;
import JDBC.SDSStatement;
import java.sql.SQLException;

/**
 *
 * @author Administrator
 */
public class SadasAccess {
    private final String classe = "SadasAccess";
    private SDSConnection connection = null;
    private SDSStatement sqlStmt = null;
    private SDSResultSet row = null;

    public SadasAccess(SDSConnection connection) throws SQLException {
        this.connection = connection;
    }

    public void execQuery(String strQuery) throws SQLException {
        final String metodo = "execQuery";
        try {
            sqlStmt = (SDSStatement) connection.createStatement();
            row = (SDSResultSet) sqlStmt.executeQuery(strQuery);
        } catch (SQLException ex) {
            String errore = "Errore nella classe "+classe+" metodo "+metodo+"\n"+
                "Statement: "+strQuery+"\n"+
                ex.getMessage();
            SQLException ne = new SQLException(errore);
            throw(ne);
        }
    }

    public boolean fetchRow() throws SQLException {
        final String metodo = "fetchRow";
	try {
	    return row.next();
        } catch (SQLException ex) {
            String errore = "Errore nella classe "+classe+" metodo "+metodo+"\n"+
                "Statement: "+sqlStmt.toString()+"\n"+
                ex.getMessage();
            SQLException ne = new SQLException(errore);
            throw(ne);
        }
    }

    public SDSResultSet getRow() throws SQLException {
        return row;
    }

    public int execUpdate(String strQuery) throws SQLException {
        final String metodo = "execUpdate";
        try {
            sqlStmt = (SDSStatement) connection.createStatement();
            int ru = sqlStmt.executeUpdate(strQuery);
            sqlStmt.close();
            sqlStmt = null;
            return ru;
        } catch (SQLException ex) {
            String errore = "Errore nella classe "+classe+" metodo "+metodo+"\n"+
                "Statement: "+strQuery+"\n"+
                ex.getMessage();
            SQLException ne = new SQLException(errore);
            throw(ne);
        }
    }

    public SDSQueryStruct structureQuery(String strQuery) throws SQLException {
        final String metodo = "structureQuery";
        try {
            sqlStmt = (SDSStatement) connection.createStatement();
            return sqlStmt.structureQuery(strQuery);
        } catch (SQLException ex) {
            String errore = "Errore nella classe "+classe+" metodo "+metodo+"\n"+
                "Statement: "+strQuery+"\n"+
                ex.getMessage();
            SQLException ne = new SQLException(errore);
            throw(ne);
        }
    }

    public String stringStructureQuery(String strQuery) throws SQLException {
        final String metodo = "stringStructureQuery";
        try {
            sqlStmt = (SDSStatement) connection.createStatement();
            return sqlStmt.stringStructureQuery(strQuery);
        } catch (SQLException ex) {
            String errore = "Errore nella classe "+classe+" metodo "+metodo+"\n"+
                "Statement: "+strQuery+"\n"+
                ex.getMessage();
            SQLException ne = new SQLException(errore);
            throw(ne);
        }
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void close() throws SQLException {
        if (row != null) {
            row = null;
        }
        if (sqlStmt != null) {
            sqlStmt = null;
        }
    }

    public String likeString(String text) {
        String t = text.trim();
        if (!t.isEmpty()) {
            if (t.indexOf('%') == -1) {
                t = "%" + t + "%";
            }
        }
        return normString(t);
    }

    public String normString(String text) {
        String t = "";
        if (text != null) {
            t = text.replace("'", "''");
        }
        return t;
    }
}
