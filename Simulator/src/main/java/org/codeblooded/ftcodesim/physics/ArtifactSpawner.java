package org.codeblooded.ftcodesim.physics;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

public class ArtifactSpawner {
    MotionVector[] greenArtifacts = new MotionVector[] {
            // GREEN
            new MotionVector(2.89, 8.17),
            new MotionVector(138.5, 8.17),

            new MotionVector(18, 82.5),
            new MotionVector(23, 58.5),
            new MotionVector(28, 35.5),

            new MotionVector(122.5, 82.5),
            new MotionVector(117.5, 58.5),
            new MotionVector(112.5, 35.5),
    };

    MotionVector[] purpleArtifacts = new MotionVector[] {
            // PURPLE
            new MotionVector(2.89, 13.17),
            new MotionVector(2.89, 3.17),
            new MotionVector(138.5, 13.17),
            new MotionVector(138.5, 3.17),

            new MotionVector(23, 82.5),
            new MotionVector(28, 82.5),
            new MotionVector(18, 58.5),

            new MotionVector(28, 58.5),
            new MotionVector(18, 35.5),
            new MotionVector(23, 35.5),

            new MotionVector(112.5, 82.5),
            new MotionVector(117.5, 82.5),
            new MotionVector(112.5, 58.5),

            new MotionVector(122.5, 58.5),
            new MotionVector(117.5, 35.5),
            new MotionVector(122.5, 35.5),
    };


    public void spawn() {
        for (int i = 0; i < greenArtifacts.length; i++) {
            greenArtifacts[i].log3d("Artifacts/green" + i, 4.9/2);
        }

        for (int i = 0; i < purpleArtifacts.length; i++) {
            purpleArtifacts[i].log3d("Artifacts/purple" + i, 4.9/2);
        }

    }
}
