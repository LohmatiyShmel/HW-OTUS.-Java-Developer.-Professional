package logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoggingHandler implements InvocationHandler {
    private final Object target;
    private final Map<Method, Boolean> methodLoggingCache = new HashMap<>();

    public LoggingHandler(Object target) {
        this.target = target;
        initMethodCache();
    }

    private void initMethodCache() {
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Log.class)) {
                try {
                    Method interfaceMethod = findInterfaceMethod(method);
                    methodLoggingCache.put(interfaceMethod, true);
                } catch (NoSuchMethodException ignored) {
                }
            }
        }
    }

    private Method findInterfaceMethod(Method implementationMethod) throws NoSuchMethodException {
        return target.getClass().getInterfaces()[0]
                .getMethod(implementationMethod.getName(), implementationMethod.getParameterTypes());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (methodLoggingCache.containsKey(method)) {
            System.out.printf("executed method: %s, param: %s%n",
                    method.getName(), Arrays.toString(args));
        }
        return method.invoke(target, args);
    }
}
