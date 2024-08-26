package protein;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

public class ProteinDataReader {

    public List<ProteinMetaData> readProteinMetaData(String fileName) throws IOException, CsvValidationException {
        List<ProteinMetaData> proteinMetaDataList = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                String structureId = nextLine[0];
                String classification = nextLine[1];
                String experimentalTechnique = nextLine[2];
                String macromoleculeType = nextLine[3];
                int residueCount = nextLine[4].isEmpty() ? 0 : Integer.parseInt(nextLine[4]);
                double resolution = nextLine[5].isEmpty() ? 0 : Double.parseDouble(nextLine[5]);
                double structureMolecularWeight = nextLine[6].isEmpty() ? 0 :Double.parseDouble(nextLine[6]);
                String crystallizationMethod = nextLine[7];
                Double crystallizationTempK = nextLine[8].isEmpty() ? null : Double.parseDouble(nextLine[8]);
                double densityMatthews = nextLine[9].isEmpty() ? 0 : Double.parseDouble(nextLine[9]);
                double densityPercentSol = nextLine[10].isEmpty() ? 0 : Double.parseDouble(nextLine[10]);
                String pdbxDetails = nextLine[11];
                Double phValue = nextLine[12].isEmpty() ? null : Double.parseDouble(nextLine[12]);
                int publicationYear = nextLine[13].isEmpty() ? 0 : Integer.parseInt(nextLine[13]);

                proteinMetaDataList.add(new ProteinMetaData(structureId, classification, experimentalTechnique, macromoleculeType,
                        residueCount, resolution, structureMolecularWeight, crystallizationMethod, crystallizationTempK,
                        densityMatthews, densityPercentSol, pdbxDetails, phValue, publicationYear));
            }
        }
        return proteinMetaDataList;
    }

    public Map<String, List<ProteinSequence>> readProteinSequences(String fileName) throws IOException, CsvValidationException {
        Map<String, List<ProteinSequence>> proteinSequenceMap = new HashMap<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] nextLine;
            reader.readNext(); // Skip header
            while ((nextLine = reader.readNext()) != null) {
                String structureId = nextLine[0];
                String chainId = nextLine[1];
                String sequence = nextLine[2];
                int residueCount = Integer.parseInt(nextLine[3]);
                String macromoleculeType = nextLine[4];

                ProteinSequence proteinSequence = new ProteinSequence(structureId, chainId, sequence, residueCount, macromoleculeType);
                proteinSequenceMap.computeIfAbsent(structureId, k -> new ArrayList<>()).add(proteinSequence);
            }
        }
        return proteinSequenceMap;
    }


    public static List<ProteinMetaData> getData() {

        try {
            ProteinDataReader dataReader = new ProteinDataReader();
            List<ProteinMetaData> metaDataList = dataReader.readProteinMetaData("protein/pdb_data_no_dups.csv");
            Map<String, List<ProteinSequence>> sequenceMap = dataReader.readProteinSequences("protein/pdb_data_seq.csv");

            // Example of accessing the data
            for (ProteinMetaData metaData : metaDataList) {
                List<ProteinSequence> sequences = sequenceMap.get(metaData.getStructureId());
                if (sequences != null) {
                    metaData.setProteinSequences(sequences);
                }
            }

            return metaDataList;

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        try {
            ProteinDataReader dataReader = new ProteinDataReader();
            List<ProteinMetaData> metaDataList = dataReader.readProteinMetaData("protein/pdb_data_no_dups.csv");
            Map<String, List<ProteinSequence>> sequenceMap = dataReader.readProteinSequences("protein/pdb_data_seq.csv");

            // Example of accessing the data
            for (ProteinMetaData metaData : metaDataList) {
//                System.out.println("Structure ID: " + metaData.getStructureId() +
//                        ", Classification: " + metaData.getClassification() +
//                        ", Experimental Technique: " + metaData.getExperimentalTechnique() +
//                        ", Macromolecule Type: " + metaData.getMacromoleculeType() +
//                        ", Residue Count: " + metaData.getResidueCount() +
//                        ", Resolution: " + metaData.getResolution() +
//                        ", Structure Molecular Weight: " + metaData.getStructureMolecularWeight() +
//                        ", Crystallization Method: " + metaData.getCrystallizationMethod() +
//                        ", Crystallization Temp K: " + metaData.getCrystallizationTempK() +
//                        ", Density Matthews: " + metaData.getDensityMatthews() +
//                        ", Density Percent Sol: " + metaData.getDensityPercentSol() +
//                        ", PDBx Details: " + metaData.getPdbxDetails() +
//                        ", pH Value: " + metaData.getPhValue() +
//                        ", Publication Year: " + metaData.getPublicationYear());

                List<ProteinSequence> sequences = sequenceMap.get(metaData.getStructureId());
                if (sequences != null) {

                    metaData.setProteinSequences(sequences);

//                    for (ProteinSequence sequence : sequences) {
//                        System.out.println("Chain ID: " + sequence.getChainId() +
//                                ", Sequence: " + sequence.getSequence() +
//                                ", Residue Count: " + sequence.getResidueCount() +
//                                ", Macromolecule Type: " + sequence.getMacromoleculeType());
//                    }
                }


            }

            ProteinMetaDataStatistics stats = new ProteinMetaDataStatistics(metaDataList);

            System.out.println("Residue Count Stats: " + Arrays.toString(stats.getResidueCountStats()));
            System.out.println("Resolution Stats: " + Arrays.toString(stats.getResolutionStats()));
            System.out.println("Structure Molecular Weight Stats: " + Arrays.toString(stats.getStructureMolecularWeightStats()));
            System.out.println("Experimental Technique Counts: " + stats.getDistinctExperimentalTechniques());
            System.out.println("Macromolecule Type Counts: " + stats.getDistinctMacromoleculeTypes());
            System.out.println("Density Percent Sol Stats: " + Arrays.toString(stats.getDensityPercentSolStats()));
            System.out.println("Density Matthews Stats: " + Arrays.toString(stats.getDensityMatthewsStats()));
            System.out.println("pH Value Stats: " + Arrays.toString(stats.getPhValueStats()));
            System.out.println("Crystallization Method Counts: " + stats.getDistinctCrystallizationMethods());



        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
