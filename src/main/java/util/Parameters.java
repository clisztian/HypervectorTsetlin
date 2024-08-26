package util;

public class Parameters {

    public int n_clauses;
    public int threshold;
    public float s;
    public int maximum_number_of_literals;

    public Parameters(int n_clauses, int threshold, float s, int maximum_number_of_literals) {
        this.n_clauses = n_clauses;
        this.threshold = threshold;
        this.s = s;
        this.maximum_number_of_literals = maximum_number_of_literals;
    }


}
