package concurrency_db;

import java.util.Random;

public class UserThread implements Runnable {

    private String id;

    public UserThread(String s) {
        this.id = s;
    }

    @Override
    public void run() {
        try {
            Stats.setUserThreads();
            int id = Concurrency_DB.generateId();
            String seatNumber = Concurrency_DB.reserve("CR9", id);
            //System.out.println(Thread.currentThread().getName() + " has reserved");
            Thread.sleep((long) (Math.random() * 10000) + 1000);

            if (new Random().nextInt(100) < 75) {
                //System.out.println(Thread.currentThread().getName() + ": " + Concurrency_DB.book("CR9", seatNumber, id));
            } else {
                //System.out.println("ABANDON SHIP!");
            }

        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return this.id;
    }  

}
