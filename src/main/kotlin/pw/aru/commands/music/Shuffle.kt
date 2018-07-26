package pw.aru.commands.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import pw.aru.core.commands.Command
import pw.aru.core.commands.ICommand
import pw.aru.core.commands.UseFullInjector
import pw.aru.core.commands.context.CommandContext
import pw.aru.core.music.GuildMusicPlayer
import pw.aru.core.music.MusicManager
import pw.aru.utils.commands.HelpFactory
import pw.aru.utils.emotes.SUCCESS
import java.util.concurrent.LinkedBlockingDeque

@Command("shuffle")
@UseFullInjector
class Shuffle(musicManager: MusicManager) : MusicPermissionCommand(musicManager, "voteshuffle"), ICommand.HelpDialogProvider {
    override fun CommandContext.actionWithPerms(musicPlayer: GuildMusicPlayer, currentTrack: AudioTrack) {
        musicPlayer.queue = LinkedBlockingDeque(musicPlayer.queue.shuffled())
        event.channel.sendMessage("$SUCCESS Queue shuffled!").queue()
    }

    override val helpHandler = HelpFactory("Shuffle Command") {
        description(
            "Shuffles the current queue!",
            "",
            "To be able to shuffle the queue, you have to:",
            "- The only user one listening to me",
            "- Have either DJ or Server Admin permissions"
        )

        alsoSee("voteshuffle", "Create a poll to vote to shuffle the queue.")
    }
}

@Command("voteshuffle")
@UseFullInjector
class VoteShuffle(musicManager: MusicManager) : MusicVotingCommand(musicManager), ICommand.HelpDialogProvider {
    override fun getVotes(musicPlayer: GuildMusicPlayer) = musicPlayer.voteShuffles

    override fun CommandContext.onVoteAdded(votesLeft: Int) {
        send("$SUCCESS Your vote to shuffle the music has been added. More $votesLeft votes are required to shuffle.").queue()
    }

    override fun CommandContext.onVoteRemoved(votesLeft: Int) {
        send("$SUCCESS Your vote to shuffle the music has been removed. More $votesLeft votes are required to shuffle.").queue()
    }

    override fun CommandContext.onVotesReached(musicPlayer: GuildMusicPlayer, currentTrack: AudioTrack, args: String) {
        musicPlayer.queue = LinkedBlockingDeque(musicPlayer.queue.shuffled())
        send("$SUCCESS Enough votes reached! Queue shuffled.").queue()
    }

    override val helpHandler = HelpFactory("VoteShuffle Command") {
        description(
            "Create a poll to shuflle the queue.",
            "",
            "If 60% or more of the users listening vote, the queue will be shuffled."
        )

        alsoSee("shuffle", "Pause the player without requiring voting.")
    }
}