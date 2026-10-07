package co.wethinkcode.healthsafe;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void shouldReturnNullForNegativeBeds() {
        String dirtyBeds = "-1";

        Integer actual = WardDataNormalizer.normalizeBedsAvailable(dirtyBeds);

        assertNull(actual);
    }

    @Test
    void shouldRemoveWhitespaceFromBeds() {
        String dirtyBeds = " 3 ";

        Integer actual = WardDataNormalizer.normalizeBedsAvailable(dirtyBeds);

        assertEquals(3, actual);
    }
    @Test
    void shouldNormalizeCompleteWardRecord() {
        WardRecord ward = WardDataNormalizer.normalizeWard(
                "w-05",
                "east wing ",
                "PAEDIATRICS",
                "five"
        );

        assertEquals("W-05", ward.getWardId());
        assertEquals("East Wing", ward.getWing());
        assertEquals("Paediatrics", ward.getDepartment());
        assertNull(ward.getBedsAvailable());
        assertEquals(
                "bedsAvailable was non-numeric ('five') — flagged for follow-up",
                ward.getNotes()
        );
    }

    @Test
    void shouldFlagNegativeBedsInWardRecord() {
        WardRecord ward = WardDataNormalizer.normalizeWard(
                "W-04",
                "North Wing",
                "Oncology",
                "-1"
        );

        assertNull(ward.getBedsAvailable());
        assertEquals(
                "bedsAvailable was negative ('-1') — flagged for follow-up",
                ward.getNotes()
        );

    }

    @Test
    void shouldReturnNullForMissingWing() {
        String dirtyWing = "";

        String actual = WardDataNormalizer.normalizeWing(dirtyWing);

        assertNull(actual);
    }

}