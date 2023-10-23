package net.defade.bismuth.core;

import net.defade.bismuth.core.packet.Packet;
import net.defade.bismuth.core.packet.PacketFlow;
import net.defade.bismuth.core.packet.PacketSet;
import net.defade.bismuth.core.packet.client.login.ClientLoginPasswordValidationPacket;
import net.defade.bismuth.core.packet.client.login.ClientLoginRSAKeyPacket;
import net.defade.bismuth.core.packet.handlers.PacketHandler;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientLoginPacketHandler;
import net.defade.bismuth.core.packet.handlers.clientbound.ClientRheniumPacketHandler;
import net.defade.bismuth.core.packet.handlers.serverbound.ServerLoginPacketHandler;
import net.defade.bismuth.core.packet.handlers.serverbound.ServerRheniumPacketHandler;
import net.defade.bismuth.core.packet.server.login.ServerLoginAESKeyPacket;
import net.defade.bismuth.core.packet.server.login.ServerLoginPasswordPacket;
import net.defade.bismuth.core.packet.server.login.ServerLoginRequestedProtocolPacket;
import java.util.HashMap;
import java.util.Map;

public enum BismuthProtocol {
    LOGIN(protocol()
            .addFlow(PacketFlow.CLIENT_BOUND, new PacketSet<>(ClientLoginPacketHandler.class)
                    .addPacket(ClientLoginRSAKeyPacket.class, ClientLoginRSAKeyPacket::new)
                    .addPacket(ClientLoginPasswordValidationPacket.class, ClientLoginPasswordValidationPacket::new)
            )
            .addFlow(PacketFlow.SERVER_BOUND, new PacketSet<>(ServerLoginPacketHandler.class)
                    .addPacket(ServerLoginAESKeyPacket.class, ServerLoginAESKeyPacket::new)
                    .addPacket(ServerLoginPasswordPacket.class, ServerLoginPasswordPacket::new)
                    .addPacket(ServerLoginRequestedProtocolPacket.class, ServerLoginRequestedProtocolPacket::new)
            )
    ),
    RHENIUM(protocol()
            .addFlow(PacketFlow.CLIENT_BOUND, new PacketSet<>(ClientRheniumPacketHandler.class)

            )
            .addFlow(PacketFlow.SERVER_BOUND, new PacketSet<>(ServerRheniumPacketHandler.class)

            )
    );

    private final Map<PacketFlow, PacketSet<?>> packets;

    BismuthProtocol(ProtocolBuilder protocolBuilder) {
        this(protocolBuilder.build());
    }

    BismuthProtocol(Map<PacketFlow, PacketSet<?>> packets) {
        this.packets = packets;
    }

    /**
     * Get the id of a packet
     * @param flow The flow of the packet
     * @param packet The packet to get the id from
     * @return The id of the packet or null if the packet is not registered for this protocol
     */
    public Integer getPacketId(PacketFlow flow, Packet<? extends PacketHandler> packet) {
        return packets.get(flow).getId(packet.getClass());
    }

    /**
     * Deserialize a packet
     * @param flow The flow of the packet
     * @param packetId The id of the packet to deserialize
     * @param byteBuf The byte buffer to deserialize the packet from
     * @return The deserialized packet or null if the packet is not registered for this protocol
     * @throws Throwable If an error occurs while deserializing the packet
     */
    public Packet<? extends PacketHandler> createPacket(PacketFlow flow, int packetId, BismuthByteBuffer byteBuf) throws Throwable {
        return packets.get(flow).createPacket(packetId, byteBuf);
    }

    /**
     * Get the protocol from a packet handler
     * @param packetHandler The packet handler to get the protocol from
     * @return The protocol or null if the packet handler is not registered for any protocol
     */
    public static BismuthProtocol getHandledProtocolByHandler(PacketHandler packetHandler) {
        for(BismuthProtocol protocols : values()) {
            for(PacketSet<?> packetSets : protocols.packets.values()) {
                if(packetSets.getPacketHandlerClass().isAssignableFrom(packetHandler.getClass())) {
                    return protocols;
                }
            }
        }

        return null;
    }

    private static ProtocolBuilder protocol() {
        return new ProtocolBuilder();
    }

    private static class ProtocolBuilder {
        private final Map<PacketFlow, PacketSet<?>> packets = new HashMap<>();

        public <T extends PacketHandler> ProtocolBuilder addFlow(PacketFlow side, PacketSet<T> packetSet) {
            packets.put(side, packetSet);
            return this;
        }

        public Map<PacketFlow, PacketSet<?>> build() {
            return packets;
        }
    }
}
