package web_app_evaluation_systems;

import java.sql.*;
import java.io.*;
import java.text.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sfina
 */
public class Web_App_Evaluation_Systems {

    private static ResultSet rs;
    private int shop = 15;

    private final static String DB_URL = "jdbc:mysql://localhost:3306/ergasia2?useSSL=false&characterEncoding=UTF-8";
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    private final static String USER = "root";
    private final static String PASS = "root";

    public static void main(String[] args) {
        new Web_App_Evaluation_Systems();
    }

    public Web_App_Evaluation_Systems() {
        initDBConnection();
        readFile();
    }

    private void initDBConnection() {
        try {
            Class.forName(DRIVER).newInstance();

            java.sql.Connection conn = java.sql.DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery("select * from log");
            System.out.println("Connected to Database...");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(Web_App_Evaluation_Systems.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exc" + ex.getLocalizedMessage());
        }
    }

    public void readFile() {
        String s;
        String newx;
        String prefix = "kk5502_access.2020-05-";
//        kk5502_access.2020-05-08.log
        for (int x = 8; x < 22; x++) {
            if (x < 10) {
                newx = "0" + x;
            } else {
                newx = Integer.toString(x);
            }
            s = prefix + newx + ".log";
            int lin = 0;
            try {
                BufferedReader input = new BufferedReader(new FileReader(s));
                System.out.println("LOGFILE READ : " + s);
                try {
                    String line = null; //not declared within while loop
                    while (((line = input.readLine()) != null) && (!Thread.interrupted())) {
                        try {

                            //updateDBFields(line);
                            rs.moveToInsertRow();
                            updateDBFields(line);
                            java.sql.Timestamp t = rs.getTimestamp("tDateAndTime");
                            rs.insertRow();

                        } catch (Exception se) {
                            se.printStackTrace();
                        }
                    }
                } finally {
                    input.close();
                    System.out.println("Finished! (" + lin + ")");
                }
            } catch (IOException exe) {
                System.out.println("Exception: " + exe.getMessage());
            }
        }
    }

    private void updateDBFields(String oneLine) {
        int nValue;
        String lStr;
        String agent, referer;

        String[] components = oneLine.split(" ", 0);
        int x = 0;
        int ref2 = oneLine.lastIndexOf('\"');
        int ref1 = oneLine.lastIndexOf('\"', ref2 - 1);
        int ag2 = oneLine.lastIndexOf('\"', ref1 - 1);
        int ag1 = oneLine.lastIndexOf('\"', ag2 - 1);

        agent = oneLine.substring(ag1 + 1, ag2);
        referer = oneLine.substring(ref1 + 1, ref2);

        try {
//            for (String c : components) {
//                System.out.println(x++ + " " + c);
//            }
            rs.updateString("aRemoteIPAddress", components[0]);
            rs.updateString("ALocalIPAddress", components[1]);

            if (components[2].equals("-")) {
                nValue = 0;
            } else {
                nValue = Integer.parseInt(components[2]);
            }
            rs.updateInt("bBytesSentOrDash", nValue);

            nValue = Integer.parseInt(components[3]);
            rs.updateInt("BBytesSent", nValue);

            rs.updateString("hRemoteHostName", components[4]);

            rs.updateString("lRemoteLogicalUserName", components[5]);

            rs.updateString("mRequestMethod", components[6]);

            nValue = Integer.parseInt(components[7]);
            rs.updateInt("pLocalPort", nValue);

            rs.updateString("qQueryString", components[8]);

            lStr = components[12] + components[13] + components[14];
            rs.updateString("rFirstLineOfRequest", lStr);

            rs.updateString("sHTTPStatusCode", components[15]);

            rs.updateString("SUserSessionID", components[18]);

            rs.updateString("tDateAndTime", components[10] + components[11]);

            String dateAsIs = components[10].substring(1); // get rid of [
            dateAsIs = dateAsIs.replace("/", " ");
            dateAsIs = dateAsIs.replaceFirst(":", " ");
            dateAsIs = dateAsIs + " " + components[11].substring(0, 5); //09 Feb 2009 20:22:01 +0200
            //System.out.println("--->" + dateAsIs);
            // initialize a well-known Date
            java.text.DateFormat dfm = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
            dfm.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Athens"));
            System.out.println("dateAsIs " + dateAsIs);
            java.util.Date a;
            try {
                a = dfm.parse(dateAsIs);

                java.sql.Timestamp ts1 = new java.sql.Timestamp(a.getTime());
                //System.out.println("ts1 " + ts1.toString());
                rs.updateTimestamp("tDateAndTime", ts1);
                //System.out.println("...");

            } catch (ParseException ex) {
                System.out.println("ads" + ex.getMessage());
            }

            rs.updateString("uRemoteUserAuthenticated", components[9]);
            rs.updateString("URequestedURLPath", components[16]);

            nValue = Integer.parseInt(components[17]);
            rs.updateInt("DMillisToProcess", nValue);

            rs.updateString("agent", agent);
            rs.updateString("referer", referer);

        } catch (SQLException slwp) {
            System.out.println("ads1" + slwp.getMessage());
        }
    }
}
