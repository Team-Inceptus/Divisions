package us.teaminceptus.divisions.api.division;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestDivision {

    @Test
    @DisplayName("Test Level Calculation")
    public void testLevelCalculation() {
        for (int i = 0; i < 100; i++) {
            double exp = Division.toExperience(i);
            Assertions.assertEquals(exp, Division.toExperience(Division.toLevel(exp)));
            Assertions.assertEquals(i, Division.toLevel(exp));
        }
    }

}
