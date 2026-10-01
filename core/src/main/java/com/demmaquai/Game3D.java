package com.demmaquai;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Game3D extends ApplicationAdapter {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;
    private Environment environment;

    private Model playerModel;
    private Array<ModelInstance> instances = new Array<>();

    private float camYaw = 0f;
    private float camPitch = -20f;
    private float camDistance = 8f;

    private final Vector3 playerPos = new Vector3(0, 0, 0);
    private final Vector3 moveInput = new Vector3();

    private final String serverAddress;

    public Game3D(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public void create() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 300f;

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.5f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.9f, -1f, -0.8f, -0.2f));

        ModelBuilder mb = new ModelBuilder();
        playerModel = mb.createBox(
            1f, 1.8f, 1f,
            new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.9f, 0.3f, 1f))),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        ModelInstance me = new ModelInstance(playerModel);
        me.transform.setToTranslation(playerPos);
        instances.add(me);

        // Bot placeholder
        ModelInstance bot = new ModelInstance(playerModel);
        bot.transform.setToTranslation(5, 0, 0);
        bot.materials.get(0).set(ColorAttribute.createDiffuse(new Color(0.9f, 0.2f, 0.2f, 1f)));
        instances.add(bot);

        Gdx.input.setInputProcessor(null);
        System.out.println("[Game3D] server=" + serverAddress);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();

        handleInput(dt);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0.05f, 0.02f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        updateCamera();

        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    private void handleInput(float dt) {
        moveInput.set(0, 0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveInput.z -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveInput.z += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveInput.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveInput.x += 1;

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) camYaw += 60 * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.E)) camYaw -= 60 * dt;

        if (moveInput.len() > 0) {
            moveInput.nor().rotateRad(Vector3.Y, (float) Math.toRadians(camYaw));
            playerPos.mulAdd(moveInput, 5f * dt);
        }

        instances.get(0).transform.setToTranslation(playerPos);
    }

    private void updateCamera() {
        float yawRad = (float) Math.toRadians(camYaw);
        float pitchRad = (float) Math.toRadians(camPitch);

        float offsetX = (float) (Math.sin(yawRad) * Math.cos(pitchRad) * camDistance);
        float offsetY = (float) (-Math.sin(pitchRad) * camDistance);
        float offsetZ = (float) (Math.cos(yawRad) * Math.cos(pitchRad) * camDistance);

        camera.position.set(
            playerPos.x + offsetX,
            playerPos.y + 1.5f + offsetY,
            playerPos.z + offsetZ
        );
        camera.lookAt(playerPos.x, playerPos.y + 1f, playerPos.z);
        camera.up.set(Vector3.Y);
        camera.update();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        playerModel.dispose();
    }
}
