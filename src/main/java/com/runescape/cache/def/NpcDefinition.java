package com.runescape.cache.def;

import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.config.VariableBits;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import net.runelite.api.HeadIcon;
import net.runelite.api.IterableHashTable;
import net.runelite.rs.api.RSIterableNodeHashTable;
import net.runelite.rs.api.RSNPCComposition;

import java.util.HashMap;
import java.util.Map;

/**
 * Refactored reference:
 * http://www.rune-server.org/runescape-development/rs2-client/downloads/575183-almost-fully-refactored-317-client.html
 */
public final class NpcDefinition implements RSNPCComposition {

	private static final String PETS[][] = { { "318", "Dark Core" }, { "495", "Venenatis Spiderling" },
			{ "497", "Callisto Cub" }, { "964", "Hellpuppy" }, { "2055", "Chaos Elemental Jr." },
			{ "2130", "Snakeling" }, { "2131", "Magma Snakeling" }, { "2132", "Tanzanite Snakeling" },
			{ "5536", "Vet'ion" }, { "5537", "Vet'ion Reborn" }, { "5561", "Scorpias' Offspring" },
			{ "5884", "Abyssal Orphan" }, { "5892", "TzRek-Jad" }, { "6628", "Dagganoth Supreme Jr." },
			{ "6629", "Dagganoth Prime Jr." }, { "6630", "Dagganoth Rex Jr." }, { "6631", "Chick'arra" },
			{ "6632", "General Awwdor" }, { "6633", "Commander Miniana" }, { "6634", "K'ril Tinyroth" },
			{ "6635", "Baby Mole" }, { "6636", "Prince Black Dragon" }, { "6637", "Kalphite Princess" },
			{ "6638", "Kalphite Princess" }, { "6639", "Smoke Devil" }, { "6640", "Baby Kraken" },
			{ "6642", "Penance Princess" }, { "7520", "Olmlet" },

			{ "6715", "Heron" }, { "6717", "Beaver" }, { "6718", "Red Chinchompa" }, { "6719", "Grey Chinchompa" },
			{ "6720", "Black Chimchompa" }, { "6723", "Rock Golem" }, { "7334", "Giant Squirrel" },
			{ "7335", "Tangleroot" }, { "7336", "Rocky" },

			{ "7337", "Fire Rift Guardian" }, { "7338", "Air Rift Guardian" }, { "7339", "Mind Rift Guardian" },
			{ "7340", "Water Rift Guardian" }, { "7341", "Earth Rift Guardian" }, { "7342", "Body Rift Guardian" },
			{ "7343", "Cosmic Rift Guardian" }, { "7344", "Chaos Rift Guardian" }, { "7345", "Nature Rift Guardian" },
			{ "7346", "Law Rift Guardian" }, { "7347", "Death Rift Guardian" }, { "7348", "Soul Rift Guardian" },
			{ "7349", "Astral Rift Guardian" }, { "7350", "Blood Rift Guardian" } };

	public static int anInt56;
	public static Buffer dataBuf;
	public static int[] offsets;
	public static NpcDefinition[] cache;
	public static Client clientInstance;
	public static ReferenceCache modelCache = new ReferenceCache(30);
	public final int anInt64;
	public int rotate90CCWAnimIndex;
	public int varbitId;
	public int rotate180AnimIndex;
	public int varpIndex;
	public int combatLevel;
	public String name;
	public String actions[];
	public int walkingAnimation;
	public int size;
	public int[] recolorToReplace;
	public int[] chatheadModels;
	public int headIcon;
	public int[] recolorToFind;
	public int standingAnimation;
	public long interfaceType;
	public int rotationSpeed;
	public boolean isPet = false;
	public int rotate90CWAnimIndex;
	public boolean clickable;
	public int ambient;
	public int heightScale;
	public boolean isMinimapVisible;
	public int configs[];
	public boolean rotationFlag = true;
	public int rotateLeftAnimation = -1;
	public int rotateRightAnimation = -1;
	private int category;
	public short[] textureReplace;
	public short[] textureFind;
	public byte description[];
	public int widthScale;
	public int contrast;
	public boolean priorityRender;
	public int[] models;
	public int id;
	private Map<Integer, Object> params = null;

	public NpcDefinition() {
		rotate90CCWAnimIndex = -1;
		varbitId = -1;
		rotate180AnimIndex = -1;
		varpIndex = -1;
		combatLevel = -1;
		anInt64 = 1834;
		walkingAnimation = -1;
		size = 1;
		headIcon = -1;
		standingAnimation = -1;
		interfaceType = -1L;
		rotationSpeed = 32;
		rotate90CWAnimIndex = -1;
		clickable = true;
		heightScale = 128;
		isMinimapVisible = true;
		widthScale = 128;
		priorityRender = false;
	}

	/**
	 * Lookup an NpcDefinition by its id
	 *
	 * @param id
	 */
	public static NpcDefinition lookup(int id) {
		for (int index = 0; index < 20; index++)
			if (cache[index].interfaceType == (long) id)
				return cache[index];

		anInt56 = (anInt56 + 1) % 20;
		NpcDefinition definition = cache[anInt56] = new NpcDefinition();
		dataBuf.currentPosition = offsets[id];
		definition.interfaceType = id;
		definition.id = id;
		definition.decode(dataBuf);

		switch (id) {
		// Pets
		case 497: // Callisto pet
			definition.widthScale = 45;
			definition.size = 2;
			break;
		case 6609: // Callisto
			definition.size = 4;
			break;
		case 995:
			definition.recolorToFind = new int[2];
			definition.recolorToReplace = new int[2];
			definition.recolorToFind[0] = 528;
			definition.recolorToReplace[0] = 926;
			break;
		case 7456:
			definition.actions = new String[] { "Repairs", null, null, null, null, null, null };
			break;
		case 1274:
			definition.combatLevel = 35;
			break;
		case 2660:
			definition.combatLevel = 0;
			definition.actions = new String[] { "Trade", null, null, null, null, null, null };
			definition.name = "Pker";
			break;
		case 6477:
			definition.combatLevel = 210;
			break;
		case 6471:
			definition.combatLevel = 131;
			break;
		case 5816:
			definition.combatLevel = 38;
			break;
		case 100:
			definition.isMinimapVisible = true;
			break;
		case 1306:
			definition.actions = new String[] { "Make-over", null, null, null, null, null, null };
			break;
		case 3309:
			definition.name = "Mage";
			definition.actions = new String[] { "Trade", null, "Equipment", "Runes", null, null, null };
			break;
		case 1158:
			definition.name = "@or1@Maxed bot";
			definition.combatLevel = 126;
			definition.actions = new String[] { null, "Attack", null, null, null, null, null };
			definition.models[5] = 268; // platelegs rune
			definition.models[0] = 18954; // Str cape
			definition.models[1] = 21873; // Head - neitznot
			definition.models[8] = 15413; // Shield rune defender
			definition.models[7] = 5409; // weapon whip
			definition.models[4] = 13307; // Gloves barrows
			definition.models[6] = 3704; // boots climbing
			definition.models[9] = 290; // amulet glory
			break;
		case 1200:
			definition.copy(lookup(1158));
			definition.models[7] = 539; // weapon dds
			break;
		case 4096:
			definition.name = "@or1@Archer bot";
			definition.combatLevel = 90;
			definition.actions = new String[] { null, "Attack", null, null, null, null, null };
			definition.models[0] = 20423; // cape avas
			definition.models[1] = 21873; // Head - neitznot
			definition.models[7] = 31237; // weapon crossbow
			definition.models[4] = 13307; // Gloves barrows
			definition.models[6] = 3704; // boots climbing
			definition.models[5] = 20139; // platelegs zammy hides
			definition.models[2] = 20157; // platebody zammy hides
			definition.standingAnimation = 7220;
			definition.walkingAnimation = 7223;
			definition.rotate180AnimIndex = 7220;
			definition.rotate90CCWAnimIndex = 7220;
			definition.rotate90CWAnimIndex = 7220;
			break;
		case 1576:
			definition.actions = new String[] { "Trade", null, "Equipment", "Ammunition", null, null, null };
			break;
		case 3343:
			definition.actions = new String[] { "Trade", null, "Heal", null, null, null, null };
			break;
		case 506:
		case 526:
			definition.actions = new String[] { "Trade", null, null, null, null, null, null };
			break;
		case 315:
			definition.actions = new String[] { "Talk-to", null, "Trade", "Sell Emblems", "Request Skull", null, null };
			break;

		}
		return definition;
	}

	public static int TOTAL_NPCS;

	public static void init(FileArchive archive) {
        dataBuf = new Buffer(archive.readFile("npc.dat"));
        Buffer idxBuf = new Buffer(archive.readFile("npc.idx"));

		int size = idxBuf.readUShort();
		TOTAL_NPCS = size;

		offsets = new int[size];

		int offset = 2;

		for (int count = 0; count < size; count++) {
			offsets[count] = offset;
			offset += idxBuf.readUShort();
		}

		cache = new NpcDefinition[20];

		for (int count = 0; count < 20; count++) {
			cache[count] = new NpcDefinition();
		}

		System.out.println("Loaded: " + size + " mobs");
	}

	public static void clear() {
		modelCache = null;
		offsets = null;
		cache = null;
		dataBuf = null;
	}

	private void copy(NpcDefinition copy) {
		size = copy.size;
		rotationSpeed = copy.rotationSpeed;
		walkingAnimation = copy.walkingAnimation;
		rotate180AnimIndex = copy.rotate180AnimIndex;
		rotate90CWAnimIndex = copy.rotate90CWAnimIndex;
		rotate90CCWAnimIndex = copy.rotate90CCWAnimIndex;
		varbitId = copy.varbitId;
		varpIndex = copy.varpIndex;
		combatLevel = copy.combatLevel;
		name = copy.name;
		description = copy.description;
		headIcon = copy.headIcon;
		clickable = copy.clickable;
		ambient = copy.ambient;
		heightScale = copy.heightScale;
		widthScale = copy.widthScale;
		isMinimapVisible = copy.isMinimapVisible;
		contrast = copy.contrast;
		actions = new String[copy.actions.length];
		for (int i = 0; i < actions.length; i++) {
			actions[i] = copy.actions[i];
		}
		models = new int[copy.models.length];
		for (int i = 0; i < models.length; i++) {
			models[i] = copy.models[i];
		}
		priorityRender = copy.priorityRender;
	}

	public Model model() {
		if (configs != null) {
			NpcDefinition entityDef = morph();
			if (entityDef == null)
				return null;
			else
				return entityDef.model();
		}
		if (chatheadModels == null)
			return null;
		boolean flag1 = false;
		for (int index = 0; index < chatheadModels.length; index++)
			if (!Model.isCached(chatheadModels[index]))
				flag1 = true;

		if (flag1)
			return null;
		Model models[] = new Model[chatheadModels.length];
		for (int index = 0; index < chatheadModels.length; index++)
			models[index] = Model.getModel(chatheadModels[index]);

		Model model;
		if (models.length == 1)
			model = models[0];
		else
			model = new Model(models.length, models);
		if (recolorToFind != null) {
			for (int index = 0; index < recolorToFind.length; index++)
				model.recolor(recolorToFind[index], recolorToReplace[index]);

		}
		return model;
	}

	public NpcDefinition morph() {
		int child = -1;
		if (varbitId != -1) {
			VariableBits varBit = VariableBits.varbits[varbitId];
			int variable = varBit.getSetting();
			int low = varBit.getLow();
			int high = varBit.getHigh();
			int mask = Client.BIT_MASKS[high - low];
			child = clientInstance.settings[variable] >> low & mask;
		} else if (varpIndex != -1)
			child = clientInstance.settings[varpIndex];
		if (child < 0 || child >= configs.length || configs[child] == -1)
			return null;
		else
			return lookup(configs[child]);
	}

	public Model method164(int j, int frame, int ai[]) {
		if (configs != null) {
			NpcDefinition entityDef = morph();
			if (entityDef == null)
				return null;
			else
				return entityDef.method164(j, frame, ai);
		}
		Model model = (Model) modelCache.get(interfaceType);
		if (model == null) {
			boolean flag = false;
			for (int i1 = 0; i1 < models.length; i1++)
				if (!Model.isCached(models[i1]))
					flag = true;

			if (flag)
				return null;
			Model models[] = new Model[this.models.length];
			for (int j1 = 0; j1 < this.models.length; j1++)
				models[j1] = Model.getModel(this.models[j1]);

			if (models.length == 1)
				model = models[0];
			else
				model = new Model(models.length, models);
			if (recolorToFind != null) {
				for (int k1 = 0; k1 < recolorToFind.length; k1++)
					model.recolor(recolorToFind[k1], recolorToReplace[k1]);

			}
			model.generateBones();
			model.scale(132, 132, 132);
			model.light(84 + ambient, 1000 + contrast, -90, -580, -90, true);
			modelCache.put(model, interfaceType);
		}
		Model empty = Model.emptyModel;
		empty.replaceModel(model, Frame.noAnimationInProgress(frame) & Frame.noAnimationInProgress(j));
		if (frame != -1 && j != -1)
			empty.mix(ai, j, frame);
		else if (frame != -1)
			empty.applyTransform(frame);
		if (widthScale != 128 || heightScale != 128)
			empty.scale(widthScale, widthScale, heightScale);
		empty.calculateDiagonals();
		empty.faceGroups = null;
		empty.vertexGroups = null;
		if (size == 1)
			empty.singleTile = true;
		return empty;
	}

	public Model getAnimatedModel(int primaryFrame, int secondaryFrame, int interleaveOrder[]) {
		if (configs != null) {
			NpcDefinition definition = morph();
			if (definition == null)
				return null;
			else
				return definition.getAnimatedModel(primaryFrame, secondaryFrame, interleaveOrder);
		}
		Model model = (Model) modelCache.get(interfaceType);
		if (model == null) {
			boolean flag = false;
			for (int index = 0; index < models.length; index++)
				if (!Model.isCached(models[index]))
					flag = true;
			if (flag) {
				return null;
			}
			Model models[] = new Model[this.models.length];
			for (int index = 0; index < this.models.length; index++)
				models[index] = Model.getModel(this.models[index]);

			if (models.length == 1)
				model = models[0];
			else
				model = new Model(models.length, models);
			if (recolorToFind != null) {
				for (int index = 0; index < recolorToFind.length; index++)
					model.recolor(recolorToFind[index], recolorToReplace[index]);

			}
			model.generateBones();
			model.light(64 + ambient, 850 + contrast, -30, -50, -30, true);
			modelCache.put(model, interfaceType);
		}
		Model model_1 = Model.emptyModel;
		model_1.replaceModel(model,
				Frame.noAnimationInProgress(secondaryFrame) & Frame.noAnimationInProgress(primaryFrame));
		if (secondaryFrame != -1 && primaryFrame != -1)
			model_1.mix(interleaveOrder, primaryFrame, secondaryFrame);
		else if (secondaryFrame != -1)
			model_1.applyTransform(secondaryFrame);
		if (widthScale != 128 || heightScale != 128)
			model_1.scale(widthScale, widthScale, heightScale);
		model_1.calculateDiagonals();
		model_1.faceGroups = null;
		model_1.vertexGroups = null;
		if (size == 1)
			model_1.singleTile = true;
		return model_1;
	}

	public void decode(Buffer buffer) {
	    
	    while(true) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0) {
                return;
            } else if (opcode == 1) {
                int len = buffer.readUnsignedByte();
				models = new int[len];
                for (int i = 0; i < len; i++) {
                    models[i] = buffer.readUShort();
                }
            } else if (opcode == 2) {
                name = buffer.readJagexString();
            } else if (opcode == 12) {
                size = buffer.readUnsignedByte();
            } else if (opcode == 13) {
				standingAnimation = buffer.readUShort();
            } else if (opcode == 14) {
				walkingAnimation = buffer.readUShort();
            } else if (opcode == 15) {
				rotateLeftAnimation = buffer.readUShort();
            } else if (opcode == 16) {
				rotateRightAnimation = buffer.readUShort();
            } else if (opcode == 17) {
                walkingAnimation = buffer.readUShort();
				rotate180AnimIndex = buffer.readUShort();
                rotate90CWAnimIndex = buffer.readUShort();
                rotate90CCWAnimIndex = buffer.readUShort();

				if (walkingAnimation == 65535) {
					walkingAnimation = -1;
				}

				if (rotate180AnimIndex == 65535) {
					rotate180AnimIndex = -1;
				}

				if (rotate90CWAnimIndex == 65535) {
					rotate90CWAnimIndex = -1;
				}

				if (rotate90CCWAnimIndex == 65535) {
					rotate90CCWAnimIndex = -1;
				}
			} else if (opcode == 18) {
				category = buffer.readUShort();
            } else if (opcode >= 30 && opcode < 35) {
                if (actions == null) {
                    actions = new String[5];
                }

                actions[opcode - 30] = buffer.readString();

                if (actions[opcode - 30].equalsIgnoreCase("Hidden")) {
                    actions[opcode - 30] = null;
                }
            } else if (opcode == 40) {
                int len = buffer.readUnsignedByte();
                recolorToFind = new int[len];
                recolorToReplace = new int[len];
                for (int i = 0; i < len; i++) {
					recolorToFind[i] = buffer.readUShort();
					recolorToReplace[i] = buffer.readUShort();
                }

			} else if (opcode == 41) {
				int length = buffer.readUnsignedByte();
				textureFind = new short[length];
				textureReplace = new short[length];
				for (int index = 0; index < length; index++) {
					textureFind[index] = (short) buffer.readUShort();
					textureReplace[index] = (short) buffer.readUShort();
				}
            } else if (opcode == 60) {
                int len = buffer.readUnsignedByte();
				chatheadModels = new int[len];
                for (int i = 0; i < len; i++) {
                    chatheadModels[i] = buffer.readUShort();
                }
            } else if (opcode == 93) {
				isMinimapVisible = false;
            } else if (opcode == 95)
                combatLevel = buffer.readUShort();
            else if (opcode == 97)
				widthScale = buffer.readUShort();
            else if (opcode == 98)
				heightScale = buffer.readUShort();
            else if (opcode == 99)
                priorityRender = true;
            else if (opcode == 100)
				ambient = buffer.readSignedByte();
            else if (opcode == 101)
				contrast = buffer.readSignedByte();
            else if (opcode == 102)
                headIcon = buffer.readUShort();
            else if (opcode == 103)
				rotationSpeed = buffer.readUShort();
            else if (opcode == 106 || opcode == 118) {
				varbitId = buffer.readUShort();

				if (varbitId == 65535) {
					varbitId = -1;
				}

				varpIndex = buffer.readUShort();

				if (varpIndex == 65535) {
					varpIndex = -1;
				}

				int value = -1;

				if (opcode == 118) {
					value = buffer.readUShort();
				}

				int len = buffer.readUnsignedByte();
				configs = new int[len + 2];
				for (int i = 0; i <= len; i++) {
					configs[i] = buffer.readUShort();
					if (configs[i] == 65535) {
						configs[i] = -1;
					}
				}
				configs[len + 1] = value;
			} else if (opcode == 109) {
					rotationFlag = false;
			} else if (opcode == 111) {
					isPet = true;
			} else if (opcode == 107) {
				clickable = false;
			} else if (opcode == 249)  {
				int length = buffer.readUnsignedByte();

				params = new HashMap<>(length);

				for (int i = 0; i < length; i++) {
					boolean isString = buffer.readUnsignedByte() == 1;
					int key = buffer.read24Int();
					Object value;

					if (isString) {
						value = buffer.readString();
					} else {
						value = buffer.readInt();
					}

					params.put(key, value);
				}
            } else {
                System.out.println(String.format("npc def invalid opcode: %d", opcode));
            }
        }
	}


	@Override
	public HeadIcon getOverheadIcon() {
		return null;
	}

	@Override
	public int getIntValue(int paramID) {
		return 0;
	}

	@Override
	public void setValue(int paramID, int value) {

	}

	@Override
	public String getStringValue(int paramID) {
		return null;
	}

	@Override
	public void setValue(int paramID, String value) {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int[] getModels() {
		return new int[0];
	}

	@Override
	public String[] getActions() {
		return new String[0];
	}

	@Override
	public boolean isClickable() {
		return false;
	}

	@Override
	public boolean isFollower() {
		return false;
	}

	@Override
	public boolean isInteractible() {
		return false;
	}

	@Override
	public boolean isMinimapVisible() {
		return false;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public int getCombatLevel() {
		return 0;
	}

	@Override
	public int[] getConfigs() {
		return new int[0];
	}

	@Override
	public RSNPCComposition transform() {
		return null;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public int getRsOverheadIcon() {
		return 0;
	}

	@Override
	public RSIterableNodeHashTable getParams() {
		return null;
	}

	@Override
	public void setParams(IterableHashTable params) {

	}

	@Override
	public void setParams(RSIterableNodeHashTable params) {

	}
}
