package concurrency_db;

public final class Stats {
    
    private static int UserThreads;
    private static int SuccessBookings;
    private static int BookingsWithoutReserv;
    private static int IncorrectBookingIDs;
    private static int IncorrectBookings;

    public Stats(int UserThreads, int SuccessBookings, int BookingsWithoutReserv, int IncorrectBookingIDs, int IncorrectBookings) {
        Stats.UserThreads = UserThreads;
        Stats.SuccessBookings = SuccessBookings;
        Stats.BookingsWithoutReserv = BookingsWithoutReserv;
        Stats.IncorrectBookingIDs = IncorrectBookingIDs;
        Stats.IncorrectBookings = IncorrectBookings;
    }

    public static int getUserThreads() {
        return UserThreads;
    }

    public static int getSuccessBookings() {
        return SuccessBookings;
    }

    public static int getBookingsWithoutReserv() {
        return BookingsWithoutReserv;
    }

    public static int getIncorrectBookingIDs() {
        return IncorrectBookingIDs;
    }

    public static int getIncorrectBookings() {
        return IncorrectBookings;
    }

    public static void setUserThreads() {
        Stats.UserThreads++;
    }

    public static void setSuccessBookings() {
        Stats.SuccessBookings++;
    }

    public static void setBookingsWithoutReserv() {
        Stats.BookingsWithoutReserv++;
    }

    public static void setIncorrectBookingIDs() {
        Stats.IncorrectBookingIDs++;
    }

    public static void setIncorrectBookings() {
        Stats.IncorrectBookings++;
    }

    public static String twoString() {
        return "Stats{" + "UserThreads=" + UserThreads + ", SuccessBookings=" 
                + SuccessBookings + ", BookingsWithoutReserv=" + BookingsWithoutReserv 
                + ", IncorrectBookingIDs=" + IncorrectBookingIDs + ", IncorrectBookings=" 
                + IncorrectBookings + '}';
    }
}
