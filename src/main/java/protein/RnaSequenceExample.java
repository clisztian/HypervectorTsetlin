package protein;

public class RnaSequenceExample {
    public static void main(String[] args) {
        String rnaSequence = "ACGUACGUGCA";

        for (char nucleotideChar : rnaSequence.toCharArray()) {
            RnaNucleotide nucleotide = RnaNucleotide.fromChar(nucleotideChar);
            System.out.println(nucleotideChar + " : " + nucleotide.getFullName());
        }
    }
}
