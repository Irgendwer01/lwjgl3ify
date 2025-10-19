package me.eigenraven.lwjgl3ify.rfb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gtnewhorizons.retrofuturabootstrap.api.RetroFuturaBootstrap;

public class EarlyConfig {

    public static final String[] DEFAULT_EXTENSIBLE_ENUMS = new String[] {
        // From EnumHelper
        "net.minecraft.item.EnumAction", "net.minecraft.item.ItemArmor$ArmorMaterial",
        "net.minecraft.entity.item.EntityPainting$EnumArt", "net.minecraft.entity.EnumCreatureAttribute",
        "net.minecraft.entity.EnumCreatureType",
        "net.minecraft.world.gen.structure.StructureStrongholdPieces$Stronghold$Door",
        "net.minecraft.enchantment.EnumEnchantmentType", "net.minecraft.block.BlockPressurePlate$Sensitivity",
        "net.minecraft.util.math.RayTraceResult$Type", "net.minecraft.world.EnumSkyBlock",
        "net.minecraft.entity.player.EntityPlayer$SleepResult", "net.minecraft.item.Item$ToolMaterial",
        "net.minecraft.item.EnumRarity", "net.minecraft.entity.passive.HorseArmorType",
        "net.minecraft.entity.EntityLiving$SpawnPlacementType",
        // Not in EnumHelper but relied on by Forge anyway
        "net.minecraft.world.DimensionType",

        // GTCEu
        "net.minecraft.util.BlockRenderLayer", "net.minecraft.util.EnumBlockRenderType",

        // Wizardry
        "net.minecraft.util.SoundCategory",

        // Dark Utilities + Actually Additions
        "net.minecraft.tileentity.BannerPattern",

        // Extra Utilities
        "net.minecraftforge.common.EnumPlantType",

        // Nomilabs
        "com.elytradev.architecture.common.shape.EnumShape", "com.projecturanus.betterp2p.item.BetterMemoryCardModes",
        "com.projecturanus.betterp2p.client.gui.Filter",

        // Quark
        "net.minecraft.block.BlockChest$Type",

        // Tinkers Construct
        "net.minecraftforge.common.EnumPlantType",

        // JAOPCA
        "thelm.jaopca.api.EnumEntryType",

        // Twilight Forest
        "net.minecraft.client.audio.MusicTicker$MusicType",

        // Forestry
        "net.minecraftforge.event.terraingen.PopulateChunkEvent$Populate$EventType",
        "net.minecraftforge.event.terraingen.InitMapGenEvent$EventType",
        "net.minecraftforge.event.terraingen.OreGenEvent$GenerateMinable$EventType",
        "net.minecraftforge.event.terraingen.DecorateBiomeEvent$Decorate$EventType",

        // MIA
        "com.buuz135.industrial.tile.misc.FrosterTile$FrosterResult",
        // Bewitchment
        "thaumcraft.api.golems.EnumGolemTrait",
        // Gravisuite
        "ic2.core.util.Keyboard$Key", };

    public static final Set<String> EXTENSIBLE_ENUMS = new HashSet<>(Arrays.asList(DEFAULT_EXTENSIBLE_ENUMS));

    public static class ConfigObject {

        public String[] extensibleEnums;
    }

    private static boolean isLoaded = false;

    public static void load() {
        if (isLoaded) {
            return;
        }
        isLoaded = true;

        final Path gamePath = RetroFuturaBootstrap.API.gameDirectory();
        final Path earlyConfigPath = gamePath.resolve("config")
            .resolve("lwjgl3ify-early.json");
        final Gson gson = new GsonBuilder().disableJdkUnsafe()
            .setPrettyPrinting()
            .create();
        ConfigObject cfg = new ConfigObject();
        if (Files.exists(earlyConfigPath)) {
            try {
                final String earlyConfigContents = new String(
                    Files.readAllBytes(earlyConfigPath),
                    StandardCharsets.UTF_8);
                ConfigObject parsedConfig = gson.fromJson(earlyConfigContents, ConfigObject.class);
                if (Objects.nonNull(parsedConfig)) {
                    cfg = parsedConfig;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (cfg.extensibleEnums != null) {
            EXTENSIBLE_ENUMS.addAll(Arrays.asList(cfg.extensibleEnums));
        }
        cfg.extensibleEnums = EXTENSIBLE_ENUMS.toArray(new String[0]);

        final String jsonCfg = gson.toJson(cfg);
        try {
            Files.createDirectories(earlyConfigPath.getParent());
            Files.write(earlyConfigPath, jsonCfg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
