package hyperdimension.examples.iris;

import hyperdimension.encoders.IntervalEmbedding;
import hyperdimension.encoders.VanillaBHV;

import java.util.Arrays;

public class IrisEncoderDecoder {

    //create an IntervalEmbedding for each feature of Iris dataset
    //use the min and max values of sepal length, sepal width, petal length, and petal width
    private static IntervalEmbedding sepalLengthEmbedding = new IntervalEmbedding(4.3- .01, 7.9 + .01, 10);
    private static IntervalEmbedding sepalWidthEmbedding = new IntervalEmbedding(2.0 - .01, 4.4+ .01, 10);
    private static IntervalEmbedding petalLengthEmbedding = new IntervalEmbedding(1.0- .01, 6.9+ .01, 10);
    private static IntervalEmbedding petalWidthEmbedding = new IntervalEmbedding(0.1- .01, 2.5+ .01, 10);

    public static VanillaBHV encode(Iris iris) {

        VanillaBHV sepalLengthHV = sepalLengthEmbedding.forward(iris.getSepalLength()).permute(0);
        VanillaBHV sepalWidthHV = sepalWidthEmbedding.forward(iris.getSepalWidth()).permute(1);
        VanillaBHV petalLengthHV = petalLengthEmbedding.forward(iris.getPetalLength()).permute(2);
        VanillaBHV petalWidthHV = petalWidthEmbedding.forward(iris.getPetalWidth()).permute(3);

        //combine the list of BHVs into a single BHV
        return VanillaBHV.logic_majority(Arrays.asList( sepalLengthHV, sepalWidthHV, petalLengthHV, petalWidthHV));

    }

    public Iris decode(VanillaBHV hv) {

        //decode the HV into the original values
        double sepalLength = sepalLengthEmbedding.back(hv.permute(0));
        double sepalWidth = sepalWidthEmbedding.back(hv.permute(-1));
        double petalLength = petalLengthEmbedding.back(hv.permute(-2));
        double petalWidth = petalWidthEmbedding.back(hv.permute(-3));

        return new Iris(sepalLength, sepalWidth, petalLength, petalWidth, "");
    }

    public static void main(String[] args) {
        Iris iris = new Iris(5.1, 3.5, 1.4, 0.2, "setosa");
        IrisEncoderDecoder encoderDecoder = new IrisEncoderDecoder();
        VanillaBHV hv = encoderDecoder.encode(iris);
        Iris decodedIris = encoderDecoder.decode(hv);
        System.out.println("Original Iris: " + iris);
        System.out.println("Decoded Iris: " + decodedIris);
    }


}
