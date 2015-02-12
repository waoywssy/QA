package util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class BeanUtils {

    public static void setValue(Object object, String fieldName, Object fieldValue) {
        String METHOD_NAME = "setValue";
        Method method = null;
        Object[] args = {fieldValue};
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(object.getClass(), fieldName, Accessor.set);
        if ((propertyDescriptor != null)
                && ((method = propertyDescriptor.getWriteMethod()) != null)) {
            try {
                Class[] parameters = method.getParameterTypes();
                if ((parameters.length == 1) && (parameters[0].isAssignableFrom(Integer.TYPE))) {
                    args[0] = Integer.valueOf(args[0].toString());
                }
                method.invoke(object, args);
            } catch (Exception e) {
            }
        }
    }

    public static Object getValue(Object object, String fieldName) {
        String METHOD_NAME = "getValue";
        Method method = null;
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(object.getClass(), fieldName, Accessor.get);
        if ((propertyDescriptor != null)
                && ((method = propertyDescriptor.getReadMethod()) != null)) {
            try {
                return method.invoke(object, new Object[0]);
            } catch (Exception e) {
            }

        }

        return null;
    }

    public static PropertyDescriptor getPropertyDescriptor(Class klass, String paramName, Accessor type) {
        String METHOD_NAME = "getPropertyDescriptor";

        String method = type.name() + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);

        PropertyDescriptor descriptor = null;
        try {
            if ((type.equals(Accessor.get)) || (type.equals(Accessor.is))) {
                descriptor = new PropertyDescriptor(paramName, klass, method, null);
            } else {
                descriptor = new PropertyDescriptor(paramName, klass, null, method);
            }

        } catch (IntrospectionException e) {
        }

        return descriptor;
    }
}

enum Accessor {

    get, set, is;
}