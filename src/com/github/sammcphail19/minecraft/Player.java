package com.github.sammcphail19.minecraft;

import com.github.sammcphail19.engine.core.Camera;
import com.github.sammcphail19.engine.core.Input;
import com.github.sammcphail19.engine.physics.BoxCollider;
import lombok.Getter;
import lombok.Setter;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;
import org.lwjgl.glfw.GLFW;

public class Player {
    private static final double MOUSE_SENSITIVITY = 0.24;
    private static final double MOVEMENT_SPEED = 0.000000005;

    @Getter
    private final Camera camera;
    private final BoxCollider collider;

    @Getter
    @Setter
    private Vector3 pos = new Vector3();
    private double pitch = 0;
    private double yaw = 0;
    private Vector2 mousePos = Input.getMousePos();
    private long lastUpdateTime = 0;

    public Player() {
        this.camera = new Camera(pos, pitch, yaw);
        this.collider = new BoxCollider(pos, new Vector3(1, 2, 1));
    }

    public void update() {
        PlayerControl playerControl = getPlayerControl();

        this.pos = this.pos.add(playerControl.getMovement());

        addPitch(playerControl.getPitch());
        addYaw(playerControl.getYaw());

        camera.setPitch(pitch);
        camera.setYaw(yaw);
        camera.setPos(pos);
    }

    private PlayerControl getPlayerControl() {
        long currentTime = System.nanoTime();
        long deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        double oldY = mousePos.getY();
        double oldX = mousePos.getX();
        mousePos = Input.getMousePos().copy();

        double pitch = (mousePos.getY() - oldY) * MOUSE_SENSITIVITY;
        pitch = pitch < 90f && pitch > -90f ? pitch : 0;

        double yaw = (mousePos.getX() - oldX) * MOUSE_SENSITIVITY;

        Vector3 viewDir = calculateViewDirection();
        Vector3 strafeDir = calculateStrafeDirection(viewDir);

        Vector3 movement = new Vector3();
        if (Input.isKeyPressed(GLFW.GLFW_KEY_W)) {
            movement = movement.add(viewDir);
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_A)) {
            movement = movement.add(strafeDir.multiply(-1));
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_S)) {
            movement = movement.add(viewDir.multiply(-1));
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_D)) {
            movement = movement.add(strafeDir);
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            movement = movement.add(Vector3.yAxis());
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            movement = movement.add(Vector3.yAxis().multiply(-1));
        }

        return PlayerControl.builder()
            .pitch(pitch)
            .yaw(yaw)
            .movement(movement.multiply(MOVEMENT_SPEED * deltaTime))
            .build();
    }

    public void addPitch(double pitch) {
        this.pitch += pitch;

        this.pitch = Math.max(this.pitch, -90);
        this.pitch = Math.min(this.pitch, 90);
        camera.setPitch(this.pitch);
    }

    public void addYaw(double yaw) {
        this.yaw += yaw;

        if (this.yaw > 360 || this.yaw < -360) {
            this.yaw = 0;
        }
        camera.setYaw(this.yaw);
    }

    private Vector3 calculateViewDirection() {
        return new Vector3(
            Math.sin(Math.toRadians(yaw)),
            0,
            -Math.cos(Math.toRadians(yaw)) // inverted because yaw=0 points towards -z
        );
    }

    private Vector3 calculateStrafeDirection(Vector3 viewDir) {
        return viewDir.cross(Vector3.yAxis()).normalize();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("com.github.sammcphail19.minecraft.Player(");
        sb.append("pos=");
        sb.append(pos.toString());
        sb.append(", ");
        sb.append("viewDirection=");
        sb.append(calculateViewDirection());
        sb.append(")");

        return sb.toString();
    }
}
