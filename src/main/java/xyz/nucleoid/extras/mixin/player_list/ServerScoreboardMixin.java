package xyz.nucleoid.extras.mixin.player_list;

import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nucleoid.extras.PlayerListHelper;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "addPlayerToTeam", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/ServerScoreboard;runUpdateListeners()V", shift = At.Shift.AFTER))
    private void updatePlayerAfterJoining(String playerName, Team team, CallbackInfoReturnable<Boolean> cir) {
        PlayerListHelper.updatePlayer(this.server.getPlayerManager().getPlayer(playerName));
    }

    @Inject(method = "removePlayerFromTeam", at = @At("TAIL"))
    private void updatePlayerAfterLeaving(String playerName, Team team, CallbackInfo ci) {
        PlayerListHelper.updatePlayer(this.server.getPlayerManager().getPlayer(playerName));
    }

    @Inject(method = "updateRemovedTeam", at = @At("TAIL"))
    private void updatePlayerAfterRemovingTeam(Team team, CallbackInfo ci) {
        for (String playerName : team.getPlayerList()) {
            PlayerListHelper.updatePlayer(this.server.getPlayerManager().getPlayer(playerName));
        }
    }
}
