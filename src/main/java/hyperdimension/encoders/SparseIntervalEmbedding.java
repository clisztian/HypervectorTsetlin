package hyperdimension.encoders;

import hyperdimension.sparse.SparseBinaryVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class SparseIntervalEmbedding {

    public List<SparseBinaryVector> intervals;
    private double low;
    private double high;
    private double step;

    private double SLACK = .00001;
    public SparseIntervalEmbedding(double low, double high, int divisions) {
        this.low = low - SLACK;
        this.high = high + SLACK;
        this.step = (this.high - this.low) / divisions;
        this.intervals = new ArrayList<>();

        IntervalEncoding encoding = new IntervalEncoding(divisions);
        List<SparseBinaryVector> intervalVectors = encoding.intervalVectors;

        for (int i = 0; i < divisions; i++) {
            this.intervals.add(intervalVectors.get(i));
        }
    }

    public SparseBinaryVector forward(double x) {

        //if x less than low, set to low
        //if x greater than high, set to high

        if (x < low) {
            x = low + SLACK;
        }
        else if (x > high) {
            x = high - SLACK;
        }


        int index = (int) ((x - low) / step);
        return intervals.get(index);
    }

    public double back(SparseBinaryVector hv) {
        int index = IntStream.range(0, intervals.size())
                .boxed()
                .min(Comparator.comparingInt(i -> hv.hammingDistance(intervals.get(i))))
                .orElse(0);
        return low + index * step;
    }

}
