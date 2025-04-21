package me.eigenraven.lwjgl3ify.mixins.early.fml;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import me.eigenraven.lwjgl3ify.EnumValuesField;
import me.eigenraven.lwjgl3ify.IExtensibleEnum;
import me.eigenraven.lwjgl3ify.UnsafeHacks;

@Mixin(value = { net.minecraftforge.common.util.EnumHelper.class }, remap = false)
public class EnumHelper {

    @Unique
    private static final Map<MethodType, MethodHandle> ENUM_CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();
    @Unique
    private static final Map<Class<?>, Field> ENUM_VALUES_FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * @author TwilightFlower
     * @reason If the function this is overwriting ever runs, the game will crash
     */
    @Overwrite
    private static void setup() {}

    /**
     * @author TwilightFlower
     * @reason Original reflection logic causes crash
     */
    @Overwrite
    public static void setFailsafeFieldValue(Field field, @Nullable Object target, @Nullable Object value)
        throws Exception {
        UnsafeHacks.setField(field, target, value);
    }

    /**
     * @author TwilightFlower
     * @reason Completely changed reflection logic for Java 9+ compatibility and simplification
     */
    @Nullable
    @Overwrite
    @SuppressWarnings("unchecked")
    private static <T extends Enum<?>> T addEnum(boolean test, final Class<T> enumType, @Nullable String enumName,
        final Class<?>[] paramTypes, @Nullable Object[] paramValues) {
        if (!IExtensibleEnum.class.isAssignableFrom(enumType)) {
            throw new RuntimeException(
                "Enum " + enumType.getName() + " was not made extensible, add it to lwjgl3ify configs.");
        }

        Class<?>[] actualParamTypes = new Class<?>[paramTypes.length + 2];
        actualParamTypes[0] = String.class;
        actualParamTypes[1] = int.class;
        System.arraycopy(paramTypes, 0, actualParamTypes, 2, paramTypes.length);
        MethodType ctorCacheType = MethodType.methodType(enumType, actualParamTypes);

        MethodHandle constructor = ENUM_CONSTRUCTOR_CACHE
            .computeIfAbsent(ctorCacheType, EnumHelper::lwjgl3ify$findConstructorHandle);
        Field valuesField = ENUM_VALUES_FIELD_CACHE.computeIfAbsent(enumType, EnumHelper::lwjgl3ify$findValuesField);

        // I don't know why this exists
        if (test) {
            return null;
        }

        try {
            Object[] actualParams = new Object[actualParamTypes.length];
            actualParams[0] = enumName;
            if (paramValues != null) {
                System.arraycopy(paramValues, 0, actualParams, 2, paramValues.length);
            }

            synchronized (enumType) {
                T[] values = (T[]) valuesField.get(null);
                actualParams[1] = values.length;
                T newValue;
                if (constructor.isVarargsCollector()) {
                    Object[] paramsWithoutVarArgs = new Object[actualParamTypes.length - 1];
                    System.arraycopy(actualParams, 0, paramsWithoutVarArgs, 0, actualParams.length - 1);
                    newValue = (T) constructor.invokeWithArguments(
                        lwjgl3ify$mergeArrays(paramsWithoutVarArgs, actualParams[actualParams.length - 1]));
                } else {
                    newValue = (T) constructor.invokeWithArguments(actualParams);
                }
                T[] newValues = ArrayUtils.add(values, newValue);
                valuesField.set(null, newValues);
                return newValue;
            }
        } catch (Throwable t) {
            throw new RuntimeException("Error extending enum " + enumType.getName(), t);
        }
    }

    @Unique
    @SuppressWarnings({ "unchecked", "PrimitiveArrayArgumentToVariableArgMethod" })
    private static <T> T[] lwjgl3ify$mergeArrays(T array1, T array2) {
        if (array2.getClass()
            .getComponentType()
            .isPrimitive()) {
            switch (array2.getClass()
                .getComponentType()
                .getName()) {
                case "int": {
                    return (T[]) ArrayUtils.addAll((T[]) array1, (int[]) array2);
                }
                case "double": {
                    return (T[]) ArrayUtils.addAll((T[]) array1, (double[]) array2);
                }
                case "float": {
                    return (T[]) ArrayUtils.addAll((T[]) array1, (float[]) array2);
                }
                case "long": {
                    return (T[]) ArrayUtils.addAll((T[]) array1, (long[]) array2);
                }
                case "short": {
                    return (T[]) ArrayUtils.addAll((T[]) array1, (short[]) array2);
                }
                case "byte": {
                    return (T[]) ArrayUtils.addAll((T[]) array1, (byte[]) array2);
                }
                case "char": {
                    return (T[]) ArrayUtils.addAll((T[]) array1, (char[]) array2);
                }
                case "boolean": {
                    return (T[]) ArrayUtils.addAll((T[]) array1, (boolean[]) array2);
                }
            }
        }
        return ArrayUtils.addAll((T[]) array1, (T[]) array2);
    }

    @Unique
    private static MethodHandle lwjgl3ify$findConstructorHandle(@NotNull MethodType cacheType) {
        Class<?> on = cacheType.returnType();
        MethodType ctorType = cacheType.changeReturnType(void.class);

        try {
            return MethodHandles.publicLookup()
                .findConstructor(on, ctorType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(
                String.format("Error getting constructor with type %s for enum %s", ctorType, on.getName()),
                e);
        }
    }

    @Unique
    private static Field lwjgl3ify$findValuesField(@NotNull Class<?> enumClass) {
        for (Field field : enumClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(EnumValuesField.class)) {
                return field;
            }
        }

        throw new RuntimeException("Could not find values field on enum " + enumClass.getName());
    }
}
