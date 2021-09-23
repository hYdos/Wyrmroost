package com.github.wolfshotz.wyrmroost.network.packets;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.SilverGliderEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SGGlidePacket
{
    private final boolean gliding;

    public SGGlidePacket(FriendlyByteBuf buffer)
    {
        this.gliding = buffer.readBoolean();
    }

    public SGGlidePacket(boolean gliding)
    {
        this.gliding = gliding;
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeBoolean(gliding);
    }

    public boolean handle(Supplier<NetworkEvent.Context> context)
    {
        ServerPlayer reciever = context.get().getSender();
        if (reciever != null && !reciever.getPassengers().isEmpty())
        {
            Entity entity = reciever.getPassengers().get(0);
            if (entity instanceof SilverGliderEntity)
            {
                ((SilverGliderEntity) entity).isGliding = gliding;
                return true;
            }
        }
        return false;
    }

    public static void send(boolean gliding)
    {
        Wyrmroost.NETWORK.sendToServer(new SGGlidePacket(gliding));
    }
}
