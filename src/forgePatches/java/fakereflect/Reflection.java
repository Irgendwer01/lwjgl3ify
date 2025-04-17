package fakereflect;

import net.minecraft.launchwrapper.Launch;

// Dummy methods to replace sun.reflect.Reflection
public class Reflection {

    public static void registerFieldsToFilter(Class<?> clazz, String... strings) {}

    public static void registerMethodsToFilter(Class<?> clazz, String... strings) {}

    public static <T> Class getCallerClass(int depth) {
        try {
            return Launch.classLoader.findClass(
                Thread.currentThread()
                    .getStackTrace()[depth+1].getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
