package rocks.milspecsg.msdatasync.tests;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@DisplayName("MSDataSync sponge rocks.milspecsg.msdatasync.tests")
public class ExampleTests {


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Basic Serialization")
    class Basic_Serialization {

        @DisplayName("Test serializing")
        @Test
        @Order(1)
        void test_serializing() {
            System.out.println("Starting serializer test...");
            System.out.println("Finished!");
        }
    }
}
