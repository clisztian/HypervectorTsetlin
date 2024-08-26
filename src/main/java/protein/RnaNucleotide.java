package protein;

public enum RnaNucleotide {
    A("Adenine"),
    C("Cytosine"),
    G("Guanine"),
    U("Uracil");

    private final String fullName;

    RnaNucleotide(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static RnaNucleotide fromChar(char nucleotideChar) {
        switch (nucleotideChar) {
            case 'A': return A;
            case 'C': return C;
            case 'G': return G;
            case 'U': return U;
            default: throw new IllegalArgumentException("Unknown RNA nucleotide: " + nucleotideChar);
        }
    }


}
