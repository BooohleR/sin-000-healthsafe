package co.wethinkcode.healthsafe;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WardDataNormalizerTest {

    @Test
    void shouldNormalizeLowercaseWardId() {

        // Arrange
        String dirtyWardId = "w-02";

        // Act
        String actual = WardDataNormalizer.normalizeWardId(dirtyWardId);

        // Assert
        assertEquals("W-02", actual);

    }
}