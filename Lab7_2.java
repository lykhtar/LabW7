package lab7_2;

import java.util.concurrent.Phaser;

public class Tunnel {

    public static void main(String[] args) {

        Phaser phaser = new Phaser(1);

        new Thread(new PhaseThread(phaser, "Поезд 1 ")).start();

        int phase = phaser.getPhase() + 1;
        phaser.arriveAndAwaitAdvance();
        System.out.println("Выехал из туннеля " + phase + " поезд 1");

        new Thread(new PhaseThread(phaser, "Поезд 2 ")).start();

        phase = phaser.getPhase() + 1;
        phaser.arriveAndAwaitAdvance();
        System.out.println("Выехал из туннеля " + phase + " поезд 2");

        phaser.arriveAndDeregister();
    }
}

class PhaseThread implements Runnable {

    Phaser phaser;
    String name;

    PhaseThread(Phaser p, String n) {

        this.phaser = p;
        this.name = n;
        phaser.register();
    }

    public void run() {
        int m = phaser.getPhase() + 1;
        System.out.println(name + " заехал в туннель " + m);
        phaser.arriveAndAwaitAdvance(); // сообщаем, что первая фаза достигнута
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }

        phaser.arriveAndDeregister(); // сообщаем о завершении фаз и удаляем с регистрации объекты 
    }
}
