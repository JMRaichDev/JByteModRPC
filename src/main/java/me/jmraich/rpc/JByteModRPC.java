package me.jmraich.rpc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

import me.grax.jbytemod.JByteMod;
import me.grax.jbytemod.plugin.Plugin;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.util.Map;

public class JByteModRPC extends Plugin {
    public JByteModRPC() {
        super("Discord-RPC", "1.0", "JMRaichDev");
    }

    public void init() {
        DiscordRPC lib = DiscordRPC.INSTANCE;
        String applicationId = "814078085066522655";
        String steamId = "";

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> System.out.println("Ready!");
        lib.Discord_Initialize(applicationId, handlers, true, steamId);

        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.details = "Idling";
        presence.largeImageKey = "java";
        presence.largeImageText = "JByteMod";
        lib.Discord_UpdatePresence(presence);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    if (JByteMod.instance.getCurrentNode() != null && JByteMod.instance.getCurrentMethod() != null) {
                        String[] cnodeName = JByteMod.instance.getCurrentNode().name.split("/", 999);
                        MethodNode mnode = JByteMod.instance.getCurrentMethod();

                        String className = cnodeName[cnodeName.length-1];

                        presence.details = "Working on : " + className;

                        presence.state = "Method : " + mnode.name + mnode.desc;
                    }else if(JByteMod.instance.getCurrentNode() != null){ // so JByteMod.instance.getCurrentMethod() is null
                        String[] cnodeName = JByteMod.instance.getCurrentNode().name.split("/", 999);
                        String className = cnodeName[cnodeName.length-1];

                        presence.details = "Working on : " + className;
                        presence.state = "";
                    }else { // so JByteMod.instance.getCurrentNode() and JByteMod.instance.getCurrentMethod() are both null
                        presence.details = "Idling";
                        presence.state = "";
                    }

                    lib.Discord_UpdatePresence(presence);
                    Thread.sleep(1700);
                } catch (InterruptedException ignored) {}
            }
        }, "RPC-Callback-Handler").start();
    }

    public void loadFile(Map<String, ClassNode> map) {    }

    public boolean isClickable() {
        return false;
    }

    public void menuClick() {    }
}
