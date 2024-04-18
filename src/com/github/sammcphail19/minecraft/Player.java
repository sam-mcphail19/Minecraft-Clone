package com.github.sammcphail19.minecraft;

import com.github.sammcphail19.engine.core.Camera;
import com.github.sammcphail19.engine.core.Input;
import com.github.sammcphail19.engine.physics.BoxCollider;
import com.github.sammcphail19.engine.vector.Vector2;
import com.github.sammcphail19.engine.vector.Vector3;
import lombok.Data;
import org.lwjgl.glfw.GLFW;

@Data
public class Player {
    private static final double MOUSE_SENSITIVITY = 0.24;
    private static final double MOVEMENT_SPEED = 0.15;
    private static final double JUMP_SPEED = 0.25;

    private static final double GRAVITY = 0.01;

    private final Vector3 size = new Vector3(0.9, 1.8, 0.9);
    private final Vector3 cameraPosDiff = new Vector3(size.getX() / 2, 2, size.getZ() / 2);

    private final Camera camera;
    private final BoxCollider collider;

    private Vector3 pos;
    private Vector3 velocity = new Vector3();
    private double pitch = 0;
    private double yaw = 0;
    private Vector2 mousePos = Input.getMousePos();
    private long lastUpdateTime = 0;
    private PlayerControl lastPlayerControl;
    private boolean isJumping = false;

    public Player(Vector3 pos) {
        this.pos = pos;
        this.camera = new Camera(pos.add(cameraPosDiff), pitch, yaw);
        this.collider = new BoxCollider(pos, size);
    }

    public void getInput() {
        lastPlayerControl = getPlayerControl();

        velocity.setX(lastPlayerControl.getMovement().getX());
        velocity.setZ(lastPlayerControl.getMovement().getZ());
        velocity.setY(velocity.getY() + lastPlayerControl.getMovement().getY());

        if (isJumping) {
            velocity = velocity.subtract(new Vector3(0, GRAVITY, 0));
        }

        addPitch(lastPlayerControl.getPitch());
        addYaw(lastPlayerControl.getYaw());

        camera.setPitch(pitch);
        camera.setYaw(yaw);
        camera.setPos(pos.add(cameraPosDiff));
    }

    public void update() {
        getInput();
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
        collider.setPos(this.pos);
    }

    private PlayerControl getPlayerControl() {
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
            movement = movement.add(viewDir.multiply(MOVEMENT_SPEED));
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_A)) {
            movement = movement.add(strafeDir.multiply(-MOVEMENT_SPEED));
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_S)) {
            movement = movement.add(viewDir.multiply(-MOVEMENT_SPEED));
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_D)) {
            movement = movement.add(strafeDir.multiply(MOVEMENT_SPEED));
        }

        if (Input.isKeyPressed(GLFW.GLFW_KEY_SPACE) && !isJumping) {
            movement.setY(JUMP_SPEED);
            isJumping = true;
        }

        return PlayerControl.builder()
            .pitch(pitch)
            .yaw(yaw)
            .movement(movement)
            .build();
    }

    private PlayerControl getCreativePlayerControl() {
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
            movement = movement.add(viewDir.multiply(MOVEMENT_SPEED));
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_A)) {
            movement = movement.add(strafeDir.multiply(-MOVEMENT_SPEED));
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_S)) {
            movement = movement.add(viewDir.multiply(-MOVEMENT_SPEED));
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_D)) {
            movement = movement.add(strafeDir.multiply(MOVEMENT_SPEED));
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
            .movement(movement)
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
}
