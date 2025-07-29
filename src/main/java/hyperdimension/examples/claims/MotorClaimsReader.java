package hyperdimension.examples.claims;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MotorClaimsReader {
    public static List<MotorClaim> readClaims(String resourcePath) throws Exception {
        List<MotorClaim> claims = new ArrayList<>();

        try (InputStream is = MotorClaimsReader.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false; // skip header
                    continue;
                }
                String[] tokens = line.split(",", -1);
                if (tokens.length == 12) {
                    try {
                        claims.add(new MotorClaim(tokens));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid line: " + line);
                    }
                }
            }
        }

        return claims;
    }
}