/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package testsdsstruct;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Administrator
 */
public class TestSdsStruct {
    private static final String HOST = "SDS64";
    private static final String PORT = "3020";
    private static final String DBNAME = "TPCH";
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "";
    private static final String QUERY_FILE = "Query.sql";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String sql = "";
        try  {
            FileInputStream queryFile = new FileInputStream(QUERY_FILE);
            InputStreamReader queryStream = new InputStreamReader(queryFile);
            BufferedReader queryReader = new BufferedReader(queryStream);
            String riga;
            while ((riga = queryReader.readLine()) != null) {
                sql += riga+"\n";
            }
            queryStream.close();
            queryFile.close();
            System.out.println("Query:\n"+sql);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-15);
        }
//
        SadasConn connection = null;
        SadasAccess query;
        try {
            connection = new SadasConn(HOST, PORT, DBNAME, USER, PASSWORD,
                "", //new password
                null, //ip
                null, //app
                null, //driver
                null //conn
            );
//
            query = new SadasAccess(connection.getConnection());
            query.execQuery(sql);
            System.out.println("Righe: "+query.getRow().getRowCount());
//
            System.out.println("Struttura:\n"+query.stringStructureQuery(sql));
            
//INIZIO SCEMPIO NICOLA 
            Query aquery= new Query(query.stringStructureQuery(sql));
            
            aquery.AnalizzaQuery();
            
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-15);
        }

        try {
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-15);
        }
    }

}
