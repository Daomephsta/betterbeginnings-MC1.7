package net.einsteinsci.betterbeginnings.register.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

public class SmelterRecipeHandler
{
	private static final SmelterRecipeHandler SMELTINGBASE = new SmelterRecipeHandler();

	private Map experienceList = new HashMap();

	private List<SmelterRecipe> recipes = new ArrayList<>();

	private SmelterRecipeHandler()
	{
		// nothing here
	}

	public static void addRecipe(Item input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		smelting().addLists(input, output, experience, gravel, bonus, chance);
	}

	public void addLists(Item input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		putLists(new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output, experience, gravel, bonus, chance);
	}

	public static SmelterRecipeHandler smelting()
	{
		return SMELTINGBASE;
	}

	public void putLists(ItemStack input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		experienceList.put(output, Float.valueOf(experience));

		recipes.add(new SmelterRecipe(output, input, experience, gravel, bonus, chance));
	}

	public static void addRecipe(Block input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		smelting().addLists(Item.getItemFromBlock(input), output, experience, gravel, bonus, chance);
	}

	public static void addRecipe(ItemStack input, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		smelting().putLists(input, output, experience, gravel, bonus, chance);
	}

	public static void addRecipe(String oreDict, ItemStack output, float experience, int gravel, int bonus, float chance)
	{
		for(ItemStack ore : OreDictionary.getOres(oreDict, false))
		{
			addRecipe(ore, output, experience, gravel, bonus, chance);
		}
	}
	
	public static void addRecipe(SmelterRecipe recipe, float experience)
	{
		smelting().recipes.add(recipe);
		smelting().experienceList.put(recipe.getInput(), experience);
	}

	public static void removeRecipe(ItemStack input, ItemStack output)
	{
		for(Iterator<SmelterRecipe> iter = SmelterRecipeHandler.getRecipes().iterator(); iter.hasNext();)
		{
			SmelterRecipe recipe = iter.next();
			if(ItemStack.areItemStacksEqual(recipe.getInput(), input))
			{
				iter.remove();
				smelting().experienceList.remove(input);
				break;
			}
		}
	}
	
	public static Map<SmelterRecipe, Float> removeOutput(ItemStack output)
	{
		Map<SmelterRecipe, Float> removedRecipes = Maps.newHashMap();
		for(Iterator<SmelterRecipe> iter = SmelterRecipeHandler.getRecipes().iterator(); iter.hasNext();)
		{
			SmelterRecipe recipe = iter.next();
			if(ItemStack.areItemStacksEqual(recipe.getOutput(), output))
			{
				removedRecipes.put(recipe, smelting().giveExperience(recipe.getInput()));
				iter.remove();
				smelting().experienceList.remove(output);
			}
		}
		return removedRecipes;
	}

	public ItemStack getSmeltingResult(ItemStack input)
	{
		for (SmelterRecipe recipe : recipes)
		{
			if (recipe.getInput().getItem() == input.getItem())
			{
				return recipe.getOutput();
			}
		}

		return null;
	}

	public int getGravelCount(ItemStack stack)
	{
		for (SmelterRecipe recipe : recipes)
		{
			if (recipe.getInput() != null)
			{
				if (recipe.getInput().getItem() == stack.getItem())
				{
					return recipe.getGravel();
				}
			}
		}

		return -1;
	}

	public float giveExperience(ItemStack stack)
	{
		Iterator iterator = experienceList.entrySet().iterator();
		Entry entry;

		do
		{
			if (!iterator.hasNext())
			{
				return 0.0f;
			}

			entry = (Entry)iterator.next();
		} while (!canBeSmelted(stack, (ItemStack)entry.getKey()));

		if (stack.getItem().getSmeltingExperience(stack) != -1)
		{
			return stack.getItem().getSmeltingExperience(stack);
		}

		return ((Float)entry.getValue()).floatValue();
	}

	private boolean canBeSmelted(ItemStack stack, ItemStack stack2)
	{
		return stack2.getItem() == stack.getItem() &&
				(stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == stack
				.getItemDamage());
	}

	public int getBonus(ItemStack input)
	{
		for (SmelterRecipe recipe : recipes)
		{
			if (recipe.getInput().getItem() == input.getItem())
			{
				return recipe.getBonus();
			}
		}

		return 0;
	}

	public float getBonusChance(ItemStack input)
	{
		for (SmelterRecipe recipe : recipes)
		{
			if (recipe.getInput().getItem() == input.getItem())
			{
				return recipe.getBonusChance();
			}
		}

		return 0.0f;
	}

	public static List<SmelterRecipe> getRecipes()
	{
		return smelting().recipes;
	}
	
	public static Map getXPList()
	{
		return smelting().experienceList;
	}
}
