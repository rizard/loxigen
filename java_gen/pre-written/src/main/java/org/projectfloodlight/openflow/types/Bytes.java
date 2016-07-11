package org.projectfloodlight.openflow.types;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.nio.ByteBuffer;

import com.google.common.hash.PrimitiveSink;

import org.projectfloodlight.openflow.exceptions.OFParseError;
import org.projectfloodlight.openflow.protocol.Writeable;
import org.projectfloodlight.openflow.protocol.OFMessageReader;
import org.projectfloodlight.openflow.util.ChannelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import com.google.common.hash.PrimitiveSink;

/**
 * A wrapper for a variable-length sequence of bytes.
 * This is primarily for use in OXMs.
 *
 * @author Ryan Izard {@literal <}ryan.izard@bigswitch.com{@literal >}
 */
public class Bytes implements Writeable, OFValueType<Bytes> {
    private static final Logger logger =
            LoggerFactory.getLogger(Bytes.class);

    public static final Bytes NONE = new Bytes(new byte[0]);

    private final byte[] data;

    private Bytes(byte[] data) {
        this.data = data;
    }

    public static Bytes of(byte[] data) {
        return new Bytes(Arrays.copyOf(data, data.length));
    }

    public byte[] getData() {
        return data == null ? NONE.getData() : Arrays.copyOf(data, data.length);
    }

    @Override
    public int compareTo(Bytes o) {
        return this.getLength() - o.getLength(); // naive comparison
    }

    @Override
    public int getLength() {
        return data == null ? 0 : data.length;
    }

    @Override
    public Bytes applyMask(Bytes mask) {
        if (this.getLength() != mask.getData().length) {
            throw new IllegalArgumentException("Cannot mask Bytes of different lengths");
        }
        ByteBuffer bb = ByteBuffer.allocate(this.getLength());
        for (int i = 0; i < this.getLength(); i++) {
            bb.put((byte) (this.getData()[i] & mask.getData()[i]));
        }
        return Bytes.of(bb.array());
    }

    @Override
    public void putTo(PrimitiveSink sink) {
        sink.putInt(this.getLength());
        sink.putBytes(this.getData());
    }

    @Override
    public void writeTo(ByteBuf bb) {
        bb.writeInt(this.getLength());
        bb.writeBytes(this.getData());
    }

    public static final Reader READER = new Reader();

    public static class Reader implements OFMessageReader<Bytes> {
        @Override
        public Bytes readFrom(ByteBuf bb) throws OFParseError {
            int length = bb.readInt();
            byte[] ba = new byte[length];
            bb.readBytes(ba, 0, length);
            return new Bytes(ba);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("0x[ (len=");
        b.append(Integer.toString(data.length));
        b.append(")");
        for (int i = 0; i < data.length; i++) {
            b.append(" ");
            b.append(String.format("%02x", data[i]));
        }
        b.append("]");
        return b.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Bytes other = (Bytes) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        return true;
    }

}
