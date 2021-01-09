package tfar.reciperandomizer.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import tfar.reciperandomizer.RecipeRandomizer;

public class PacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(RecipeRandomizer.MODID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, C2SToggleRandomCraftingPacket.class,
            (message, buffer) -> {},
            buffer -> new C2SToggleRandomCraftingPacket(),
            C2SToggleRandomCraftingPacket::handle);

  }
}
