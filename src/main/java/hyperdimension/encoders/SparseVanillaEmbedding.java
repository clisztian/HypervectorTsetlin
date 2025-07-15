package hyperdimension.encoders;



import hyperdimension.sparse.SparseBinaryVector;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SparseVanillaEmbedding {
    private Map<String, SparseBinaryVector> hvs;

    public SparseVanillaEmbedding() {
        this.hvs = new HashMap<>();
    }

    public SparseBinaryVector forward(String x) {
        return hvs.computeIfAbsent(x, k -> SparseBinaryVector.rand());
    }

    public String back(SparseBinaryVector hv) {
        return hvs.entrySet().stream()
                .min(Comparator.comparingInt(e -> hv.hammingDistance(e.getValue())))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public static void main(String[] args) {
        SparseVanillaEmbedding nameEmbed = new SparseVanillaEmbedding();
        SparseBinaryVector nameHV = nameEmbed.forward("John");
        System.out.println(nameEmbed.back(nameHV));
    }
}


