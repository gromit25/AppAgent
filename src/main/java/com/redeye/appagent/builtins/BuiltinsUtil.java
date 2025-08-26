package com.redeye.appagent.builtins;

public class BuiltinsUtil {
  
  public static void measureExecTime(
    String apiType,
    Object obj,
    String message,
    ExecuteMethod execMethod
  ) throws Exception {
    
    long start = System.currentTimeMillis();
    executeMethod.execute();
    long end = System.currentTimeMillis();

    Log.write(apiType, obj, end-start, message);
  }

  public static void measureExecTime(
    String apiType,
    String message,
    ExecuteMethod execMethod
  ) throws Exception {
    
    measureExecTime(apiType, null, message, execMethod);
  }
}
