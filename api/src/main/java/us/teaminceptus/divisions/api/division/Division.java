package us.teaminceptus.divisions.api.division;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.divisions.api.DivConfig;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

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
    private double experience = 0.0;

    private final Map<DivisionAchievement, Integer> achievements = new EnumMap<>(DivisionAchievement.class);
    private final Set<OfflinePlayer> members = new HashSet<>();

    {
        for (DivisionAchievement value : DivisionAchievement.values()) achievements.putIfAbsent(value, 0);
    }

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
     * Sets the Division's Name.
     * @param name Division Name
     * @since 1.0.0
     */
    public void setName(@NotNull String name) {
        if (name == null) throw new IllegalArgumentException("Division name cannot be null");

        this.name = name;
        save();
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
     * Sets the home of this Division.
     * @param home Division Home
     * @since 1.0.0
     */
    public void setHome(@Nullable Location home) {
        this.home = home;
        save();
    }

    /**
     * Resets the home of the Division.
     * @since 1.0.0
     */
    public void resetHome() {
        setHome(null);
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

    /**
     * Fetches the level of a DivisionAchievement.
     * @param achievement DivisionAchievement to use
     * @return Level of Achievement
     * @since 1.0.0
     */
    public int getAchievementLevel(@NotNull DivisionAchievement achievement) {
        if (achievement == null) throw new IllegalArgumentException("Achievement cannot be null");

        return achievements.getOrDefault(achievement, 0);
    }

    // Static Methods

    /**
     * Fetches an immutable list of all divisions.
     * @return Immutable List of Divisions
     * @throws IllegalStateException if one or more divisions is invalid
     * @since 1.0.0
     */
    @NotNull
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

    /**
     * Fetches a Division by its name.
     * @param name Division Name
     * @return Division
     * @since 1.0.0
     */
    @Nullable
    public static Division byName(@NotNull String name) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        return getDivisions()
                .stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Fetches a Division by its unique identifier.
     * @param id Division ID
     * @return Division
     * @since 1.0.0
     */
    @Nullable
    public static Division byId(@NotNull UUID id) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        return getDivisions()
                .stream()
                .filter(d -> d.getUniqueId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Fetches a Division by its owner.
     * @param owner Division Owner
     * @return Division
     * @since 1.0.0
     */
    @Nullable
    public static Division byOwner(@NotNull OfflinePlayer owner) {
        if (owner == null) throw new IllegalArgumentException("owner cannot be null");
        return getDivisions()
                .stream()
                .filter(d -> d.getOwner().equals(owner))
                .findFirst()
                .orElse(null);
    }

    /**
     * Deletes a Division.
     * @param d Division to delete
     * @since 1.0.0
     */
    public static void removeDivision(@NotNull Division d) {
        if (d == null) throw new IllegalArgumentException("Division cannot be null");

        File folder = d.getFolder();
        for (File f : folder.listFiles()) f.delete();
        folder.delete();
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

        ObjectOutputStream infoOs = new ObjectOutputStream(Files.newOutputStream(info.toPath()));
        infoOs.writeObject(this.id);
        infoOs.writeLong(this.creationDate);
        infoOs.writeObject(this.owner.getUniqueId());
        infoOs.close();

        // Members

        File members = new File(folder, "members.dat");
        if (!members.exists()) members.createNewFile();

        ObjectOutputStream membersOs = new ObjectOutputStream(Files.newOutputStream(members.toPath()));
        membersOs.writeObject(
                this.members
                .stream()
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toList())
        );
        membersOs.close();

        // Achievements
        File achievements = new File(folder, "achievements.dat");
        if (!achievements.exists()) achievements.createNewFile();

        ObjectOutputStream achievementsOs = new ObjectOutputStream(Files.newOutputStream(achievements.toPath()));
        achievementsOs.writeObject(this.achievements);
        achievementsOs.close();

        // Other Information

        File other = new File(folder, "other.yml");
        if (!other.exists()) other.createNewFile();

        FileConfiguration oConfig = YamlConfiguration.loadConfiguration(other);
        oConfig.set("name", this.name);
        oConfig.set("home", this.home);
        oConfig.set("experience", this.experience);

        oConfig.save(other);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static Division read(File folder) throws IOException, IllegalStateException, ReflectiveOperationException {
        // ID Information

        File info = new File(folder, "info.dat");
        if (!info.exists()) throw new IllegalStateException("Could not find: info.dat");

        ObjectInputStream infoIs = new ObjectInputStream(Files.newInputStream(info.toPath()));
        UUID id = (UUID) infoIs.readObject();
        long creationDate = infoIs.readLong();
        OfflinePlayer owner = Bukkit.getOfflinePlayer((UUID) infoIs.readObject());
        infoIs.close();

        Division d = new Division(folder, id, creationDate, owner, false);

        // Members

        File members = new File(folder, "members.dat");
        if (!members.exists()) throw new IllegalStateException("Could not find: members.dat");

        ObjectInputStream membersIs = new ObjectInputStream(Files.newInputStream(members.toPath()));
        List<UUID> memberIds = (List<UUID>) membersIs.readObject();
        membersIs.close();

        d.members.addAll(memberIds
                .stream()
                .map(Bukkit::getOfflinePlayer)
                .collect(Collectors.toList()));

        // Achievements

        File achievements = new File(folder, "achievements.dat");
        if (!achievements.exists()) throw new IllegalStateException("Could not find: achievements.dat");

        ObjectInputStream achievementsIs = new ObjectInputStream(Files.newInputStream(achievements.toPath()));
        Map<DivisionAchievement, Integer> achievementsMap = (Map<DivisionAchievement, Integer>) achievementsIs.readObject();
        achievementsIs.close();

        d.achievements.putAll(achievementsMap);

        // Other Information

        File other = new File(folder, "other.yml");
        if (!other.exists()) throw new IllegalStateException("Could not find: other.yml");

        FileConfiguration oConfig = YamlConfiguration.loadConfiguration(other);
        d.name = oConfig.getString("name");
        d.home = (Location) oConfig.get("home");
        d.experience = oConfig.getDouble("experience", 0);

        return d;
    }

}
