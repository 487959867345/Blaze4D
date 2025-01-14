package me.hydos.rosella.example;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.render.Topology;
import me.hydos.rosella.render.font.FontHelper;
import me.hydos.rosella.render.font.RosellaFont;
import me.hydos.rosella.render.io.Window;
import me.hydos.rosella.render.material.Material;
import me.hydos.rosella.render.model.GuiRenderObject;
import me.hydos.rosella.render.resource.Global;
import me.hydos.rosella.render.resource.Identifier;
import me.hydos.rosella.render.shader.RawShaderProgram;
import me.hydos.rosella.render.texture.SamplerCreateInfo;
import me.hydos.rosella.render.texture.TextureFilter;
import me.hydos.rosella.render.vertex.VertexFormats;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.vulkan.VK10;

public class PortalJava {

    public static final Window window = new Window("Portal 3: Java Edition", 1280, 720, true);
    public static final Rosella rosella = new Rosella("portal3", true, window);

    public static final Identifier menuBackground = new Identifier("example", "menu_background");
    public static final Identifier portalLogo = new Identifier("example", "portal_logo");

    public static final Identifier basicShader = new Identifier("rosella", "example_shader");
    public static final Identifier guiShader = new Identifier("rosella", "gui_shader");

    public static final Identifier background = new Identifier("example", "sounds/music/mainmenu/portal2_background01.ogg");

    public static final Identifier fontShader = new Identifier("rosella", "font_shader");

    private static RosellaFont portalFont;

    public static void main(String[] args) {
        loadShaders();
        loadFonts();
        loadMaterials();
        setupMainMenuScene();
//        SoundManager.playback(Global.INSTANCE.ensureResource(background));
        doMainLoop();
    }

    private static void loadFonts() {
        portalFont = FontHelper.INSTANCE.loadFont(Global.INSTANCE.ensureResource(new Identifier("rosella", "fonts/DIN Bold.otf")), rosella);
    }

    private static void setupMainMenuScene() {
        rosella.addToScene(
                new GuiRenderObject(menuBackground, -1f, new Vector3f(0, 0, 0), 1.5f, 1f)
        );

        rosella.addToScene(
                new GuiRenderObject(portalLogo, -0.9f, new Vector3f(0, 0, 0), 0.4f, 0.1f, -1f, -2.6f)
        );

//        rosella.addRenderObject(
//                portalFont.createString("The Quick Brown\n Fox Jumped Over\n The Lazy Dog", new Vector3f(255, 255, 255), -0.8f, 0.05f, -4f, 0f),
//                "fontTest"
//        );
    }

    private static void loadMaterials() {
        rosella.registerMaterial(
                menuBackground, new Material(
                        Global.INSTANCE.ensureResource(new Identifier("example", "textures/background/background01.png")),
                        guiShader,
                        VK10.VK_FORMAT_R8G8B8A8_UNORM,
                        false,
                        Topology.TRIANGLES,
                        VertexFormats.Companion.getPOSITION_COLOR_UV(),
                        new SamplerCreateInfo(TextureFilter.NEAREST)
                )
        );
        rosella.registerMaterial(
                portalLogo, new Material(
                        Global.INSTANCE.ensureResource(new Identifier("example", "textures/gui/portal2logo.png")),
                        guiShader,
                        VK10.VK_FORMAT_R8G8B8A8_SRGB,
                        true,
                        Topology.TRIANGLES,
                        VertexFormats.Companion.getPOSITION_COLOR_UV(),
                        new SamplerCreateInfo(TextureFilter.NEAREST)
                )
        );
        rosella.reloadMaterials();
    }

    private static void loadShaders() {
        rosella.registerShader(
                basicShader, new RawShaderProgram(
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/base.v.glsl")),
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/base.f.glsl")),
                        rosella.getDevice(),
                        rosella.getMemory(),
                        10,
                        RawShaderProgram.PoolObjType.UBO,
                        RawShaderProgram.PoolObjType.SAMPLER
                )
        );

        rosella.registerShader(
                guiShader, new RawShaderProgram(
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/gui.v.glsl")),
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/gui.f.glsl")),
                        rosella.getDevice(),
                        rosella.getMemory(),
                        10,
                        RawShaderProgram.PoolObjType.UBO,
                        RawShaderProgram.PoolObjType.SAMPLER
                )
        );
    }

    private static void doMainLoop() {
        rosella.getRenderer().rebuildCommandBuffers(rosella.getRenderer().renderPass, rosella);
        GLFW.glfwSetKeyCallback(window.getWindowPtr(), new GLFWKeyCallback() {
            boolean hasDelet;

            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key == GLFW.GLFW_KEY_V && !hasDelet) {
                    hasDelet = true;
                    System.out.println("Delet");
                    rosella.getRenderObjects().remove("portalLogo");
                    rosella.getRenderer().rebuildCommandBuffers(rosella.getRenderer().renderPass, rosella);
                }
            }
        });
        window.onMainLoop(() -> {
//            rosella.getRenderObjects().get("portalLogo").getTransformMatrix().rotate(new AxisAngle4f(0.1f, 0f, 1f, 0f)); // TODO: make this easier somehow
            rosella.getRenderer().render(rosella);

            GLFW.glfwPollEvents();
        });
        window.startLoop();
        window.close();
    }
}
