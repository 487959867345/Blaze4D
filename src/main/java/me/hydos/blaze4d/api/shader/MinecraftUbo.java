package me.hydos.blaze4d.api.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import me.hydos.rosella.render.device.Device;
import me.hydos.rosella.render.shader.ubo.LowLevelUbo;
import me.hydos.rosella.render.swapchain.SwapChain;
import me.hydos.rosella.render.util.memory.Memory;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

import static me.hydos.rosella.render.util.VkUtilsKt.alignas;
import static me.hydos.rosella.render.util.VkUtilsKt.alignof;

public class MinecraftUbo extends LowLevelUbo {

    public MinecraftUbo(@NotNull Device device, @NotNull Memory memory) {
        super(device, memory);
    }

    @Override
    public void update(int currentImg, @NotNull SwapChain swapChain, @NotNull Matrix4f view, @NotNull Matrix4f proj, @NotNull Matrix4f modelMatrix) {
        if (getUboFrames().size() == 0) {
            create(swapChain); //TODO: CONCERN. why did i write this
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer data = stack.mallocPointer(1);
            getMemory().map(getUboFrames().get(currentImg).getAllocation(), false, data);
            ByteBuffer buffer = data.getByteBuffer(0, getSize());
            int mat4Size = 16 * java.lang.Float.BYTES;

            Matrix4f mcViewMatrix = toJoml(RenderSystem.getModelViewMatrix());
            Matrix4f mcProjMatrix = toJoml(RenderSystem.getProjectionMatrix());

            modelMatrix.get(0, buffer);
            mcViewMatrix.get(alignas(mat4Size, alignof(mcViewMatrix)), buffer);
            mcProjMatrix.get(alignas(mat4Size * 2, alignof(mcViewMatrix)), buffer);

            getMemory().unmap(getUboFrames().get(currentImg).getAllocation());
        }
    }

    private Matrix4f toJoml(net.minecraft.util.math.Matrix4f mcMatrix) {
        Matrix4f jomlMatrix = new Matrix4f();

        jomlMatrix.m00(mcMatrix.a00);
        jomlMatrix.m01(mcMatrix.a01);
        jomlMatrix.m02(mcMatrix.a02);
        jomlMatrix.m03(mcMatrix.a03);

        jomlMatrix.m10(mcMatrix.a10);
        jomlMatrix.m11(mcMatrix.a11);
        jomlMatrix.m12(mcMatrix.a12);
        jomlMatrix.m13(mcMatrix.a13);

        jomlMatrix.m20(mcMatrix.a20);
        jomlMatrix.m21(mcMatrix.a21);
        jomlMatrix.m22(mcMatrix.a22);
        jomlMatrix.m23(mcMatrix.a23);

        jomlMatrix.m30(mcMatrix.a30);
        jomlMatrix.m31(mcMatrix.a31);
        jomlMatrix.m32(mcMatrix.a32);
        jomlMatrix.m33(mcMatrix.a33);

        return jomlMatrix;
    }
}