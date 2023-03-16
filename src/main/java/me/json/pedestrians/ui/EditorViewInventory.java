package me.json.pedestrians.ui;

import me.json.pedestrians.ui.tasks.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EditorViewInventory {

    private static final String nodeArrowSkin = "{SkullOwner:{Id:[I;-1683800122,-617199414,-1319910397,1295179531],Properties:{textures:[{Value:\"ewogICJ0aW1lc3RhbXAiIDogMTY3MTEwNzY4NTA3NSwKICAicHJvZmlsZUlkIiA6ICI2ZTIyNjYxZmNlMTI0MGE0YWE4OTA0NDA0NTFiYjBiNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJncnZleWFyZCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mOGI1ZGNhM2IxMzRmYmNmZDBhZDQ4NmY5MDI4NjBkNzA4YzU0MzY1NTJmMmU2YWVlODEyYjNkZWJjMDYwYTI4IgogICAgfQogIH0KfQ==\",Signature:\"ZeTbJokwgcBpTeRb4tTua8VxzKROfZ3CKiggp8+FTixg/QHHFRdPv3sENw8OhD+CN4xicDGfAyq64NaptxE1Xh4tFBMqg8PgATu7Bx+2KH7kW96Hp1eG0KzFRxq3P5XWCdsGxYmRBpIaVOMp6SFzpGBxeuhknPbZ5hlvR5XCC3fNrqrUnc5Lj8Ewwbt02JKkmkcYEXFME/M1/AUcxsOTpHdTf3/ILsrLh2LT8uq/uqHzfLhBT8yu6pYQWWXHTGSPCJO+qNBwierjSHxaK5ajjS4gJoWTRxqDNXG9iLorH794s/HEXkGRVFWn2eneQYxTnGKD7mqxss8zkREWknhI3d+C4ZoxkB5v2BorxLUvX3gxpDb4ixGpm4C+qyB6slz3KAdsBxTbx6SJgNnHYg0xwvvhKdN3CHFxVLbnwlk8gVRMQeVIZ1p3Uv1yNCesqj60vpdk/7v9W8Cq06acRhVyKYCg3HiBkY4JQx7qKrt4d8zHUumakKBOqewnatV0VmmYO5gIwRHi/gJyRSWPOmKnuGR1SxqNIjikBWDcjwDaZFJeBelY4j6yCdofQT9MOHvxHd0SzuNh1j1s0fwbEPdGYVY2tnTonXwdIfXGvA9vXsXk7DelfhfaAgw+8daoW1TJIt/obYXolYnEyMfpc4iBiUzmGuORmy7NzxGdV+Ho63g=\"}]}}}";
    private static final String uploadSkin = "{SkullOwner:{Id:[I;1548839307,-940487451,-1579485687,1433907142],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQ3ZDFkZDRhN2RhZmYyYWFmMjhlNmExMmEwMWY0MmQ3ZTUxNTkzZWYzZGVhNzYyZWY4MTg0N2IxZDRjNTUzOCJ9fX0=\"}]}}}";
    private static final String settingsSkin = "{SkullOwner:{Id:[I;964276231,-288534935,-1275237935,-1202727934],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkNDliYWU5NWM3OTBjM2IxZmY1YjJmMDEwNTJhNzE0ZDYxODU0ODFkNWIxYzg1OTMwYjNmOTlkMjMyMTY3NCJ9fX0=\"}]}}}";
    private static final String plusSkin = "{SkullOwner:{Id:[I;1695939662,981878947,-1528958437,-1266742275],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThjZjJjMmI3NWI5NzM0MzkwMWY2N2VjMGVmYmNmYzBmNDkzMzVlYmFjNDQwNGUyN2NmMjVhNzlkYmQyMTU2MSJ9fX0=\"}]}}}";
    private static final String minusSkin = "{SkullOwner:{Id:[I;-2054272208,-1346547786,-1358978337,-325750104],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNkN2E5ZWUzMTM0OGEzNTc1NDM4M2MxNjdmYTMzYWJjMDJlOGU2OGNhMmM0YTk2OTE0MDBlN2ZlMzRiM2ViNSJ9fX0=\"}]}}}";
    private static final String crossSkin = "{SkullOwner:{Id:[I;-357038652,-1170846806,-2078329597,496329811],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTljZGI5YWYzOGNmNDFkYWE1M2JjOGNkYTc2NjVjNTA5NjMyZDE0ZTY3OGYwZjE5ZjI2M2Y0NmU1NDFkOGEzMCJ9fX0=\"}]}}}";
    private static final String arrowSkin = "{SkullOwner:{Id:[I;1020901283,-927184379,-1934029971,1181990057],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFjMGVkZWRkNzExNWZjMWIyM2Q1MWNlOTY2MzU4YjI3MTk1ZGFmMjZlYmI2ZTQ1YTY2YzM0YzY5YzM0MDkxIn19fQ==\"}]}}}";

    private final Map<ItemStack, TaskType> taskItems = new HashMap<>();
    private final Inventory inventory;

    private final ItemStack nodeArrowHead;

    public EditorViewInventory() {

        inventory = Bukkit.createInventory(null, InventoryType.PLAYER);

        //Items
        ItemStack uploadHead = head(uploadSkin, "Export/Save PathNetwork");
        inventory.setItem(7, uploadHead);

        ItemStack settingsHead = head(settingsSkin, "Node Properties");
        inventory.setItem(3, settingsHead);

        ItemStack plusHead = head(plusSkin, "Add Node");
        inventory.setItem(0, plusHead);
        taskItems.put(plusHead, TaskType.ADD_TASK);

        ItemStack minusHead = head(minusSkin, "Remove Node");
        inventory.setItem(1, minusHead);

        ItemStack crossHead = head(crossSkin, "Close Editor");
        inventory.setItem(8, crossHead);

        ItemStack arrowHead = head(arrowSkin, "Connect Nodes");
        inventory.setItem(2, arrowHead);

        //For the stands
        nodeArrowHead = head(nodeArrowSkin);

    }

    //Getters


    @Nullable
    public TaskType task(ItemStack itemStack) {
        return taskItems.getOrDefault(itemStack, null);
    }

    //Functionality
    public void pushToPlayer(Player player) {
        player.getInventory().setContents(inventory.getContents());
    }

    private ItemStack head(String metadata) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        return Bukkit.getUnsafe().modifyItemStack(head, metadata);
    }

    private ItemStack head(String metadata, String name) {
        ItemStack head = head(metadata);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(name);
        head.setItemMeta(meta);
        return head;
    }

}
