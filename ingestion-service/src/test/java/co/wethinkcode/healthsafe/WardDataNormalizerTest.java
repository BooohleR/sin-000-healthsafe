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
    @Test
    void shouldRemoveWhitespaceFromWardId() {

        // Arrange
        String dirtyWardId = " W-03 ";

        // Act
        String actual = WardDataNormalizer.normalizeWardId(dirtyWardId);

        // Assert
        assertEquals("W-03",actual);
    }

    @Test
    void shouldNormalizeWingCasing() {

        String dirtyWing = "east wing";

        String actual = WardDataNormalizer.normalizeWing(dirtyWing);

        assertEquals("East Wing", actual);

    }

    @Test
    void shouldRemoveWhitespaceFromWing() {
        // Arrange
        String dirtyWing = " East Wing ";

        // Act
        String actual = WardDataNormalizer.normalizeWing(dirtyWing);

        // Assert
        assertEquals("East Wing", actual);
    }

    @Test
    void shouldRemoveExtraSpacesInsideWing() {
        // Arrange
        String dirtyWing = "South  Wing";

        // Act
        String actual = WardDataNormalizer.normalizeWing(dirtyWing);

        // Assert
        assertEquals("South Wing", actual);
    }

    @Test
    void shouldNormalizeDepartmentCasing() {

        String dirtyDep = "cardiology";

        String actual = WardDataNormalizer.normalizeDepartment(dirtyDep);

        assertEquals("Cardiology", actual);
    }

    @Test
    void shouldNormalizeUppercaseDepartmentCasing() {

        String dirtyDep = "PAEDIATRICS";

        String actual = WardDataNormalizer.normalizeDepartment(dirtyDep);

        assertEquals("Paediatrics", actual);

    } @Test
    void shouldNormalizePediatricsSpelling() {
        String dirtyDep = "Pediatrics";

        String actual = WardDataNormalizer.normalizeDepartment(dirtyDep);

        assertEquals("Paediatrics", actual);
    }

    @Test
    void shouldConvertValidBedsToInteger() {
        String dirtyBeds = "3";

        Integer actual = WardDataNormalizer.normalizeBedsAvailable(dirtyBeds);

        assertEquals(3, actual);
    }

    @Test
    void shouldReturnNullForMissingBeds() {
        String dirtyBeds = "N/A";

        Integer actual = WardDataNormalizer.normalizeBedsAvailable(dirtyBeds);

        assertEquals(null, actual);
    }

}