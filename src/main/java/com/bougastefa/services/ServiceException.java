package com.bougastefa.services;

/**
 * Custom runtime exception class for service-layer specific exceptions.
 * This exception is thrown when operations in the service layer encounter errors
 * that cannot be handled at that level and need to be propagated to higher layers.
 * Using a specific exception type allows for more precise error handling and
 * distinguishes service-layer failures from other types of exceptions.
 */
public class ServiceException extends RuntimeException {
  
  /**
   * Constructs a new ServiceException with the specified detail message.
   * 
   * @param message The detail message explaining the reason for the exception.
   *                This message is saved for later retrieval by the getMessage() method.
   */
  public ServiceException(String message) {
    super(message);
  }

  /**
   * Constructs a new ServiceException with the specified detail message and cause.
   * 
   * @param message The detail message explaining the reason for the exception.
   *                This message is saved for later retrieval by the getMessage() method.
   * @param cause The cause of this exception (typically a lower-level exception).
   *              This is saved for later retrieval by the getCause() method.
   */
  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
