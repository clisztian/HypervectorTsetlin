package hyperdimensional;

import hyperdimension.sequences.SequenceEncoder;
import org.junit.jupiter.api.Test;

public class SequenceTest {


    @Test
    public void testSequence() {
        // Example sequences (e.g., sequences of integers representing different patterns)
        double[][] sequences = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 0},
                {1, 3, 5, 7, 9},
                {2, 4, 6, 8, 0}
        };

        int nGramSize = 3;
        int quantizationLevels = 10;

        double[] querySequence = {1, 3, 5};


        SequenceEncoder encoder = new SequenceEncoder(sequences, quantizationLevels, nGramSize);

        Number prediction = encoder.predict(querySequence);

        //assert that the prediction is close to 7
        assert prediction.doubleValue() == 7.0;

    }
}
