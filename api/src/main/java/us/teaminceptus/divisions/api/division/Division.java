package us.teaminceptus.divisions.api.division;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import us.teaminceptus.divisions.api.DivConfig;
import us.teaminceptus.divisions.api.division.logs.AuditAction;
import us.teaminceptus.divisions.api.division.logs.AuditLogEntry;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a Division
 * @since 1.0.0
 */
public final class Division {

    // Constants

    /**
     * The maximum amount of players a Division can have.
     * @since 1.0.0
     */
    public static final int MAX_PLAYERS = 1000;

    // Fields

    private final File folder;

    private final UUID id;
    private final long creationDate;
    private final OfflinePlayer owner;

    private String name;
    private Location home = null;
    private double experience = 0.0;
    private String prefix = null;
    private String tagline = "";

    private final List<String> messageLog = new ArrayList<>();
    private final Map<AuditAction, AuditLogEntry> auditLog = new HashMap<>();

    private final Map<DivisionAchievement, Integer> achievements = new EnumMap<>(DivisionAchievement.class);

    private final Set<OfflinePlayer> members = new HashSet<>();

    private final Map<SocialMedia, String> socialMedia = new EnumMap<>(SocialMedia.class);

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
    @Unmodifiable
    public Set<OfflinePlayer> getMembers() {
        return ImmutableSet.copyOf(members);
    }

    /**
     * Fetches an immutable set of this Division's online members.
     * @return Online Division Members
     * @since 1.0.0
     */
    @NotNull
    @Unmodifiable
    public Set<Player> getOnlineMembers() {
        return members.stream()
                .filter(OfflinePlayer::isOnline)
                .map(OfflinePlayer::getPlayer)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    /**
     * Adds a member to this Division.
     * @param player Player to add
     * @since 1.0.0
     * @throws IllegalArgumentException if player is null
     * @throws IllegalStateException if player is already a member of any division, or division is full
     */
    public void addMember(@NotNull OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (members.contains(player) || isInDivision(player)) throw new IllegalStateException("Player is already a member of a division");
        if (members.size() >= MAX_PLAYERS) throw new IllegalStateException("Division is full");

        members.add(player);
        save();
    }

    /**
     * Whether this OfflinePlayer is currently a member of this Division.
     * @param player Player to check
     * @return true if member, false otherwise
     */
    public boolean isMember(@NotNull OfflinePlayer player) {
        return members.contains(player);
    }

    /**
     * <p>Adds members to this Division.</p>
     * <p>This method will automatically sort through the Iterable to only add players that are not members.</p>
     * @param players Players to add
     * @throws IllegalArgumentException if iterable is null
     * @throws IllegalStateException if division is full
     */
    public void addMembers(@NotNull Iterable<? extends OfflinePlayer> players) throws IllegalArgumentException, IllegalStateException {
        if (players == null) throw new IllegalArgumentException("Players cannot be null");
        if (members.size() >= MAX_PLAYERS) throw new IllegalStateException("Division is full");

        for (OfflinePlayer player : players) if (!isInDivision(player)) addMember(player);
    }

    /**
     * Fetches the level of a DivisionAchievement.
     * @param achievement DivisionAchievement to use
     * @return Level of Achievement
     * @since 1.0.0
     * @throws IllegalArgumentException if achievement is null
     */
    public int getAchievementLevel(@NotNull DivisionAchievement achievement) throws IllegalArgumentException {
        if (achievement == null) throw new IllegalArgumentException("Achievement cannot be null");
        return achievements.getOrDefault(achievement, 0);
    }

    /**
     * Sets the level of a DivisionAchievement.
     * @param achievement DivisionAchievement to use
     * @param value Level of Achievement
     * @since 1.0.0
     * @throws IllegalArgumentException if achievement is null or value is negative
     */
    public void setAchievementLevel(@NotNull DivisionAchievement achievement, int value) throws IllegalArgumentException {
        if (achievement == null) throw new IllegalArgumentException("Achievement cannot be null");
        if (value < 0) throw new IllegalArgumentException("Value cannot be negative");

        achievements.put(achievement, value);
        save();
    }

    /**
     * Resets this Division's Achievement Level, setting it to 0.
     * @param achievement DivisionAchievement to use
     * @since 1.0.0
     * @throws IllegalArgumentException if achievement is null
     */
    public void resetAchievementLevel(@NotNull DivisionAchievement achievement) throws IllegalArgumentException {
        setAchievementLevel(achievement, 0);
    }

    /**
     * Fetches the social media link for the given SocialMedia from this Division.
     * @param media SocialMedia to use
     * @return Social Media Link
     * @since 1.0.0
     * @throws IllegalArgumentException if media is null
     */
    @NotNull
    public String getSocialMedia(@NotNull SocialMedia media) throws IllegalArgumentException {
        if (media == null) throw new IllegalArgumentException("Social Media cannot be null");

        return socialMedia.getOrDefault(media, "");
    }

    /**
     * Sets the social media link for the given SocialMedia from this Division.
     * @param media SocialMedia to use
     * @param link Social Media Link
     * @since 1.0.0
     * @throws IllegalArgumentException if media is null, or link is invalid for this SocialMedia
     */
    public void setSocialMedia(@NotNull SocialMedia media, @Nullable String link) throws IllegalArgumentException {
        if (media == null) throw new IllegalArgumentException("Social Media cannot be null");
        if (!media.isValidLink(link)) throw new IllegalArgumentException("Invalid Link for SocialMedia " + media.name());

        socialMedia.put(media, link);
        save();
    }

    /**
     * Resets the social media link for the given SocialMedia from this Division.
     * @param media SocialMedia to use
     * @since 1.0.0
     * @throws IllegalArgumentException if media is null
     */
    public void removeSocialMedia(@NotNull SocialMedia media) throws IllegalArgumentException {
        setSocialMedia(media, null);
    }

    /**
     * Fetches the experience amount of this Division.
     * @return Division Experience
     * @since 1.0.0
     */
    public double getExperience() {
        return experience;
    }

    /**
     * Sets the experience amount of this Division.
     * @param experience Division Experience
     * @since 1.0.0
     */
    public void setExperience(double experience) {
        this.experience = experience;
        save();
    }

    /**
     * Fetches the Division's current level.
     * @return Division Level
     * @since 1.0.0
     */
    public int getLevel() {
        return toLevel(experience);
    }

    /**
     * Sets the Division's current level.
     * @param level Division Level
     * @since 1.0.0
     */
    public void setLevel(int level) {
        this.experience = toExperience(level);
        save();
    }

    /**
     * Fetches the prefix used by this Division.
     * @return Division Prefix
     * @since 1.0.0
     */
    @Nullable
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix used by this Division.
     * @param prefix Division Prefix
     * @since 1.0.0
     */
    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
        save();
    }

    /**
     * Resets the prefix used by this Division.
     * @since 1.0.0
     */
    public void resetPrefix() {
        setPrefix(null);
    }

    /**
     * Fetches the tagline used by this Division.
     * @return Division Tagline
     * @since 1.0.0
     */
    @NotNull
    public String getTagline() {
        return tagline;
    }

    /**
     * Sets the tagline used by this Division.
     * @param tagline Division Tagline
     * @throws IllegalArgumentException if tagline is null
     */
    public void setTagline(@NotNull String tagline) throws IllegalArgumentException {
        if (tagline == null) throw new IllegalArgumentException("Tagline cannot be null");
        this.tagline = tagline;
        save();
    }

    /**
     * Resets the tagline used by this division, setting it to being empty.
     * @since 1.0.0
     */
    public void resetTagline() {
        setTagline("");
    }

    /**
     * Fetches an immutable list of the message log for this Division.
     * @return Message Log
     * @since 1.0.0
     */
    @Unmodifiable
    @NotNull
    public List<String> getMessageLog() {
        return ImmutableList.copyOf(messageLog);
    }

    /**
     * Fetches the prefix used in Division-only chat.
     * @return Division Chat Prefix
     * @since 1.0.0
     */
    public String getChatPrefix() {
        return ChatColor.GREEN + name + ChatColor.WHITE + " > " + ChatColor.RESET;
    }

    /**
     * Broadcasts a message to all members of this Division.
     * @param message Message to broadcast
     * @throws IllegalArgumentException if message is null
     */
    public void broadcastMessage(@NotNull String message) throws IllegalArgumentException {
        if (message == null) throw new IllegalArgumentException("Message cannot be null");
        for (Player player : getOnlineMembers()) player.sendMessage(getChatPrefix() + message);

        String logPrefix = new SimpleDateFormat("[HH:mm:ss] ").format(new Date());
        messageLog.add(logPrefix + message);
    }

    // Static Methods

    private static final List<Division> DIVISION_CACHE = new ArrayList<>();

    /**
     * Fetches an immutable list of all divisions.
     * @return Immutable List of Divisions
     * @throws IllegalStateException if one or more divisions is invalid
     * @since 1.0.0
     */
    @Unmodifiable
    @NotNull
    public static List<Division> getDivisions() throws IllegalStateException {
        if (!DIVISION_CACHE.isEmpty()) return DIVISION_CACHE;
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

        DIVISION_CACHE.addAll(divisions);

        return ImmutableList.copyOf(divisions);
    }

    /**
     * Fetches a Division by its name.
     * @param name Division Name
     * @return Division found, or null if not found
     * @since 1.0.0
     * @throws IllegalArgumentException if name is null
     */
    @Nullable
    public static Division byName(@NotNull String name) throws IllegalArgumentException {
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
     * @return Division found, or null if not found
     * @since 1.0.0
     * @throws IllegalArgumentException if id is null
     */
    @Nullable
    public static Division byId(@NotNull UUID id) throws IllegalArgumentException {
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
     * @return Division found, or null if not found
     * @since 1.0.0
     * @throws IllegalArgumentException if owner is null
     */
    @Nullable
    public static Division byOwner(@NotNull OfflinePlayer owner) throws IllegalArgumentException {
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
     * @throws IllegalArgumentException if division is null
     */
    public static void removeDivision(@NotNull Division d) throws IllegalArgumentException {
        if (d == null) throw new IllegalArgumentException("Division cannot be null");

        File folder = d.getFolder();
        for (File f : folder.listFiles()) f.delete();
        folder.delete();

        DIVISION_CACHE.clear();
    }

    /**
     * Determines whether a player is in any division.
     * @param p Player to check
     * @return true if in a division, false otherwise
     * @throws IllegalArgumentException if player is null
     */
    public static boolean isInDivision(@NotNull OfflinePlayer p) throws IllegalArgumentException {
        if (p == null) throw new IllegalArgumentException("Player cannot be null");
        return getDivisions()
                .stream()
                .anyMatch(d -> d.getMembers().contains(p));
    }

    /**
     * Converts a Division's experience amount to their Division Level.
     * @param experience Division's Experience
     * @return Division Level
     * @since 1.0.0
     */
    public static int toLevel(double experience) {
        if (experience < 0) return 0;

        int level = 0;
        while (toExperience(level) < experience) level++;
        return level;
    }

    /**
     * Converts a Division's level to the minimum required experience needed to get to that level.
     * @param level Division Level
     * @return Minimum Division Experience
     */
    public static double toExperience(int level) {
        if (level < 0) return 0;
        return Math.floor(Math.pow(level, 2.2 + (level / 3D)) + 1000 * level);
    }

    // Builder

    /**
     * Fetches the Divisions Builder.
     * @return Divisions Builder
     * @since 1.0.0
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The Main class used in the creation of a Division.
     * @since 1.0.0
     */
    public static final class Builder {

        String name;
        String tagline;
        String prefix;
        OfflinePlayer owner;

        final Map<SocialMedia, String> socialMedia = new HashMap<>();

        private Builder() {}

        /**
         * Sets the name of the Division.
         * @param name Division Name
         * @return this class, for chaining
         * @since 1.0.0
         * @throws IllegalArgumentException if name is null
         */
        @NotNull
        public Builder setName(@NotNull String name) throws IllegalArgumentException {
            if (name == null) throw new IllegalArgumentException("Name cannot be null");
            this.name = name;
            return this;
        }

        /**
         * Sets the tagline of the Division.
         * @param tagline Division Tagline
         * @return this class, for chaining
         * @since 1.0.0
         */
        @NotNull
        public Builder setTagline(@Nullable String tagline) {
            this.tagline = tagline == null ? "" : tagline;
            return this;
        }

        /**
         * Sets the prefix of the Division.
         * @param prefix Division Prefix
         * @return this class, for chaining
         * @since 1.0.0
         */
        @NotNull
        public Builder setPrefix(@Nullable String prefix) {
            this.prefix = prefix;
            return this;
        }

        /**
         * Sets the owner of the Division.
         * @param owner Division Owner
         * @return this class, for chaining
         * @since 1.0.0
         * @throws IllegalArgumentException if owner is null
         */
        @NotNull
        public Builder setOwner(@NotNull OfflinePlayer owner) throws IllegalArgumentException {
            if (owner == null) throw new IllegalArgumentException("Owner cannot be null");
            this.owner = owner;
            return this;
        }

        /**
         * Sets a social media link for the Division.
         * @param media Social Media
         * @param link Link to Social Media
         * @return this class, for chaining
         * @since 1.0.0
         * @throws IllegalArgumentException if media is null, or link is invalid for this media
         */
        @NotNull
        public Builder setSocialMedia(@NotNull SocialMedia media, @Nullable String link) throws IllegalArgumentException {
            if (media == null) throw new IllegalArgumentException("Media cannot be null");
            if (!media.isValidLink(link)) throw new IllegalArgumentException("Invalid link for " + media.name());
            socialMedia.put(media, link);
            return this;
        }

        /**
         * Builds this Division.
         * @return Division
         */
        @NotNull
        public Division build() {
            DIVISION_CACHE.clear();
            return null;
        }

    }

    // Writing & Reading

    /**
     * <p>Saves the logs for this Division.</p>
     * <p>This method is automatically called every 24 hours from the plugin starting.</p>
     */
    public void saveLogs() {
        try {
            writeLogs();
        } catch (IOException e) {
            DivConfig.print(e);
        }

        messageLog.clear();
    }

    private void writeLogs() throws IOException {
        File chatF = new File(folder, "logs/chat");
        if (!chatF.exists()) chatF.mkdirs();

        File today = new File(chatF, new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log");
        if (!today.exists()) today.createNewFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(today, true))) {
            for (String s : messageLog) {
                writer.write(s);
                writer.newLine();
            }

            writer.flush();
        }
    }

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
        oConfig.set("prefix", this.prefix);
        oConfig.set("tagline", this.tagline);
        oConfig.save(other);

        File socials = new File(folder, "socials.dat");
        if (!socials.exists()) socials.createNewFile();

        ObjectOutputStream socialsOs = new ObjectOutputStream(Files.newOutputStream(socials.toPath()));
        socialsOs.writeObject(this.socialMedia);
        socialsOs.close();
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
        d.prefix = oConfig.getString("prefix");
        d.tagline = oConfig.getString("tagline", "");

        File socials = new File(folder, "socials.dat");
        if (!socials.exists()) throw new IllegalStateException("Could not find: socials.dat");

        ObjectInputStream socialsIs = new ObjectInputStream(Files.newInputStream(socials.toPath()));
        Map<SocialMedia, String> socialsMap = (Map<SocialMedia, String>) socialsIs.readObject();
        socialsIs.close();

        d.socialMedia.putAll(socialsMap);

        return d;
    }

}
