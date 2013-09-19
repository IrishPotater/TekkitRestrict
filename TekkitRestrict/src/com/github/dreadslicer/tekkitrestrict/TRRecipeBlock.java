package com.github.dreadslicer.tekkitrestrict;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.FurnaceRecipes;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.github.dreadslicer.tekkitrestrict.Log.Warning;
import com.github.dreadslicer.tekkitrestrict.objects.TREnums.ConfigFile;
import com.github.dreadslicer.tekkitrestrict.objects.TRItem;

public class TRRecipeBlock {
	public static void reload() {
		blockConfigRecipes();
	}

	public static void blockConfigRecipes() {
		// config
		List<String> ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeBlock");
		for (String s : ssr) {
			List<TRItem> iss = TRCacheItem.processItemStringNoCache(s);
			for (TRItem ir : iss) {
				try {
					blockRecipeVanilla(ir.id, ir.data);
				} catch (Exception e) {
					Warning.other("Error! [TRRecipe-RecipeBlockVanilla] " + e.getMessage());
				}
				try {
					blockRecipeForge(ir.id, ir.data);
				} catch (Exception e) {
					Warning.other("Error! [TRRecipe-RecipeBlockForge] " + e.getMessage());
				}
			}
		}

		ssr = tekkitrestrict.config.getStringList(ConfigFile.Advanced, "RecipeFurnaceBlock");
		for (String s : ssr) {
			List<TRItem> iss = TRCacheItem.processItemStringNoCache(s);
			for (TRItem ir : iss) {
				try {
					blockFurnaceRecipe(ir.id, ir.data);
				} catch (Exception ex) {
					Warning.other("Error! [TRRecipe-Furnace Block] " + ex.getMessage());
				}
			}
		}
	}

	public static boolean blockRecipeVanilla(int id, int data) {
		boolean status = false;
		Iterator<Recipe> recipes = Bukkit.recipeIterator();
		Recipe recipe;

		while (recipes.hasNext()) {
			if ((recipe = recipes.next()) != null) {
				int tid = recipe.getResult().getData().getItemTypeId();//TODO .getTypeId()?
				int tdata = recipe.getResult().getDurability();
				if (tid == id && (tdata == data || data == 0)) {
					recipes.remove();
					status = true;
				}
			}
		}

		return status;
	}

	public static boolean blockRecipeForge(int id, int data) {
		boolean status = false;
		// loop through recipes...
		List<Object> recipes = CraftingManager.getInstance().recipies;

		for (int i = 0; i < recipes.size(); i++) {
			Object r = recipes.get(i);
			if (r instanceof ShapedRecipe) {
				ShapedRecipe recipe = (ShapedRecipe) r;
				int tid = recipe.getResult().getTypeId();
				int tdata = recipe.getResult().getDurability();
				if (tid == id && (tdata == data || data == 0)) {
					recipes.remove(i);
					i--;
					status = true;
				}
			}
			if (r instanceof ShapelessRecipe) {
				ShapelessRecipe recipe = (ShapelessRecipe) r;
				int tid = recipe.getResult().getTypeId();
				int tdata = recipe.getResult().getDurability();
				if (tid == id && (tdata == data || data == 0)) {
					recipes.remove(i--);
					status = true;
				}
			}
		}
		return status;
	}

	public static boolean blockFurnaceRecipe(int id, int data) {
		boolean status = false;
		FurnaceRecipes.getInstance().addSmelting(id, data, null);
		FurnaceRecipes.getInstance().recipies.remove(id);
		return status;
	}
}
