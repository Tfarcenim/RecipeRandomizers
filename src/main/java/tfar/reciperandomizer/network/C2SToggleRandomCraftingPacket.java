package tfar.reciperandomizer.network;

import net.minecraftforge.fml.network.NetworkEvent;
import tfar.reciperandomizer.RecipeRandomizer;

import java.util.function.Supplier;

public class C2SToggleRandomCraftingPacket {

  public C2SToggleRandomCraftingPacket() {
  }

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    RecipeRandomizer.ServerConfig.randomToggle.set(!RecipeRandomizer.ServerConfig.randomToggle.get());
    RecipeRandomizer.reload();
  }
}