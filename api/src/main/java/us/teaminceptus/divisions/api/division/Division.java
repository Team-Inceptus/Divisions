package us.teaminceptus.divisions.api.division;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import us.teaminceptus.divisions.api.division.logs.AuditLogEntry;
import us.teaminceptus.divisions.api.events.division.DivisionBanEvent;
import us.teaminceptus.divisions.api.events.division.DivisionCreateEvent;
import us.teaminceptus.divisions.api.events.division.DivisionKickEvent;
import us.teaminceptus.divisions.api.events.division.DivisionUnbanEvent;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a Division
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public final class Division {

    // Constants

    /**
     * The internal maximum amount of players a Division can have.
     * @since 1.0.0
     */
    public static final int MAX_PLAYERS = 1000;

    /**
     * The prefix used for communication in Divisions.
     * @since 1.0.0
     */
    public static final String CHAT_PREFIX = ChatColor.DARK_GREEN
            + DivConfig.getConfiguration().get("constants.division")
            + ChatColor.WHITE + " > ";

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

    private final Map<DivSetting<?>, Object> settings = new HashMap<>();

    private final List<AuditLogEntry> auditLog = new ArrayList<>();

    private final Map<DivisionAchievement, Integer> achievements = new EnumMap<>(DivisionAchievement.class);

    private final Set<OfflinePlayer> members = new HashSet<>();

    private final Set<UUID> banList = new HashSet<>();

    private final Map<SocialMedia, String> socialMedia = new EnumMap<>(SocialMedia.class);

    {
        for (DivisionAchievement value : DivisionAchievement.values()) achievements.putIfAbsent(value, 0);
    }

    private Division(File folder, UUID id, long creationDate, OfflinePlayer owner) {
        this.folder = folder;

        this.id = id;
        this.creationDate = creationDate;
        this.owner = owner;
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
     * @since 1.0.0
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
     * @throws IllegalArgumentException if player is null, banned, or is a member of another division
     * @throws IllegalStateException if division is full
     */
    public void addMember(@NotNull OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        addMember(player, true);
    }

    private void addMember(@NotNull OfflinePlayer player, boolean save) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (members.contains(player) || isInDivision(player)) throw new IllegalArgumentException("Player is already a member of a division");
        if (banList.contains(player.getUniqueId())) throw new IllegalArgumentException("Player is banned from this division");

        if (members.size() >= DivConfig.getConfiguration().getMaxDivisionSize()) throw new IllegalStateException("Division is full");

        members.add(player);

        AuditLogEntry entry = new AuditLogEntry(new Date(), AuditLogEntry.Action.MEMBER_JOINED, player);
        auditLog.add(entry);
        writeLog(entry);

        save();
    }

    /**
     * Whether this OfflinePlayer is currently a member of this Division.
     * @param player Player to check
     * @return true if member, false otherwise
     * @since 1.0.0
     */
    public boolean isMember(@NotNull OfflinePlayer player) {
        return members.contains(player);
    }

    /**
     * <p>Adds members to this Division.</p>
     * <p>This method will automatically sort through the Iterable to only add players that are not members and are not banned.</p>
     * @param players Players to add
     * @since 1.0.0
     * @throws IllegalArgumentException if iterable is null
     * @throws IllegalStateException if division is full
     */
    public void addMembers(@NotNull Iterable<? extends OfflinePlayer> players) throws IllegalArgumentException, IllegalStateException {
        if (players == null) throw new IllegalArgumentException("Players cannot be null");
        List<OfflinePlayer> toAdd = ImmutableList.copyOf(players)
                .stream()
                .filter(player -> !members.contains(player) && !banList.contains(player.getUniqueId()))
                .collect(Collectors.toList());

        if (members.size() + toAdd.size() >= DivConfig.getConfiguration().getMaxDivisionSize()) throw new IllegalStateException("Division cannot support that many members (" + toAdd.size() + ")");

        for (OfflinePlayer player : toAdd) addMember(player, false);
        save();
    }

    /***
     * Removes a member from this Division.
     * @param player Player to remove
     * @since 1.0.0
     * @throws IllegalArgumentException if player is null
     */
    public void kickMember(@NotNull OfflinePlayer player) throws IllegalArgumentException {
        kickMember(player, null);
    }

    /**
     * Removes a member from this Division.
     * @param player Player to remove
     * @param initiator Optional Player that initiated the kick
     * @since 1.0.0
     * @throws IllegalArgumentException if player is null or not a member
     */
    public void kickMember(@NotNull OfflinePlayer player, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        kickMember(player, initiator, true);
    }

    private void kickMember(@NotNull OfflinePlayer player, @Nullable OfflinePlayer initiator, boolean save) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (!members.contains(player)) throw new IllegalArgumentException("Player is not a member of this division");

        members.remove(player);

        DivisionKickEvent event = new DivisionKickEvent(this, player, initiator);
        Bukkit.getPluginManager().callEvent(event);

        AuditLogEntry entry = new AuditLogEntry(new Date(), AuditLogEntry.Action.MEMBER_KICKED, player.getName(), event.getInitiator());
        auditLog.add(entry);
        writeLog(entry);

        if (save) save();
    }

    /**
     * <p>Removes members from this Division.</p>
     * <p>This method will automatically sort through the Iterable to only remove players that are members.</p>
     * @param players Players to remove
     * @since 1.0.0
     * @throws IllegalArgumentException if iterable is null
     */
    public void kickMembers(@NotNull Iterable<? extends OfflinePlayer> players) throws IllegalArgumentException{
        kickMembers(players, null);
    }

    /**
     * <p>Removes members from this Division.</p>
     * <p>This method will automatically sort through the iterable and only kick members of the Divison.</p>
     * @param players Players to remove
     * @param initiator Optional Player that initiated the kick
     * @since 1.0.0
     * @throws IllegalArgumentException if iterable is null
     */
    public void kickMembers(@NotNull Iterable<? extends OfflinePlayer> players, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        if (players == null) throw new IllegalArgumentException("Players cannot be null");

        for (OfflinePlayer player : players) if (isMember(player)) kickMember(player, initiator, false);
        save();
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
     * @throws IllegalArgumentException if achievement is null or value is not between 0 and Achievement's Max Value
     */
    public void setAchievementLevel(@NotNull DivisionAchievement achievement, int value) throws IllegalArgumentException {
        if (achievement == null) throw new IllegalArgumentException("Achievement cannot be null");
        if (value < 0 || value > achievement.getMaxLevel()) throw new IllegalArgumentException("Value cannot be negative or greater than max level");

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
     * @throws IllegalArgumentException if experience is negative
     */
    public void setExperience(double experience) throws IllegalArgumentException {
        if (experience < 0) throw new IllegalArgumentException("Experience cannot be negative");
        this.experience = experience;
        save();
    }

    /**
     * Adds experience to this Division.
     * @param experience Experience to add
     * @since 1.0.0
     */
    public void addExpereince(double experience) {
        setExperience(getExperience() + experience);
    }

    /**
     * Removes experience from this Division.
     * @param experience Experience to remove
     * @since 1.0.0
     * @throws IllegalArgumentException if removed amount turns the balance to negative
     */
    public void removeExperience(double experience) throws IllegalArgumentException {
        setExperience(getExperience() - experience);
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
     * @throws IllegalArgumentException if level is negative
     */
    public void setLevel(int level) throws IllegalArgumentException {
        if (level < 0) throw new IllegalArgumentException("Level cannot be negative");
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
     * @since 1.0.0
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
        return ImmutableList.copyOf(getLog("chat"));
    }

    /**
     * Broadcasts a message to all members of this Division.
     * @param message Message to broadcast
     * @since 1.0.0
     * @throws IllegalArgumentException if message is null
     */
    public void broadcastMessage(@NotNull String message) throws IllegalArgumentException {
        if (message == null) throw new IllegalArgumentException("Message cannot be null");

        for (Player player : getOnlineMembers()) player.sendMessage(CHAT_PREFIX + message);

        String logPrefix = new SimpleDateFormat("[HH:mm:ss] ").format(new Date());
        writeLog("chat", logPrefix + message);
    }

    /**
     * Fetches an immutable map of all of this Division's Achievements to their levels.
     * @return Achievements
     * @since 1.0.0
     */
    @NotNull
    @Unmodifiable
    public Map<DivisionAchievement, Integer> getAchievements() {
        return ImmutableMap.copyOf(achievements);
    }

    /**
     * Fetches an immutable list of the Audit Log Entries for this Division.
     * @return Audit Log
     * @since 1.0.0
     */
    @NotNull
    @Unmodifiable
    public List<AuditLogEntry> getAuditLog() {
        return ImmutableList.copyOf(auditLog);
    }

    /**
     * Fetches an immutable set of all of the players banned from this Division.
     * @return Banned Players
     * @since 1.0.0
     */
    @NotNull
    @Unmodifiable
    public Set<OfflinePlayer> getBanList() {
        return banList.stream()
                .map(Bukkit::getOfflinePlayer)
                .collect(Collectors.collectingAndThen(Collectors.toSet(), ImmutableSet::copyOf));
    }

    /**
     * Determines whether this Division has banned the given player.
     * @param player Player to check
     * @return true if banned, false otherwise
     * @since 1.0.0
     * @throws IllegalArgumentException if player is null
     */
    public boolean isBanned(@NotNull OfflinePlayer player) throws IllegalArgumentException {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        return banList.contains(player.getUniqueId());
    }

    /**
     * Bans the given player from this Division, kicking them if they are in the Division.
     * @param uid UUID of Player to ban
     * @since 1.0.0
     * @throws IllegalArgumentException if uid is null
     */
    public void ban(@NotNull UUID uid) throws IllegalArgumentException {
        ban(uid, null);
    }

    /**
     * Bans the given player from this Division, kicking them if they are in the Division.
     * @param uid UUID of Player to ban
     * @param initiator Player who initiated the ban
     * @since 1.0.0
     * @throws IllegalArgumentException if uid is null
     */
    public void ban(@NotNull UUID uid, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        if (uid == null) throw new IllegalArgumentException("UUID cannot be null");

        ban(Bukkit.getOfflinePlayer(uid), initiator);
    }

    /**
     * Bans the given player from this Division, kicking them if they are in the Division.
     * @param player Player to ban
     * @since 1.0.0
     * @throws IllegalArgumentException if player is null
     */
    public void ban(@NotNull OfflinePlayer player) throws IllegalArgumentException {
        ban(player, null);
    }

    /**
     * Bans the given player from this Division, kicking them if they are in the Division.
     * @param player Player to ban
     * @param initiator Optional Player who initiated the ban
     * @since 1.0.0
     * @throws IllegalArgumentException if player is null
     */
    public void ban(@NotNull OfflinePlayer player, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        ban(player, initiator, true);
    }

    private void ban(@NotNull OfflinePlayer player, @Nullable OfflinePlayer initiator, boolean save) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (isMember(player)) kickMember(player, initiator);

        banList.add(player.getUniqueId());

        DivisionBanEvent event = new DivisionBanEvent(this, player, initiator);
        Bukkit.getPluginManager().callEvent(event);

        AuditLogEntry entry = new AuditLogEntry(new Date(), AuditLogEntry.Action.PLAYER_BANNED, player.getName(), event.getInitiator());
        auditLog.add(entry);
        writeLog(entry);

        if (save) save();
    }

    /**
     * Bans an entire list of players from this Division, kicking them if they are in the Division.
     * @param players Players to ban
     * @since 1.0.0
     * @throws IllegalArgumentException if iterable is null
     */
    public void ban(@NotNull Iterable<? extends OfflinePlayer> players) throws IllegalArgumentException {
        ban(players, null);
    }

    /**
     * Bans an iterable of players from this Division, kicking them if they are in the Division.
     * @param players Players to ban
     * @param initiator Optional Player who initiated the ban
     * @since 1.0.0
     * @throws IllegalArgumentException if iterable is null
     */
    public void ban(@NotNull Iterable<? extends OfflinePlayer> players, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        if (players == null) throw new IllegalArgumentException("Players cannot be null");

        for (OfflinePlayer player : players) ban(player, initiator, false);
        save();
    }

    /**
     * Unbans the given player from this Division.
     * @param uid UUID of Player to unban
     * @since 1.0.0
     * @throws IllegalArgumentException if uid is null or player is not banned
     */
    public void unban(@NotNull UUID uid) throws IllegalArgumentException {
        unban(uid, null);
    }

    /**
     * Unbans the given player from this Division.
     * @param player Player to unban
     * @since 1.0.0
     * @throws IllegalArgumentException if player is null or not banned
     */
    public void unban(@NotNull OfflinePlayer player) throws IllegalArgumentException {
        unban(player, null);
    }

    /**
     * Unbans the given player from this Division.
     * @param uid UUID of Player to unban
     * @param initiator Optional Player who initiated the unban
     * @since 1.0.0
     * @throws IllegalArgumentException if uid is null or player is not banned
     */
    public void unban(@NotNull UUID uid, @Nullable OfflinePlayer initiator) throws IllegalArgumentException{
        if (uid == null) throw new IllegalArgumentException("UUID cannot be null");

        unban(Bukkit.getOfflinePlayer(uid), initiator);
    }

    /**
     * Unbans the given player from this Division.
     * @param player Player to unban
     * @param initiator Optional Player who initiated the unban
     * @since 1.0.0
     * @throws IllegalArgumentException if player is null or not banned
     */
    public void unban(@NotNull OfflinePlayer player, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        unban(player, initiator, true);
    }

    private void unban(@NotNull OfflinePlayer player, @Nullable OfflinePlayer initiator, boolean save) {
        if (player == null) throw new IllegalArgumentException("Player cannot be null");
        if (!isBanned(player)) throw new IllegalArgumentException("Player is not banned");

        banList.remove(player.getUniqueId());

        DivisionUnbanEvent event = new DivisionUnbanEvent(this, player, initiator);
        Bukkit.getPluginManager().callEvent(event);

        AuditLogEntry entry = new AuditLogEntry(new Date(), AuditLogEntry.Action.PLAYER_UNBANNED, player.getName(), event.getInitiator());
        auditLog.add(entry);
        writeLog(entry);

        if (save) save();
    }

    /**
     * <p>Unbans am iterable of players from this Division.</p>
     * <p>This method will automatically filter through this iterable, only unbanning banned players.</p>
     * @param players Players to unban
     * @since 1.0.0
     * @throws IllegalArgumentException if iterable is null
     */
    public void unban(@NotNull Iterable<? extends OfflinePlayer> players) throws IllegalArgumentException {
        unban(players, null);
    }

    /**
     * <p>Unbans an iterable of players from this Division.</p>
     * <p>This method will automatically filter through this iterable, only unbanning banned players.</p>
     * @param players Players to unban
     * @param initiator Optional Player who initiated the unban
     * @since 1.0.0
     * @throws IllegalArgumentException if iterable is null
     */
    public void unban(@NotNull Iterable<? extends OfflinePlayer> players, @Nullable OfflinePlayer initiator) throws IllegalArgumentException {
        if (players == null) throw new IllegalArgumentException("Players cannot be null");

        for (OfflinePlayer player : players) if (isBanned(player)) unban(player, initiator, false);
        save();
    }

    /**
     * Fetches the value of the inputted setting for this Division.
     * @param setting Setting to fetch
     * @return Value of setting
     * @param <T> Type of setting
     * @since 1.0.0
     * @throws IllegalStateException if setting is not unlocked
     * @throws IllegalArgumentException if setting is null
     */
    public <T> T getSetting(@NotNull DivSetting<T> setting) throws IllegalStateException, IllegalArgumentException {
        if (setting == null) throw new IllegalArgumentException("Setting cannot be null");
        if (getLevel() < setting.getUnlockedLevel()) throw new IllegalStateException("Setting is not unlocked");

        return (T) settings.getOrDefault(setting, setting.getDefaultValue());
    }

    /**
     * Sets the value of the inputted setting for this Division.
     * @param setting Setting to fetch
     * @param value Value to set
     * @param <T> Type of setting
     * @since 1.0.0
     * @throws IllegalStateException if setting is not unlocked
     * @throws IllegalArgumentException if setting or value is null
     */
    public <T> void setSetting(@NotNull DivSetting<T> setting, @NotNull T value) throws IllegalStateException, IllegalArgumentException {
        if (setting == null) throw new IllegalArgumentException("Setting cannot be null");
        if (value == null) throw new IllegalArgumentException("Value cannot be null");
        if (getLevel() < setting.getUnlockedLevel()) throw new IllegalStateException("Setting is not unlocked");

        settings.put(setting, value);
        save();
    }

    // Overrides

    @NotNull
    @Override
    public String toString() {
        return "Division{" +
                "id=" + id +
                ", creation_date=" + creationDate +
                ", owner='" + owner.getName() + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Division division = (Division) o;
        return creationDate == division.creationDate && id.equals(division.id) && name.equals(division.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, name);
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
     * Fetches a Division by a member.
     * @param member Division Member
     * @return Division found, or null if not found
     * @since 1.0.0
     * @throws IllegalArgumentException if member is null
     */
    @Nullable
    public static Division byMember(@NotNull OfflinePlayer member) throws IllegalArgumentException {
        if (member == null) throw new IllegalArgumentException("member cannot be null");
        return getDivisions()
                .stream()
                .filter(d -> d.getMembers().contains(member))
                .findFirst()
                .orElse(null);
    }

    /**
     * Determines whether a Division exists by its name.
     * @param name Division Name
     * @return true if Division exists, false otherwise
     * @since 1.0.0
     */
    public static boolean exists(@Nullable String name) {
        if (name == null) return false;
        return getDivisions()
                .stream()
                .anyMatch(d -> d.getName().equalsIgnoreCase(name));
    }

    /**
     * Determines whether a Division exists by its unique identifier.
     * @param id Division ID
     * @return true if Division exists, false otherwise
     * @since 1.0.0
     */
    public static boolean exists(@Nullable UUID id) {
        if (id == null) return false;
        return getDivisions()
                .stream()
                .anyMatch(d -> d.getUniqueId().equals(id));
    }

    /**
     * Determines whether a Division exists by its owner.
     * @param owner Division Owner
     * @return true if Division exists, false otherwise
     * @since 1.0.0
     */
    public static boolean exists(@Nullable OfflinePlayer owner) {
        if (owner == null) return false;
        return getDivisions()
                .stream()
                .anyMatch(d -> d.getOwner().equals(owner));
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
     * @since 1.0.0
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
     * @since 1.0.0
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
         * @since 1.0.0
         */
        @NotNull
        public Division build() {
            UUID id = UUID.randomUUID();
            File folder = new File(DivConfig.getDivisionsDirectory(), id.toString());
            Date now = new Date();
            folder.mkdir();

            Division d = new Division(folder, id, now.getTime(), owner);
            d.prefix = prefix;
            d.name = name;
            d.tagline = tagline;

            d.socialMedia.putAll(socialMedia);

            AuditLogEntry entry = new AuditLogEntry(now, AuditLogEntry.Action.CREATED, d, owner);
            d.auditLog.add(entry);

            DIVISION_CACHE.clear();
            d.save();
            d.writeLog(entry);

            DivisionCreateEvent event = new DivisionCreateEvent(d);
            Bukkit.getPluginManager().callEvent(event);

            return d;
        }

    }

    // Writing & Reading

    private void writeLog(@NotNull AuditLogEntry entry) {
        writeLog("audit", entry.toString());
    }

    private void writeLog(String type, String message) {
        try {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";

            File todayF = new File(new File(folder, "logs/" + message), today);
            if (!todayF.exists()) {
                todayF.getParentFile().mkdirs();
                todayF.createNewFile();
            }

            String logPrefix = new SimpleDateFormat("[HH:mm:ss] ").format(new Date());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(todayF, true))) {
                writer.write(logPrefix + message);
                writer.newLine();

                writer.flush();
            }
        } catch (IOException e) {
            DivConfig.print(e);
        }
    }

    @NotNull
    private List<String> getLog(String type) {
        List<String> list = new ArrayList<>();
        try {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log";
            File todayF = new File(new File(folder, "logs/" + type), today);
            if (!todayF.exists()) return list;

            try (BufferedReader reader = new BufferedReader(new FileReader(todayF))) {
                String line;
                while ((line = reader.readLine()) != null) list.add(line);
            }
        } catch (IOException e) {
            DivConfig.print(e);
        }

        return list;
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

        // Ban List

        File bans = new File(folder, "bans.dat");
        if (!bans.exists()) bans.createNewFile();

        ObjectOutputStream bansOs = new ObjectOutputStream(Files.newOutputStream(bans.toPath()));
        bansOs.writeObject(this.banList);
        bansOs.close();

        // Achievements
        File achievements = new File(folder, "achievements.dat");
        if (!achievements.exists()) achievements.createNewFile();

        ObjectOutputStream achievementsOs = new ObjectOutputStream(Files.newOutputStream(achievements.toPath()));
        achievementsOs.writeObject(this.achievements);
        achievementsOs.close();

        // Audit Log
        File auditLog = new File(new File(folder, "logs/audit"), "audit.dat");
        if (!auditLog.exists()) {
            auditLog.getParentFile().mkdirs();
            auditLog.createNewFile();
        }

        ObjectOutputStream auditLogOs = new ObjectOutputStream(Files.newOutputStream(auditLog.toPath()));
        auditLogOs.writeObject(this.auditLog);
        auditLogOs.close();

        // Settings
        File settings = new File(folder, "settings.yml");
        if (!settings.exists()) settings.createNewFile();

        YamlConfiguration settingsYml = new YamlConfiguration();
        for (Map.Entry<DivSetting<?>, Object> entry : this.settings.entrySet())
            settingsYml.set(entry.getKey().getKey(), entry.getValue());

        settingsYml.save(settings);

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

        Division d = new Division(folder, id, creationDate, owner);

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

        // Ban List

        File bans = new File(folder, "bans.dat");
        if (!bans.exists()) throw new IllegalStateException("Could not find: bans.dat");

        ObjectInputStream bansIs = new ObjectInputStream(Files.newInputStream(bans.toPath()));
        List<UUID> banIds = (List<UUID>) bansIs.readObject();
        bansIs.close();

        d.banList.addAll(banIds);

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

        // Social Media

        File socials = new File(folder, "socials.dat");
        if (!socials.exists()) throw new IllegalStateException("Could not find: socials.dat");

        ObjectInputStream socialsIs = new ObjectInputStream(Files.newInputStream(socials.toPath()));
        Map<SocialMedia, String> socialsMap = (Map<SocialMedia, String>) socialsIs.readObject();
        socialsIs.close();

        d.socialMedia.putAll(socialsMap);

        // Settings

        File settings = new File(folder, "settings.yml");
        if (!settings.exists()) throw new IllegalStateException("Could not find: settings.yml");

        YamlConfiguration settingsYml = YamlConfiguration.loadConfiguration(settings);
        for (DivSetting<?> setting : DivSetting.values()) {
            if (toLevel(d.experience) < setting.getUnlockedLevel()) continue;
            d.settings.put(setting, settingsYml.get(setting.getKey()));
        }

        return d;
    }

}
