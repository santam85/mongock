package com.github.cloudyrock.mongock;

/**
 *
 */
public class MongockException extends RuntimeException {
  public MongockException(String message) {
    super(message);
  }

  public MongockException(String message, Exception e) {
    super(message, e);
  }
}
