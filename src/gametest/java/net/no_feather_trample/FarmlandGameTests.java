package net.no_feather_trample;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.impl.game.minecraft.McVersionLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;

public class FarmlandGameTests {
    private static final BlockPos SOIL = new BlockPos(2, 1, 2);

    @GameTest
    public void supportedMinecraftRange(GameTestHelper h) throws Exception {
        var dependency = FabricLoader.getInstance().getModContainer(NoFeatherTrample.MOD_ID).orElseThrow()
                .getMetadata().getDependencies().stream()
                .filter(entry -> entry.getModId().equals("minecraft")).findFirst().orElseThrow();
        for (String id : new String[] { "26.4-snapshot-6", "26.4-snapshot-7", "26.4-snapshot-10",
                "26.4-pre-1", "26.4-rc-1", "26.4" }) {
            h.assertTrue(dependency.matches(Version.parse(McVersionLookup.normalizeVersion(id,
                    McVersionLookup.getRelease(id)))), "Supported Minecraft version: " + id);
        }
        for (String id : new String[] { "26.3", "26.4-snapshot-1", "26.4-snapshot-5",
                "26.4.1-pre-1", "26.4.1", "26.5-snapshot-1", "26.5", "27.1" }) {
            h.assertTrue(!dependency.matches(Version.parse(McVersionLookup.normalizeVersion(id,
                    McVersionLookup.getRelease(id)))), "Excluded Minecraft version: " + id);
        }
        h.succeed();
    }

    private ItemStack enchantedBoots(GameTestHelper h, int level) {
        var boots = new ItemStack(Items.DIAMOND_BOOTS);
        boots.enchant(h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FEATHER_FALLING), level);
        return boots;
    }

    private void prepare(GameTestHelper h) {
        h.setBlock(SOIL, Blocks.FARMLAND.defaultBlockState().setValue(FarmlandBlock.MOISTURE, 7));
        h.setBlock(SOIL.above(), Blocks.WHEAT);
    }

    private void convert(GameTestHelper h, Entity cause) {
        var pos = h.absolutePos(SOIL);
        ((FarmlandBlock) Blocks.FARMLAND).turnToBaseBlock(cause, h.getLevel().getBlockState(pos), h.getLevel(), pos);
    }

    private void protectedSoil(GameTestHelper h) {
        h.assertBlockPresent(Blocks.FARMLAND, SOIL);
        h.assertBlockProperty(SOIL, FarmlandBlock.MOISTURE, 7);
        h.assertBlockPresent(Blocks.WHEAT, SOIL.above());
    }

    @GameTest
    public void playerFeatherFallingAllLevels(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        for (int level = 1; level <= 4; level++) {
            prepare(h);
            player.setItemSlot(EquipmentSlot.FEET, enchantedBoots(h, level));
            convert(h, player);
            protectedSoil(h);
        }
        h.succeed();
    }

    @GameTest
    public void playerSlowFallingAndRemoval(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        prepare(h);
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200));
        convert(h, player);
        protectedSoil(h);
        player.removeEffect(MobEffects.SLOW_FALLING);
        convert(h, player);
        h.assertBlockPresent(Blocks.DIRT, SOIL);
        h.succeed();
    }

    @GameTest
    public void mobFeatherFalling(GameTestHelper h) {
        var zombie = h.spawn(EntityTypes.ZOMBIE, new BlockPos(4, 2, 4));
        zombie.setItemSlot(EquipmentSlot.FEET, enchantedBoots(h, 1));
        prepare(h);
        convert(h, zombie);
        protectedSoil(h);
        zombie.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
        convert(h, zombie);
        h.assertBlockPresent(Blocks.DIRT, SOIL);
        h.succeed();
    }

    @GameTest
    public void mobSlowFallingAndBothProtections(GameTestHelper h) {
        var zombie = h.spawn(EntityTypes.ZOMBIE, new BlockPos(4, 2, 4));
        zombie.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200));
        prepare(h);
        convert(h, zombie);
        protectedSoil(h);
        zombie.setItemSlot(EquipmentSlot.FEET, enchantedBoots(h, 4));
        convert(h, zombie);
        protectedSoil(h);
        zombie.removeEffect(MobEffects.SLOW_FALLING);
        convert(h, zombie);
        protectedSoil(h);
        h.succeed();
    }

    @GameTest
    public void unprotectedPlayersAndMobs(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var cow = h.spawn(EntityTypes.COW, new BlockPos(4, 2, 4));
        for (LivingEntity entity : new LivingEntity[] { player, cow }) {
            prepare(h);
            convert(h, entity);
            h.assertBlockPresent(Blocks.DIRT, SOIL);
        }
        h.succeed();
    }

    @GameTest
    public void inventoryAndHandAreNotEquippedBoots(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        player.getInventory().add(enchantedBoots(h, 4));
        player.setItemSlot(EquipmentSlot.MAINHAND, enchantedBoots(h, 4));
        prepare(h);
        convert(h, player);
        h.assertBlockPresent(Blocks.DIRT, SOIL);
        h.succeed();
    }

    @GameTest
    public void unrelatedEnchantmentAndEffect(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var boots = new ItemStack(Items.DIAMOND_BOOTS);
        boots.enchant(h.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.PROTECTION), 4);
        player.setItemSlot(EquipmentSlot.FEET, boots);
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 200));
        prepare(h);
        convert(h, player);
        h.assertBlockPresent(Blocks.DIRT, SOIL);
        h.succeed();
    }

    @GameTest
    public void environmentAndNonLivingCause(GameTestHelper h) {
        prepare(h);
        convert(h, null);
        h.assertBlockPresent(Blocks.DIRT, SOIL);
        prepare(h);
        convert(h, h.spawn(EntityTypes.MINECART, new BlockPos(4, 2, 4)));
        h.assertBlockPresent(Blocks.DIRT, SOIL);
        h.succeed();
    }

    @GameTest
    public void solidBlockStillConvertsFarmland(GameTestHelper h) {
        prepare(h);
        h.setBlock(SOIL.above(), Blocks.STONE);
        h.runAfterDelay(5, () -> {
            h.assertBlockPresent(Blocks.DIRT, SOIL);
            h.succeed();
        });
    }

    @GameTest
    public void actualFallKeepsSoilAndFallDamage(GameTestHelper h) {
        var zombie = h.spawn(EntityTypes.ZOMBIE, new BlockPos(4, 2, 4));
        zombie.setItemSlot(EquipmentSlot.FEET, enchantedBoots(h, 1));
        prepare(h);
        float before = zombie.getHealth();
        var pos = h.absolutePos(SOIL);
        Blocks.FARMLAND.fallOn(h.getLevel(), h.getLevel().getBlockState(pos), pos, zombie, 8.0);
        protectedSoil(h);
        h.assertTrue(zombie.getHealth() < before, "Vanilla fall damage still runs");
        h.succeed();
    }
}


