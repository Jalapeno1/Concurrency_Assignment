/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency_db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Jonas
 */
public final class Concurrency_DB {

    public static Connection conn;
    public static Stats stats;

    public Concurrency_DB(String user, String pw) {

        try {
            conn = createConn(user, pw);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection createConn(String user, String pw) throws SQLException {

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat", user, pw);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return connection;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }

        return connection;
    }

    public static synchronized int generateId() throws SQLException {

        Statement stmt = conn.createStatement();
        int newID = 1;

        try {
            ResultSet rs = stmt.executeQuery(
                    "Select reserved from (SELECT RESERVED FROM SEAT WHERE RESERVED IS NOT NULL ORDER BY RESERVED DESC) where rownum = 1");

            if (rs.next()) {
                newID = rs.getInt("RESERVED");
                newID++;
            }

        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        return newID;
    }

    public static synchronized int book(String plane_no, String seat_no, long id) throws SQLException {
        Statement stmt = conn.createStatement();

        try {
            ResultSet rs = stmt.executeQuery(
                    "SELECT RESERVED, BOOKED, BOOKING_TIME FROM SEAT WHERE SEAT_NO = '" + seat_no + "'");

            
            if (rs.next()) {

                if (rs.getObject("BOOKED") != null) {
                    return -4;
                }

                if (rs.getObject("RESERVED") == null) {
                    return -1;
                }
                
                if ((System.currentTimeMillis() - rs.getLong("BOOKING_TIME")) > 5000) {
                    return -3;
                }

                if ((int) id == rs.getInt("RESERVED")) {
                    Statement update = conn.createStatement();
                    update.execute("UPDATE SEAT SET BOOKED = " + id + " WHERE SEAT_NO = '" + seat_no + "'");
                    update.execute("UPDATE SEAT SET BOOKING_TIME = " + System.currentTimeMillis() + " WHERE SEAT_NO = '" + seat_no + "'");
                    update.close();
                    Stats.setSuccessBookings();
                    return 0;
                } else {
                    Stats.setBookingsWithoutReserv();
                    return -2;
                }
            } else {
                return -5;
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return -5;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    public static synchronized String reserve(String plane_no, long id) throws SQLException {

        Statement stmt = conn.createStatement();
        String seatNumber = null;

        try {
            ResultSet rs = stmt.executeQuery(
                    "SELECT SEAT_NO FROM SEAT WHERE RESERVED is NULL AND BOOKED is NULL AND ROWNUM = 1");

            if (rs.next()) {
                seatNumber = rs.getString("SEAT_NO");

                Statement update = conn.createStatement();
                update.execute("UPDATE SEAT SET RESERVED = " + id + " WHERE SEAT_NO = '" + seatNumber + "'");
                update.execute("UPDATE SEAT SET BOOKING_TIME = " + System.currentTimeMillis() + " WHERE SEAT_NO = '" + seatNumber + "'");
                update.close();
            }

        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return seatNumber;
    }

    public static void main(String[] args) throws SQLException {
        Concurrency_DB db = new Concurrency_DB("cphjp154", "cphjp154");
        stats = new Stats(0, 0, 0, 0, 0);

        ExecutorService exe = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 20; i++) {
            Runnable user = new UserThread("" + i);
            exe.execute(user);
        }
        exe.shutdown();
        while(!exe.isTerminated()){}
        System.out.println("All done!");
        System.out.println(Stats.twoString());
        
        //db.run();
    }

    public synchronized void run() throws SQLException {

        for (int i = 0; i < 10; i++) {
            
            Runnable run = new Runnable() {
                @Override
                public void run() {

                    try {
                        int id = generateId();
                        String seatNumber = reserve("CR9", id);
                        System.out.println(Thread.currentThread().getId() + " has reserved");
                        Thread.sleep((long)(Math.random() * 10000) + 1000);
                        
                        if(new Random().nextInt(100) < 75){
                            System.out.println(Thread.currentThread().getId() + ": " +book("CR9", seatNumber, id));
                        } else {System.out.println("ABANDON SHIP!");}
                                            
                    } catch (Exception e) {
                    }
                }
            };
            
            Thread thread = new Thread(run);
            thread.start();            
        }
    }

}
