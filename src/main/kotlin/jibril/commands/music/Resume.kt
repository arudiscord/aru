package jibril.commands.music

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import jibril.core.commands.Command
import jibril.core.commands.ICommand
import jibril.core.music.GuildMusicPlayer
import jibril.core.music.MusicManager
import jibril.utils.commands.HelpFactory
import jibril.utils.emotes.PLAY
import jibril.utils.emotes.SUCCESS
import jibril.utils.emotes.THINKING
import jibril.utils.emotes.X
import jibril.utils.extensions.withPrefix
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import javax.inject.Inject

@Command("resume")
class Resume @Inject constructor(musicManager: MusicManager) : MusicPermissionCommand(musicManager, "voteresume"), ICommand.HelpDialogProvider {
    override fun action(event: GuildMessageReceivedEvent, musicPlayer: GuildMusicPlayer, currentTrack: AudioTrack, args: String) {
        if (!musicPlayer.audioPlayer.isPaused) {
            event.channel.sendMessage(
                "$X The music is already playing, silly!\n\n$THINKING Maybe you want to pause the music with ``${"pause".withPrefix()}``, instead?"
            ).queue()
            return
        }

        musicPlayer.audioPlayer.isPaused = false
        event.channel.sendMessage("$PLAY Music resumed.").queue()
    }

    override val helpHandler = HelpFactory("Resume Command") {
        description(
            "Resumes the player",
            "",
            "To be able to resume the player, you have to:",
            "- The only user one listening to me",
            "- Have either DJ or Server Admin permissions"
        )

        alsoSee("voteresume", "Create a poll to resume the player.")
        alsoSee("pause", "Pause the player.")
        alsoSee("votepause", "Create a poll to pause the player.")
    }
}

@Command("voteresume")
class VoteResume @Inject constructor(musicManager: MusicManager) : MusicVotingCommand(musicManager), ICommand.HelpDialogProvider {
    override fun checkRequirements(event: GuildMessageReceivedEvent, musicPlayer: GuildMusicPlayer, currentTrack: AudioTrack, args: String): Boolean {
        if (!musicPlayer.audioPlayer.isPaused) {
            event.channel.sendMessage(
                "$X The music is already playing, silly!\n\n$THINKING Maybe you want to pause the music with ``${"votepause".withPrefix()}``, instead?"
            ).queue()
            return false
        }

        return true
    }

    override fun getVotes(musicPlayer: GuildMusicPlayer) = musicPlayer.votePauses

    override fun onVoteAdded(event: GuildMessageReceivedEvent, votesLeft: Int) {
        event.channel.sendMessage(
            "$SUCCESS Your vote to resume the music has been added. More $votesLeft votes are required to resume."
        ).queue()
    }

    override fun onVoteRemoved(event: GuildMessageReceivedEvent, votesLeft: Int) {
        event.channel.sendMessage(
            "$SUCCESS Your vote to resume the music has been removed. More $votesLeft votes are required to resume."
        ).queue()
    }

    override fun onVotesReached(event: GuildMessageReceivedEvent, musicPlayer: GuildMusicPlayer, currentTrack: AudioTrack, args: String) {
        event.channel.sendMessage("$SUCCESS Enough votes reached! Music resumed.").queue()
        musicPlayer.audioPlayer.isPaused = false
    }

    override val helpHandler = HelpFactory("VoteResume Command") {
        description(
            "Create a poll to resume the player.",
            "",
            "If 60% or more of the users listening vote, the player will be resumed."
        )

        alsoSee("resume", "Resume the player without needing voting.")
        alsoSee("pause", "Pause the player.")
        alsoSee("votepause", "Create a poll to pause the player.")
    }
}