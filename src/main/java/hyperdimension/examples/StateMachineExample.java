package hyperdimension.examples;

import hyperdimension.encoders.VanillaBHV;
import hyperdimension.encoders.VanillaPermutation;

import java.util.Arrays;

import static hyperdimension.encoders.VanillaBHV.hammingDistance;

public class StateMachineExample {
    // States
    private static final VanillaBHV locked = VanillaBHV.randVector();
    private static final VanillaBHV unlocked = VanillaBHV.randVector();

    // Input symbols
    private static final VanillaBHV token = VanillaBHV.randVector();
    private static final VanillaBHV push = VanillaBHV.randVector();

    // Next state permutation
    private static final VanillaPermutation PNext = VanillaPermutation.random();
    private static final VanillaPermutation QNext = PNext.invert();

    // Transition hypervector
    private static final VanillaBHV transition = VanillaBHV.logic_majority(Arrays.asList(
            push.xor(locked).xor(PNext.apply(locked)),
            token.xor(locked).xor(PNext.apply(unlocked)),
            push.xor(unlocked).xor(PNext.apply(locked)),
            token.xor(unlocked).xor(PNext.apply(unlocked))
    ));

    // Compute next state
    public static VanillaBHV nextState(VanillaBHV state, VanillaBHV input) {
        return QNext.apply(transition.xor(input).xor(state));
    }

    // Find closest state
    public static VanillaBHV closest(VanillaBHV noisy) {
        return Arrays.asList(locked, unlocked).stream()
                .min((a, b) -> Integer.compare(noisy.hammingDistance(a), noisy.hammingDistance(b)))
                .orElse(null);
    }

    public static void main(String[] args) {
        // Check if the transition system works as expected


        VanillaBHV ns = nextState(locked, push);


        System.out.println("Distance (locked, push) -> locked: " + hammingDistance(locked.toBooleanVector(), ns.toBooleanVector()));
        System.out.println("Distance (locked, push) -> unlocked: " + hammingDistance(unlocked.toBooleanVector(), ns.toBooleanVector()));

        VanillaBHV ns2 = nextState(locked, token);

        System.out.println("Distance (locked, token) -> locked: " + hammingDistance(locked.toBooleanVector(), ns2.toBooleanVector()));
        System.out.println("Distance (locked, token) -> unlocked: " + hammingDistance(unlocked.toBooleanVector(), ns2.toBooleanVector()));

        VanillaBHV ns3 = nextState(unlocked, push);

        System.out.println("Distance (unlocked, push) -> locked: " + hammingDistance(locked.toBooleanVector(), ns3.toBooleanVector()));
        System.out.println("Distance (unlocked, push) -> unlocked: " + hammingDistance(unlocked.toBooleanVector(), ns3.toBooleanVector()));

        VanillaBHV ns4 = nextState(unlocked, token);

        System.out.println("Distance (unlocked, token) -> locked: " + hammingDistance(locked.toBooleanVector(), ns4.toBooleanVector()));
        System.out.println("Distance (unlocked, token) -> unlocked: " + hammingDistance(unlocked.toBooleanVector(), ns4.toBooleanVector()));



    }
}
