package Logging;

import Demo.TestLogging;
import Demo.TestLoggingInterface;

import java.lang.reflect.Proxy;

public class LoggingFactory {
    public static TestLoggingInterface create() {
        TestLoggingInterface target = new TestLogging();
        return (TestLoggingInterface) Proxy.newProxyInstance(
                LoggingFactory.class.getClassLoader(),
                new Class<?>[]{TestLoggingInterface.class},
                new LoggingHandler(target)
        );
    }
}
