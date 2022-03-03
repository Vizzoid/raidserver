package org.vizzoid.raidserver.raidserver.minecraft.hub.npc;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.vizzoid.raidserver.raidserver.main.meta.PluginHolder;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities.AIEntity;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities.NPCEntity;
import org.vizzoid.raidserver.raidserver.minecraft.hub.npc.entities.ServerNPC;
import org.vizzoid.raidserver.raidserver.minecraft.utils.convenience.NMS;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Data;
import org.vizzoid.raidserver.raidserver.minecraft.utils.objects.Key;
import org.vizzoid.raidserver.raidserver.minecraft.utils.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creates fake player and mimics npc activity by using a server-side entity invisible to players, and pairing it with its clientside npc
 */
public class NPC extends PluginHolder {

  private static final Scheduler scheduler = new Scheduler();
  private final List<Player> nearby = new ArrayList<>();
  private ServerPlayer npc;
  private NPCEntity entity;
  private ServerNPC serverNPC;
  private boolean ai = false;

  private NPC(NPCEntity entity) {
    if (entity.profile() == null) return;
    this.entity = entity;

    Location loc = entity.loc();
    MinecraftServer mcServer = NMS.to(getServer());
    ServerLevel nmsWorld = NMS.to(loc.getWorld());

    npc = new ServerPlayer(mcServer, nmsWorld, entity.profile());
    npc.setRot(loc.getYaw(), loc.getPitch());
    npc.setPos(loc.getX(), loc.getY(), loc.getZ());

    npc.getBukkitEntity().teleport(loc);

    NPCUtils.add(this);

    scheduler.repeat("DISPLAY_UPDATE", () -> {
      Collection<Player> nearbyNow = loc().getNearbyPlayers(98);
      Collection<Player> nearbyInner = loc().getNearbyPlayers(50);
      if (entity instanceof AIEntity ai && ai.approach() != null) {
        Collection<Player> nearbyApproach = loc().getNearbyPlayers(5);
        for (Player p : nearbyApproach) {
          Data data = new Data(p);
          Key key = new Key(entity.name().content() + "_00");
          if (!data.has(key)) {
            data.set(key, true);
            p.sendMessage(ai.approach());
          }
        }
      }
      for (Player p : nearbyInner) {
        if (!nearby.contains(p)) {
          // Avoid loading npc through walls if it is meant to be 'hidden'
          // If player passes loading block the player will load in and wont unload until chunk is unloaded
          send(p);
          nearby.add(p);
        }
      }
      for (Player p : new ArrayList<>(nearby)) {
        if (!nearbyNow.contains(p)) {
          remove(p);
          nearby.remove(p);
        }
      }
    }, 0, 20);

    if (entity instanceof AIEntity) {
      ai(true);
      loc().getChunk().setForceLoaded(true);
      List<Pillager> serverNPCs = new ArrayList<>(loc.getNearbyEntitiesByType(Pillager.class, 0.5));
      if (serverNPCs.isEmpty()) {
        this.serverNPC = new ServerNPC(entity.loc());
      } else if (NMS.to(serverNPCs.get(0)) instanceof ServerNPC npc) {
        this.serverNPC = npc;
      }

      Location l = serverNPC().getLocation();
      final Location[] oldLoc = {l};

      getPlugin().addDisableLogic("NPC_REMOVE", () -> serverNPC().remove());
      scheduler.repeat("FOLLOW_SERVER", () -> {
        serverNPC.setPos(loc().getX(), loc().getY(), loc().getZ());
        for (Player p : nearby) {
          update(p, oldLoc[0]);
        }
        oldLoc[0] = l;
      }, 2, 2);

    }
  }

  @CanIgnoreReturnValue
  public static NPC create(NPCEntity entity) {
    return new NPC(entity);
  }

  public NPCEntity entity() {
    return entity;
  }

  // TODO include update method with new loc
  private void send(Player player) {

    ClientboundPlayerInfoPacket a = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc);
    ClientboundAddPlayerPacket b = new ClientboundAddPlayerPacket(npc);
    ClientboundSetEntityDataPacket c = new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true);
    ClientboundRotateHeadPacket d = new ClientboundRotateHeadPacket(npc, (byte) (loc().getYaw() * 256F / 360F));
    ClientboundMoveEntityPacket e = new ClientboundMoveEntityPacket.Rot(npc.getId(), (byte) (loc().getYaw() * 256F / 360F), (byte) (loc().getPitch() * 256F / 360F), true);


    ServerPlayer p = NMS.to(player);
    ServerGamePacketListenerImpl connection = p.connection;
    connection.send(a);
    connection.send(b);
    connection.send(c);
    connection.send(d);
    connection.send(e);

    if (ai()) {
      PlayerTeam team = new PlayerTeam(NMS.to(Bukkit.getScoreboardManager().getMainScoreboard()), player.getName());
      team.setNameTagVisibility(Team.Visibility.NEVER);
      ClientboundSetPlayerTeamPacket f = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
      ClientboundSetPlayerTeamPacket g = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false);
      ClientboundSetPlayerTeamPacket h = ClientboundSetPlayerTeamPacket.createPlayerPacket(team, entity.name().content(), ClientboundSetPlayerTeamPacket.Action.ADD);
      connection.send(f);
      connection.send(g);
      connection.send(h);
    }

    scheduler.delay("REMOVE_TABLIST", () -> remove(player), 20 * 5);

  }

  private void remove(Player player) {
    ServerPlayer p = NMS.to(player);
    ClientboundPlayerInfoPacket a = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc);
    p.connection.send(a);

  }

  public void destroy() {
    scheduler.cancelAll();
    npc.remove(Entity.RemovalReason.KILLED);
    NPCUtils.remove(this);
    nearby.forEach(player -> {
      ServerPlayer p = NMS.to(player);
      ClientboundPlayerInfoPacket a = new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc);
      ClientboundRemoveEntitiesPacket b = new ClientboundRemoveEntitiesPacket(npc.getId());
      p.connection.send(a);
      p.connection.send(b);
    });
    nearby.clear();
  }

  // Movement calculation when its needed:
  // ((l.getX() * 32 - oldLoc.getX() * 32) * 128)
  private void update(Player p, @Nullable Location oldLoc) {
    ClientboundRotateHeadPacket a = new ClientboundRotateHeadPacket(npc, (byte) (serverNPC.getYHeadRot() * 256F / 360F));

    ServerGamePacketListenerImpl connection = NMS.to(p).connection;
    if (ai() && oldLoc != null) {
      ClientboundMoveEntityPacket b = new ClientboundMoveEntityPacket.Rot(npc.getId(),
        (byte) (serverNPC.yBodyRotO * 256F / 360F),
        (byte) (serverNPC.getXRot() * 256F / 360F), true);
      connection.send(b);
    }
    connection.send(a);
  }

  private ServerPlayer npc() {
    return npc;
  }

  private Location loc() {
    return entity.loc();
  }

  private Location head() {
    return entity.loc().add(0, 1.6, 0);
  }

  private Creature serverNPC() {
    return serverNPC.getBukkitCreature();
  }

  private boolean ai() {
    return ai;
  }

  private void ai(boolean ai) {
    this.ai = ai;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NPC npc = (NPC) o;

    return entity.equals(npc.entity);
  }

  @Override
  public int hashCode() {
    return entity.hashCode();
  }
}
