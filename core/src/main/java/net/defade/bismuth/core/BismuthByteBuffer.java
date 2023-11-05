package net.defade.bismuth.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Objects;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BismuthByteBuffer {
    private final ByteBuffer byteBuffer;

    public BismuthByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public String getString() {
        return new String(getByteArray());
    }

    public void putString(String string) {
        putByteArray(string.getBytes());
    }

    public byte[] getByteArray() {
        short length = getShort();
        byte[] bytes = new byte[length];
        get(bytes);

        return bytes;
    }

    public byte[] putByteArray(byte[] bytes) {
        putShort((short) bytes.length);
        put(bytes);

        return bytes;
    }

    public boolean getBoolean() {
        return byteBuffer.get() == 1;
    }

    public ByteBuffer putBoolean(boolean bool) {
        return byteBuffer.put((byte) (bool ? 1 : 0));
    }

    public ByteBuffer slice() {
        return byteBuffer.slice();
    }

    public ByteBuffer slice(int i, int i1) {
        return byteBuffer.slice(i, i1);
    }

    public ByteBuffer duplicate() {
        return byteBuffer.duplicate();
    }

    public ByteBuffer asReadOnlyBuffer() {
        return byteBuffer.asReadOnlyBuffer();
    }

    public byte get() {
        return byteBuffer.get();
    }

    public ByteBuffer put(byte b) {
        return byteBuffer.put(b);
    }

    public byte get(int i) {
        return byteBuffer.get(i);
    }

    public ByteBuffer put(int i, byte b) {
        return byteBuffer.put(i, b);
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {
        return byteBuffer.get(dst, offset, length);
    }

    public ByteBuffer get(byte[] dst) {
        return byteBuffer.get(dst);
    }

    public ByteBuffer get(int index, byte[] dst, int offset, int length) {
        return byteBuffer.get(index, dst, offset, length);
    }

    public ByteBuffer get(int index, byte[] dst) {
        return byteBuffer.get(index, dst);
    }

    public ByteBuffer put(ByteBuffer src) {
        return byteBuffer.put(src);
    }

    public ByteBuffer put(int index, ByteBuffer src, int offset, int length) {
        return byteBuffer.put(index, src, offset, length);
    }

    public ByteBuffer put(byte[] src, int offset, int length) {
        return byteBuffer.put(src, offset, length);
    }

    public ByteBuffer put(byte[] src) {
        return byteBuffer.put(src);
    }

    public ByteBuffer put(int index, byte[] src, int offset, int length) {
        return byteBuffer.put(index, src, offset, length);
    }

    public ByteBuffer put(int index, byte[] src) {
        return byteBuffer.put(index, src);
    }

    public boolean hasArray() {
        return byteBuffer.hasArray();
    }

    public byte[] array() {
        return byteBuffer.array();
    }

    public int arrayOffset() {
        return byteBuffer.arrayOffset();
    }

    public ByteBuffer position(int newPosition) {
        return byteBuffer.position(newPosition);
    }

    public ByteBuffer limit(int newLimit) {
        return byteBuffer.limit(newLimit);
    }

    public ByteBuffer mark() {
        return byteBuffer.mark();
    }

    public ByteBuffer reset() {
        return byteBuffer.reset();
    }

    public ByteBuffer clear() {
        return byteBuffer.clear();
    }

    public ByteBuffer flip() {
        return byteBuffer.flip();
    }

    public ByteBuffer rewind() {
        return byteBuffer.rewind();
    }

    public ByteBuffer compact() {
        return byteBuffer.compact();
    }

    public boolean isDirect() {
        return byteBuffer.isDirect();
    }

    @Override
    public String toString() {
        return byteBuffer.toString();
    }

    @Override
    public int hashCode() {
        return byteBuffer.hashCode();
    }

    public int compareTo(ByteBuffer that) {
        return byteBuffer.compareTo(that);
    }

    public int mismatch(ByteBuffer that) {
        return byteBuffer.mismatch(that);
    }

    public ByteOrder order() {
        return byteBuffer.order();
    }

    public ByteBuffer order(ByteOrder bo) {
        return byteBuffer.order(bo);
    }

    public int alignmentOffset(int index, int unitSize) {
        return byteBuffer.alignmentOffset(index, unitSize);
    }

    public ByteBuffer alignedSlice(int unitSize) {
        return byteBuffer.alignedSlice(unitSize);
    }

    public char getChar() {
        return byteBuffer.getChar();
    }

    public ByteBuffer putChar(char c) {
        return byteBuffer.putChar(c);
    }

    public char getChar(int i) {
        return byteBuffer.getChar(i);
    }

    public ByteBuffer putChar(int i, char c) {
        return byteBuffer.putChar(i, c);
    }

    public CharBuffer asCharBuffer() {
        return byteBuffer.asCharBuffer();
    }

    public short getShort() {
        return byteBuffer.getShort();
    }

    public ByteBuffer putShort(short i) {
        return byteBuffer.putShort(i);
    }

    public short getShort(int i) {
        return byteBuffer.getShort(i);
    }

    public ByteBuffer putShort(int i, short i1) {
        return byteBuffer.putShort(i, i1);
    }

    public ShortBuffer asShortBuffer() {
        return byteBuffer.asShortBuffer();
    }

    public int getInt() {
        return byteBuffer.getInt();
    }

    public ByteBuffer putInt(int i) {
        return byteBuffer.putInt(i);
    }

    public int getInt(int i) {
        return byteBuffer.getInt(i);
    }

    public ByteBuffer putInt(int i, int i1) {
        return byteBuffer.putInt(i, i1);
    }

    public IntBuffer asIntBuffer() {
        return byteBuffer.asIntBuffer();
    }

    public long getLong() {
        return byteBuffer.getLong();
    }

    public ByteBuffer putLong(long l) {
        return byteBuffer.putLong(l);
    }

    public long getLong(int i) {
        return byteBuffer.getLong(i);
    }

    public ByteBuffer putLong(int i, long l) {
        return byteBuffer.putLong(i, l);
    }

    public LongBuffer asLongBuffer() {
        return byteBuffer.asLongBuffer();
    }

    public float getFloat() {
        return byteBuffer.getFloat();
    }

    public ByteBuffer putFloat(float v) {
        return byteBuffer.putFloat(v);
    }

    public float getFloat(int i) {
        return byteBuffer.getFloat(i);
    }

    public ByteBuffer putFloat(int i, float v) {
        return byteBuffer.putFloat(i, v);
    }

    public FloatBuffer asFloatBuffer() {
        return byteBuffer.asFloatBuffer();
    }

    public double getDouble() {
        return byteBuffer.getDouble();
    }

    public ByteBuffer putDouble(double v) {
        return byteBuffer.putDouble(v);
    }

    public double getDouble(int i) {
        return byteBuffer.getDouble(i);
    }

    public ByteBuffer putDouble(int i, double v) {
        return byteBuffer.putDouble(i, v);
    }

    public DoubleBuffer asDoubleBuffer() {
        return byteBuffer.asDoubleBuffer();
    }

    public int capacity() {
        return byteBuffer.capacity();
    }

    public int position() {
        return byteBuffer.position();
    }

    public int limit() {
        return byteBuffer.limit();
    }

    public int remaining() {
        return byteBuffer.remaining();
    }

    public boolean hasRemaining() {
        return byteBuffer.hasRemaining();
    }

    public boolean isReadOnly() {
        return byteBuffer.isReadOnly();
    }

    public ByteBuffer getBackingByteBuffer() {
        return byteBuffer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BismuthByteBuffer that = (BismuthByteBuffer) o;
        return Objects.equals(byteBuffer, that.byteBuffer);
    }
}
