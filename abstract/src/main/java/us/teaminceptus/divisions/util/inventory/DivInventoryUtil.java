package us.teaminceptus.divisions.util.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.DivConfig;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;

public final class DivInventoryUtil {

    @Nullable
    public static ItemStack getHead(String key) {
        try {
            Properties p = new Properties();
            p.load(DivInventoryUtil.class.getResourceAsStream("/util/heads.properties"));

            String value = p.getProperty(key);
            if (value == null) return null;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta hMeta = (SkullMeta) head.getItemMeta();

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", value));
            Method mtd = hMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(hMeta, profile);

            head.setItemMeta(hMeta);
            return head;
        } catch (IOException | ReflectiveOperationException e) {
            DivConfig.print(e);
        }

        return null;
    }

}
