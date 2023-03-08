package me.json.pedestrians.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class EditorViewInventory {

    private final Inventory inventory;
    private final ItemStack arrowHead;
    private final ItemStack stick;

    public EditorViewInventory() {
        inventory = Bukkit.createInventory(null, InventoryType.PLAYER);
        stick = createGuiItem(Material.BLAZE_ROD, "PathNetwork editor stick", "");

        inventory.setItem(0, stick);

        //For the stands
        arrowHead = initArrowHead();
    }

    //Getters
    public ItemStack arrowHead() {
        return this.arrowHead;
    }

    public ItemStack stick() {
        return this.stick;
    }

    //Functionality
    public void pushToPlayer(Player player) {
        player.getInventory().setContents(inventory.getContents());
    }

    private ItemStack createGuiItem(Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack initArrowHead() {
        ItemStack arrowHead = new ItemStack(Material.PLAYER_HEAD);
        return Bukkit.getUnsafe().modifyItemStack(arrowHead, "{SkullOwner:{Id:[I;-1683800122,-617199414,-1319910397,1295179531],Properties:{textures:[{Value:\"ewogICJ0aW1lc3RhbXAiIDogMTY3MTEwNzY4NTA3NSwKICAicHJvZmlsZUlkIiA6ICI2ZTIyNjYxZmNlMTI0MGE0YWE4OTA0NDA0NTFiYjBiNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJncnZleWFyZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mOGI1ZGNhM2IxMzRmYmNmZDBhZDQ4NmY5MDI4NjBkNzA4YzU0MzY1NTJmMmU2YWVlODEyYjNkZWJjMDYwYTI4IgogICAgfQogIH0KfQ==\",Signature:\"ZeTbJokwgcBpTeRb4tTua8VxzKROfZ3CKiggp8+FTixg/QHHFRdPv3sENw8OhD+CN4xicDGfAyq64NaptxE1Xh4tFBMqg8PgATu7Bx+2KH7kW96Hp1eG0KzFRxq3P5XWCdsGxYmRBpIaVOMp6SFzpGBxeuhknPbZ5hlvR5XCC3fNrqrUnc5Lj8Ewwbt02JKkmkcYEXFME/M1/AUcxsOTpHdTf3/ILsrLh2LT8uq/uqHzfLhBT8yu6pYQWWXHTGSPCJO+qNBwierjSHxaK5ajjS4gJoWTRxqDNXG9iLorH794s/HEXkGRVFWn2eneQYxTnGKD7mqxss8zkREWknhI3d+C4ZoxkB5v2BorxLUvX3gxpDb4ixGpm4C+qyB6slz3KAdsBxTbx6SJgNnHYg0xwvvhKdN3CHFxVLbnwlk8gVRMQeVIZ1p3Uv1yNCesqj60vpdk/7v9W8Cq06acRhVyKYCg3HiBkY4JQx7qKrt4d8zHUumakKBOqewnatV0VmmYO5gIwRHi/gJyRSWPOmKnuGR1SxqNIjikBWDcjwDaZFJeBelY4j6yCdofQT9MOHvxHd0SzuNh1j1s0fwbEPdGYVY2tnTonXwdIfXGvA9vXsXk7DelfhfaAgw+8daoW1TJIt/obYXolYnEyMfpc4iBiUzmGuORmy7NzxGdV+Ho63g=\"}]}},display:{Name:\"{\\\"text\\\":\\\"fca2a5c1\\\"}\",Lore:[\"{\\\"text\\\":\\\"Skin fca2a5c1\\\"}\",\"{\\\"text\\\":\\\"generated on Dec 15, 2022 1:34:45 PM\\\"}\",\"{\\\"text\\\":\\\"via MineSkin.org\\\"}\",\"{\\\"text\\\":\\\"https://minesk.in/fca2a5c110dc445b83b0a42e4c84cd66\\\"}\"]}}\n");

    }

}
