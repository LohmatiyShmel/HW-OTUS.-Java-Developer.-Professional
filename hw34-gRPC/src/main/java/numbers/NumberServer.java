package numbers;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

public class NumberServer {
    private static final Logger logger = Logger.getLogger(NumberServer.class.getName());
    private Server server;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT [%4$-7s] %5$s %n");
    }

    private void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new NumbersServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            NumberServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {
        @Override
        public void getNumbers(Numbers.NumberRequest request, StreamObserver<Numbers.NumberResponse> responseObserver) {
            int first = request.getFirstValue();
            int last = request.getLastValue();
            logger.info("Received request: first=" + first + ", last=" + last);

            if (first >= last) {
                logger.warning("Invalid range: firstValue must be less than lastValue.");
                responseObserver.onCompleted();
                return;
            }

            try {
                for (int i = first + 1; i <= last; i++) {
                    Numbers.NumberResponse response = Numbers.NumberResponse.newBuilder().setNumber(i).build();
                    responseObserver.onNext(response);
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.severe("Thread was interrupted during number generation.");
            } finally {
                responseObserver.onCompleted();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final NumberServer server = new NumberServer();
        server.start();
        server.blockUntilShutdown();
    }
}
