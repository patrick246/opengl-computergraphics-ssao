package de.patrick246.dhbw.cg.learnopengl;

import de.patrick246.dhbw.cg.learnopengl.camera.Camera;
import de.patrick246.dhbw.cg.learnopengl.camera.OrthographicCamera;
import de.patrick246.dhbw.cg.learnopengl.camera.PerspectiveCamera;
import de.patrick246.dhbw.cg.learnopengl.framebuffer.Framebuffer;
import de.patrick246.dhbw.cg.learnopengl.framebuffer.RenderTexture;
import de.patrick246.dhbw.cg.learnopengl.lighting.DirectionalLight;
import de.patrick246.dhbw.cg.learnopengl.lighting.PointLight;
import de.patrick246.dhbw.cg.learnopengl.opengl.Shader;
import de.patrick246.dhbw.cg.learnopengl.opengl.Texture;
import de.patrick246.dhbw.cg.learnopengl.opengl.Vao;
import de.patrick246.dhbw.cg.learnopengl.renderable.ColoredCube;
import de.patrick246.dhbw.cg.learnopengl.renderable.Plane;
import de.patrick246.dhbw.cg.learnopengl.renderable.Renderable;
import de.patrick246.dhbw.cg.learnopengl.renderable.TexturedCube;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL43C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Slf4j
public class Application {

    private long window;
    private PerspectiveCamera camera;

    private DirectionalLight dirLight;
    private List<PointLight> pointLight = new ArrayList<>();

    private Vector2f mousePos;

    private List<Renderable> renderables = new ArrayList<>();
    private List<Updatable> updatables = new ArrayList<>();

    private Framebuffer shadowMap;
    private Shader simpleDepthShader;
    private OrthographicCamera lightCamera;
    private Vao depthBufferVao;
    private Shader depthBufferShader;
    private Texture testtexture;

    private Shader normalShader;

    private Shader pointLightShader;

    private Shader depthDisplayCube;

    private int screenWidth;
    private int screenHeight;

    private Framebuffer depthFbo;
    private Framebuffer ssaoFbo;

    private Shader ssaoShader;

    private Random random = new Random();

    private Shader textureDisplayShader;

    private static final int MAX_KERNEL_SIZE = 128;
    private Vector3f[] kernel = new Vector3f[MAX_KERNEL_SIZE];

    private Shader blurShader;
    private Framebuffer blurFBO;

    private boolean ssaoEnabled = false;
    private boolean renderSSAOBuffer = false;


    public void run() {
        log.info("LWJGL " + Version.getVersion());
        createWindow();
        init();
        loop();
    }

    private void createWindow() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        this.window = glfwCreateWindow(1024, 768, "Hello World!", NULL, NULL);

        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, this::keyCallback);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            screenWidth = pWidth.get(0);
            screenHeight = pHeight.get(0);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwSetWindowSizeCallback(window, (w, width, height) -> {
            this.screenWidth = width;
            this.screenHeight = height;
            this.camera.setAspectRatio(width / (float) height);
        });

        glfwSetFramebufferSizeCallback(window, (w, width, height) -> glViewport(0, 0, width, height));

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        mousePos = new Vector2f(screenWidth / 2f, screenHeight / 2f);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPos(window, mousePos.x, mousePos.y);
        glfwSetCursorPosCallback(window, this::mouseCallback);

        GL.createCapabilities();
    }

    private void init() {
        log.info("OpenGL " + glGetString(GL_VERSION));

        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_MULTISAMPLE);

        GLUtil.setupDebugMessageCallback();

        dirLight = new DirectionalLight(
                new Vector3f(1.0f, -1.0f, -1.0f),
                new Vector3f(0.5f),
                new Vector3f(0.2f),
                new Vector3f(0.5f)
        );


        pointLight.add(new PointLight(
                new Vector3f(),
                new Vector3f(0.5f),
                new Vector3f(0.7f),
                new Vector3f(1.0f),
                1f, 0.14f, 0.07f
        ));

        /*pointLight.add(new PointLight(
                new Vector3f(2.5f, 3.0f, -5f),
                new Vector3f(0.2f),
                new Vector3f(0.7f),
                new Vector3f(1.0f),
                1f, 0.014f, 0.07f
        ));

        pointLight.add(new PointLight(
                new Vector3f(-3f, 1f, -6f),
                new Vector3f(0.2f),
                new Vector3f(0.7f),
                new Vector3f(1.0f),
                1f, 0.14f, 0.07f
        ));*/


        Shader shader = new Shader("/res/shaders/shader.vert", "/res/shaders/shader.frag")
                .setCompileTimeConstant("NR_POINT_LIGHTS", pointLight.size())
                .compile();

        camera = new PerspectiveCamera(
                new Vector3f(0.0f, 0.0f, 10.0f),
                new Vector3f(0.0f, 0.0f, -1.0f),
                new Vector3f(0.0f, 1.0f, 0.0f),
                (float) screenWidth / screenHeight
        );
        Material wood2Material = new Material(
                new Vector3f(0.5f),
                new Texture("/res/textures/container2.png"),
                new Texture("/res/textures/container2_specular.png"),
                8.f
        );

        Material wood1Material = new Material(
                new Vector3f(0.5f),
                new Texture("/res/textures/container.jpg"),
                new Texture("/res/textures/container.jpg"),
                8.f
        );

        Material dirtMaterial = new Material(
                new Vector3f(0.5f),
                new Texture("/res/textures/dirt.jpg"),
                new Texture("/res/textures/no_specular.png"),
                4.0f
        );

        Vector3f[] cubePositions = {
                new Vector3f(0.0f, 0.0f, 2.0f),
                new Vector3f(2.0f, 5.0f, -15.0f),
                new Vector3f(-1.5f, -2.2f, -2.5f),
                new Vector3f(-3.8f, -2.0f, -12.3f),
                new Vector3f(2.4f, -0.4f, -3.5f),
                new Vector3f(-1.7f, 3.0f, -7.5f),
                new Vector3f(1.3f, -2.0f, -2.5f),
                new Vector3f(1.5f, 2.0f, -2.5f),
                new Vector3f(1.5f, 0.2f, -1.5f),
                new Vector3f(-1.3f, 1.0f, -1.5f),
        };

        /*for (int i = 0; i < 10; i++) {
            Cube cube = new TexturedCube(shader, wood2Material).translate(cubePositions[i]);
            renderables.add(cube);
            updatables.add(cube);
        }*/

        Shader lightShader = new Shader("/res/shaders/lightShader.vert", "/res/shaders/lightShader.frag").compile();


        renderables.add(new ColoredCube(lightShader, false).scale(new Vector3f(0.25f)));
        renderables.add(new ColoredCube(lightShader, false).translate(new Vector3f(2.5f, 3.0f, -5f)).scale(new Vector3f(0.25f)));
        renderables.add(new ColoredCube(lightShader, false).translate(new Vector3f(-3f, 1f, -6f)).scale(new Vector3f(0.25f)));


        renderables.add(new Plane(shader, dirtMaterial)
                .translate(new Vector3f(0f, -3f, -3f))
                .rotate((float) Math.toRadians(90.0), new Vector3f(1.0f, 0.0f, 0.0f))
                .scale(new Vector3f(100f)).setTextureScale(10));

        renderables.add(new TexturedCube(shader, wood2Material)
                .translate(new Vector3f(0f, -2.5f, -3f))
        );

        renderables.add(new TexturedCube(shader, wood2Material)
                .translate(new Vector3f(0f, -2.5f, -4f))
        );

        renderables.add(new TexturedCube(shader, wood2Material)
                .translate(new Vector3f(0f, -1.5f, -4f))
        );

        renderables.add(new TexturedCube(shader, wood2Material)
                .translate(new Vector3f(1f, -2.5f, -4f))
        );

        /*renderables.add(new Skybox(Arrays.asList(
                "/res/textures/skybox/right.jpg",
                "/res/textures/skybox/left.jpg",
                "/res/textures/skybox/top.jpg",
                "/res/textures/skybox/bottom.jpg",
                "/res/textures/skybox/front.jpg",
                "/res/textures/skybox/back.jpg"
        )));*/

        shadowMap = new Framebuffer().attach(new RenderTexture(RenderTexture.Type.DEPTH, new Vector2i(1024, 1024)));

        simpleDepthShader = new Shader("/res/shaders/simpleDepthShader.vert", "/res/shaders/simpleDepthShader.frag")
                .compile();

        lightCamera = new OrthographicCamera(
                dirLight.getDirection().mul(-1.f, new Vector3f()),
                new Vector3f(0.f, 0.f, 0.f),
                new Vector3f(0.f, 1.f, 0.f)
        );

        depthBufferVao = new Vao(new float[]{
                -1f, 1f, 0f,
                -1f, -1f, 0f,
                1f, -1f, 0f,
                1f, -1f, 0f,
                1f, 1f, 0f,
                -1f, 1f, 0f,
        }).setTextureCoords(new float[]{
                0f, 1f,
                0f, 0f,
                1f, 0f,
                1f, 0f,
                1f, 1f,
                0f, 1f,
        }).fillBuffer();

        depthBufferShader = new Shader("/res/shaders/depthDisplay.vert", "/res/shaders/depthDisplay.frag")
                .compile();

        log.info("DepthBufferShader program id {}", depthBufferShader.getProgramId());

        testtexture = new Texture("/res/textures/container2.png");

        normalShader = new Shader("/res/shaders/normalDisplay.vert", "/res/shaders/normalDisplay.frag").compile();

        pointLightShader = new Shader("/res/shaders/pointLightShader.vert", "/res/shaders/pointLightShader.geom", "/res/shaders/pointLightShader.frag").compile();

        depthDisplayCube = new Shader("/res/shaders/depthDisplayCube.vert", "/res/shaders/depthDisplayCube.frag")
                .setCompileTimeConstant("NR_POINT_LIGHTS", pointLight.size())
                .compile();

        depthFbo = new Framebuffer()
                .attach(new RenderTexture(RenderTexture.Type.DEPTH, new Vector2i(screenWidth, screenHeight)))
                .attach(new RenderTexture(RenderTexture.Type.POSITION, new Vector2i(screenWidth, screenHeight)));

        ssaoFbo = new Framebuffer()
                .attach(new RenderTexture(RenderTexture.Type.AO, new Vector2i(screenWidth, screenHeight)));

        ssaoShader = new Shader("/res/shaders/ssaoPass.vert", "/res/shaders/ssaoPass.frag").compile();

        textureDisplayShader = new Shader("/res/shaders/textureDisplay.vert", "/res/shaders/textureDisplay.frag").compile();

        for (int i = 0; i < MAX_KERNEL_SIZE; i++) {
            float scale = i / 64f;
            scale = lerp(0.1f, 1f, scale * scale);
            Vector3f vector = new Vector3f(random.nextFloat() * 2f - 1f, random.nextFloat() * 2f - 1f, random.nextFloat() * 2f - 1f)
                    .normalize()
                    .mul(random.nextFloat())
                    .mul(scale);
            kernel[i] = vector;
        }

        blurShader = new Shader("/res/shaders/blur.vert", "/res/shaders/blur.frag").compile();

        blurFBO = new Framebuffer()
                .attach(new RenderTexture(RenderTexture.Type.COLOR, new Vector2i(screenWidth, screenHeight)));
    }


    private void loop() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        double timeBank = 0.0f;
        double lastStep = glfwGetTime();
        double timestep = 1 / 60.0;

        while (!glfwWindowShouldClose(window)) {
            double now = glfwGetTime();
            timeBank += now - lastStep;
            lastStep = now;
            while (timeBank >= timestep) {
                update((float) timestep);
                timeBank -= timestep;
            }

            render();

            GLFW.glfwPollEvents();
            handleErrors();
        }
    }

    private void update(float dt) {
        processKeyboard(dt);

        for (Updatable u : updatables) {
            u.update(dt);
        }
    }

    private void render() {
        glClearColor(0.3f, 0.2f, 0.2f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        //if(ssaoEnabled) {
            renderPositionPass();
            renderSSAOPass();
            renderBlurPass();
        //}
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        if(renderSSAOBuffer) {
            renderTexture((RenderTexture)blurFBO.getAttachments().get(0));
        }
        dirLight.renderLightPass(renderables, simpleDepthShader);
        pointLight.forEach(l -> l.renderLightPass(renderables, pointLightShader));

        glViewport(0, 0, screenWidth, screenHeight);
        if(!renderSSAOBuffer) {
            renderables.forEach(renderable -> renderSingle(renderable, camera));
        }

        //renderNormals();
        //renderDepthBuffer((RenderTexture)depthFbo.getAttachments().get(0));

        GLFW.glfwSwapBuffers(window);
    }

    private void renderPositionPass() {
        glViewport(0, 0, screenWidth, screenHeight);
        depthFbo.bind();
        simpleDepthShader.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        camera.setupView(simpleDepthShader);

        renderables.forEach(r -> r.render(simpleDepthShader, RenderPass.FORWARD_DEPTH_PASS));

        depthFbo.unbind();
    }

    private void renderSSAOPass() {
        glViewport(0, 0, screenWidth, screenHeight);
        ssaoFbo.bind();
        if (ssaoEnabled) {
            glClear(GL_COLOR_BUFFER_BIT);
            ssaoShader.bind();
            ((RenderTexture) depthFbo.getAttachments().get(1)).bind(0);

            ssaoShader.setInt("positionMap", 0);
            ssaoShader.setFloat("sampleRad", 1.5f);
            for (int i = 0; i < MAX_KERNEL_SIZE; i++) {
                ssaoShader.setVec3f("kernel[" + i + "]", kernel[i]);
            }
            camera.setupView(ssaoShader);

            depthBufferVao.bind();
            glDrawArrays(GL_TRIANGLES, 0, 6);
        } else {
            glClearColor(0.5f, 0.5f,0.5f, 0.5f);
            glClear(GL_COLOR_BUFFER_BIT);
            glClearColor(0.3f, 0.2f, 0.2f, 1.0f);
        }

        ssaoFbo.unbind();
    }

    private void renderBlurPass() {
        glViewport(0, 0, screenWidth, screenHeight);
        blurFBO.bind();
        blurShader.bind();
        ((RenderTexture) ssaoFbo.getAttachments().get(0)).bind(0);
        blurShader.setInt("gColorMap", 0);

        depthBufferVao.bind();
        glDrawArrays(GL_TRIANGLES, 0, 6);
        blurFBO.unbind();
    }

    private float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    private void renderLightPass(Camera lightCam) {
        glViewport(0, 0, 1024, 1024);
        shadowMap.bind();
        simpleDepthShader.bind();
        glClear(GL_DEPTH_BUFFER_BIT);
        lightCam.setupView(simpleDepthShader);
        renderables.forEach(renderable -> renderable.render(simpleDepthShader, RenderPass.FORWARD_LIGHT_PASS));
        shadowMap.unbind();
    }

    private void renderNormals() {
        normalShader.bind();
        camera.setupView(normalShader);
        renderables.forEach(r -> r.render(normalShader, RenderPass.FORWARD_GEOMETRY_PASS));
    }

    private void renderSingle(Renderable renderable, Camera camera) {
        Shader s = renderable.getShader();
        s.bind();
        s.setVec2f("screenSize", new Vector2f(screenWidth, screenHeight));
        ((RenderTexture) blurFBO.getAttachments().get(0)).bind(2);
        s.setInt("ssaoMap", 2);
        dirLight.apply(s, 3);
        for (int i = 0; i < pointLight.size(); i++) {
            pointLight.get(i).apply(s, i, 4 + i);
        }
        camera.setupView(s);

        renderable.render(s, RenderPass.FORWARD_GEOMETRY_PASS);
    }

    private void renderDepthBuffer(RenderTexture texture) {
        depthBufferShader.bind();
        texture.bind(0);
        depthBufferShader.setInt("depthMap", 0);
        depthBufferVao.bind();
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    private void renderTexture(RenderTexture texture) {
        textureDisplayShader.bind();
        texture.bind(0);
        textureDisplayShader.setInt("myTex", 0);
        depthBufferVao.bind();
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    private void handleErrors() {
        int error;
        while ((error = glGetError()) != GL_NO_ERROR) {
            log.error("OpenGL Error {}", error);
        }
    }

    private void processKeyboard(float dt) {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.getPosition().add(camera.getFront().mul(camera.getSpeed() * dt, new Vector3f()));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.getPosition().sub(camera.getFront().cross(camera.getUp(), new Vector3f()).normalize(camera.getSpeed() * dt));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.getPosition().sub(camera.getFront().mul(camera.getSpeed() * dt, new Vector3f()));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.getPosition().add(camera.getFront().cross(camera.getUp(), new Vector3f()).normalize(camera.getSpeed() * dt));
        }

    }

    private void mouseCallback(long window, double xPos, double yPos) {
        float sensitivity = 0.05f;

        float xOffset = ((float) xPos - mousePos.x) * sensitivity;
        float yOffset = (mousePos.y - (float) yPos) * sensitivity;

        mousePos.x = (float) xPos;
        mousePos.y = (float) yPos;

        camera.yawPitch(xOffset, yOffset);
    }

    public static void main(String[] args) {
        new Application().run();
    }

    private void keyCallback(long myWindow, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }
        if(key == GLFW_KEY_O && action == GLFW_RELEASE) {
            ssaoEnabled = !ssaoEnabled;
            System.out.println("SSAO " + (ssaoEnabled ? "ON" : "OFF"));
        }
        if(key == GLFW_KEY_P && action == GLFW_RELEASE) {
            renderSSAOBuffer = !renderSSAOBuffer;
        }
    }
}
