package org.libGML4J.core;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;

public class Audio {
    private static long context;
    private static long device;
    private static List<Integer> buffers = new ArrayList<>();
    private static List<Integer> sources = new ArrayList<>();

    public static void init() {
        device = alcOpenDevice((ByteBuffer) null);

        if (device == 0) {
            throw new RuntimeException("Failed to open audio device");
        }

        ALCCapabilities caps = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer)  null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(caps);
    }

    public static void play(int sourceID) { alSourcePlay(sourceID); }
    public static void pause(int sourceID) { alSourcePause(sourceID); }
    public static void stop(int sourceID) { alSourceStop(sourceID); }

    public static void destroy() {
        for (int s : sources) alDeleteSources(s);
        for (int b : buffers) alDeleteBuffers(b);
        sources.clear();
        buffers.clear();

        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    public static int loadSound(String path) {
        IntBuffer channels = MemoryUtil.memAllocInt(1);
        IntBuffer sampleRate = MemoryUtil.memAllocInt(1);

        ShortBuffer rawAudio = stb_vorbis_decode_filename(path, channels, sampleRate);
        if (rawAudio == null) {
            MemoryUtil.memFree(channels);
            MemoryUtil.memFree(sampleRate);
            throw new RuntimeException("Failed to load audio: " + path);
        }

        int format = channels.get(0) == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;
        int rate = sampleRate.get(0);

        int bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudio, rate);

        int sourceId = alGenSources();
        alSourcei(sourceId, AL_BUFFER, bufferId);

        buffers.add(bufferId);
        sources.add(sourceId);

        MemoryUtil.memFree(rawAudio);
        MemoryUtil.memFree(channels);
        MemoryUtil.memFree(sampleRate);

        return sourceId;
    }

    public static void setLooping(int sourceID, boolean looping) {
        alSourcei(sourceID, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
    }

    public static void setVolume(int sourceID, float volume) {
        alSourcef(sourceID, AL_GAIN, volume);
    }
}
