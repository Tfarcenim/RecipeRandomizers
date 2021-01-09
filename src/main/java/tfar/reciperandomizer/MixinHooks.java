package tfar.reciperandomizer;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.*;

public class MixinHooks {

    public static Random random = new Random();

    public static Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> scramble(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> original) {
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> scrambledRecipes = new HashMap<>();
        List<IRecipe<?>> craftingRecipes = new ArrayList<>(original.get(IRecipeType.CRAFTING).values());
        List<IRecipe<?>> smeltingRecipes = new ArrayList<>(original.get(IRecipeType.SMELTING).values());
        List<IRecipe<?>> smokingRecipes = new ArrayList<>(original.get(IRecipeType.SMOKING).values());
        List<IRecipe<?>> blastingRecipes = new ArrayList<>(original.get(IRecipeType.BLASTING).values());
        List<IRecipe<?>> campfireRecipes = new ArrayList<>(original.get(IRecipeType.CAMPFIRE_COOKING).values());
        List<IRecipe<?>> outputs = new ArrayList<>();
        outputs.addAll(craftingRecipes);
        outputs.addAll(smeltingRecipes);
        outputs.addAll(smokingRecipes);
        outputs.addAll(blastingRecipes);
        outputs.addAll(campfireRecipes);


        for (Map.Entry<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> entry : original.entrySet()) {
            IRecipeType<?> recipeType = entry.getKey();
            if (recipeType == IRecipeType.CRAFTING ||
                    recipeType == IRecipeType.SMELTING ||
                    recipeType == IRecipeType.SMOKING ||
                    recipeType == IRecipeType.BLASTING ||
                    recipeType == IRecipeType.CAMPFIRE_COOKING) {
                Map<ResourceLocation, IRecipe<?>> scrambledCrafting = scrambleRecipes(entry.getValue(), outputs);
                scrambledRecipes.put(entry.getKey(), scrambledCrafting);
            } else {
                scrambledRecipes.put(entry.getKey(), entry.getValue());
            }
        }
        return scrambledRecipes;
    }

    public static Map<ResourceLocation, IRecipe<?>> scrambleRecipes(Map<ResourceLocation, IRecipe<?>> original, List<IRecipe<?>> outputs) {
        Map<ResourceLocation, IRecipe<?>> scrambledCrafting = new HashMap<>();
        for (Map.Entry<ResourceLocation, IRecipe<?>> entry : original.entrySet()) {
            IRecipe<?> iRecipe = entry.getValue();
                if (iRecipe.getClass() == ShapedRecipe.class) {
                    ItemStack newResult = getRandom(outputs);
                    ObfuscationReflectionHelper.setPrivateValue(ShapedRecipe.class,
                            (ShapedRecipe) iRecipe, newResult, "field_77575_e");
                    scrambledCrafting.put(entry.getKey(), iRecipe);
                } else if (iRecipe.getClass() == ShapelessRecipe.class) {
                    ItemStack newResult = getRandom(outputs);
                    ObfuscationReflectionHelper.setPrivateValue(ShapelessRecipe.class,
                            (ShapelessRecipe) iRecipe, newResult, "field_77580_a");
                    scrambledCrafting.put(entry.getKey(), iRecipe);
                } else if (iRecipe instanceof AbstractCookingRecipe) {
                    ItemStack newResult = getRandom(outputs);
                    ObfuscationReflectionHelper.setPrivateValue(AbstractCookingRecipe.class,
                            (AbstractCookingRecipe) iRecipe, newResult, "field_222143_e");
                    scrambledCrafting.put(entry.getKey(), iRecipe);
                } else {
                    scrambledCrafting.put(entry.getKey(), entry.getValue());
                }
        }
        return scrambledCrafting;
    }

    public static ItemStack getRandom(List<IRecipe<?>> outputs) {
        ItemStack stack = ItemStack.EMPTY;
        while (stack.isEmpty()) {
            stack = outputs.get(random.nextInt(outputs.size())).getRecipeOutput();
        }
        return stack.copy();
    }
}
