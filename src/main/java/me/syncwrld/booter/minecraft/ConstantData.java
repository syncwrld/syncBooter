package me.syncwrld.booter.minecraft;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import lombok.SneakyThrows;

public interface ConstantData extends Serializable {
  void load();

  @SneakyThrows
  default void resetGenerics() {
    Class<? extends ConstantData> aClass = this.getClass();
    Field[] fields = aClass.getFields();

    for (Field field : fields) {
      field.setAccessible(true);

      Type genericType = field.getGenericType();
      if (genericType == String.class) {
        field.set(field.get(String.class), "");
      }

      if (genericType == Integer.class) {
        field.set(field.get(Integer.class), 0);
      }

      if (genericType == Double.class) {
        field.set(field.get(Double.class), 0.0);
      }

      if (genericType == Character.class) {
        field.set(field.get(Character.class), null);
      }

      if (genericType == Array.class) {
        Class<?> type = field.getType();
        Object object = field.get(Array.class);
        Array array = (Array) object;

        field.set(array, Array.newInstance(type));
      }
    }
  }

}
