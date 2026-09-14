package org.libGML4J.core;

/*
 * Copyright (c) 2026 ImPulseStory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.libGML4J.Exceptions.SoundLoadException;
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

/*
Audio System class.
This may to play, stop, pause and load audio

Using: Need to init audio system by audio.init();
On program exit need to audio.destroy();
 */
public class Audio {
    private static long context;
    private static long device;
    private static List<Integer> buffers = new ArrayList<>();
    private static List<Integer> sources = new ArrayList<>();

    // Init audio system
    public static void init() {
        device = alcOpenDevice((ByteBuffer) null);

        if (device == 0) {
            throw new SoundLoadException("Failed to open ALC device");
        }

        ALCCapabilities caps = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer)  null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(caps);
    }

    // Play an audio by source ID, sourceID need to be integer
    public static void play(int sourceID) { alSourcePlay(sourceID); }

    // Pause audio by sourceID, sourceID need to be integer
    public static void pause(int sourceID) { alSourcePause(sourceID); }

    // Stop an audio by sourceID, sourceID need to be integer
    public static void stop(int sourceID) { alSourceStop(sourceID); }

    // Destroy all open devices and clean all buffers and sources
    // Calls on program exit
    public static void destroy() {
        for (int s : sources) alDeleteSources(s);
        for (int b : buffers) alDeleteBuffers(b);
        sources.clear();
        buffers.clear();

        alcDestroyContext(context);
        alcCloseDevice(device);
    }

    // Load the sound in memory
    // Return an int - sourceID
    public static int loadSound(String path) {
        IntBuffer channels = MemoryUtil.memAllocInt(1);
        IntBuffer sampleRate = MemoryUtil.memAllocInt(1);

        ShortBuffer rawAudio = stb_vorbis_decode_filename(path, channels, sampleRate);
        if (rawAudio == null) {
            MemoryUtil.memFree(channels);
            MemoryUtil.memFree(sampleRate);
            throw new SoundLoadException("Failed to load sound: " + path);
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

    // Loop play, need sourceID(int) and loopint(bool) - true or false
    public static void setLooping(int sourceID, boolean looping) {
        alSourcei(sourceID, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
    }

    // set volume to play, need sourceID(int) and volume(float) - 0 by 1
    public static void setVolume(int sourceID, float volume) {
        alSourcef(sourceID, AL_GAIN, volume);
    }
}
