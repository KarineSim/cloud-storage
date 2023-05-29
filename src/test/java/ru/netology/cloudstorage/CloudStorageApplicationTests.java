//package ru.netology.cloudstorage;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.Map;
//
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class CloudStorageApplicationTests {
//
//    private final String HOST = "http://localhost:";
//    private final int PORT_SERVER = 8090;
//    private static final int PORT_DATABASE = 3306;
//
//    private static final String MYSQL_DATABASE = "netology";
//    private static final String MYSQL_USERNAME = "root";
//    private static final String MYSQL_PASSWORD = "mysql";
//
//    private static final Network NEW_NETWORK = Network.newNetwork();
//
//    @Container
//    private static MySQLContainer<?> database = new MySQLContainer<>("mysql")
//            .withNetwork(NEW_NETWORK)
//            .withExposedPorts(PORT_DATABASE)
//            .withDatabaseName(MYSQL_DATABASE)
//            .withUsername(MYSQL_USERNAME)
//            .withPassword(MYSQL_PASSWORD);
//
//    @Container
//    private GenericContainer<?> server = new GenericContainer<>("storage")
//            .withNetwork(NEW_NETWORK)
//            .withExposedPorts(PORT_SERVER)
//            .withEnv(Map.of("SPRING_DATASOURCE_URL", "jdbc:mysql://database:3306/netology"))
//            .dependsOn(database);
//
//    @Test
//    void contextDatabase() {
//        Assertions.assertTrue(database.isRunning());
//    }
//
//    @Test
//    void contextServer() {
//        Assertions.assertFalse(server.isRunning());
//    }
//}
//
