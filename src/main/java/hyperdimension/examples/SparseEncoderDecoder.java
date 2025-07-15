package hyperdimension.examples;




import hyperdimension.encoders.SparseIntervalEmbedding;
import hyperdimension.encoders.SparseVanillaEmbedding;
import hyperdimension.sparse.SparseBinaryVector;

import java.util.Arrays;

public class SparseEncoderDecoder {
    private static final SparseBinaryVector PersonHV = SparseBinaryVector.rand();
    private static final SparseBinaryVector CatHV = SparseBinaryVector.rand();
    private static final SparseBinaryVector DogHV = SparseBinaryVector.rand();
    private static final SparseVanillaEmbedding nameEmbed = new SparseVanillaEmbedding();
    private static final SparseIntervalEmbedding ageEmbed = new SparseIntervalEmbedding(0, 100, 200);

    public static SparseBinaryVector encodePerson(Person p) {
        SparseBinaryVector nameHV = nameEmbed.forward(p.name).permute(1);
        SparseBinaryVector ageHV = ageEmbed.forward(p.age).permute(1);

        SparseBinaryVector petHV;
        if (p.pet instanceof Cat) {
            Cat pet = (Cat) p.pet;
            SparseBinaryVector catNameHV = nameEmbed.forward(pet.name).permute(2);
            SparseBinaryVector catAgeHV = ageEmbed.forward(pet.age).permute(2);
            petHV = SparseBinaryVector.bundle(Arrays.asList(CatHV, catNameHV, catAgeHV)).permute(1);
        } else {
            Dog pet = (Dog) p.pet;
            SparseBinaryVector dogNameHV = nameEmbed.forward(pet.name).permute(3);
            SparseBinaryVector dogAgeHV = ageEmbed.forward(pet.age).permute(3);
            petHV = SparseBinaryVector.bundle(Arrays.asList(DogHV, dogNameHV, dogAgeHV)).permute(1);
        }

        return SparseBinaryVector.bundle(Arrays.asList(PersonHV, nameHV, ageHV, petHV));
    }

    public static Person decodePerson(SparseBinaryVector hv) {
        if (!hv.related(PersonHV)) {
            throw new IllegalArgumentException("Hypervector is not related to a person");
        }
        SparseBinaryVector personHV = hv.permute(-1);
        String name = nameEmbed.back(personHV);
        double age = ageEmbed.back(personHV);

        System.out.println(name + " " + age);
        int distanceCat = personHV.hammingDistance(CatHV);
        int distanceDog = personHV.hammingDistance(DogHV);

        if (distanceCat < distanceDog) {
            SparseBinaryVector catHV = personHV.permute(-2);
            String catName = nameEmbed.back(catHV);
            double catAge = ageEmbed.back(catHV);
            return new Person(name, age, new Cat(catName, catAge));
        } else if (distanceDog < distanceCat) {
            SparseBinaryVector dogHV = personHV.permute(-3);
            String dogName = nameEmbed.back(dogHV);
            double dogAge = ageEmbed.back(dogHV);
            return new Person(name, age, new Dog(dogName, dogAge));
        } else {
            throw new IllegalArgumentException("Hypervector is not related to a valid pet");
        }
    }

    public static void main(String[] args) {
        Person joe = new Person("Joe", 16.5, new Dog("Blacky", 1));
        Person mia = new Person("Mia", 61, new Cat("Lucy", 9));

        SparseBinaryVector joeHV = SparseEncoderDecoder.encodePerson(joe);
        SparseBinaryVector miaHV = SparseEncoderDecoder.encodePerson(mia);

        Person decodedJoe = SparseEncoderDecoder.decodePerson(joeHV);
        Person decodedMia = SparseEncoderDecoder.decodePerson(miaHV);

        System.out.println(decodedJoe);
        System.out.println(decodedMia);
    }
}
