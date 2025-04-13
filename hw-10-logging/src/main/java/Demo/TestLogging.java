package Demo;

import Logging.Log;

public class TestLogging implements TestLoggingInterface {
    @Log
    @Override
    public void calculation(int param) {
        System.out.println("calculation with one int param");
    }

    @Log
    @Override
    public void calculation(int param1, int param2) {
        System.out.println("calculation with two int param");
    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {
        System.out.println("calculation with two int and one string param");
    }
}
