package us.teaminceptus.divisions.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

/**
 * Represents a Division
 * @since 1.0.0
 */
public final class Division {

    private final File folder;

    private final UUID id;
    private final long creationDate;
    private final OfflinePlayer owner;

    private String name;
    private Location home = null;
    private final Set<OfflinePlayer> members = new HashSet<>();

    private Division(File folder, UUID id, long creationDate, OfflinePlayer owner, boolean save) {
        this.folder = folder;

        this.id = id;
        this.creationDate = creationDate;
        this.owner = owner;

        if (save) save();
    }

    /**
     * Fetches the folder that this Division's metadata is stored in.
     * @return Division Folder
     * @since 1.0.0
     */
    @NotNull
    public File getFolder() {
        return folder;
    }

    /**
     * Fetches the Division's Name.
     * @return Division Name
     * @since 1.0.0
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Fetches this Division's unique identifier.
     * @return Division ID
     */
    @NotNull
    public UUID getUniqueId() {
        return id;
    }

    /**
     * Fetches the Owner of this Division.
     * @return OfflinePlayer
     * @since 1.0.0
     */
    @NotNull
    public OfflinePlayer getOwner() {
        return owner;
    }

    /**
     * Fetches the date this Division was created.
     * @return Date Created
     * @since 1.0.0
     */
    @NotNull
    public Date getCreationDate() {
        return new Date(creationDate);
    }

    /**
     * Fetches the home of this Division. May be null.
     * @return Division Home
     * @since 1.0.0
     */
    @Nullable
    public Location getHome() {
        return home;
    }

    /**
     * Fetches an immutable set of this Division's members.
     * @return Division Members
     * @since 1.0.0
     */
    @NotNull
    public Set<OfflinePlayer> getMembers() {
        return ImmutableSet.copyOf(members);
    }

    // Static Methods

    /**
     * Fetches an immutable list of all divisions.
     * @return Immutable List of Divisions
     * @throws IllegalStateException if one or more divisions is invalid
     * @since 1.0.0
     */
    public static List<Division> getDivisions() throws IllegalStateException {
        List<Division> divisions = new ArrayList<>();
        for (File f : DivConfig.getDivisionsDirectory().listFiles()) {
            if (!f.isDirectory()) continue;

            Division d;

            try {
                d = read(f.getAbsoluteFile());
            } catch (OptionalDataException e) {
                DivConfig.print(e);
                continue;
            } catch (IOException | ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }

            divisions.add(d);
        }

        return ImmutableList.copyOf(divisions);
    }

    // Writing & Reading

    /**
     * <p>Saves this Division to the file system.</p>
     * <p>This method is called automatically and does not need to be repeated.</p>
     * @since 1.0.0
     */
    public void save() {
        if (!folder.exists()) folder.mkdir();

        try {
            write();
        } catch (IOException e) {
            DivConfig.print(e);
        }
    }

    private void write() throws IOException {
        // Permanent Information

        File info = new File(folder, "info.dat");
        if (!info.exists()) info.createNewFile();

        FileOutputStream infoFs = new FileOutputStream(info);
        ObjectOutputStream infoOs = new ObjectOutputStream(infoFs);
        infoOs.writeObject(this.id);
        infoOs.writeLong(this.creationDate);
        infoOs.writeObject(this.owner.getUniqueId());
        infoOs.close();

        // Members

        File members = new File(folder, "members.dat");
        if (!members.exists()) members.createNewFile();

        FileOutputStream membersFs = new FileOutputStream(members);
        ObjectOutputStream membersOs = new ObjectOutputStream(membersFs);
        membersOs.writeObject(this.members.toArray(new OfflinePlayer[0]));
        membersOs.close();

        // Other Information

        File other = new File(folder, "other.yml");
        if (!other.exists()) other.createNewFile();

        FileConfiguration oConfig = YamlConfiguration.loadConfiguration(other);
        oConfig.set("name", this.name);

        if (home != null) oConfig.set("home", this.home);

        oConfig.save(other);
    }

    @NotNull
    private static Division read(File folder) throws IOException, IllegalStateException, ReflectiveOperationException {
        File info = new File(folder, "info.dat");
        if (!info.exists()) throw new IllegalStateException("Could not find: info.dat");

        FileInputStream infoFs = new FileInputStream(info);
        ObjectInputStream infoIs = new ObjectInputStream(infoFs);
        UUID id = (UUID) infoIs.readObject();
        long creationDate = infoIs.readLong();
        OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID) infoIs.readObject());
        infoIs.close();

        Division d = new Division(folder, id, creationDate, owner, false);

        File members = new File(folder, "members.dat");
        if (!members.exists()) throw new IllegalStateException("Could not find: members.dat");

        FileInputStream membersFs = new FileInputStream(members);
        ObjectInputStream membersIs = new ObjectInputStream(membersFs);
        OfflinePlayer[] membersA = (OfflinePlayer[]) membersIs.readObject();
        membersIs.close();

        d.members.addAll(ImmutableSet.copyOf(membersA));

        File other = new File(folder, "other.yml");
        if (!other.exists()) throw new IllegalStateException("Could not find: other.yml");

        FileConfiguration oConfig = YamlConfiguration.loadConfiguration(other);
        d.name = oConfig.getString("name");

        if (oConfig.isSet("home")) d.home = (Location) oConfig.get("home");

        return d;
    }

}
