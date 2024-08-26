package protein;

public enum DnaNucleotide {
    A("Adenine"),
    C("Cytosine"),
    G("Guanine"),
    T("Thymine");

    private final String fullName;

    DnaNucleotide(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static DnaNucleotide fromChar(char nucleotideChar) {
        switch (nucleotideChar) {
            case 'A': return A;
            case 'C': return C;
            case 'G': return G;
            case 'T': return T;
            default: throw new IllegalArgumentException("Unknown DNA nucleotide: " + nucleotideChar);
        }
    }
}
