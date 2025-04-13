package Demo;

import Logging.LoggingFactory;

public class Demo {
    public void action() {
        TestLoggingInterface test = LoggingFactory.create();
        test.calculation(6);
        test.calculation(6, 7);
        test.calculation(6, 7, "восемь");
    }
}
