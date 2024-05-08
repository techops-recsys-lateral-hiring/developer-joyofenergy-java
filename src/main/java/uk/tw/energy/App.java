/**
 * Package containing the Joy of Energy application.
 *
 * <p>This package contains the main entry point for the application, as well as the configuration
 * for the Spring Boot application.
 */
package uk.tw.energy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the application.
 *
 * <p>This class is the main entry point for the application, and it is responsible for starting the
 * application.
 */
@SpringBootApplication
public class App {

  /**
   * Constructor for App.
   *
   * <p>This constructor is public so that Spring can create a new instance of the class when it is
   * autowired.
   */
  public App() {}

  /***
   * Starts the application.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
