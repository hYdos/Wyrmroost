package com.github.wolfshotz.wyrmroost.client.screen.widgets;

import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.screen.DragonControlScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.TranslatableComponent;

public class PinButton extends AbstractButton
{
    private boolean pinned;

    public PinButton(int x, int y)
    {
        super(x, y, 18, 18, new TranslatableComponent("narrator.button.pin"));
    }

    @Override
    public void renderButton(PoseStack ms, int mouseX, int mouseY, float partialTicks)
    {
        ClientEvents.getClient().getTextureManager().bind(DragonControlScreen.SPRITES);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        blit(ms, x, y, isHovered()? 230 : 212, pinned? 18 : 0, width, height);
    }

    @Override
    public void onPress()
    {
        pin();
    }

    public boolean pin()
    {
        return this.pinned = !pinned;
    }

    public boolean pin(boolean bool)
    {
        return this.pinned = bool;
    }

    public boolean pinned()
    {
        return pinned;
    }
}
