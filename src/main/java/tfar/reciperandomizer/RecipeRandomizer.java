package tfar.reciperandomizer;

import com.google.common.collect.Lists;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandSource;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import tfar.reciperandomizer.network.C2SToggleRandomCraftingPacket;
import tfar.reciperandomizer.network.PacketHandler;

import java.util.Collection;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RecipeRandomizer.MODID)
public class RecipeRandomizer {
    // Directly reference a log4j logger.

    public static final String MODID = "reciperandomizer";

    private static final Logger LOGGER = LogManager.getLogger();

    public RecipeRandomizer() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::configReload);
        bus.addListener(this::common);
        if (FMLEnvironment.dist.isClient()) {
            MinecraftForge.EVENT_BUS.addListener(Client::keyPress);
            bus.addListener(Client::keybind);
        }
    }

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }

    private static final Logger field_241057_a_ = LogManager.getLogger();

    public static void func_241062_a_(Collection<String> p_241062_0_, CommandSource p_241062_1_) {
        p_241062_1_.getServer().func_240780_a_(p_241062_0_).exceptionally((p_241061_1_) -> {
            field_241057_a_.warn("Failed to execute reload", p_241061_1_);
            p_241062_1_.sendErrorMessage(new TranslationTextComponent("commands.reload.failure"));
            return null;
        });
    }

    private static Collection<String> func_241058_a_(ResourcePackList p_241058_0_, IServerConfiguration p_241058_1_, Collection<String> p_241058_2_) {
        p_241058_0_.reloadPacksFromFinders();
        Collection<String> collection = Lists.newArrayList(p_241058_2_);
        Collection<String> collection1 = p_241058_1_.getDatapackCodec().getDisabled();

        for(String s : p_241058_0_.func_232616_b_()) {
            if (!collection1.contains(s) && !collection.contains(s)) {
                collection.add(s);
            }
        }

        return collection;
    }

    private void common(FMLCommonSetupEvent e) {
        PacketHandler.registerMessages(MODID);
    }

    private void configReload(ModConfig.Reloading e) {
        if (e.getConfig().getModId().equals(MODID)) {
            reload();
        }
    }

    public static void reload() {
        MinecraftServer minecraftserver = ServerLifecycleHooks.getCurrentServer();
        if (minecraftserver != null) {
            ResourcePackList resourcepacklist = minecraftserver.getResourcePacks();
            IServerConfiguration iserverconfiguration = minecraftserver.getServerConfiguration();
            Collection<String> collection = resourcepacklist.func_232621_d_();
            Collection<String> collection1 = func_241058_a_(resourcepacklist, iserverconfiguration, collection);
            minecraftserver.getCommandSource().sendFeedback(new TranslationTextComponent("commands.reload.success"), true);
            func_241062_a_(collection1, minecraftserver.getCommandSource());
        }
    }

    public static class Client {

        public static final KeyBinding TOGGLE = new KeyBinding("toggle", GLFW.GLFW_KEY_Y,MODID);

        private static void keybind(FMLClientSetupEvent e) {
            ClientRegistry.registerKeyBinding(TOGGLE);
        }

        private static void keyPress(InputEvent.KeyInputEvent e) {
            while (TOGGLE.isPressed()) {
                PacketHandler.INSTANCE.sendToServer(new C2SToggleRandomCraftingPacket());
            }
        }
    }

    public static class ServerConfig {

        public static ForgeConfigSpec.BooleanValue randomToggle;

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("server");
            randomToggle = builder.
                    comment("controls whether or not random crafting is active")
                    .define("active", true);
            builder.pop();
        }
    }
}
