package de.j3ramy.edom.entity.renderer;

import de.j3ramy.edom.EdomMod;
import de.j3ramy.edom.entity.entities.TestCartEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.util.ResourceLocation;

public class TestCartRenderer<T extends TestCartEntity> extends MinecartRenderer<T> {

    private static final ResourceLocation TEXTURE_LOC = new ResourceLocation("textures/entity/minecart.png");

    public TestCartRenderer(EntityRendererManager manager) {
        super(manager);
        this.shadowSize = 0.7F;
    }

    @Override
    public ResourceLocation getEntityTexture(T p_110775_1_) {
        return TEXTURE_LOC;
    }


}
