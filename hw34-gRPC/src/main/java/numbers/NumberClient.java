package numbers;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NumberClient {
    private static final Logger logger = Logger.getLogger(NumberClient.class.getName());
    private final ManagedChannel channel;
    private final NumbersServiceGrpc.NumbersServiceStub asyncStub;

    private final AtomicLong lastServerValue = new AtomicLong(0);

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT [%4$-7s] %5$s %n");
    }

    public NumberClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.asyncStub = NumbersServiceGrpc.newStub(channel);
    }

    public void requestNumbers(int first, int last) {
        Numbers.NumberRequest request = Numbers.NumberRequest.newBuilder()
                .setFirstValue(first)
                .setLastValue(last)
                .build();

        asyncStub.getNumbers(request, new StreamObserver<>() {
            @Override
            public void onNext(Numbers.NumberResponse response) {
                long number = response.getNumber();
                logger.info("New value from server: " + number);
                lastServerValue.set(number);
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.SEVERE, "Error in server stream", t);
            }

            @Override
            public void onCompleted() {
                logger.info("Server completed the stream.");
            }
        });
    }

    public void startClientLoop() throws InterruptedException {
        long currentValue = 0;

        for (int i = 0; i <= 50; i++) {
            long serverValue = lastServerValue.getAndSet(0);

            if (serverValue != 0) {
                currentValue += serverValue + 1;
                logger.info("currentValue: " + currentValue);
            } else {
                currentValue += 1;
                logger.info("currentValue: " + currentValue);
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        NumberClient client = new NumberClient("localhost", 50051);
        client.requestNumbers(0, 30);

        try {
            client.startClientLoop();
        } finally {
            client.shutdown();
        }
    }
}
