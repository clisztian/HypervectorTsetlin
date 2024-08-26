package protein;

import hyperdimension.encoders.IntervalEmbedding;
import hyperdimension.encoders.VanillaBHV;
import hyperdimension.encoders.VanillaEmbedding;
import util.Quantization;

import java.util.*;

/**
 * Encoding the protein metadata
 */
public class ProteinEncoderDecoder {

    private ProteinMetaDataStatistics proteinMetaDataStatistics;
    private IntervalEmbedding residueCountEmbedding;
    private IntervalEmbedding resolutionEmbedding;
    private IntervalEmbedding structureMolecularWeightEmbedding;
    private IntervalEmbedding crystallizationTempKEmbedding;
    private IntervalEmbedding densityMatthewsEmbedding;
    private IntervalEmbedding densityPercentSolEmbedding;
    private IntervalEmbedding phValueEmbedding;

    private VanillaEmbedding experimentalTechniques = new VanillaEmbedding();
    private VanillaEmbedding macromoleculeTypes = new VanillaEmbedding();
    private int nGramSize = 3;
    private ArrayList<VanillaBHV> gramVectorList = new ArrayList<>();


    public ProteinEncoderDecoder(ProteinMetaDataStatistics proteinMetaDataStatistics) {
        this.proteinMetaDataStatistics = proteinMetaDataStatistics;
        buildNGrams();
    }

    public void initialize() {

        residueCountEmbedding = new IntervalEmbedding(proteinMetaDataStatistics.getResidueCountStats()[0], proteinMetaDataStatistics.getResidueCountStats()[2], 50);
        resolutionEmbedding = new IntervalEmbedding(proteinMetaDataStatistics.getResolutionStats()[0], proteinMetaDataStatistics.getResolutionStats()[2], 50);
        structureMolecularWeightEmbedding = new IntervalEmbedding(proteinMetaDataStatistics.getStructureMolecularWeightStats()[0], proteinMetaDataStatistics.getStructureMolecularWeightStats()[2], 50);
        crystallizationTempKEmbedding = new IntervalEmbedding(proteinMetaDataStatistics.getCrystallizationTempKStats()[0], proteinMetaDataStatistics.getCrystallizationTempKStats()[2], 50);
        densityMatthewsEmbedding = new IntervalEmbedding(proteinMetaDataStatistics.getDensityMatthewsStats()[0], proteinMetaDataStatistics.getDensityMatthewsStats()[2], 50);
        densityPercentSolEmbedding = new IntervalEmbedding(proteinMetaDataStatistics.getDensityPercentSolStats()[0], proteinMetaDataStatistics.getDensityPercentSolStats()[2], 50);
        phValueEmbedding = new IntervalEmbedding(proteinMetaDataStatistics.getPhValueStats()[0], proteinMetaDataStatistics.getPhValueStats()[2], 50);


    }

    public VanillaBHV encode(ProteinMetaData proteinMetaData) {

        VanillaBHV residueCountVector = residueCountEmbedding.forward(proteinMetaData.getResidueCount()).permute(0);
        VanillaBHV resolutionVector = resolutionEmbedding.forward(proteinMetaData.getResolution()).permute(1);
        VanillaBHV structureMolecularWeightVector = structureMolecularWeightEmbedding.forward(proteinMetaData.getStructureMolecularWeight()).permute(2);
        VanillaBHV crystallizationTempKVector = crystallizationTempKEmbedding.forward(proteinMetaData.getCrystallizationTempK() == null ? proteinMetaDataStatistics.getCrystallizationTempKStats()[1] : proteinMetaData.getCrystallizationTempK()).permute(3);
        VanillaBHV densityMatthewsVector = densityMatthewsEmbedding.forward(proteinMetaData.getDensityMatthews()).permute(4);
        VanillaBHV densityPercentSolVector = densityPercentSolEmbedding.forward(proteinMetaData.getDensityPercentSol()).permute(5);
        VanillaBHV phValueVector = phValueEmbedding.forward(proteinMetaData.getPhValue() == null ? proteinMetaDataStatistics.getPhValueStats()[1] : proteinMetaData.getPhValue()).permute(6);

//        VanillaBHV experimentalTechniqueVector = experimentalTechniques.forward(proteinMetaData.getExperimentalTechnique()).permute(7);
//        VanillaBHV macromoleculeTypeVector = macromoleculeTypes.forward(proteinMetaData.getMacromoleculeType()).permute(8);

        return VanillaBHV.logic_majority(Arrays.asList(residueCountVector, resolutionVector, structureMolecularWeightVector, crystallizationTempKVector, densityMatthewsVector, densityPercentSolVector, phValueVector));

    }

    public int[] simpleEncoder(ProteinMetaData proteinMetaData) {

        int[] residueEncoded = Quantization.quantizeValue(proteinMetaDataStatistics.getResidueCountStats()[0], proteinMetaDataStatistics.getResidueCountStats()[2], 20, proteinMetaData.getResidueCount());
        int[] resolutionEncoded = Quantization.quantizeValue(proteinMetaDataStatistics.getResolutionStats()[0], proteinMetaDataStatistics.getResolutionStats()[2], 20, proteinMetaData.getResolution());
        int[] structureMolecularWeightEncoded = Quantization.quantizeValue(proteinMetaDataStatistics.getStructureMolecularWeightStats()[0], proteinMetaDataStatistics.getStructureMolecularWeightStats()[2], 20, proteinMetaData.getStructureMolecularWeight());
        int[] crystallizationTempKEncoded = Quantization.quantizeValue(proteinMetaDataStatistics.getCrystallizationTempKStats()[0], proteinMetaDataStatistics.getCrystallizationTempKStats()[2], 20, proteinMetaData.getCrystallizationTempK());
        int[] densityMatthewsEncoded = Quantization.quantizeValue(proteinMetaDataStatistics.getDensityMatthewsStats()[0], proteinMetaDataStatistics.getDensityMatthewsStats()[2], 20, proteinMetaData.getDensityMatthews());
        int[] densityPercentSolEncoded = Quantization.quantizeValue(proteinMetaDataStatistics.getDensityPercentSolStats()[0], proteinMetaDataStatistics.getDensityPercentSolStats()[2], 20, proteinMetaData.getDensityPercentSol());
        int[] phValueEncoded = Quantization.quantizeValue(proteinMetaDataStatistics.getPhValueStats()[0], proteinMetaDataStatistics.getPhValueStats()[2], 20, proteinMetaData.getPhValue());


        return concatenateArrays(residueEncoded, resolutionEncoded, structureMolecularWeightEncoded, crystallizationTempKEncoded, densityMatthewsEncoded, densityPercentSolEncoded, phValueEncoded);


    }

    public ProteinMetaData decode(VanillaBHV vector) {

        int residueCount = (int)residueCountEmbedding.back(vector.permute(0));
        double resolution = resolutionEmbedding.back(vector.permute(-1));
        double structureMolecularWeight = structureMolecularWeightEmbedding.back(vector.permute(-2));
        double crystallizationTempK = crystallizationTempKEmbedding.back(vector.permute(-3));
        double densityMatthews = densityMatthewsEmbedding.back(vector.permute(-4));
        double densityPercentSol = densityPercentSolEmbedding.back(vector.permute(-5));
        double phValue = phValueEmbedding.back(vector.permute(-6));

//        String experimentalTechnique = experimentalTechniques.back(vector.permute(-7));
//        String macromoleculeType = macromoleculeTypes.back(vector.permute(-8));


        return new ProteinMetaData("", "",  "",  "",
                residueCount,  resolution,   structureMolecularWeight, "",
                crystallizationTempK,  densityMatthews,   densityPercentSol, "",
                phValue, 0);

    }

    public static int[] concatenateArrays(int[]... arrays) {
        // Calculate the total length of the concatenated array
        int totalLength = 0;
        for (int[] array : arrays) {
            totalLength += array.length;
        }

        // Create the resulting array with the calculated length
        int[] result = new int[totalLength];

        // Copy each array into the result array
        int currentPosition = 0;
        for (int[] array : arrays) {
            System.arraycopy(array, 0, result, currentPosition, array.length);
            currentPosition += array.length;
        }

        return result;
    }


    /**
     * Encodes the sequence using N-gram method
     * @param proteinMetaData
     * @return
     */
    public VanillaBHV encodeSequence(ProteinMetaData proteinMetaData) {

        ProteinSequence seq = proteinMetaData.getProteinSequences().get(0);

        String sequence = seq.getSequence();


        //for each letter in the sequence, get the corresponding vector from proteinNucleotides
        List<VanillaBHV> sequenceVectors = new ArrayList<>();
        for(int i = 0; i < sequence.length(); i++) {
            if (AtomicVectorLibrary.getProteinNucleotide(sequence.charAt(i)) == null)
                System.out.println("Null vector found for: " + sequence.charAt(i));

            sequenceVectors.add(AtomicVectorLibrary.getProteinNucleotide(sequence.charAt(i)));
        }

        int length = sequence.length();
        int numGrams = length - nGramSize + 1;


        // Generate n-grams with temporal binding
        List<VanillaBHV> nGrams = new ArrayList<>();

        VanillaBHV nGramVector;


        //are any of the sequence vectors null?
        if(sequenceVectors.contains(null)) {
            System.out.println("Null sequence vector found in sequence: " + sequence);
            return VanillaBHV.zeroVector();
        }


        for(int i = 0; i < numGrams; i++) {

            nGramVector = VanillaBHV.zeroVector();
            for (int j = 0; j < nGramSize; j++) {


                VanillaBHV permutedSj = sequenceVectors.get(i+j).permute(j);
                VanillaBHV combinedVector = permutedSj.xor(gramVectorList.get(j));
                nGramVector = nGramVector.xor(combinedVector);
            }
            nGrams.add(nGramVector);
        }

        return VanillaBHV.logic_majority(nGrams);

    }


    private void buildNGrams() {

        for(int i = 0; i < nGramSize; i++) {
            gramVectorList.add(VanillaBHV.randVector());
        }
    }


    public static VanillaBHV encodeSequence(int[] sequence, List<VanillaBHV> gramVectors, IntervalEmbedding sequenceIntervalAtoms, int n) {
        int length = sequence.length;
        int numGrams = length - n + 1;

        // Generate n-grams with temporal binding
        List<VanillaBHV> nGrams = new ArrayList<>();

        VanillaBHV nGramVector = VanillaBHV.zeroVector();

        for(int i = 0; i < numGrams; i++) {

            for (int j = 0; j < n; j++) {

                VanillaBHV permutedSj = sequenceIntervalAtoms.forward(sequence[i + j]).permute(j);
                VanillaBHV combinedVector = permutedSj.xor(gramVectors.get(j));
                nGramVector = nGramVector.xor(combinedVector);
            }
            nGrams.add(nGramVector);
        }

        // Aggregate n-grams to form the final sequence vector
        return VanillaBHV.logic_majority(nGrams);

    }

    public int getnGramSize() {
        return nGramSize;
    }

    public void setnGramSize(int nGramSize) {
        this.nGramSize = nGramSize;
        //create new gramVectorList
        gramVectorList = new ArrayList<>();
        for(int i = 0; i < nGramSize; i++) {
            gramVectorList.add(VanillaBHV.randVector());
        }
    }
}
