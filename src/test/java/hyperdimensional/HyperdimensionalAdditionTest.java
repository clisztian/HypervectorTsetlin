package hyperdimensional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static hyperdimension.examples.validations.HyperdimensionalDecoding.aggregateVectors;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static util.Quantize.listToMatrix;

public class HyperdimensionalAdditionTest {

    @Test
    public void testAddition() {
        boolean[] vector1 = {true, false, true, false, true};
        boolean[] vector2 = {false, true, true, false, true};
        boolean[] vector3 = {true, true, false, false, false};

        List<boolean[]> vectors = Arrays.asList(vector1, vector2, vector3);

        boolean[] expected = {true, true, true, false, true};
        boolean[] result = aggregateVectors(listToMatrix(vectors));

        assertArrayEquals(expected, result, "The addition function did not produce the expected result.");
    }
}