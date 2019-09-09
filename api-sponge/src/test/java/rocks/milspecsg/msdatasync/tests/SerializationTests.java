package rocks.milspecsg.msdatasync.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import rocks.milspecsg.msdatasync.service.implementation.data.ApiSpongeSnapshotSerializer;

import javax.inject.Inject;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@DisplayName("MSDataSync api-sponge serialization tests")
public class SerializationTests {

    protected Duration timeout = Duration.ofSeconds(10000);


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Basic Serialization")
    class Basic_Serialization {

        @Inject
        ApiSpongeSnapshotSerializer apiSpongeSnapshotSerializer;

        @DisplayName("Test serializing")
        @Test
        @Order(1)
        void test_serializing() {
            System.out.println("Starting serializer test...");
            assertTimeoutPreemptively(timeout, () -> {

            });
            System.out.println("Finished CityParser test successfully!");
        }
    }

}
