package protein;

import java.util.*;
import java.util.stream.Collectors;

public class ProteinMetaDataStatistics {

    private double[] residueCountStats = new double[3]; // [min, mean, max]
    private double[] resolutionStats = new double[3]; // [min, mean, max]
    private double[] structureMolecularWeightStats = new double[3]; // [min, mean, max]
    private double[] crystallizationTempKStats = new double[3]; // [min, mean, max]
    private double[] densityMatthewsStats = new double[3]; // [min, mean, max]
    private double[] densityPercentSolStats = new double[3]; // [min, mean, max]
    private double[] phValueStats = new double[3]; // [min, mean, max]
    private int[] publicationYearStats = new int[3]; // [min, mean, max]

    private Set<String> distinctStructureIds = new HashSet<>();
    private Set<String> distinctClassifications = new HashSet<>();
    private Set<String> distinctExperimentalTechniques = new HashSet<>();
    private Set<String> distinctMacromoleculeTypes = new HashSet<>();
    private Set<String> distinctCrystallizationMethods = new HashSet<>();
    private Set<String> distinctPdbxDetails = new HashSet<>();

    public ProteinMetaDataStatistics(List<ProteinMetaData> proteinMetaDataList) {
        computeStatistics(proteinMetaDataList);
    }

    private void computeStatistics(List<ProteinMetaData> proteinMetaDataList) {
        List<Double> residueCounts = new ArrayList<>();
        List<Double> resolutions = new ArrayList<>();
        List<Double> structureMolecularWeights = new ArrayList<>();
        List<Double> crystallizationTempKs = new ArrayList<>();
        List<Double> densityMatthewsList = new ArrayList<>();
        List<Double> densityPercentSols = new ArrayList<>();
        List<Double> phValues = new ArrayList<>();
        List<Integer> publicationYears = new ArrayList<>();

        for (ProteinMetaData pmd : proteinMetaDataList) {
            residueCounts.add(pmd.getResidueCount());
            resolutions.add(pmd.getResolution());
            structureMolecularWeights.add(pmd.getStructureMolecularWeight());
            if (pmd.getCrystallizationTempK() != null) {
                crystallizationTempKs.add(pmd.getCrystallizationTempK());
            }
            densityMatthewsList.add(pmd.getDensityMatthews());
            densityPercentSols.add(pmd.getDensityPercentSol());
            if (pmd.getPhValue() != null) {
                phValues.add(pmd.getPhValue());
            }
            publicationYears.add(pmd.getPublicationYear());

            distinctStructureIds.add(pmd.getStructureId());
            distinctClassifications.add(pmd.getClassification());
            distinctExperimentalTechniques.add(pmd.getExperimentalTechnique());
            distinctMacromoleculeTypes.add(pmd.getMacromoleculeType());
            distinctCrystallizationMethods.add(pmd.getCrystallizationMethod());
            distinctPdbxDetails.add(pmd.getPdbxDetails());
        }

        residueCountStats = computeStats(residueCounts);
        resolutionStats = computeStats(resolutions);
        structureMolecularWeightStats = computeStats(structureMolecularWeights);
        crystallizationTempKStats = computeStats(crystallizationTempKs);
        densityMatthewsStats = computeStats(densityMatthewsList);
        densityPercentSolStats = computeStats(densityPercentSols);
        phValueStats = computeStats(phValues);
        publicationYearStats = computeIntStats(publicationYears);
    }

    private double[] computeStats(List<? extends Number> values) {
        double[] stats = new double[3];
        stats[0] = values.stream().mapToDouble(Number::doubleValue).min().orElse(Double.NaN);
        stats[1] = values.stream().mapToDouble(Number::doubleValue).average().orElse(Double.NaN);
        stats[2] = values.stream().mapToDouble(Number::doubleValue).max().orElse(Double.NaN);
        return stats;
    }

    private int[] computeIntStats(List<Integer> values) {
        int[] stats = new int[3];
        stats[0] = values.stream().mapToInt(Integer::intValue).min().orElse(Integer.MIN_VALUE);
        stats[1] = (int) values.stream().mapToInt(Integer::intValue).average().orElse(Double.NaN);
        stats[2] = values.stream().mapToInt(Integer::intValue).max().orElse(Integer.MAX_VALUE);
        return stats;
    }

    // Getters for the statistics

    public double[] getResidueCountStats() { return residueCountStats; }
    public double[] getResolutionStats() { return resolutionStats; }
    public double[] getStructureMolecularWeightStats() { return structureMolecularWeightStats; }
    public double[] getCrystallizationTempKStats() { return crystallizationTempKStats; }
    public double[] getDensityMatthewsStats() { return densityMatthewsStats; }
    public double[] getDensityPercentSolStats() { return densityPercentSolStats; }
    public double[] getPhValueStats() { return phValueStats; }
    public int[] getPublicationYearStats() { return publicationYearStats; }

    public Set<String> getDistinctStructureIds() { return distinctStructureIds; }
    public Set<String> getDistinctClassifications() { return distinctClassifications; }
    public Set<String> getDistinctExperimentalTechniques() { return distinctExperimentalTechniques; }
    public Set<String> getDistinctMacromoleculeTypes() { return distinctMacromoleculeTypes; }
    public Set<String> getDistinctCrystallizationMethods() { return distinctCrystallizationMethods; }
    public Set<String> getDistinctPdbxDetails() { return distinctPdbxDetails; }



    public static void main(String[] args) {
        List<ProteinMetaData> proteinMetaDataList = Arrays.asList(
                new ProteinMetaData("100D", "HYDROLASE", "X-RAY DIFFRACTION", "Protein", 100, 2.0, 12345.6, "Method A", 298.0, 2.5, 50.0, "Details A", 7.0, 2001),
                new ProteinMetaData("101D", "OXIDOREDUCTASE", "X-RAY DIFFRACTION", "Protein", 150, 1.8, 23456.7, "Method B", 300.0, 2.6, 60.0, "Details B", 6.5, 2002),
                new ProteinMetaData("102D", "TRANSFERASE", "X-RAY DIFFRACTION", "Protein", 200, 2.1, 34567.8, "Method C", 310.0, 2.7, 70.0, "Details C", 6.0, 2003),
                new ProteinMetaData("103D", "OTHER", "X-RAY DIFFRACTION", "Protein", 250, 1.9, 45678.9, "Method D", 320.0, 2.8, 80.0, "Details D", 5.5, 2004)
        );

        ProteinMetaDataStatistics stats = new ProteinMetaDataStatistics(proteinMetaDataList);

        System.out.println("Residue Count Stats: " + Arrays.toString(stats.getResidueCountStats()));
        System.out.println("Resolution Stats: " + Arrays.toString(stats.getResolutionStats()));
        System.out.println("Structure Molecular Weight Stats: " + Arrays.toString(stats.getStructureMolecularWeightStats()));
        System.out.println("Distinct Classifications: " + stats.getDistinctClassifications());
    }
}
