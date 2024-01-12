package io.github.tomasborsje.slugcraft.quickfire;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.tomasborsje.slugcraft.core.Registration;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class StartRoundCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("startround").executes(StartRoundCommand::execute));
    }
    private static int execute(CommandContext<CommandSourceStack> command){
        // Check we're not clientside
        if(command.getSource().getLevel().isClientSide()){
            return Command.SINGLE_SUCCESS;
        }

        if(command.getSource().getEntity() instanceof ServerPlayer player){
            // Get quickfire capability
            LazyOptional<IQuickfireCapability> quickfireData = player.serverLevel().getCapability(Registration.QUICKFIRE_HANDLER);
            quickfireData.ifPresent(quickfire -> quickfire.startRound(player.serverLevel(), player));
        }
        return Command.SINGLE_SUCCESS;
    }
}
