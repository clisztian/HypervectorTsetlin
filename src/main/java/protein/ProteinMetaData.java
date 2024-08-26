package protein;

import hyperdimension.encoders.VanillaBHV;

import java.util.List;

public class ProteinMetaData {
    private String structureId;
    private String classification;
    private String experimentalTechnique;
    private String macromoleculeType;
    private int residueCount;
    private double resolution;
    private double structureMolecularWeight;
    private String crystallizationMethod;
    private Double crystallizationTempK; // Use Double to handle null values
    private double densityMatthews;
    private double densityPercentSol;
    private String pdbxDetails;
    private Double phValue; // Use Double to handle null values
    private int publicationYear;

    //reference to the ProteinSequence class
    private List<ProteinSequence> proteinSequences;

    private VanillaBHV encodedProteinMetaData;




    // Constructor
    public ProteinMetaData(String structureId, String classification, String experimentalTechnique, String macromoleculeType,
                           int residueCount, double resolution, double structureMolecularWeight, String crystallizationMethod,
                           Double crystallizationTempK, double densityMatthews, double densityPercentSol, String pdbxDetails,
                           Double phValue, int publicationYear) {
        this.structureId = structureId;
        this.classification = classification;
        this.experimentalTechnique = experimentalTechnique;
        this.macromoleculeType = macromoleculeType;
        this.residueCount = residueCount;
        this.resolution = resolution;
        this.structureMolecularWeight = structureMolecularWeight;
        this.crystallizationMethod = crystallizationMethod;
        this.crystallizationTempK = crystallizationTempK;
        this.densityMatthews = densityMatthews;
        this.densityPercentSol = densityPercentSol;
        this.pdbxDetails = pdbxDetails;
        this.phValue = phValue;
        this.publicationYear = publicationYear;
    }

    // Getters and setters
    public String getStructureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getExperimentalTechnique() {
        return experimentalTechnique;
    }

    public void setExperimentalTechnique(String experimentalTechnique) {
        this.experimentalTechnique = experimentalTechnique;
    }

    public String getMacromoleculeType() {
        return macromoleculeType;
    }

    public void setMacromoleculeType(String macromoleculeType) {
        this.macromoleculeType = macromoleculeType;
    }

    public double getResidueCount() {
        return residueCount > 0 ? Math.log(residueCount) : 0;
    }

    public void setResidueCount(int residueCount) {
        this.residueCount = residueCount;
    }

    public double getResolution() {
        return resolution > 0 ? Math.sqrt(resolution) : 0;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    public double getStructureMolecularWeight() {
        return structureMolecularWeight > 0 ? Math.log(structureMolecularWeight) : 0;
    }

    public void setStructureMolecularWeight(double structureMolecularWeight) {
        this.structureMolecularWeight = structureMolecularWeight;
    }

    public String getCrystallizationMethod() {
        return crystallizationMethod;
    }

    public void setCrystallizationMethod(String crystallizationMethod) {
        this.crystallizationMethod = crystallizationMethod;
    }

    public Double getCrystallizationTempK() {
        return crystallizationTempK;
    }

    public void setCrystallizationTempK(Double crystallizationTempK) {
        this.crystallizationTempK = crystallizationTempK;
    }

    public double getDensityMatthews() {
        return densityMatthews > 0 ? Math.sqrt(densityMatthews) : 0;
    }

    public void setDensityMatthews(double densityMatthews) {
        this.densityMatthews = densityMatthews;
    }

    public double getDensityPercentSol() {
        return densityPercentSol;
    }

    public void setDensityPercentSol(double densityPercentSol) {
        this.densityPercentSol = densityPercentSol;
    }

    public String getPdbxDetails() {
        return pdbxDetails;
    }

    public void setPdbxDetails(String pdbxDetails) {
        this.pdbxDetails = pdbxDetails;
    }

    public Double getPhValue() {
        return phValue;
    }

    public void setPhValue(Double phValue) {
        this.phValue = phValue;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public List<ProteinSequence> getProteinSequences() {
        return proteinSequences;
    }

    public void setProteinSequences(List<ProteinSequence> proteinSequences) {
        this.proteinSequences = proteinSequences;
    }

    public void setEncodedProteinMetaData(VanillaBHV encodedProteinMetaData) {
        this.encodedProteinMetaData = encodedProteinMetaData;
    }
    public VanillaBHV getEncodedProteinMetaData() {
        return encodedProteinMetaData;
    }

    public float[] getVector() {

        //return a vector of all the numerical values
        return new float[] {(float)getResidueCount(), (float)getResolution(), (float)getStructureMolecularWeight(), (float)getCrystallizationTempK().floatValue(), (float)getDensityMatthews(), (float)getDensityPercentSol(), (float)getPhValue().floatValue()};

    }
}
