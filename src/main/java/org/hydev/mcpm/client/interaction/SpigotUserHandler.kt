package org.hydev.mcpm.client.interaction

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.HIGHEST
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerCommandEvent
import org.hydev.mcpm.utils.ColorLogger.printc
import org.hydev.mcpm.utils.ConsoleUtils.RAW_OUT
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Take in user input through Spigot
 *
 * @author Azalea (https://github.com/hykilpikonna)
 * @since 2023-01-02
 */
class SpigotUserHandler : Listener
{
    // Unique id to identify the server console user
    private val serverUuid = UUID.randomUUID()

    // Listening[uuid] = handler
    private val listening: HashMap<UUID, (String) -> Unit> = HashMap()

    @EventHandler(priority = HIGHEST)
    fun onSay(e: AsyncPlayerChatEvent) {
        e.hijack(e.player.uniqueId, e.message)
    }

    @EventHandler(priority = HIGHEST)
    fun onConsole(e: ServerCommandEvent) {
        e.hijack(serverUuid, e.command)
    }

    /**
     * When the console types something or when the player say something:
     * If we're listening, we hijack the event.
     */
    private fun Cancellable.hijack(uuid: UUID, msg: String) = listening.remove(uuid)?.let {
        // Run callback
        it(msg)

        // Prevent further event handlers from processing
        isCancelled = true
    }

    /**
     * UUID for players or server senders
     */
    private fun CommandSender.uuid() = if (this is Player) this.uniqueId else serverUuid

    /**
     * When player quit, we stop listening to them.
     */
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        listening.remove(e.player.uniqueId)
    }

    /**
     * Create a user interactor for a player
     */
    fun create(p: CommandSender) = object : ILogger
    {
        // Callback responded, resume the coroutine
        override suspend fun input() = suspendCoroutine { async -> listening[p.uuid()] = { async.resume(it) } }

        override fun print(txt: String) {
            // if (p is Player) p.sendMessage(txt.replace("&", "§")) else RAW_OUT.printc(txt)
            p.sendMessage(txt.replace("&", "§"))
        }
    }
}
