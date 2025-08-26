package com.redeye.appagent.builtins;

public class BuiltinsUtil {
  
  public static <T> T logExecTime(
    String apiType,
    Object obj,
    String message,
    Supplier<T> supplier
  ) throws Exception {

    //
    long start = System.currentTimeMillis();
    T result = supplier.get();
    long end = System.currentTimeMillis();

    //
    Log.write(apiType, obj, end-start, message);

    //
    return result;
  }
}
