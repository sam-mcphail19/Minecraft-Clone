import core.Transform;
import math.matrix.Mat4;
import math.vector.Vector3;

public class Main {

    public static void main(String[] args) {
        Application application = new Application("Minecraft clone");

        for (int i = -3; i < 3; i++) {
            for (int j = -3; j < 3; j++) {
                Transform transform = Transform.builder()
                    .translation(new Vector3(i * 2, j * 2, -5))
                    .rotation(Mat4.yRotation(45))
                    .build();

                application.submitMesh(new Cube(transform, (i + j) % 2 == 0 ? Texture.DIRT : Texture.STONE));
            }
        }

        while (!application.shouldClose()) {
            application.update();
        }

        application.exit();
    }
}
