package hyperdimensional;

import hyperdimension.encoders.VanillaBHV;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class VanillaBHVTest {

    @Test
    void testPermutePositiveShift() {
        VanillaBHV hv = VanillaBHV.randVector();
        VanillaBHV shiftedHV = hv.permute(2);

        VanillaBHV reshifted = shiftedHV.permute(-2);

        //check they are equal
        assertArrayEquals(hv.toBytes(), reshifted.toBytes());


    }

}
