package net.defade.bismuth.core.packet;

import net.defade.bismuth.core.packet.handlers.PacketHandler;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A set of packets with their IDs and deserializers for a given protocol and packet flow.
 * @param <T> the packet handler in charge of handling packets in this set
 */
public class PacketSet<T extends PacketHandler> {
    private final Class<T> packetHandlerClass; // Handler for this packet set
    private final List<Function<ByteBuffer, Packet<T>>> packetDeserializers = new ArrayList<>(); // Deserializers for this packet set
    private final Map<Class<? extends Packet<T>>, Integer> packetClassToId = new HashMap<>(); // Map of packet classes to their IDs

    public PacketSet(Class<T> packetHandlerClass) {
        this.packetHandlerClass = packetHandlerClass;
    }

    /**
     * Add a packet to this set and assign it an ID
     * @param packetClass the packet class
     * @param deserializer the packet deserializer
     * @return this packet set for chaining
     */
    public PacketSet<T> addPacket(Class<? extends Packet<T>> packetClass, Function<ByteBuffer, Packet<T>> deserializer) {
        int id = packetDeserializers.size();

        packetClassToId.put(packetClass, id);
        packetDeserializers.add(deserializer);

        return this;
    }

    /**
     * Get the ID of a packet
     * @param packetClass the packet class
     * @return the packet ID or null if the packet is not present in this set
     */
    public Integer getId(Class<?> packetClass) {
        return packetClassToId.get(packetClass);
    }

    /**
     * Deserializes a packet
     * @param id the id of the packet to deserialize
     * @param byteBuffer the byte buffer to deserialize the packet from
     * @return the deserialized packet or null if the packet id is invalid for this set
     */
    public Packet<T> createPacket(int id, ByteBuffer byteBuffer) {
        if(id < 0 || id > packetDeserializers.size()) return null;

        Function<ByteBuffer, Packet<T>> deserializer = packetDeserializers.get(id);
        return deserializer != null ? deserializer.apply(byteBuffer) : null;
    }

    /**
     * @return the packet handler class for this packet set
     */
    public Class<?> getPacketHandlerClass() {
        return packetHandlerClass;
    }
}
