package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class SPacketTitle implements Packet<INetHandlerPlayClient>
{
    private SPacketTitle.Type type;
    private IChatComponent message;
    private int fadeInTime;
    private int displayTime;
    private int fadeOutTime;

    public SPacketTitle()
    {
    }

    public SPacketTitle(SPacketTitle.Type type, IChatComponent message)
    {
        this(type, message, -1, -1, -1);
    }

    public SPacketTitle(int fadeInTime, int displayTime, int fadeOutTime)
    {
        this(SPacketTitle.Type.TIMES, (IChatComponent)null, fadeInTime, displayTime, fadeOutTime);
    }

    public SPacketTitle(SPacketTitle.Type type, IChatComponent message, int fadeInTime, int displayTime, int fadeOutTime)
    {
        this.type = type;
        this.message = message;
        this.fadeInTime = fadeInTime;
        this.displayTime = displayTime;
        this.fadeOutTime = fadeOutTime;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.type = (SPacketTitle.Type)buf.readEnumValue(SPacketTitle.Type.class);

        if (this.type == SPacketTitle.Type.TITLE || this.type == SPacketTitle.Type.SUBTITLE)
        {
            this.message = buf.readChatComponent();
        }

        if (this.type == SPacketTitle.Type.TIMES)
        {
            this.fadeInTime = buf.readInt();
            this.displayTime = buf.readInt();
            this.fadeOutTime = buf.readInt();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.type);

        if (this.type == SPacketTitle.Type.TITLE || this.type == SPacketTitle.Type.SUBTITLE)
        {
            buf.writeChatComponent(this.message);
        }

        if (this.type == SPacketTitle.Type.TIMES)
        {
            buf.writeInt(this.fadeInTime);
            buf.writeInt(this.displayTime);
            buf.writeInt(this.fadeOutTime);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleTitle(this);
    }

    public SPacketTitle.Type getType()
    {
        return this.type;
    }

    public IChatComponent getMessage()
    {
        return this.message;
    }

    public int getFadeInTime()
    {
        return this.fadeInTime;
    }

    public int getDisplayTime()
    {
        return this.displayTime;
    }

    public int getFadeOutTime()
    {
        return this.fadeOutTime;
    }

    public static enum Type
    {
        TITLE,
        SUBTITLE,
        TIMES,
        CLEAR,
        RESET;

        public static SPacketTitle.Type byName(String name)
        {
            for (SPacketTitle.Type s45packettitle$type : values())
            {
                if (s45packettitle$type.name().equalsIgnoreCase(name))
                {
                    return s45packettitle$type;
                }
            }

            return TITLE;
        }

        public static String[] getNames()
        {
            String[] astring = new String[values().length];
            int i = 0;

            for (SPacketTitle.Type s45packettitle$type : values())
            {
                astring[i++] = s45packettitle$type.name().toLowerCase();
            }

            return astring;
        }
    }
}
