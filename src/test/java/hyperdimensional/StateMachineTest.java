package hyperdimensional;

import hyperdimension.encoders.VanillaBHV;
import org.junit.jupiter.api.Test;

import static hyperdimension.encoders.VanillaBHV.hammingDistance;
import static hyperdimension.examples.StateMachineExample.nextState;

public class StateMachineTest {

    private static final VanillaBHV locked = VanillaBHV.randVector();
    private static final VanillaBHV unlocked = VanillaBHV.randVector();

    // Input symbols
    private static final VanillaBHV token = VanillaBHV.randVector();
    private static final VanillaBHV push = VanillaBHV.randVector();

    //write a test that shows that
    //nextState(locked, push) is closer to locked than unlocked using the hamming distance

    @Test
    public void testStateMachine() {

        VanillaBHV ns = nextState(locked, push);

//        int distanceToLocked = ns.hammingDistance(locked);
//        int distanceToUnlocked = ns.hammingDistance(unlocked);

        System.out.println("Distance (locked, push) -> locked: " + hammingDistance(locked.toBooleanVector(), ns.toBooleanVector()));
        System.out.println("Distance (locked, push) -> unlocked: " + hammingDistance(unlocked.toBooleanVector(), ns.toBooleanVector()));

//
//        System.out.println("Distance (locked, token) -> locked: " + distanceToLocked + " " + distanceToUnlocked);
//
//        assert(distanceToLocked < distanceToUnlocked);
//
//
//        //now do the same for the other transitions
//        VanillaBHV ns2 = nextState(locked, token);
//
//        int distanceToLocked2 = ns2.hammingDistance(locked);
//        int distanceToUnlocked2 = ns2.hammingDistance(unlocked);
//
//        System.out.println("Distance (locked, token) -> locked: " + distanceToLocked2 + " " + distanceToUnlocked2);
//
//        assert(distanceToUnlocked2 < distanceToLocked2);
//
//        VanillaBHV ns3 = nextState(unlocked, push);
//
//        int distanceToLocked3 = ns3.hammingDistance(locked);
//        int distanceToUnlocked3 = ns3.hammingDistance(unlocked);
//
//        //locked should be less than unlocked
//        assert(distanceToLocked3 < distanceToUnlocked3);
//
//        VanillaBHV ns4 = nextState(unlocked, token);
//
//        int distanceToLocked4 = ns4.hammingDistance(locked);
//        int distanceToUnlocked4 = ns4.hammingDistance(unlocked);
//
//        //locked should be less than unlocked
//        assert(distanceToUnlocked4 < distanceToLocked4);
    }

}
