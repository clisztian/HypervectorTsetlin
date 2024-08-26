package protein;

public enum AminoAcid {
    A("Alanine"),
    R("Arginine"),
    N("Asparagine"),
    D("Aspartic acid"),
    C("Cysteine"),
    Q("Glutamine"),
    E("Glutamic acid"),
    G("Glycine"),
    H("Histidine"),
    I("Isoleucine"),
    L("Leucine"),
    K("Lysine"),
    M("Methionine"),
    F("Phenylalanine"),
    P("Proline"),
    S("Serine"),
    T("Threonine"),
    W("Tryptophan"),
    Y("Tyrosine"),
    V("Valine"),
    U("Selenocysteine"),
    X("Unknown");

    private final String fullName;

    AminoAcid(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public static AminoAcid fromChar(char aminoAcidChar) {
        switch (aminoAcidChar) {
            case 'A': return A;
            case 'R': return R;
            case 'N': return N;
            case 'D': return D;
            case 'C': return C;
            case 'Q': return Q;
            case 'E': return E;
            case 'G': return G;
            case 'H': return H;
            case 'I': return I;
            case 'L': return L;
            case 'K': return K;
            case 'M': return M;
            case 'F': return F;
            case 'P': return P;
            case 'S': return S;
            case 'T': return T;
            case 'W': return W;
            case 'Y': return Y;
            case 'V': return V;
            default: throw new IllegalArgumentException("Unknown amino acid: " + aminoAcidChar);
        }
    }
}
