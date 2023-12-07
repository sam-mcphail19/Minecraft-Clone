import core.Transform;
import graphics.Cube;
import graphics.texture.Texture;
import math.vector.Vector3;

public class Main {

    public static void main(String[] args) {
        Application application = new Application("Minecraft clone");
        application.getCamera().move(new Vector3(0, 3, 5));

        for (int i = -3; i < 3; i++) {
            for (int j = -3; j < 3; j++) {
                Transform transform = Transform.builder()
                    .translation(new Vector3(i, 0, j))
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
