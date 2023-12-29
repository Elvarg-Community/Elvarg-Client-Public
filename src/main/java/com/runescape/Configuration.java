package com.runescape;

/**
 * The main configuration for the Client
 *
 * @author Seven
 */
public final class Configuration {

    public static final String CACHE_HASH_LINK = "https://assets.illerai.com/hash";
    public static final String CACHE_LINK = "https://assets.illerai.com/elvarg_cache.zip";

    public static final int UPDATE_SERVER_PORT = 43580;
    public static final int CACHE_VERSION = 160;
    public static final int UPDATE_SERVER_VERSION = 1;
    public static final int UID = 8784521;
    public static final String CACHE_DIRECTORY = "./Cache/"; //System.getProperty("user.home") + File.separator + "OSRSPKV"+CLIENT_VERSION+"/";
    /**
     * Toggles a security feature called RSA to prevent packet sniffers
     */
    public static final boolean ENABLE_RSA = true;
    /**
     * A string which indicates the Client's name.
     */
    public static final String CLIENT_NAME = "Elvarg";
    /**
     * npcBits can be changed to what your server's bits are set to.
     */
    public static final int npcBits = 14;
    /**
     * Sends client-related debug messages to the client output stream
     */
    public static boolean PRODUCTION_MODE = false;
    public static String SERVER_ADDRESS = PRODUCTION_MODE ? "localhost" : "localhost";
    public static int SERVER_PORT = 43595;
    /**
     * Dumps map region images when new regions are loaded.
     */
    public static boolean dumpMapRegions = false;

    /**
     * Displays fps and memory
     */
    public static boolean displayFps = false;

    /**
     * Displays debug information
     */
    public static boolean clientData = false;

    /**
     * Used to repack indexes Index 1 = Models Index 2 = Animations Index 3 =
     * Sounds/Music Index 4 = Maps
     */
    public static boolean repackIndexOne = false, repackIndexTwo = false, repackIndexThree = false, repackIndexFour = false;

    /**
     * Dump Indexes Index 1 = Models Index 2 = Animations Index 3 = Sounds/Music
     * Index 4 = Maps
     */
    public static boolean dumpIndexOne = false, dumpIndexTwo = false, dumpIndexThree = false, dumpIndexFour = false;

    /**
     * Enables exp counter
     */
    public static boolean expCounterOpen = true;


    /**
     * Enables/Disables Revision 554 hitmarks
     */
    public static boolean hitmarks554 = false;
    /**
     * Enables the use of run energy
     */
    public static boolean runEnergy = false;

    /**
     * Displays names above entities
     */
    public static boolean namesAboveHeads = false;


    /**
     * Enables/Disables Revision 554 health bar
     */
    public static boolean hpBar554 = false;
    /**
     * Enables the HUD to display 10 X the amount of hitpoints
     */
    public static boolean tenXHp = false;

    /**
     * Enables bounty hunter interface
     */
    public static boolean bountyHunterInterface = true;

}
