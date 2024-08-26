package protein;

import hyperdimension.encoders.VanillaBHV;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AtomicVectorLibrary {
    static final Map<Character, VanillaBHV> proteinNucleotides = new HashMap<>();
    static final Map<Character, VanillaBHV> dnaNucleotides = new HashMap<>();
    static final Map<Character, VanillaBHV> rnaNucleotides = new HashMap<>();

    static {
        // Initialize protein nucleotides
        for (ProteinAminoAcid aa : ProteinAminoAcid.values()) {
            proteinNucleotides.put(aa.getSymbol(), VanillaBHV.randVector());
        }

        // Initialize DNA nucleotides
        for (DnaNucleotide dna : DnaNucleotide.values()) {
            dnaNucleotides.put(dna.getSymbol(), VanillaBHV.randVector());
        }

        // Initialize RNA nucleotides
        for (RnaNucleotide rna : RnaNucleotide.values()) {
            rnaNucleotides.put(rna.getSymbol(), VanillaBHV.randVector());
        }
    }

    public static VanillaBHV getProteinNucleotide(char symbol) {
        return proteinNucleotides.get(symbol);
    }

    public static VanillaBHV getDnaNucleotide(char symbol) {
        return dnaNucleotides.get(symbol);
    }

    public static VanillaBHV getRnaNucleotide(char symbol) {
        return rnaNucleotides.get(symbol);
    }

    // Enum for Protein Amino Acids
    public enum ProteinAminoAcid {
        A('A', "Alanine"),
        B('B', "Aspartic acid or Asparagine"),
        R('R', "Arginine"),
        N('N', "Asparagine"),
        D('D', "Aspartic acid"),
        C('C', "Cysteine"),
        Q('Q', "Glutamine"),
        E('E', "Glutamic acid"),
        G('G', "Glycine"),
        H('H', "Histidine"),
        I('I', "Isoleucine"),
        L('L', "Leucine"),
        K('K', "Lysine"),
        M('M', "Methionine"),
        O('O', "Pyrrolysine"),
        F('F', "Phenylalanine"),
        P('P', "Proline"),
        S('S', "Serine"),
        T('T', "Threonine"),
        W('W', "Tryptophan"),
        Y('Y', "Tyrosine"),
        U('U', "Selenocysteine"),
        V('V', "Valine"),
        Z('Z', "Glutamic acid or Glutamine"),
        X('X', "Unknown");


        private final char symbol;
        private final String name;

        ProteinAminoAcid(char symbol, String name) {
            this.symbol = symbol;
            this.name = name;
        }

        public char getSymbol() {
            return symbol;
        }

        public String getName() {
            return name;
        }
    }

    // Enum for DNA Nucleotides
    public enum DnaNucleotide {
        A('A', "Adenine"),
        C('C', "Cytosine"),
        G('G', "Guanine"),
        T('T', "Thymine");

        private final char symbol;
        private final String name;

        DnaNucleotide(char symbol, String name) {
            this.symbol = symbol;
            this.name = name;
        }

        public char getSymbol() {
            return symbol;
        }

        public String getName() {
            return name;
        }
    }

    // Enum for RNA Nucleotides
    public enum RnaNucleotide {
        A('A', "Adenine"),
        C('C', "Cytosine"),
        G('G', "Guanine"),
        U('U', "Uracil");

        private final char symbol;
        private final String name;

        RnaNucleotide(char symbol, String name) {
            this.symbol = symbol;
            this.name = name;
        }

        public char getSymbol() {
            return symbol;
        }

        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        // Example usage
        System.out.println("Protein Nucleotide A: " + getProteinNucleotide('A'));
        System.out.println("DNA Nucleotide A: " + getDnaNucleotide('A'));
        System.out.println("RNA Nucleotide A: " + getRnaNucleotide('A'));
    }
}
