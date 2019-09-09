package tests;

import org.junit.jupiter.api.*;
import rocks.milspecsg.msdatasync.service.implementation.data.ApiSpongeSnapshotSerializer;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@DisplayName("MSDataSync sponge tests")
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
