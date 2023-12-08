import math.vector.Vector3;
import world.Chunk;
import world.World;

public class Main {

    public static void main(String[] args) {
        Application application = new Application("Minecraft clone");
        application.getCamera().move(new Vector3(8, 66, 8));

        World world = new World();
        Chunk chunk = new Chunk(new Vector3(), world);
        application.submitMesh(chunk.getMesh());

        while (!application.shouldClose()) {
            application.update();
        }

        application.exit();
    }
}
