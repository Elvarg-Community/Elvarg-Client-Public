package com.runescape.cache.anim;

import com.runescape.cache.FileArchive;
import com.runescape.io.Buffer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Animation {

    public static Animation animations[];
    public static int anInt367;
    public int frameCount;
    public int primaryFrames[];
    public int secondaryFrames[];
    public int chatFrameIds[];
    public int frameSounds[];
    public int[] durations;
    public int loopOffset;
    public int interleaveOrder[];
    public boolean stretches;
    public int forcedPriority;
    public int playerOffhand;
    public int playerMainhand;
    public int maximumLoops;
    public int animatingPrecedence;
    public int priority;
    public int replayMode;

    private int skeletalRangeBegin;
    private int skeletalRangeEnd;
    private int skeletalId;

    private boolean[] masks;

    public Map<Integer, Integer> skeletalSounds;


    private Animation() {
        loopOffset = -1;
        skeletalId = -1;
        skeletalRangeEnd = -1;
        skeletalRangeBegin = -1;
        stretches = false;
        forcedPriority = 5;
        playerOffhand = -1; //Removes shield
        playerMainhand = -1; //Removes weapon
        maximumLoops = 99;
        animatingPrecedence = -1; //Stops character from moving
        priority = -1;
        replayMode = 1;
    }

    public static void init(FileArchive archive) {
        Buffer stream = new Buffer(archive.readFile("seq.dat"));
        int length = stream.readUShort();
        if (animations == null)
            animations = new Animation[length];
        for (int j = 0; j < length; j++) {
            if (animations[j] == null) {
                animations[j] = new Animation();
            }
            animations[j].decode(stream);

        }

        System.out.println("Loaded: " + length + " animations");
    }

    public int duration(int i) {
        int j = durations[i];
        if (j == 0) {
            Frame frame = Frame.method531(primaryFrames[i]);
            if (frame != null) {
                j = durations[i] = frame.duration;
            }
        }
        if (j == 0)
            j = 1;
        return j;
    }

    private void decode(Buffer buffer) {
        while(true) {
            int opcode = buffer.readUnsignedByte();

            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                frameCount = buffer.readUShort();
                primaryFrames = new int[frameCount];
                secondaryFrames = new int[frameCount];
                durations = new int[frameCount];

                for (int i = 0; i < frameCount; i++) {
                    durations[i] = buffer.readUShort();
                }

                for (int i = 0; i < frameCount; i++) {
                    primaryFrames[i] = buffer.readUShort();
                    secondaryFrames[i] = -1;
                }

                for (int i = 0; i < frameCount; i++) {
                    primaryFrames[i] += buffer.readUShort() << 16;
                }

            } else if (opcode == 2) {
                loopOffset = buffer.readUShort();
            } else if (opcode == 3) {
                int len = buffer.readUnsignedByte();
                interleaveOrder = new int[len + 1];
                for (int i = 0; i < len; i++) {
                    interleaveOrder[i] = buffer.readUnsignedByte();
                }
                interleaveOrder[len] = 9999999;
            } else if (opcode == 4) {
                stretches = true;
            } else if (opcode == 5) {
                forcedPriority = buffer.readUnsignedByte();
            } else if (opcode == 6) {
                playerOffhand = buffer.readUShort();
            } else if (opcode == 7) {
                playerMainhand = buffer.readUShort();
            } else if (opcode == 8) {
                maximumLoops = buffer.readUnsignedByte();
            } else if (opcode == 9) {
                animatingPrecedence = buffer.readUnsignedByte();
            } else if (opcode == 10) {
                priority = buffer.readUnsignedByte();
            } else if (opcode == 11) {
                replayMode = buffer.readUnsignedByte();
            } else if (opcode == 12) {
                int len = buffer.readUnsignedByte();

                chatFrameIds = new int[len];
                for (int i = 0; i < len; i++) {
                    chatFrameIds[i] = buffer.readUShort();
                }

                for (int i = 0; i < len; i++) {
                    chatFrameIds[i] = buffer.readUShort() << 16;
                }
            } else if (opcode == 13) {
                int len = buffer.readUnsignedByte();
                frameSounds = new int[len];
                for (int i = 0; i < len; i++) {
                    frameSounds[i] = buffer.read24Int();
                }
            } else if (opcode == 14) {
                skeletalId = buffer.readInt();
            } else if (opcode == 15) {
                this.skeletalSounds = new HashMap<Integer, Integer>();
                int count = buffer.readUShort();

                for (int index = 0; index < count; index++) {
                    skeletalSounds.put(buffer.readUShort(),buffer.read24Int());
                }
            } else if (opcode == 16) {
                skeletalRangeBegin = buffer.readUShort();
                skeletalRangeEnd = buffer.readUShort();
            } else if (opcode == 17) {

                this.masks = new boolean[256];
                Arrays.fill(masks,false);

                int count = buffer.readUnsignedByte();

                for(int index = 0; index < count; ++index) {
                    this.masks[buffer.readUnsignedByte()] = true;
                }
            } else {
                System.err.printf("Error unrecognised {Anim} opcode: %d%n%n", opcode);
            }
        }

        if (frameCount == 0) {
            frameCount = 1;
            primaryFrames = new int[1];
            primaryFrames[0] = -1;
            secondaryFrames = new int[1];
            secondaryFrames[0] = -1;
            durations = new int[1];
            durations[0] = -1;
        }

        if (animatingPrecedence == -1) {
            animatingPrecedence = (interleaveOrder == null) ? 0 : 2;
        }

        if (priority == -1) {
            priority = (interleaveOrder == null) ? 0 : 2;
        }
    }

    public boolean hasSkeletalID() {
        return this.skeletalId >= 0;
    }

    public int skeletalStart() {
        return this.skeletalRangeEnd - this.skeletalRangeBegin;
    }

}