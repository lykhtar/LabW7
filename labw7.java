
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;

public class TrainFlow<T> {

    private final static int QUEUE_SIZE = 5; // размер очереди из поездов
    private final Semaphore semaphore = new Semaphore(QUEUE_SIZE, true);
    private final Queue<T> resources = new LinkedList<T>();

    public TrainFlow(Queue<T> source) {
        resources.addAll(source);
    }

    public T getResource(long maxWaitMillis) throws ResourсeException {
        try {
            if (semaphore.tryAcquire(maxWaitMillis, TimeUnit.MILLISECONDS)) {
                T res = resources.poll();
                return res;
            }
        } catch (InterruptedException e) {
            throw new ResourсeException(e);
        }
        throw new ResourсeException(":превышено время ожидания");
    }

    public void returnResource(T res) {
        resources.add(res); // возвращение экземпляра в пул
        semaphore.release();
    }
}

public class Train {

    private int train;

    public Train(int id) {
        super();
        this.train = id;
    }

    public int getTrainId() {
        return train;

    }

    public void setTrainId(int id) {
        this.train = id;
    }

    public void using() {
        try {
            // использование канала
            Thread.sleep(new java.util.Random().nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class ResourсeException extends Exception {

    public ResourсeException() {
        super();
    }

    public ResourсeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourсeException(String message) {
        super(message);
    }

    public ResourсeException(Throwable cause) {
        super(cause);
    }
}

public class Tunnel extends Thread {

    private boolean reading = false;
    private TrainFlow<Train> tr;
    private String str;

    public Tunnel(String str, TrainFlow<Train> tr) {
        this.tr = tr;
        this.str = str;
    }

    public synchronized void run() {

        Train train = null;
        try {
            train = tr.getResource(100);
            reading = true;
            System.out.println("Tunnel " + str + " took the train №" + train.getTrainId());
            train.using();
        } catch (ResourсeException e) {

            e.getMessage();
        } finally {
            if (train != null) {
                reading = false;
                System.out.println("Tunnel " + str + " : " + " freed the train №" + train.getTrainId());
                tr.returnResource(train);
            }
        }
    }

    public boolean isReading() {
        return reading;
    }
}
import java.util.LinkedList;

public class TrainRunner {

    public static void main(String[] args) {
        LinkedList<Train> list = new LinkedList<Train>() {
            {
                this.add(new Train(1));
                this.add(new Train(2));
                this.add(new Train(3));
                this.add(new Train(4));
                this.add(new Train(5));
            }
        };
        TrainFlow<Train> tr = new TrainFlow<>(list);
        Tunnel t1 = new Tunnel("First", tr);
        Tunnel t2 = new Tunnel("Second", tr);
        for (int i = 0; i < 5; i++) {
            t1.start();
            t2.start();
        }
    }
}
