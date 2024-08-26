package protein;


public class ProteinSequence {
    private String structureId;
    private String chainId;
    private String sequence;
    private int residueCount;
    private String macromoleculeType;

    // Constructor
    public ProteinSequence(String structureId, String chainId, String sequence, int residueCount, String macromoleculeType) {
        this.structureId = structureId;
        this.chainId = chainId;
        this.sequence = sequence;
        this.residueCount = residueCount;
        this.macromoleculeType = macromoleculeType;
    }

    // Getters and setters
    public String getStructureId() {
        return structureId;
    }

    public void setStructureId(String structureId) {
        this.structureId = structureId;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getResidueCount() {
        return residueCount;
    }

    public void setResidueCount(int residueCount) {
        this.residueCount = residueCount;
    }

    public String getMacromoleculeType() {
        return macromoleculeType;
    }

    public void setMacromoleculeType(String macromoleculeType) {
        this.macromoleculeType = macromoleculeType;
    }
}
