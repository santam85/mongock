package com.github.cloudyrock.mongock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public final class LogUtils {

  private LogUtils() {
  }

  public static void logMethodWithArguments(Logger logger, String methodName, List<Object> changelogInvocationParameters) {
    String arguments = changelogInvocationParameters.stream()
        .map(obj -> obj != null ? obj.getClass().getName() : "{null argument}")
        .collect(Collectors.joining(", "));
    logger.info("method[{}] with arguments: [{}]", methodName, arguments);

  }
}
