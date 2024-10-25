import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            String filePath = "testcase.json"; // Path to your JSON file
            JSONObject jsonObject = readJsonFile(filePath);
            Map<Integer, BigInteger> points = parseAndDecodePoints(jsonObject);
            BigInteger secret = findConstantTerm(points, jsonObject.getJSONObject("keys").getInt("k"));
            System.out.println("The constant term (secret) is: " + secret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Step 1: Read the JSON file
    private static JSONObject readJsonFile(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        JSONTokener tokener = new JSONTokener(fis);
        return new JSONObject(tokener);
    }

    // Step 2: Parse and decode points
    private static Map<Integer, BigInteger> parseAndDecodePoints(JSONObject jsonObject) {
        Map<Integer, BigInteger> points = new HashMap<>();
        JSONObject keysObject = jsonObject.getJSONObject("keys");

        for (String key : jsonObject.keySet()) {
            if (!key.equals("keys")) {
                JSONObject pointObject = jsonObject.getJSONObject(key);
                int x = Integer.parseInt(key);
                int base = pointObject.getInt("base");
                String valueStr = pointObject.getString("value");
                BigInteger y = new BigInteger(valueStr, base);  // Decode y based on the given base
                points.put(x, y);
            }
        }
        return points;
    }

    // Step 3: Lagrange Interpolation to find the constant term (c)
    private static BigInteger findConstantTerm(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;

        // Iterate through each point (xi, yi) in points
        for (Map.Entry<Integer, BigInteger> point : points.entrySet()) {
            int xi = point.getKey();
            BigInteger yi = point.getValue();
            BigInteger li = BigInteger.ONE;

            // Compute Lagrange basis polynomial L_i(x) for each term
            for (Map.Entry<Integer, BigInteger> otherPoint : points.entrySet()) {
                int xj = otherPoint.getKey();
                if (xi != xj) {
                    li = li.multiply(BigInteger.valueOf(-xj)).divide(BigInteger.valueOf(xi - xj));
                }
            }

            // Add the term to the result (yi * li(0))
            result = result.add(yi.multiply(li));
        }
        return result;
    }
}
