package util;

import java.util.ArrayList;
import java.util.Random;

public class GenerateParameters {

    public static ArrayList<Parameters> generateParameters(int num) {

        Random rng = new Random();

        ArrayList<Parameters> parameters = new ArrayList<Parameters>();
        for(int i = 0; i < num; i++) {

            int nclauses = 10 + rng.nextInt(1000);
            int threshold = 10 + rng.nextInt(1000);

            int max_literals = rng.nextFloat() < .5 ? 0 : 5 + rng.nextInt(30);

            parameters.add(new Parameters(nclauses, threshold, 2f + rng.nextFloat()*10f, max_literals));

        }
        return parameters;
    }

}
